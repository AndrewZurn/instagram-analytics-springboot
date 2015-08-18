package com.andrewzurn.instagram.analyzer.utils;

import com.andrewzurn.instagram.analyzer.exception.DataModelConverterException;
import com.andrewzurn.instagram.analyzer.exception.InstagramApiException;
import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.model.SourceUser;
import com.andrewzurn.instagram.analyzer.service.InstagramService;
import com.sola.instagram.model.Media;
import com.sola.instagram.model.User;
import com.sola.instagram.util.PaginatedCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by andrew on 8/14/15.
 */
@Component
public class DataModelConversionUtils {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Inject
  private InstagramService instagramService;
  @Inject
  private AnalyticsUtils analyticsUtils;

  public List<RawUserMedia> createRawUserMedia(PaginatedCollection<Media> userRecentMedia)
      throws DataModelConverterException {
    List<RawUserMedia> userMedias = null;
    try {
      userMedias = this.convertRawUserMedia(userRecentMedia);
    } catch (Exception e) {
      throw new DataModelConverterException("could not convert raw user media: ", e);
    }
    return userMedias;
  }

  public SourceUser createSourceUser(User user, List<RawUserMedia> userMedias) throws DataModelConverterException {
    SourceUser sourceUser = null;
    try {
      sourceUser = this.convertToSourceUser(user, userMedias);
    } catch (Exception e) {
      throw new DataModelConverterException(
          String.format("could not convert the user media to %s.", user.getClass().getCanonicalName().toString()));
    }
    return sourceUser;
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
   * @return the converted {@code com.andrewzurn.instagram.analyzer.model.SourceUser}
   * @throws Exception if an error occures while accessing the {@code come.sola.isntagram.model.User} object.
   */
  private SourceUser convertToSourceUser(User user, List<RawUserMedia> userRecentMedia) throws Exception {
    // gather engagement ratings for user
    float averagedEngagementRating = 0;
    float mostRecentMediaEngagementRating = 0;
    try {
      averagedEngagementRating = analyticsUtils.averagedEngagementRating(userRecentMedia, user.getFollowerCount());
      mostRecentMediaEngagementRating = analyticsUtils
          .averagedEngagementRating(userRecentMedia.subList(0, 1), user.getFollowerCount());
    } catch (Exception e) {
      // this was more than likely caused by calling the getFollowerCount() method
      throw new InstagramApiException("ERROR while creating the engagement ratings.\n" + e);
    }

    // gather the impression and trending info for user
    float trendingRating = analyticsUtils.getTrendingRating(userRecentMedia);
    boolean trending = trendingRating > 0.0;

    // gather location information and recent media ids
    List<String> locations = this.mapLocations(userRecentMedia);
    List<String> recentMediaIds = this.mapRecentMediaIds(userRecentMedia);

    Date createdTime = new Date();
    Date updatedTime = new Date();

    return SourceUser.build(user, averagedEngagementRating, mostRecentMediaEngagementRating, trendingRating,
        trending, locations, recentMediaIds, createdTime, updatedTime, false, null);
  }

  private List<RawUserMedia> convertRawUserMedia(PaginatedCollection<Media> userMedia) throws Exception {
    List<RawUserMedia> rawUserMedias = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      rawUserMedias.add(RawUserMedia.build(userMedia.get(i)));
    }
    return rawUserMedias;
  }

  private List<String> mapRecentMediaIds(List<RawUserMedia> userRecentMedia) {
    return userRecentMedia.stream()
        .map(RawUserMedia::getId).collect(Collectors.toList());
  }

  private List<String> mapLocations(List<RawUserMedia> userRecentMedia) {
    return userRecentMedia.stream()
        .filter(m -> !m.getLocation().equals(""))
        .map(RawUserMedia::getLocation)
        .collect(Collectors.toList());
  }
}
