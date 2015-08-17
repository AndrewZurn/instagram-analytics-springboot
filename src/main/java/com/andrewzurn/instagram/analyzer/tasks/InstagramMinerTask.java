package com.andrewzurn.instagram.analyzer.tasks;

import com.andrewzurn.instagram.analyzer.exception.DataModelConverterException;
import com.andrewzurn.instagram.analyzer.exception.InstagramApiException;
import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.service.CassandraService;
import com.andrewzurn.instagram.analyzer.service.InstagramService;
import com.andrewzurn.instagram.analyzer.utils.AnalyticsUtils;
import com.andrewzurn.instagram.analyzer.utils.DataModelConversionUtils;
import com.sola.instagram.model.Media;
import com.sola.instagram.model.User;
import com.sola.instagram.util.PaginatedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by andrew on 8/11/15.
 */
@Component
public class InstagramMinerTask {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Inject
  private InstagramService instagramService;
  @Inject
  private CassandraService cassandraService;
  @Inject
  private AnalyticsUtils analyticsUtils;
  @Inject
  private DataModelConversionUtils dataModelConversionUtils;

  private static final int ONE_HALF_MINUTE = 90000;

  @Scheduled(fixedRate = ONE_HALF_MINUTE)
  public void minePopularMedia() throws DataModelConverterException, InstagramApiException {
    if ( this.instagramService.isReady()) {
      handlePopularMining();
    }
  }

  private void handlePopularMining() throws DataModelConverterException, InstagramApiException {
    List<Media> popularMedia = null;
    try {
      popularMedia = this.instagramService.getInstagramPopularMedia();
    } catch (InstagramApiException e) {
      LOGGER.error("Issue while retrieving the instagram popular media: {}", e);
      throw e;
    }

    for(Media media : popularMedia) {
      // calls to instagram api to get our user and media models
      User user = null;
      PaginatedCollection<Media> userRecentMedia = null;
      try {
        user = instagramService.getInstagramUserById(media.getUser().getId());
        userRecentMedia = this.instagramService.getInstagramUserMedias(user.getId());
      } catch (InstagramApiException e) {
        LOGGER.error("{}", e);
        throw e;
      }

      // if user/media is not detected as english, or we already have this user
      if ( !analyticsUtils.determineLanguage(user, userRecentMedia.get(0))) {
        LOGGER.debug("User: {} with id: {} disqualified from further analysis.",
            user.getUserName(), user.getId());
        continue;
      }

      // convert them to cassandra models and save/update
      List<RawUserMedia> userMedias = null;
      try {
        userMedias = dataModelConversionUtils.createRawUserMedia(userRecentMedia);
        handleUserPersistance(user, userMedias);
      } catch (DataModelConverterException e) {
        LOGGER.error("{}", e);
        throw e;
      }
    }
  }

  private void handleFollowsForUsers() {
    this.cassandraService.getUsers();

  }

  private SourceUser handleUserPersistance(User user, List<RawUserMedia> userMedias) throws DataModelConverterException {
    // user to see if user exists, and if so, udpate
    SourceUser sourceUser = this.cassandraService.findByUserId(user.getId());
    if ( sourceUser != null) {
      // save the old created time
      Date originalCreatedTime = sourceUser.getCreatedTime();

      // recreate the user to get updated values
      try {
        sourceUser = dataModelConversionUtils.createSourceUser(user, userMedias);
      } catch (DataModelConverterException e) {
        LOGGER.error("{}", e);
        throw e;
      }
      sourceUser.setCreatedTime(originalCreatedTime);

      LOGGER.info("Updating user: {}", sourceUser);
      return this.cassandraService.saveUser(sourceUser);
    } else {
      try {
        sourceUser = dataModelConversionUtils.createSourceUser(user, userMedias);
      } catch (DataModelConverterException e) {
        LOGGER.error("{}", e);
        throw e;
      }

      LOGGER.info("Saving user: {}", sourceUser);
      return this.cassandraService.saveUser(sourceUser);
    }
  }

}
