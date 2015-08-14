package com.andrewzurn.instagram.analyzer.service;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.utils.AnalyticsUtils;
import com.sola.instagram.InstagramSession;
import com.sola.instagram.auth.AccessToken;
import com.sola.instagram.auth.InstagramAuthentication;
import com.sola.instagram.exception.InstagramException;
import com.sola.instagram.model.Comment;
import com.sola.instagram.model.Media;
import com.sola.instagram.model.User;
import com.sola.instagram.util.PaginatedCollection;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by andrew on 8/11/15.
 */
@Service
public class InstagramService {

  @Inject
  private CassandraService cassandraService;

  @Inject
  private AnalyticsUtils analyticsUtils;

  private static final String CLIENT_ID = "f5ac08e3237643f28361ffe36c1f6675";
  private static final String CLIENT_SECRET = "64ff460f3f024b0e914be02eeddfddca";
  private static final String REDIRECT_URL = "http://localhost:8080/instagram/callback";
  private InstagramAuthentication authenticator = null;
  private InstagramSession instagramSession = null;

  public void startSignon() throws InstagramException, IOException {
    this.authenticator = new InstagramAuthentication();
    String authUrl = authenticator.setRedirectUri(REDIRECT_URL)
        .setClientSecret(CLIENT_SECRET)
        .setClientId(CLIENT_ID)
        .getAuthorizationUri();
    System.out.println("Please open on the following link to finish signon.\n" + authUrl);
  }

  public void finishSignon(String urlCode) throws Exception {
    AccessToken token = authenticator.build(urlCode); // throws Exception
    this.instagramSession = new InstagramSession(token);
    System.out.println("INFO: created the session with authToken: " + token.getTokenString());
  }

  public List<Media> getPopularMedia() throws Exception {
    return this.instagramSession.getPopularMedia();
  }

  public User getUserById(int userId) throws Exception {
    try {
      return this.instagramSession.getUserById(userId);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("ERROR in getting the user by id.\n" + e);
    }
  }

  public PaginatedCollection<Media> getUserRecentMedia(int userId) throws Exception {
    return this.instagramSession.getRecentPublishedMedia(userId);
  }

  public boolean isReady() {
    return this.instagramSession != null ? true: false;
  }

  /**
   * Will convert an {@code com.sola.instagram.model.User} model to our cassandra model,
   * {@code com.andrewzurn.instagram.analyzer.model.SourceUser}, by extrapulating and calculating
   * information based on the user's list of {@code com.andrewzurn.instagram.analyzer.model.RawUserMedia}.
   * An optional updateDate can be passed in, for the case that we already have the User in our cassandra
   * cluster, and need to merely update it's values.
   *
   * @param user the user to convert
   * @param userRecentMedia the user's converted media
   * @param updateDate optional date of the user if it is already stored, else it will be a new Date().
   * @return the converted {@code com.andrewzurn.instagram.analyzer.model.SourceUser}
   * @throws Exception if an error occures while accessing the {@code come.sola.isntagram.model.User} object.
   */
  public SourceUser convertToSourceUser(User user, List<RawUserMedia> userRecentMedia,
                                        Optional<Date> updateDate) throws Exception {
    // gather engagement ratings for user
    float averagedEngagementRating = 0;
    float mostRecentMediaEngagementRating = 0;
    try {
      averagedEngagementRating = analyticsUtils.averagedEngagementRating(userRecentMedia, user.getFollowerCount());
      mostRecentMediaEngagementRating = analyticsUtils
          .averagedEngagementRating(userRecentMedia.subList(0, 1), user.getFollowerCount());
    } catch (Exception e) {
      throw new Exception("ERROR while creating the engagement ratings.\n" + e);
    }

    // gather the impression and trending info for user
    float trendingRating = analyticsUtils.getTrendingRating(userRecentMedia);
    boolean trending = trendingRating > 0.0;

    // gather location information and recent media ids
    List<String> locations = this.getLocations(userRecentMedia);
    List<String> recentMediaIds = this.getRecentMediaIds(userRecentMedia);

    Date createdTime = new Date();
    Date updatedTime = updateDate.orElse(new Date());

    return SourceUser.build(user, averagedEngagementRating, mostRecentMediaEngagementRating, trendingRating,
        trending, locations, recentMediaIds, createdTime, updatedTime);
  }

  private List<String> getRecentMediaIds(List<RawUserMedia> userRecentMedia) {
    return userRecentMedia.stream().map(RawUserMedia::getId).collect(Collectors.toList());
  }

  private List<String> getLocations(List<RawUserMedia> userRecentMedia) {
    return userRecentMedia.stream().map(RawUserMedia::getLocation).collect(Collectors.toList());
  }

  public SourceUser saveSourceUser(SourceUser user) {
    return this.cassandraService.insertUser(user);
  }

  public List<RawUserMedia> convertRawUserMedia(PaginatedCollection<Media> userMedia) throws Exception {
    List<RawUserMedia> rawUserMedias = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      rawUserMedias.add(RawUserMedia.build(userMedia.get(i)));
    }
    return rawUserMedias;
  }

  public RawUserMedia saveRawUserMedia(RawUserMedia userMedia) {
    return this.cassandraService.insertRawUserMedia(userMedia);
  }

}
