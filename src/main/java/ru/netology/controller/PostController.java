package ru.netology.controller;

import com.google.gson.Gson;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;

public class PostController {
  private static final String APPLICATION_JSON = "application/json";

  private final PostService service;
  // Gson потокобезопасен, одного экземпляра на контроллер достаточно
  private final Gson gson = new Gson();

  public PostController(PostService service) {
    this.service = service;
  }

  public void all(HttpServletResponse response) throws IOException {
    writeJson(response, service.all());
  }

  public void getById(long id, HttpServletResponse response) throws IOException {
    writeJson(response, service.getById(id));
  }

  public void save(Reader body, HttpServletResponse response) throws IOException {
    final var post = gson.fromJson(body, Post.class);
    writeJson(response, service.save(post));
  }

  public void removeById(long id, HttpServletResponse response) {
    service.removeById(id);
    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  private void writeJson(HttpServletResponse response, Object data) throws IOException {
    response.setContentType(APPLICATION_JSON);
    response.getWriter().print(gson.toJson(data));
  }
}
