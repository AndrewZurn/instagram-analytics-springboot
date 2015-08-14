package com.andrewzurn.instagram.analyzer.utils;

import com.andrewzurn.instagram.analyzer.model.RawUserMedia;
import com.andrewzurn.instagram.analyzer.service.InstagramService;
import com.sola.instagram.model.Media;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by andrew on 8/12/15.
 */
@Component
public class AnalyticsUtils {

  /**
   * Find the averaged engagement rating a user made for a list of userMedias and total followers.
   * The formula is the sum of the likes and comments for a list of media divided by the number of media,
   * divided by the total number of follower for the user
   * @param userMedias the list of recent user media
   * @param totalFollowers
   * @return the averaged engagement rating for a users recent media.
   */
  public float averagedEngagementRating(List<RawUserMedia> userMedias, int totalFollowers) {
    return averageImpressions(userMedias) / (float) totalFollowers;
  }

  /**
   * Find the average amount of impressions the users media made.
   * @param userMedias the list of recent user medias.
   * @return the amount of impressions divided by the amount of user medias.
   */
  public float averageImpressions(List<RawUserMedia> userMedias) {
    final int[] sum = {0};
    userMedias.stream().forEach(m -> sum[0] += m.getCommentCount() + m.getLikesCount());

    return sum[0] / (float) userMedias.size();
  }

  /**
   * Finds the trending rating by finding the average impression of the first half of the user's
   * recent media (most recent) and the average impression of the second half of the user's media (previous)
   * and then calculates the percentage change between the two (new - old) / new.
   * @param userRecentMedia the user's recent media
   * @return percentage change (as a float) between the user's recent impressions and their previous impressions.
   */
  public float getTrendingRating(List<RawUserMedia> userRecentMedia) {
    float mostRecentAveragedImpression =
        averageImpressions(userRecentMedia.subList(0, userRecentMedia.size() / 2));
    float previousAveragedImpression =
        averageImpressions(userRecentMedia.subList(userRecentMedia.size() / 2, userRecentMedia.size()));
    return (mostRecentAveragedImpression - previousAveragedImpression) / mostRecentAveragedImpression;
  }
}
