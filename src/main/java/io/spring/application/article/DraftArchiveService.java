package io.spring.application.article;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.article.Tag;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DraftArchiveService {
  private final ArticleRepository articleRepository;
  private final ArticleCommandService articleCommandService;
  private final SecretKey archiveKey;

  public DraftArchiveService(
      ArticleRepository articleRepository, ArticleCommandService articleCommandService)
      throws GeneralSecurityException {
    this.articleRepository = articleRepository;
    this.articleCommandService = articleCommandService;
    KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
    keyGenerator.init(256);
    this.archiveKey = keyGenerator.generateKey();
  }

  public DraftArchiveTransfer exportDraft(String slug, User currentUser)
      throws IOException, GeneralSecurityException {
    Article article =
        articleRepository.findBySlug(slug).orElseThrow(ResourceNotFoundException::new);
    if (article.isPublished()) {
      throw new ResourceNotFoundException();
    }
    if (!AuthorizationService.canWriteArticle(currentUser, article)) {
      throw new NoAuthorizationException();
    }

    List<String> tagList =
        article.getTags().stream().map(Tag::getName).collect(Collectors.toList());
    DraftArchiveContent archive =
        new DraftArchiveContent(
            article.getTitle(), article.getDescription(), article.getBody(), tagList);
    byte[] payload = serialize(archive);
    byte[] tag = createTag(payload);
    return new DraftArchiveTransfer(
        Base64.getEncoder().encodeToString(payload), Base64.getEncoder().encodeToString(tag));
  }

  public Article importDraft(DraftArchiveTransfer transfer, User currentUser)
      throws IOException, GeneralSecurityException, ClassNotFoundException {
    byte[] payload = decode(transfer.getPayload());
    byte[] suppliedTag = decode(transfer.getTag());
    byte[] expectedTag = createTag(payload);
    if (!MessageDigest.isEqual(expectedTag, suppliedTag)) {
      throw new ResponseStatusException(BAD_REQUEST, "Archive tag does not match");
    }

    Object value;
    try (ObjectInputStream inputStream =
        new ObjectInputStream(new ByteArrayInputStream(payload))) {
      value = inputStream.readObject();
    }
    if (value.getClass() != DraftArchiveContent.class) {
      throw new ResponseStatusException(BAD_REQUEST, "Archive content is not supported");
    }

    DraftArchiveContent archive = (DraftArchiveContent) value;
    String title = availableTitle(archive.getTitle());
    NewArticleParam articleParam =
        NewArticleParam.builder()
            .title(title)
            .description(archive.getDescription())
            .body(archive.getBody())
            .tagList(new ArrayList<>(archive.getTagList()))
            .published(false)
            .build();
    return articleCommandService.createArticle(articleParam, currentUser);
  }

  private byte[] serialize(DraftArchiveContent archive) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (ObjectOutputStream outputStream = new ObjectOutputStream(output)) {
      outputStream.writeObject(archive);
    }
    return output.toByteArray();
  }

  private byte[] createTag(byte[] payload) throws GeneralSecurityException {
    Mac mac = Mac.getInstance(archiveKey.getAlgorithm());
    mac.init(archiveKey);
    return mac.doFinal(payload);
  }

  private byte[] decode(String value) {
    try {
      return Base64.getDecoder().decode(value);
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(BAD_REQUEST, "Archive encoding is invalid", exception);
    }
  }

  private String availableTitle(String originalTitle) {
    String title = originalTitle;
    int copyNumber = 1;
    while (articleRepository.findBySlug(Article.toSlug(title)).isPresent()) {
      title = originalTitle + " (" + copyNumber + ")";
      copyNumber++;
    }
    return title;
  }
}
