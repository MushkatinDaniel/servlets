package ru.netology.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

@Configuration
public class JavaConfig {
  @Bean
  public PostRepository postRepository() {
    return new PostRepository();
  }

  @Bean
  public PostService postService(PostRepository repository) {
    // Spring передаст сюда бин PostRepository, созданный методом выше
    return new PostService(repository);
  }

  @Bean
  public PostController postController(PostService service) {
    return new PostController(service);
  }
}
