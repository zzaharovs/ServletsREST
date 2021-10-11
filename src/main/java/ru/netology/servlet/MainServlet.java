package ru.netology.servlet;

import com.google.gson.Gson;
import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ru.netology.controller.PostController.APPLICATION_JSON;


public class MainServlet extends HttpServlet {
    private PostController controller;
    private ConcurrentMap<String, ConcurrentMap<String, Handler>> handlers;
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_DELETE = "DELETE";

    public static final String API_PATH = "/api/posts";
    private final Long defaultValueForId = 999L;


    @Override
    public void init() {
        final var repository = new PostRepository();
        final var service = new PostService(repository);
        handlers = new ConcurrentHashMap<>();
        controller = new PostController(service);

        addHandler(HTTP_METHOD_GET, API_PATH, (req, resp, id) -> {
            if (defaultValueForId.equals(id)) {
                controller.all(resp);
                return;
            }
            controller.getById(id, resp);
        });

        addHandler(HTTP_METHOD_POST, API_PATH, (req, resp, id) -> {
            System.out.println(req.getReader());
            controller.save(req.getReader(), resp);
        });

        addHandler(HTTP_METHOD_DELETE, API_PATH, (req, resp, id) -> {
            controller.removeById(id, resp);
        });
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // если деплоились в root context, то достаточно этого
        try {

            final var method = req.getMethod();
            final var path = req.getRequestURI().matches((API_PATH + "/\\d")) ? API_PATH : req.getRequestURI();
            final var id = req.getRequestURI().matches((API_PATH + "/\\d")) ?
                    Long.parseLong(req.getRequestURI()
                            .substring(req.getRequestURI().lastIndexOf("/") + 1)) : defaultValueForId;

            Handler currentHandler = searchHandler(method, path);

            if (currentHandler == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                throw new NotFoundException("Неверно указан путь запроса");
            }
            currentHandler.Handle(req, resp, id);
        } catch (NotFoundException exception) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.setContentType(APPLICATION_JSON);
            final var gson = new Gson();
            try {
                resp.getWriter().print(gson.toJson(exception.getMessage()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType(APPLICATION_JSON);
            final var gson = new Gson();
            try {
                resp.getWriter().print(gson.toJson(e.getMessage()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private Handler searchHandler(String method, String path) {
        return handlers.get(method)
                .get(path);
    }

    public void addHandler(String method, String path, Handler handler) {

        if (!handlers.containsKey(method)) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method)
                .put(path, handler);

    }


}

