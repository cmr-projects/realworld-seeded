package io.spring.application.article;

import com.fasterxml.jackson.annotation.JsonRootName;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonRootName("archive")
public class DraftArchiveExportParam {
  @NotBlank private String slug;
}
