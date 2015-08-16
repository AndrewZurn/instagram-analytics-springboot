package com.andrewzurn.instagram.analyzer.service;

import com.andrewzurn.instagram.analyzer.exception.InstagramApiException;
import com.sola.instagram.InstagramSession;
import com.sola.instagram.auth.AccessToken;
import com.sola.instagram.auth.InstagramAuthentication;
import com.sola.instagram.exception.InstagramException;
import com.sola.instagram.model.Media;
import com.sola.instagram.model.User;
import com.sola.instagram.util.PaginatedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Created by andrew on 8/11/15.
 */
@Service
@Configuration
public class InstagramService {

  @Inject
  private CassandraService cassandraService;

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  private static final String CLIENT_ID = "f5ac08e3237643f28361ffe36c1f6675";
  private static final String CLIENT_SECRET = "64ff460f3f024b0e914be02eeddfddca";
  @Value("${instagram.callback.url}")
  private String REDIRECT_URL;
  private InstagramAuthentication authenticator = null;
  private InstagramSession instagramSession = null;

  public String startSignon() throws InstagramException, IOException {
    this.authenticator = new InstagramAuthentication();
    String authUrl = authenticator.setRedirectUri(REDIRECT_URL)
        .setClientSecret(CLIENT_SECRET)
        .setClientId(CLIENT_ID)
        .getAuthorizationUri();
    return authUrl;
  }

  public void finishSignon(String urlCode) throws Exception {
    AccessToken token = authenticator.build(urlCode); // throws Exception
    this.instagramSession = new InstagramSession(token);
    LOGGER.info("Created the session with authToken: {}", token.getTokenString());
  }

  public boolean isReady() {
    return this.instagramSession != null ? true: false;
  }

  /**
   * Call the instagram session api to get the currently trending/popular
   * media from instagram
   * @return a list of the current popular media
   * @throws InstagramApiException error while communicating with the instagram session api.
   */
  public List<Media> getInstagramPopularMedia() throws InstagramApiException {
    try {
      return this.instagramSession.getPopularMedia();
    } catch (Exception e) {
      throw new InstagramApiException("ERROR in getting the recent popular media from instagram.\n" + e);
    }
  }

  /**
   * Get the information of a user by querying the instagram session api.
   * @param userId the id of the user
   * @return the additional profile info of the user
   * @throws InstagramApiException error while communicating with the instagram session api.
   */
  public User getInstagramUserById(int userId) throws InstagramApiException {
    try {
      return this.instagramSession.getUserById(userId);
    } catch (Exception e) {
      throw new InstagramApiException("ERROR in getting the user by id.\n" + e);
    }
  }

  public User getInstagramUser(Media media) throws InstagramApiException {
    User user = null;
    try {
      // get the raw instagram models
      user = getInstagramUserById(media.getUser().getId());
    } catch (Exception e) {
      throw new InstagramApiException("ERROR while retrieving the user with id: "
          + media.getUser().getId() + ".\n" + e);
    }
    return user;
  }

  public PaginatedCollection<Media> getInstagramUserMedias(User user) throws InstagramApiException {
    PaginatedCollection<Media> userRecentMedia = null;
    try {
      // get the raw instagram models
      userRecentMedia = this.instagramSession.getRecentPublishedMedia(user.getId());
    } catch (Exception e){
      throw new InstagramApiException("ERROR while retrieving the user recent media with user_id: "
          + user.getId() + ".\n" + e);
    }
    return userRecentMedia;
  }
}
