package server;

import adapter.FileAdapter;
import adapter.HistoryManagerAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager = Managers.getDefault("http://localhost:8078", "key");
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter())
            .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
            .serializeNulls().create();
    private String response;
    private int id = 0;

    public HttpTaskServer(){
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks", this::handle);
    }

    public void handle(HttpExchange exchange){

        String path = exchange.getRequestURI().getPath();
        String param = exchange.getRequestURI().getQuery();
        switch (path) {
            case "/tasks/task" -> handleTask(exchange);
            case "/tasks/subtask" -> handleSubtask(exchange);
            case "/tasks/epic" -> handleEpic(exchange);
            case "/tasks/subtask/epic" -> {
                int id = Integer.parseInt(param.split("=")[1]);
                List<Subtask> subtasks = manager.getSubtasksByEpicId(id);
                if (subtasks == null) {
                    try {
                        exchange.sendResponseHeaders(404, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "Epic задача не найдена.";
                } else {
                    response = GSON.toJson(subtasks);
                    try {
                        exchange.sendResponseHeaders(200, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks/history" -> {
                response = GSON.toJson(manager.getHistory());
                try {
                    exchange.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                sendText(exchange, response);
                exchange.close();
            }
            case "/tasks" -> {
                response = GSON.toJson(manager.getPrioritizedTasks());
                try {
                    exchange.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleTask(HttpExchange exchange){
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> {
                response = handleTaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleTaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleTaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private void handleEpic(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> {
                response = handleEpicGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleEpicPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleEpicDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }
    private void handleSubtask(HttpExchange exchange) {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> {
                response = handleSubtaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "POST" -> {
                response = handleSubtaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
            }
            case "DELETE" -> {
                response = handleSubtaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
            }
        }
    }

    private String handleTaskGet(HttpExchange h) {
        String param = paramHttp(h);
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getAllTask());
            try {
                h.sendResponseHeaders(200, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Task task = manager.getTaskById(id);
            if (task == null) {
                try {
                    h.sendResponseHeaders(404, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response = "Task задача не найдена.";
            } else {
                response = GSON.toJson(task);
                try {
                    h.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return response;
    }

    private String handleTaskPost(HttpExchange h){
        String param = paramHttp(h);
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            try {
                h.sendResponseHeaders(404, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response = "<Task> задача отсутствует в теле запроса.";
        } else {
            Task task = GSON.fromJson(body, Task.class);
            if (param == null) {
                manager.addTask(task);
                if (task.getId() < 0) {
                    try {
                        h.sendResponseHeaders(400, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "Task задача не добавлена.";
                } else {
                    try {
                        h.sendResponseHeaders(201, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Task> задача добавлена.";
                }
            } else {
                task.setId(id);
                manager.updateTask(task);
                if (task.getId() < 0) {
                    try {
                        h.sendResponseHeaders(400, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "Не удалось обновить <Task> задачу.";
                } else {
                    try {
                        h.sendResponseHeaders(201, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Task> задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }

    private String handleTaskDelete(HttpExchange h){
        String param = paramHttp(h);
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.removeAllTasks();
            try {
                h.sendResponseHeaders(200, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response = "Все <Task> задачи удалены.";
        } else {
            manager.removeTaskById(id);
            if (manager.getTaskById(id) == null) {
                try {
                    h.sendResponseHeaders(404, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response = "<Task> задача не найдена.";
            } else {
                try {
                    h.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response = "Задача " + id + " удалена.";
            }
        }
        return response;
    }

    private String handleEpicGet(HttpExchange h){
        String param = paramHttp(h);
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getAllEpics());
            try {
                h.sendResponseHeaders(200, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Epic epic = manager.getEpicById(id);
            if (epic == null) {
                try {
                    h.sendResponseHeaders(404, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response = "Epic задача не найдена.";
            } else {
                response = GSON.toJson(epic);
                try {
                    h.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return response;
    }

    private String handleEpicPost(HttpExchange h){
        String param = paramHttp(h);
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            try {
                h.sendResponseHeaders(404, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response = "<Epic> задача отсутствует в теле запроса.";
        } else {
            Epic epic = GSON.fromJson(body, Epic.class);
            if (param == null) {
                manager.addEpic(epic);
                if (epic.getId() < 0) {
                    try {
                        h.sendResponseHeaders(400, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Epic> задача не добавлена.";
                } else {
                    try {
                        h.sendResponseHeaders(201, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Epic> задача добавлена.";
                }
            } else {
                epic.setId(id);
                manager.updateEpic(epic);
                if (epic.getId() < 0) {
                    try {
                        h.sendResponseHeaders(400, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "Не удалось обновить <Epic> задачу.";
                } else {
                    try {
                        h.sendResponseHeaders(201, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Epic> задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }

    private String handleEpicDelete(HttpExchange h) {
        String param = paramHttp(h);
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.removeAllEpic();
            try {
                h.sendResponseHeaders(200, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response = "Все <Epic> задачи удалены.";
        } else {
            manager.removeEpicById(id);
            if (manager.getEpicById(id) == null) {
                try {
                    h.sendResponseHeaders(404, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response = "<Epic> задача не найдена.";
            } else {
                try {
                    h.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response = "<Epic> задача " + id + " удалена.";
            }
        }
        return response;
    }

    private Integer parseSub(String param){
        return Integer.parseInt(param.split("=")[1]);
    }

    private String paramHttp(HttpExchange h){
        return h.getRequestURI().getQuery();
    }

    public void subIsNull(HttpExchange h) {
        if (manager.getSubtaskById(id) == null) {
            try {
                h.sendResponseHeaders(404, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String handleSubtaskGet(HttpExchange h){
        String param = paramHttp(h);
        if (param != null) {
            id = parseSub(param);
        }
        if (param == null) {
            response = GSON.toJson(manager.getAllSubtask());
            try {
                h.sendResponseHeaders(200, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Subtask subtask = manager.getSubtaskById(id);
            if (subtask == null) {
                subIsNull(h);
                response = "<Subtask> задача не найдена.";
            } else {
                response = GSON.toJson(subtask);
                try {
                    h.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return response;
    }

    private String handleSubtaskPost(HttpExchange h){
        String param = paramHttp(h);

        if (param != null) {
            id = parseSub(param);
        }
        String body = readText(h);
        if (body.isBlank()) {
            try {
                h.sendResponseHeaders(404, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response = "<Subtask> задача отсутствует в теле запроса.";
        } else {
            Subtask subtask = GSON.fromJson(body, Subtask.class);
            if (param == null) {
                if (subtask.getId() < 0) {
                    try {
                        h.sendResponseHeaders(400, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Subtask> задача не добавлена.";
                } else {
                    try {
                        h.sendResponseHeaders(201, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Subtask> задача добавлена.";
                }
            } else {
                subtask.setId(id);
                if (subtask.getId() < 0) {
                    try {
                        h.sendResponseHeaders(400, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "Не удалось обновить Subtask задачу.";
                } else {
                    try {
                        h.sendResponseHeaders(201, 0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    response = "<Subtask> задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }

    private String handleSubtaskDelete(HttpExchange h){
        String param = paramHttp(h);
        if (param != null) {
            id = parseSub(param);
        }
        if (param == null) {
            manager.removeAllSubtask();
            try {
                h.sendResponseHeaders(200, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response = "Все <Subtask> задачи удалены.";
        } else {
            manager.removeSubtaskById(id);
            if (manager.getSubtaskById(id) == null) {
                subIsNull(h);
                response = "<Subtask> задача не найдена.";
            } else {
                try {
                    h.sendResponseHeaders(200, 0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                response = "<Subtask> задача " + id + " удалена.";
            }
        }
        return response;
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Ссылка в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private String readText(HttpExchange h) {
        try {
            return new String(h.getRequestBody().readAllBytes(), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendText(HttpExchange h, String text) {
        try {
            byte[] resp = text.getBytes(UTF_8);
            h.getResponseBody().write(resp);
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public TaskManager getManager() {
        return manager;
    }
}