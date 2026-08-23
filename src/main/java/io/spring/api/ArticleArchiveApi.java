package io.spring.api;

import io.spring.application.ArticleQueryService;
import io.spring.application.article.DraftArchiveExportParam;
import io.spring.application.article.DraftArchiveService;
import io.spring.application.article.DraftArchiveTransfer;
import io.spring.application.data.ArticleData;
import io.spring.core.article.Article;
import io.spring.core.user.User;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/articles/archive")
@AllArgsConstructor
public class ArticleArchiveApi {
  private DraftArchiveService draftArchiveService;
  private ArticleQueryService articleQueryService;

  @PostMapping(path = "export")
  public ResponseEntity exportDraft(
      @AuthenticationPrincipal User currentUser,
      @Valid @RequestBody DraftArchiveExportParam exportParam)
      throws IOException, GeneralSecurityException {
    return ResponseEntity.ok(
        Collections.singletonMap(
            "archive", draftArchiveService.exportDraft(exportParam.getSlug(), currentUser)));
  }

  @PostMapping(path = "import")
  public ResponseEntity importDraft(
      @AuthenticationPrincipal User currentUser,
      @Valid @RequestBody DraftArchiveTransfer archiveTransfer)
      throws IOException, GeneralSecurityException, ClassNotFoundException {
    Article article = draftArchiveService.importDraft(archiveTransfer, currentUser);
    ArticleData articleData = articleQueryService.findById(article.getId(), currentUser).get();
    Map<String, ArticleData> response = Collections.singletonMap("article", articleData);
    return ResponseEntity.ok(response);
  }
}
