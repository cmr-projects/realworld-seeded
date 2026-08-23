package io.spring.application.article;

import com.fasterxml.jackson.annotation.JsonRootName;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("archive")
public class DraftArchiveTransfer {
  @NotBlank private String payload;
  @NotBlank private String tag;
}
