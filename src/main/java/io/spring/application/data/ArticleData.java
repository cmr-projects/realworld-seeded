package io.spring.application.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.application.DateTimeCursor;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@NoArgsConstructor
public class ArticleData implements io.spring.application.Node {
  private String id;
  private String slug;
  private String title;
  private String description;
  private String body;
  private boolean favorited;
  private int favoritesCount;
  private DateTime createdAt;
  private DateTime updatedAt;
  private List<String> tagList;

  @JsonProperty("author")
  private ProfileData profileData;

  private boolean published;

  public ArticleData(
      String id,
      String slug,
      String title,
      String description,
      String body,
      boolean favorited,
      int favoritesCount,
      DateTime createdAt,
      DateTime updatedAt,
      List<String> tagList,
      ProfileData profileData) {
    this(
        id,
        slug,
        title,
        description,
        body,
        favorited,
        favoritesCount,
        createdAt,
        updatedAt,
        tagList,
        profileData,
        true);
  }

  public ArticleData(
      String id,
      String slug,
      String title,
      String description,
      String body,
      boolean favorited,
      int favoritesCount,
      DateTime createdAt,
      DateTime updatedAt,
      List<String> tagList,
      ProfileData profileData,
      boolean published) {
    this.id = id;
    this.slug = slug;
    this.title = title;
    this.description = description;
    this.body = body;
    this.favorited = favorited;
    this.favoritesCount = favoritesCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.tagList = tagList;
    this.profileData = profileData;
    this.published = published;
  }

  @Override
  public DateTimeCursor getCursor() {
    return new DateTimeCursor(updatedAt);
  }
}
