package server;

import adapter.LocalDateTimeAdapter;
import adapter.SubtaskAdapter;
import adapter.TaskAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private final Gson gson1 = new Gson();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
            .create();

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks/task", new TaskHandler());
        httpServer.createContext("/tasks/subtask", new SubtaskHandler());
        httpServer.createContext("/tasks/epic", new EpicHandler());
        httpServer.createContext("/tasks/history", new HistoryHandler());
        httpServer.createContext("/tasks/", new PriorityHandler());
        httpServer.createContext("/tasks/subtask/epic/id=", new SubtaskByEpicHandler());
    }

    public void startServer() {
        System.out.println("Запускаем сервер на порту " + PORT);
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }

    private static void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
    }

    private class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            final String path = exchange.getRequestURI().getPath();
            final String query = exchange.getRequestURI().getQuery();
            try {
                if (path.endsWith("tasks/task")) {
                    if (exchange.getRequestMethod().equals("GET")) {
                        if (query == null) {
                            final ArrayList<Task> tasks = taskManager.getAllTask();
                            final String response = gson.toJson(tasks);
                            sendText(exchange, response);
                            exchange.close();
                            return;
                        }
                        String idParam = query.substring(3);
                        final int id = Integer.parseInt(idParam);
                        final Task task = taskManager.getTaskById(id);
                        final String response = gson.toJson(task);
                        sendText(exchange, response);
                        exchange.close();
                    } else if (exchange.getRequestMethod().equals("POST")) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                        if (body.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            exchange.close();
                            return;
                        }
                        final Task task = gson.fromJson(body, Task.class);
                        if (task.getId() == null) {
                            taskManager.addTask(task);
                        }
                        final String response = gson.toJson(task);
                        sendText(exchange, response);
                        exchange.close();
                    } else if (exchange.getRequestMethod().equals("DELETE")) {
                        if (query == null) {
                            taskManager.removeAllTasks();
                            final String response = "Все задачи удалены";
                            sendText(exchange, response);
                            exchange.close();
                        }
                        String id = query.substring(3);
                        exchange.sendResponseHeaders(200, 0);
                        taskManager.removeTaskById(Integer.parseInt(id));
                        final String response = "Задача успешно удалена";
                        sendText(exchange, response);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }
            } catch (IOException | NumberFormatException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }


    private class EpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            final String path = exchange.getRequestURI().getPath();
            final String query = exchange.getRequestURI().getQuery();
            try {
                if (path.endsWith("tasks/epic")) {
                    if (exchange.getRequestMethod().equals("GET")) {
                        if (query == null) {
                            final List<Epic> epics = taskManager.getAllEpics();
                            final String response = gson1.toJson(epics);
                            sendText(exchange, response);
                            exchange.close();
                            return;
                        }
                        String idParam = query.substring(3);
                        final int id = Integer.parseInt(idParam);
                        final Epic epic = taskManager.getEpicById(id);
                        final String response = gson1.toJson(epic);
                        sendText(exchange, response);
                        exchange.close();
                    } else if (exchange.getRequestMethod().equals("POST")) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                        if (body.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            exchange.close();
                            return;
                        }
                        final Epic epic = gson1.fromJson(body, Epic.class);
                        if (epic.getId() == null) {
                            taskManager.addEpic(epic);
                        }
                        final String response = gson1.toJson(epic);
                        sendText(exchange, response);
                        exchange.close();
                    } else if (exchange.getRequestMethod().equals("DELETE")) {
                        if (query == null) {
                            taskManager.removeAllEpic();
                            final String response = "Задачи успешно удалены";
                            sendText(exchange, response);
                            exchange.close();
                        }
                        String id = query.substring(3);
                        exchange.sendResponseHeaders(200, 0);
                        taskManager.removeEpicById(Integer.parseInt(id));
                        final String response = "Задача успешно удалена";
                        sendText(exchange, response);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }
            } catch (IOException | NumberFormatException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private class SubtaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            final String path = exchange.getRequestURI().getPath();
            final String query = exchange.getRequestURI().getQuery();
            try {
                if (path.endsWith("tasks/subtask")) {
                    if (exchange.getRequestMethod().equals("GET")) {
                        if (query == null) {
                            final List<Subtask> tasks = taskManager.getAllSubtask();
                            final String response = gson.toJson(tasks);
                            sendText(exchange, response);
                            exchange.close();
                            return;
                        }
                        String idParam = query.substring(3);
                        final int id = Integer.parseInt(idParam);
                        final Subtask subtask = taskManager.getAllSubtask().get(id);
                        final String response = gson.toJson(subtask);
                        sendText(exchange, response);
                        exchange.close();
                    } else if (exchange.getRequestMethod().equals("POST")) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                        if (body.isEmpty()) {
                            exchange.sendResponseHeaders(400, 0);
                            exchange.close();
                            return;
                        }
                        final Subtask subtask = gson.fromJson(body, Subtask.class);
                        if (subtask.getId() == null) {
                            taskManager.addSubtask(subtask);
                        }
                        final String response = gson.toJson(subtask);
                        sendText(exchange, response);
                        exchange.close();
                    } else if (exchange.getRequestMethod().equals("DELETE")) {
                        if (query == null) {
                            taskManager.removeAllSubtask();
                            final String response = "Все подзадачи удалены";
                            sendText(exchange, response);
                            exchange.close();
                        }
                        String id = query.substring(3);
                        exchange.sendResponseHeaders(200, 0);
                        taskManager.removeSubtaskById(Integer.parseInt(id));
                        final String response = "Задача успешно удалена";
                        sendText(exchange, response);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }
            } catch (IOException | NumberFormatException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            final String path = exchange.getRequestURI().getPath();
            try {
                if (path.endsWith("tasks/history")) {
                    if (exchange.getRequestMethod().equals("GET")) {
                        final List<Task> tasks = taskManager.getHistory();
                        final String response = gson.toJson(tasks);
                        sendText(exchange, response);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }
            } catch (IOException | NumberFormatException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private class PriorityHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            final String path = exchange.getRequestURI().getPath();
            try {
                if (path.endsWith("tasks/")) {
                    if (exchange.getRequestMethod().equals("GET")) {
                        final List<Task> tasks = taskManager.getPrioritizedTasks();
                        final String response = gson.toJson(tasks);
                        sendText(exchange, response);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }
            } catch (IOException | NumberFormatException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private class SubtaskByEpicHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            final String path = exchange.getRequestURI().getPath();
            final String query = exchange.getRequestURI().getQuery();
            try {
                if (path.endsWith("tasks/subtask/epic/id=")) {
                    if (exchange.getRequestMethod().equals("GET")) {
                        String idParam = query.substring(3);
                        final int id = Integer.parseInt(idParam);
                        final ArrayList<Subtask> subtask = taskManager.getSubtasksByEpicId(id);
                        final String response = gson.toJson(subtask);
                        sendText(exchange, response);
                        exchange.close();
                    } else {
                        exchange.sendResponseHeaders(405, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }
            } catch (IOException | NumberFormatException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

}
