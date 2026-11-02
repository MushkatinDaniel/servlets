package ru.netology.repository;

import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory репозиторий, безопасный для вызова из разных потоков.
 * <p>
 * Посты лежат в {@link ConcurrentHashMap}, новые id выдаёт {@link AtomicLong}:
 * два потока никогда не получат один и тот же id.
 */
public class PostRepository {
  private static final long NEW_POST_ID = 0;

  private final ConcurrentMap<Long, Post> posts = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong(0);

  public List<Post> all() {
    // копия в новый список, отсортированная по id: безопасно итерироваться снаружи
    return posts.values().stream()
        .sorted(Comparator.comparingLong(Post::getId))
        .collect(Collectors.toList());
  }

  public Optional<Post> getById(long id) {
    return Optional.ofNullable(posts.get(id));
  }

  /**
   * id == 0 — создание: присваиваем новый id и сохраняем.
   * id != 0 — обновление: заменяем существующий пост.
   * Если поста с таким id нет — бросаем {@link NotFoundException} (сервлет отдаст 404).
   */
  public Post save(Post post) {
    if (post.getId() == NEW_POST_ID) {
      final var newId = idCounter.incrementAndGet();
      post.setId(newId);
      posts.put(newId, post);
      return post;
    }

    // проверка «пост есть» и замена выполняются атомарно для этого ключа
    final var updated = posts.computeIfPresent(post.getId(), (id, old) -> post);
    if (updated == null) {
      throw new NotFoundException("Post with id " + post.getId() + " not found");
    }
    return updated;
  }

  public void removeById(long id) {
    posts.remove(id);
  }
}
