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

  public static final int FIFTY_THOUSAND = 50000;
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
  private static final int ONE_AND_ONE_QUARTER_MINUTE = 75000;

  //@Scheduled(fixedRate = ONE_HALF_MINUTE)
  public void minePopularMedia() throws DataModelConverterException, InstagramApiException {
    if ( this.instagramService.isReady()) {
      handlePopularMining();
    }
  }

  @Scheduled(fixedRate = ONE_AND_ONE_QUARTER_MINUTE)
  public void mineUserFollows() throws DataModelConverterException, InstagramApiException {
    if ( this.instagramService.isReady()) {
      handleFollowsForUsers();
    }
  }

  private void handlePopularMining() throws DataModelConverterException, InstagramApiException {
    List<Media> popularMedia = null;
    try {
      popularMedia = this.instagramService.getInstagramPopularMedia();
    } catch (InstagramApiException e) {
      LOGGER.error("Issue while retrieving the instagram popular media: {}", e.getLocalizedMessage(), e);
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
        LOGGER.error("{}", e.getLocalizedMessage(), e);
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
        LOGGER.error("{}", e.getLocalizedMessage(), e);
        throw e;
      }
    }
  }

  private void handleFollowsForUsers() throws DataModelConverterException, InstagramApiException {
    SourceUser sourceUser = this.cassandraService.findSingleUntraversedFollowsUser();
    PaginatedCollection<User> followsUser = null;
    try {
      followsUser = this.instagramService.getFollowsForUser(sourceUser.getUserId());
    } catch (InstagramApiException e) {
      LOGGER.error("{}", e.getLocalizedMessage(), e);
      throw e;
    }

    // only traverse the first page of users
    for(int i = 0; i < followsUser.size(); i++) {
      User followUser;
      PaginatedCollection<Media> userRecentMedia;
      try {
        followUser = this.instagramService.getInstagramUserById(followsUser.get(i).getId());
        userRecentMedia = this.instagramService.getInstagramUserMedias(followUser.getId());
      } catch (InstagramApiException e) {
        LOGGER.error("{}", e.getLocalizedMessage(), e);
        continue;
      }

      if (disqualifyUser(followUser, userRecentMedia)) {
        continue;
      }

      // convert them to cassandra models and save/update
      List<RawUserMedia> userMedias = null;
      try {
        userMedias = dataModelConversionUtils.createRawUserMedia(userRecentMedia);
        handleUserPersistance(followUser, userMedias);
      } catch (DataModelConverterException e) {
        LOGGER.error("{}", e.getLocalizedMessage(), e);
        continue;
      }
    }
    updateSourceUserForFollowsTraversed(sourceUser);
  }

  private boolean disqualifyUser(User user, PaginatedCollection<Media> userRecentMedia) {
    // if user/media is not detected as english, or we already have this user
    try {
      if (!analyticsUtils.determineLanguage(user, userRecentMedia.get(0))) {
        LOGGER.info("User: {} with id: {} disqualified for language detection.",
            user.getUserName(), user.getId());
        return true;
      } else if (user.getFollowerCount() < FIFTY_THOUSAND) {
        LOGGER.info("User: {} with id: {} disqualified for low follower count.",
            user.getUserName(), user.getId());
        return true;
      }
    } catch (Exception e) { // could also be an array index out of bounds exception from userRecentMedia.get(0)
      return true;
    }
    return false;
  }

  private void updateSourceUserForFollowsTraversed(SourceUser sourceUser) {
    // mark that we've traversed this user and gathered users they follow
    sourceUser.setHasBeenFollowsTraversed(true);
    sourceUser.setLastFollowsTraversalTime(new Date());
    this.cassandraService.saveUser(sourceUser);
    LOGGER.info("Updated user: {} with id {} marking they have been traversed for followers",
        sourceUser.getUserId(), sourceUser.getUserId());
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
        LOGGER.error("{}", e.getLocalizedMessage(), e);
        throw e;
      }
      sourceUser.setCreatedTime(originalCreatedTime);

      LOGGER.info("Updating user: {}", sourceUser);
      return this.cassandraService.saveUser(sourceUser);
    } else {
      try {
        sourceUser = dataModelConversionUtils.createSourceUser(user, userMedias);
      } catch (DataModelConverterException e) {
        LOGGER.error("{}", e.getLocalizedMessage(), e);
        throw e;
      }

      LOGGER.info("Saving user: {}", sourceUser);
      return this.cassandraService.saveUser(sourceUser);
    }
  }

}
