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

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this::handle);
    }

    public void handle(HttpExchange exchange) throws IOException {
        String response;
        String path = exchange.getRequestURI().getPath();
        String param = exchange.getRequestURI().getQuery();
        switch (path) {
            case "/tasks/task":
                handleTask(exchange);
                break;
            case "/tasks/subtask":
                handleSubtask(exchange);
                break;
            case "/tasks/epic":
                handleEpic(exchange);
                break;
            case "/tasks/subtask/epic":
                int id = Integer.parseInt(param.split("=")[1]);
                List<Subtask> subtasks = manager.getSubtasksByEpicId(id);
                if (subtasks == null) {
                    exchange.sendResponseHeaders(404, 0);
                    response = "Epic задача не найдена.";
                } else {
                    response = GSON.toJson(subtasks);
                    exchange.sendResponseHeaders(200, 0);
                }
                sendText(exchange, response);
                exchange.close();
                break;
            case "/tasks/history":
                response = GSON.toJson(manager.getHistory());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
                break;
            case "/tasks":
                response = GSON.toJson(manager.getPrioritizedTasks());
                exchange.sendResponseHeaders(200, 0);
                sendText(exchange, response);
                exchange.close();
                break;
        }
    }

    private void handleTask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET":
                response = handleTaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
            case "POST":
                response = handleTaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
            case "DELETE":
                response = handleTaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
        }
    }

    private void handleEpic(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET":
                response = handleEpicGet(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
            case "POST":
                response = handleEpicPost(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
            case "DELETE":
                response = handleEpicDelete(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
        }
    }
    private void handleSubtask(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String response;
        switch (method) {
            case "GET":
                response = handleSubtaskGet(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
            case "POST":
                response = handleSubtaskPost(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
            case "DELETE":
                response = handleSubtaskDelete(exchange);
                sendText(exchange, response);
                exchange.close();
                break;
        }
    }

    private String handleTaskGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getAllTask());
            h.sendResponseHeaders(200, 0);
        } else {
            Task task = manager.getTaskById(id);
            if (task == null) {
                h.sendResponseHeaders(404, 0);
                response = "Task задача не найдена.";
            } else {
                response = GSON.toJson(task);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleTaskPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "<Task> задача отсутствует в теле запроса.";
        } else {
            Task task = GSON.fromJson(body, Task.class);
            if (param == null) {
                manager.addTask(task);
                if (task.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Task задача не добавлена.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "<Task> задача добавлена.";
                }
            } else {
                task.setId(id);
                manager.updateTask(task);
                if (task.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить <Task> задачу.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "<Task> задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }

    private String handleTaskDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.removeAllTasks();
            h.sendResponseHeaders(200, 0);
            response = "Все <Task> задачи удалены.";
        } else {
            manager.removeTaskById(id);
            if (manager.getTaskById(id) == null) {
                h.sendResponseHeaders(404, 0);
                response = "<Task> задача не найдена.";
            } else {
                h.sendResponseHeaders(200, 0);
                response = "Задача " + id + " удалена.";
            }
        }
        return response;
    }

    private String handleEpicGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getAllEpics());
            h.sendResponseHeaders(200, 0);
        } else {
            Epic epic = manager.getEpicById(id);
            if (epic == null) {
                h.sendResponseHeaders(404, 0);
                response = "Epic задача не найдена.";
            } else {
                response = GSON.toJson(epic);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleEpicPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "<Epic> задача отсутствует в теле запроса.";
        } else {
            Epic epic = GSON.fromJson(body, Epic.class);
            if (param == null) {
                manager.addEpic(epic);
                if (epic.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "<Epic> задача не добавлена.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "<Epic> задача добавлена.";
                }
            } else {
                epic.setId(id);
                manager.updateEpic(epic);
                if (epic.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить <Epic> задачу.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "<Epic> задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }

    private String handleEpicDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.removeAllEpic();
            h.sendResponseHeaders(200, 0);
            response = "Все <Epic> задачи удалены.";
        } else {
            manager.removeEpicById(id);
            if (manager.getEpicById(id) == null) {
                h.sendResponseHeaders(404, 0);
                response = "<Epic> задача не найдена.";
            } else {
                h.sendResponseHeaders(200, 0);
                response = "<Epic> задача " + id + " удалена.";
            }
        }
        return response;
    }

    private String handleSubtaskGet(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            response = GSON.toJson(manager.getAllSubtask());
            h.sendResponseHeaders(200, 0);
        } else {
            Subtask subtask = manager.getAllSubtask().get(id);
            if (subtask == null) {
                h.sendResponseHeaders(404, 0);
                response = "<Subtask> задача не найдена.";
            } else {
                response = GSON.toJson(subtask);
                h.sendResponseHeaders(200, 0);
            }
        }
        return response;
    }

    private String handleSubtaskPost(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        String body = readText(h);
        if (body.isBlank()) {
            h.sendResponseHeaders(404, 0);
            response = "<Subtask> задача отсутствует в теле запроса.";
        } else {
            Subtask subtask = GSON.fromJson(body, Subtask.class);
            if (param == null) {
                if (subtask.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "<Subtask> задача не добавлена.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "<Subtask> задача добавлена.";
                }
            } else {
                subtask.setId(id);
                if (subtask.getId() < 0) {
                    h.sendResponseHeaders(400, 0);
                    response = "Не удалось обновить Subtask задачу.";
                } else {
                    h.sendResponseHeaders(201, 0);
                    response = "<Subtask> задача " + id + " обновлена.";
                }
            }
        }
        return response;
    }
    private String handleSubtaskDelete(HttpExchange h) throws IOException {
        String param = h.getRequestURI().getQuery();
        String response;
        int id = 0;
        if (param != null) {
            id = Integer.parseInt(param.split("=")[1]);
        }
        if (param == null) {
            manager.removeAllSubtask();
            h.sendResponseHeaders(200, 0);
            response = "Все <Subtask> задачи удалены.";
        } else {
            manager.removeSubtaskById(id);
            if (manager.getAllSubtask().get(id) == null) {
                h.sendResponseHeaders(404, 0);
                response = "<Subtask> задача не найдена.";
            } else {
                h.sendResponseHeaders(200, 0);
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

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseBody().write(resp);
    }

    public TaskManager getManager() {
        return manager;
    }
}