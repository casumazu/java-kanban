package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KVServer {
    public static final int PORT = 8080;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public KVServer() throws IOException {
        apiToken = "" + System.currentTimeMillis();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Сервер доступен по адресу -> http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private void register(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(httpExchange.getRequestMethod())) {
                sendText(httpExchange, apiToken);
            } else {
                System.out.println("/register - это GET-запрос, а не " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, resp.length);
        httpExchange.getResponseBody().write(resp);
    }

    private void load(HttpExchange httpExchange) throws IOException {
        try {
            if (httpExchange.getRequestURI().getPath().endsWith("/load/tasks")) {
                String jsonTask;
                jsonTask = data.get("tasks");
                System.out.println("запрос успешно обработан");
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(jsonTask.getBytes(DEFAULT_CHARSET));
                os.close();
            } else if (httpExchange.getRequestURI().getPath().endsWith("/load/epics")) {
                String jsonTask;
                jsonTask = data.get("epics");
                System.out.println("запрос успешно обработан");
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(jsonTask.getBytes(DEFAULT_CHARSET));
                os.close();
            } else if (httpExchange.getRequestURI().getPath().endsWith("/load/subtasks")) {
                String jsonTask;
                jsonTask = data.get("subtasks");
                System.out.println("запрос успешно обработан");
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(jsonTask.getBytes(DEFAULT_CHARSET));
                os.close();
            } else if (httpExchange.getRequestURI().getPath().endsWith("/load/history")) {
                String jsonTask;
                jsonTask = data.get("history");
                System.out.println("запрос успешно обработан");
                httpExchange.sendResponseHeaders(200, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.write(jsonTask.getBytes(DEFAULT_CHARSET));
                os.close();
            }
        } finally {
            httpExchange.close();
        }
    }

    private void save(HttpExchange httpExchange) throws IOException {
        try {
            System.out.println("\n/save");
            if (!auth(httpExchange)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                httpExchange.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(httpExchange.getRequestMethod())) {
                String key = httpExchange.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                String value = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    httpExchange.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                httpExchange.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + httpExchange.getRequestMethod());
                httpExchange.sendResponseHeaders(405, 0);
            }
        } finally {
            httpExchange.close();
        }
    }

    protected boolean auth(HttpExchange httpExchange) {
        String req = httpExchange.getRequestURI().getRawQuery();
        return req != null && (req.contains("API_TOKEN=" + apiToken) ||
                req.contains("API_TOKEN=DEBUG"));
    }
}
