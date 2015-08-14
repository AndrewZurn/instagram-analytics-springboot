package com.andrewzurn.instagram.analyzer.model;

import com.sola.instagram.model.Comment;
import com.sola.instagram.model.Media;
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
 * Created by andrew on 8/12/15.
 */
@Table(value = "raw_user_recent_media_entity")
public class RawUserMedia {

  @PrimaryKey("id")
  private String id;

  @Indexed
  @Column("user_id")
  private int userId;

  @Column("username")
  private String userName;

  @Column("full_name")
  private String fullName;

  @Column("caption_text")
  private String caption;

  @Indexed
  @Column("likes_count")
  private int likesCount;

  @Indexed
  @Column("comment_count")
  private int commentCount;

  @Indexed
  @Column("tags")
  private List<String> tags;

  @Column("url")
  private String url;

  @Indexed
  @Column("date_time")
  private Date createdTime;

  @Indexed
  @Column("location")
  private String location;

  public RawUserMedia(String id, int userId, String userName, String fullName, String caption,
                      int likesCount, int commentCount, List<String> tags,
                      String url, Date createdTime, String location) {
    this.id = id;
    this.userId = userId;
    this.userName = userName;
    this.fullName = fullName;
    this.caption = caption;
    this.likesCount = likesCount;
    this.commentCount = commentCount;
    this.tags = tags;
    this.url = url;
    this.createdTime = createdTime;
    this.location = location;
  }

  public String getId() {
    return id;
  }

  public int getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getCaption() {
    return caption;
  }

  public int getLikesCount() {
    return likesCount;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public List<String> getTags() {
    return tags;
  }

  public String getUrl() {
    return url;
  }

  public Date getCreatedTime() {
    return createdTime;
  }

  public String getLocation() {
    return location;
  }

  @Override
  public String toString() {
    return "RawUserMedia{" +
        "id='" + id + '\'' +
        ", userId=" + userId +
        ", userName='" + userName + '\'' +
        ", fullName='" + fullName + '\'' +
        ", caption='" + caption + '\'' +
        ", likesCount=" + likesCount +
        ", commentCount=" + commentCount +
        ", tags=" + tags +
        ", url='" + url + '\'' +
        ", createdTime=" + createdTime +
        ", location='" + location + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RawUserMedia)) return false;

    RawUserMedia that = (RawUserMedia) o;

    if (userId != that.userId) return false;
    if (!id.equals(that.id)) return false;
    if (!userName.equals(that.userName)) return false;
    if (caption != null ? !caption.equals(that.caption) : that.caption != null) return false;
    if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
    if (!url.equals(that.url)) return false;
    if (!createdTime.equals(that.createdTime)) return false;
    return !(location != null ? !location.equals(that.location) : that.location != null);
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + userId;
    result = 31 * result + userName.hashCode();
    result = 31 * result + (caption != null ? caption.hashCode() : 0);
    result = 31 * result + (tags != null ? tags.hashCode() : 0);
    result = 31 * result + url.hashCode();
    result = 31 * result + createdTime.hashCode();
    result = 31 * result + (location != null ? location.hashCode() : 0);
    return result;
  }

  public static RawUserMedia build(Media userMedia) throws Exception {
    String location = "";
    try {
      location = userMedia.getLocation().getName().replaceAll("[\t\r\n]", "");
    } catch (NullPointerException ne) { }

    String caption = "";
    try {
      caption = userMedia.getCaption().getText().replaceAll("[\t\r\n]", "");
    } catch (NullPointerException ne) { }

    String fullName = "";
    try {
      fullName = userMedia.getUser().getFullName().replaceAll("[\t\r\n]", "");
    } catch (NullPointerException ne) { }

    List<String> tags = new ArrayList<>();
    try {
      tags = userMedia.getTags();
    } catch (NullPointerException ne) { }

    try {
      return new RawUserMedia(userMedia.getId(), userMedia.getUser().getId(), userMedia.getUser().getUserName(),
          fullName, caption, userMedia.getLikeCount(), userMedia.getCommentCount(), tags,
          userMedia.getLink(), new Date(Long.parseLong(userMedia.getCreatedTimestamp())), location);
    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("ERROR during building of the RawUserMedia entity.\n" + e);
    }
  }
}