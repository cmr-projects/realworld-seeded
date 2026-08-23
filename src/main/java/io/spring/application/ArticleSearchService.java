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
public class ArticleSearchService {
  private DataSource dataSource;
  private ArticleQueryService articleQueryService;

  public ArticleDataList search(String query, User currentUser) throws SQLException {
    String sql =
        "select id from articles where published = 1 and (title like '%"
            + query
            + "%' or description like '%"
            + query
            + "%' or body like '%"
            + query
            + "%') order by created_at desc";
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
