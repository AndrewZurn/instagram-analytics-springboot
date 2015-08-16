package com.andrewzurn.instagram.analyzer.tasks;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.service.CassandraService;
import com.andrewzurn.instagram.analyzer.service.InstagramService;
import com.andrewzurn.instagram.analyzer.utils.AnalyticsUtils;
import com.andrewzurn.instagram.analyzer.utils.DataModelConversionUtils;
import com.sola.instagram.exception.InstagramException;
import com.sola.instagram.model.Media;
import com.sola.instagram.model.User;
import com.sola.instagram.util.PaginatedCollection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by andrew on 8/11/15.
 */
@Component
public class InstagramMinerTask {

  @Inject
  private InstagramService instagramService;
  @Inject
  private CassandraService cassandraService;
  @Inject
  private AnalyticsUtils analyticsUtils;
  @Inject
  private DataModelConversionUtils dataModelConversionUtils;

  public static final int ONE_HALF_MINUTE = 90000;

  @Scheduled(fixedRate = ONE_HALF_MINUTE)
  public void test() throws InstagramException, IOException {
    if ( this.instagramService.isReady()) {
      handleInstagramMining();
    }
  }

  private void handleInstagramMining() {
    List<Media> popularMedia = null;
    try {
      popularMedia = this.instagramService.getInstagramPopularMedia();
    } catch (Exception e) {
      System.out.println("ERROR while retreiving popular media: " + e);
      e.printStackTrace();
    }

    for(Media media : popularMedia) {
      // calls to instagram api to get our user and media models
      User user = instagramService.getInstagramUser(media);
      PaginatedCollection<Media> userRecentMedia = this.instagramService.getInstagramUserMedias(user);

      // if user/media is not detected as english, or we already have this user
      if ( !analyticsUtils.determineLanguage(user, userRecentMedia.get(0))) {
        System.out.println("User: " + user.getId() + " username: "
            + user.getUserName() + " disqualified from analysis.");
        continue;
      }

      // convert them to cassandra models and save/update
      List<RawUserMedia> userMedias = dataModelConversionUtils.createRawUserMedia(userRecentMedia);
      handleUserPersistance(user, userMedias);
    }
  }

  private SourceUser handleUserPersistance(User user, List<RawUserMedia> userMedias) {
    // user to see if user exists, and if so, udpate
    SourceUser sourceUser = this.cassandraService.findByUserId(user.getId());
    if ( sourceUser != null) {
      // save the old created time
      Date originalCreatedTime = sourceUser.getCreatedTime();
      // recreate the user to get updated values
      sourceUser = dataModelConversionUtils.createSourceUser(user, userMedias);
      sourceUser.setCreatedTime(originalCreatedTime);
      System.out.print("Saving: " + sourceUser);
      return this.cassandraService.saveUser(sourceUser);
    } else {
      sourceUser = dataModelConversionUtils.createSourceUser(user, userMedias);
      System.out.print("Updating: " + sourceUser);
      return this.cassandraService.saveUser(sourceUser);
    }
  }

}
