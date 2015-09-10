package com.andrewzurn.instagram.analyzer.model;

import com.sola.instagram.model.User;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.Indexed;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by andrew on 8/10/15.
 */
@Table(value = "source_user_entity")
public class SourceUser {

  @PrimaryKey("user_id")
  private Integer userId;

  @Indexed
  @Column("username")
  private String userName;

  @Column("full_name")
  private String fullName;

  @Column("bio")
  private String bio;

  @Indexed
  @Column("locations")
  private List<String> locations;

  @Column("website")
  private String website;

  @Indexed
  @Column("media_count")
  private int mediaCount;

  @Indexed
  @Column("follows")
  private int follows;

  @Indexed
  @Column("followers")
  private int followers;

  @Column("recent_media_ids")
  private List<String> recentMediaIds;

  @Indexed
  @Column("most_recent_engagement_rating")
  private double mostRecentEngagementRating;

  @Indexed
  @Column("averaged_engagement_rating")
  private double averagedEngagementRating;

  @Indexed
  @Column("trending")
  private boolean trending;

  @Column("trending_value")
  private double trendingValue;

  @Column("created_time")
  private Date createdTime;

  @Column("updated_time")
  private Date updatedTime;

  @Indexed
  @Column("has_been_follows_traversed")
  private boolean hasBeenFollowsTraversed;

  @Column("last_follows_traversal_time")
  private Date lastFollowsTraversalTime;

  public SourceUser(int userId, String userName, String fullName, String bio, List<String> locations,
                    String website, int mediaCount, int follows, int followers,
                    List<String> recentMediaIds, double mostRecentEngagementRating, double averagedEngagementRating,
                    boolean trending, double trendingValue, Date createdTime, Date updatedTime,
                    boolean hasBeenFollowsTraversed, Date lastFollowsTraversalTime) {
    this.userId = userId;
    this.userName = userName;
    this.fullName = fullName;
    this.bio = bio;
    this.locations = (locations == null) ? new ArrayList<String>() : locations;
    this.website = website;
    this.mediaCount = mediaCount;
    this.follows = follows;
    this.followers = followers;
    this.recentMediaIds = recentMediaIds;
    this.mostRecentEngagementRating = mostRecentEngagementRating;
    this.averagedEngagementRating = averagedEngagementRating;
    this.trending = trending;
    this.trendingValue = trendingValue;
    this.createdTime = createdTime;
    this.updatedTime = updatedTime;
    this.hasBeenFollowsTraversed = hasBeenFollowsTraversed;
    this.lastFollowsTraversalTime = lastFollowsTraversalTime;
  }

  public SourceUser() { }

  public int getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getBio() {
    return bio;
  }

  public List<String> getLocations() {
    return locations;
  }

  public String getWebsite() {
    return website;
  }

  public int getMediaCount() {
    return mediaCount;
  }

  public int getFollows() {
    return follows;
  }

  public int getFollowers() {
    return followers;
  }

  public List<String> getRecentMediaIds() {
    return recentMediaIds;
  }

  public double getMostRecentEngagementRating() {
    return mostRecentEngagementRating;
  }

  public double getAveragedEngagementRating() {
    return averagedEngagementRating;
  }

  public boolean isTrending() {
    return trending;
  }

  public double getTrendingValue() {
    return trendingValue;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public Date getUpdatedTime() {
    return updatedTime;
  }

  public boolean isHasBeenFollowsTraversed() {
    return hasBeenFollowsTraversed;
  }

  public Date getLastFollowsTraversalTime() {
    return lastFollowsTraversalTime;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public void setLocations(List<String> locations) {
    this.locations = locations;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public void setMediaCount(int mediaCount) {
    this.mediaCount = mediaCount;
  }

  public void setFollows(int follows) {
    this.follows = follows;
  }

  public void setFollowers(int followers) {
    this.followers = followers;
  }

  public void setRecentMediaIds(List<String> recentMediaIds) {
    this.recentMediaIds = recentMediaIds;
  }

  public void setMostRecentEngagementRating(double mostRecentEngagementRating) {
    this.mostRecentEngagementRating = mostRecentEngagementRating;
  }

  public void setAveragedEngagementRating(double averagedEngagementRating) {
    this.averagedEngagementRating = averagedEngagementRating;
  }

  public void setTrending(boolean trending) {
    this.trending = trending;
  }

  public void setTrendingValue(double trendingValue) {
    this.trendingValue = trendingValue;
  }

  public void setCreatedTime(Date createdTime) {
    this.createdTime = createdTime;
  }

  public void setUpdatedTime(Date updatedTime) {
    this.updatedTime = updatedTime;
  }

  public void setHasBeenFollowsTraversed(boolean hasBeenFollowsTraversed) {
    this.hasBeenFollowsTraversed = hasBeenFollowsTraversed;
  }

  public void setLastFollowsTraversalTime(Date lastFollowsTraversalTime) {
    this.lastFollowsTraversalTime = lastFollowsTraversalTime;
  }

  @Override
  public String toString() {
    return "SourceUser{" +
        "userId=" + userId +
        ", userName='" + userName + '\'' +
        ", fullName='" + fullName + '\'' +
        ", bio='" + bio + '\'' +
        ", locations=" + locations +
        ", website='" + website + '\'' +
        ", mediaCount=" + mediaCount +
        ", follows=" + follows +
        ", followers=" + followers +
        ", recentMediaIds=" + recentMediaIds +
        ", mostRecentEngagementRating=" + mostRecentEngagementRating +
        ", averagedEngagementRating=" + averagedEngagementRating +
        ", trending=" + trending +
        ", trendingValue=" + trendingValue +
        ", createdTime=" + createdTime +
        ", updatedTime=" + updatedTime +
        ", hasBeenFollowsTraversed=" + hasBeenFollowsTraversed +
        ", lastFollowsTraversalTime=" + lastFollowsTraversalTime +
        '}';
  }

  public static String tsvHeader() {
    return "USER_ID" + "\t" +
        "USER_NAME" + "\t" +
        "FULL_NAME" + "\t" +
        "BIO" + "\t" +
        "LOCATIONS" + "\t" +
        "WEBSITE" + "\t" +
        "MEDIA_COUNT" + "\t" +
        "FOLLOWS"  + "\t" +
        "FOLLOWERS" + "\t" +
        "RECENT_MEDIA_IDS" + "\t" +
        "MOST_RECENT_ENGAGEMENT_RATING" + "\t" +
        "AVG_ENGAGEMENT_RATING" + "\t" +
        "TRENDING" + "\t" +
        "TRENDING_VALUE" + "\t" +
        "CREATED_TIME" + "\t" +
        "UPDATED_TIME" + "\t" +
        "HAS_BEEN_FOLLOWS_TRAVERSED" + "\t" +
        "LAST_FOLLOWS_TRAVERSED_TIME" + "\n";
  }

  public String tsvRepr() {
    List<String> parsedLocations = new ArrayList();
    try {
      for (String loc : locations) {
        if (!loc.equals("")) {
          parsedLocations.add(loc.replaceAll("[\\t\\r\\n]", ""));
        }
      }
    } catch (NullPointerException ne) { }

    String username = userName.replaceAll("[\\t\\r\\n]", "");
    String fullname = fullName.replaceAll("[\\t\\r\\n]", "");
    String biog = bio.replaceAll("[\\t\\r\\n]", "");
    String websiteUrl = website.replaceAll("[\\t\\r\\n]", "");

    return userId + "\t" +
        username + "\t" +
        fullname + "\t" +
        biog + "\t" +
        parsedLocations + "\t" +
        websiteUrl + "\t" +
        mediaCount + "\t" +
        follows  + "\t" +
        followers + "\t" +
        recentMediaIds + "\t" +
        mostRecentEngagementRating + "\t" +
        averagedEngagementRating + "\t" +
        trending + "\t" +
        trendingValue + "\t" +
        createdTime + "\t" +
        updatedTime + "\t" +
        hasBeenFollowsTraversed + "\t" +
        lastFollowsTraversalTime + "\n";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SourceUser)) {
      return false;
    }

    SourceUser that = (SourceUser) o;
    if (userId != that.userId) {
      return false;
    }
    return userName.equals(that.userName);
  }

  @Override
  public int hashCode() {
    int result = userId;
    result = 31 * result + userName.hashCode();
    return result;
  }

  /**
   * Given a {@code com.sola.instagram.model.User} object instance, and and other calculated or derived
   * parameters, build a SourceUser model based on our cassandra source_user_entity table.
   *
   * @param user
   * @param averagedEngagementRating
   * @param mostRecentEngagementRating
   * @param trendingValue
   * @param trending
   * @param locations
   * @param recentMediaIds
   * @param createdTime
   * @param updatedTime
   * @return SourceUser model that can operate with the cassandra database.
   * @throws Exception if an error occures while accessing the {@code come.sola.isntagram.model.User} object.
   */
  public static SourceUser build(User user, float averagedEngagementRating, float mostRecentEngagementRating,
                                 float trendingValue, boolean trending, List<String> locations,
                                 List<String> recentMediaIds, Date createdTime, Date updatedTime,
                                 boolean hasBeenFollowsTraversed, Date lastFollowsTraversalTime) throws Exception {
    String fullName = "";
    try {
      fullName = user.getFullName().replaceAll("[\\t\\r\\n]", "");
    } catch (NullPointerException ne) { }

    String bio = "";
    try {
      bio = user.getBio().replaceAll("[\\t\\r\\n]", "");
    } catch (NullPointerException ne) { }

    String website = "";
    try {
      website = user.getWebsite().replaceAll("[\\t\\r\\n]", "");
    } catch (NullPointerException ne) { }

    try {
      return new SourceUser(user.getId(), user.getUserName(), fullName, bio, locations, website, user.getMediaCount(),
          user.getFollowingCount(), user.getFollowerCount(), recentMediaIds, mostRecentEngagementRating,
          averagedEngagementRating, trending, trendingValue, createdTime, updatedTime,
          hasBeenFollowsTraversed, lastFollowsTraversalTime);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("ERROR in building the SourceUser object.\n" + e);
    }
  }

}
