package io.spring.api;

import io.spring.application.AuthorArticleReportService;
import io.spring.core.user.User;
import java.sql.SQLException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/profiles/{username}/article-report")
@AllArgsConstructor
public class AuthorArticleReportApi {
  private AuthorArticleReportService authorArticleReportService;

  @GetMapping
  public ResponseEntity getReport(
      @PathVariable("username") String username,
      @RequestParam(value = "title", defaultValue = "") String title,
      @AuthenticationPrincipal User currentUser)
      throws SQLException {
    return ResponseEntity.ok(authorArticleReportService.generate(username, title, currentUser));
  }
}
