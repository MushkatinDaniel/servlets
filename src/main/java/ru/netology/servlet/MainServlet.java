package ru.netology.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.config.JavaConfig;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private static final String GET = "GET";
  private static final String POST = "POST";
  private static final String DELETE = "DELETE";

  private static final String POSTS_PATH = "/api/posts";
  private static final String POST_BY_ID_REGEX = POSTS_PATH + "/\\d+";

  private AnnotationConfigApplicationContext context;
  private PostController controller;

  @Override
  public void init() {
    // Spring создаёт бины по @Bean-методам из JavaConfig
    context = new AnnotationConfigApplicationContext(JavaConfig.class);
    controller = context.getBean(PostController.class);
  }

  @Override
  public void destroy() {
    context.close();
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (path.equals(POSTS_PATH)) {
        if (method.equals(GET)) {
          controller.all(resp);
          return;
        }
        if (method.equals(POST)) {
          controller.save(req.getReader(), resp);
          return;
        }
      } else if (path.matches(POST_BY_ID_REGEX)) {
        final var id = parseId(path);
        if (method.equals(GET)) {
          controller.getById(id, resp);
          return;
        }
        if (method.equals(DELETE)) {
          controller.removeById(id, resp);
          return;
        }
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (NotFoundException e) {
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private long parseId(String path) {
    return Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
  }
}
