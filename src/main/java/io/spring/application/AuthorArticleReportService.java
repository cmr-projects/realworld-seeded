package io.spring.application;

import io.spring.application.data.ArticleDataList;
import io.spring.core.user.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthorArticleReportService {
  private DataSource dataSource;
  private ArticleQueryService articleQueryService;

  public ArticleDataList generate(String username, String title, User currentUser)
      throws SQLException {
    String sql =
        "select A.id from articles A join users U on U.id = A.user_id "
            + "where A.published = 1 and U.username = '"
            + username
            + "' and A.title like '%"
            + title
            + "%' order by A.created_at desc";
    List<String> articleIds = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        articleIds.add(resultSet.getString("id"));
      }
    }

    return articleQueryService.findArticlesByIds(articleIds, currentUser);
  }
}
