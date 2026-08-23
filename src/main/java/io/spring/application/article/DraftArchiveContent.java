package io.spring.application.article;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public final class DraftArchiveContent implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String title;
  private final String description;
  private final String body;
  private final List<String> tagList;

  public DraftArchiveContent(
      String title, String description, String body, List<String> tagList) {
    this.title = title;
    this.description = description;
    this.body = body;
    this.tagList = new ArrayList<>(tagList);
  }
}
