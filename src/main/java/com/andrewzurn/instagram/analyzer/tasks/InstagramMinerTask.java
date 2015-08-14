package com.andrewzurn.instagram.analyzer.tasks;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.service.InstagramService;
import com.andrewzurn.instagram.analyzer.service.CassandraService;
import com.sola.instagram.exception.InstagramException;
import com.sola.instagram.model.Media;
import com.sola.instagram.model.User;
import com.sola.instagram.util.PaginatedCollection;
import me.champeau.ld.UberLanguageDetector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by andrew on 8/11/15.
 */
@Component
public class InstagramMinerTask {

  @Inject
  private InstagramService instagramService;

  public static final int ONE_HALF_MINUTE = 90000;

  @Scheduled(fixedRate = ONE_HALF_MINUTE)
  public void test() throws InstagramException, IOException {
    if (!this.instagramService.isReady()) {
      this.instagramService.startSignon();
    } else {
      handleInstagramMining();
    }
  }

  private void handleInstagramMining() {
    List<Media> popularMedia = null;
    try {
      popularMedia = this.instagramService.getPopularMedia();
    } catch (Exception e) {
      System.out.println("ERROR while retreiving popular media: " + e);
      e.printStackTrace();
    }

    for(Media media : popularMedia) {
      User user = null;
      PaginatedCollection<Media> userRecentMedia = null;
      try {
        // get the raw instagram models
        user = this.instagramService.getUserById(media.getUser().getId());
        userRecentMedia = this.instagramService.getUserRecentMedia(user.getId());
      } catch (Exception e){
        System.out.println("ERROR while retrieving the user or user recent media.\n" + e);
        e.printStackTrace();
      }

      // if use language within the user's info or media is not english, disqualify the user
      if ( !determineLanguage(user, userRecentMedia.get(0))){
        System.out.println("User: " + user.getId() + " username: "
            + user.getUserName() + " disqualified from analysis.");
        continue;
      }

      // convert them to cassandra models
      List<RawUserMedia> userMedias = null;
      SourceUser sourceUser = null;
      try {
        userMedias = this.instagramService.convertRawUserMedia(userRecentMedia);
        sourceUser = this.instagramService.convertToSourceUser(user, userMedias, Optional.empty());
      } catch (Exception e) {
        System.out.println("ERROR while converting the source user or raw user object.\n" + e);
        e.printStackTrace();
      }

      // and save to the cassandra cluster
      System.out.print("Saving: " + sourceUser);
      this.instagramService.saveSourceUser(sourceUser);
    }
  }

  /*
   * Try to examine the medias caption, as it might contain the longest readable text, to
   * determine if it is english.  If an error occurs (NPE while getting the Caption text,
   * try to look at the user's bio to determine if we are dealing with an english speaking
   * user.
   */
  private boolean determineLanguage(User user, Media userRecentMedia) {
    UberLanguageDetector detector = UberLanguageDetector.getInstance();
    try {
      if ( !"en".equals(detector.detectLang(userRecentMedia.getCaption().getText()))) {
        return false;
      }
    } catch (Exception e) {
      try {
        if ( !"en".equals(detector.detectLang(user.getBio()))) {
          return false;
        }
      } catch (Exception e2) {
        return false;
      }
    }
    return true;
  }

}
