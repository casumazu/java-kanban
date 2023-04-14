import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static server.HttpTaskManager.gson;
import static server.HttpTaskServer.GSON;

public class HttpTaskServerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    Task task;
    Task task1;
    Task task2;

    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void startServers() throws IOException {
        httpTaskServer = new HttpTaskServer();
        kvServer = new KVServer();
        httpTaskServer.start();
        kvServer.start();
    }

    @BeforeEach
    public void getTaskType(){
        task = new Task("task", "taskOne");
        task1 = new Task("task2", "taskTwo");
        task2 = new Task("task3", "taskTree");

        epic = new Epic("epic", "epic");
        epic.setId(0);
        subtask = new Subtask("sub1", "sub", epic.getId(), 30, LocalDateTime.now());
        subtask.setId(1);
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    public HttpRequest getRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().GET().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpRequest deleteRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().DELETE().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpResponse<String> addTaskToServer(Task task, String path) throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        String body = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        return client.send(request, handler);
    }

    @Test
    public void addTask() throws IOException, InterruptedException {

        HttpResponse<String> response = addTaskToServer(task, "/task");

        assertEquals(201, response.statusCode());
        assertEquals("<Task> задача добавлена.", response.body());
    }

    @Test
    public void addEmptyTask() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/task");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("<Task> задача отсутствует в теле запроса.", response.body());
    }

    @Test
    public void updateTask() throws IOException, InterruptedException {

        addTaskToServer(task, "/task");
        subtask.setId(0);

        HttpResponse<String> response = addTaskToServer(subtask, "/task?id=0");

        assertEquals(201, response.statusCode());
        assertEquals("<Task> задача 0 обновлена.", response.body());
    }

    @Test
    public void removeTask() throws IOException, InterruptedException {

        addTaskToServer(task, "/task");

        HttpResponse<String> response = client.send(deleteRequest("/task?id=1"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Задача 1 удалена.", response.body());
    }

    @Test
    public void removeTaskWithNullId() throws IOException, InterruptedException {

        addTaskToServer(task, "/task");

        HttpResponse<String> response = client.send(deleteRequest("/task?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("<Task> задача не найдена.", response.body());
    }

    @Test
    public void removeAllTasks() throws IOException, InterruptedException {

        addTaskToServer(task, "/task");
        addTaskToServer(subtask, "/task");

        HttpResponse<String> response = client.send(deleteRequest("/task"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все <Task> задачи удалены.", response.body());
    }

    @Test
    public void getAllTasks() throws IOException, InterruptedException {

        addTaskToServer(task, "/task");
        addTaskToServer(subtask, "/task");

        HttpResponse<String> response = client.send(getRequest("/task"), handler);
        List<Task> tasks = GSON.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(2, tasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {

        addTaskToServer(task, "/task");
        task.setId(1);

        HttpResponse<String> response = client.send(getRequest("/task?id=1"), handler);
        Task task = GSON.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task.getId(), task.getId());
        assertEquals(task.getTitle(), task.getTitle());
        assertEquals(task.getDescription(), task.getDescription());
        assertEquals(task.getStatus(), task.getStatus());
        assertEquals(task.getStartTime(), task.getStartTime());
        assertEquals(task.getDuration(), task.getDuration());
    }

    @Test
    public void getTaskWithNullId() throws IOException, InterruptedException {

        addTaskToServer(task, "/task");

        HttpResponse<String> response = client.send(getRequest("/task?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("<Task> задача не найдена.", response.body());
    }

    @Test
    public void addEpic() throws IOException, InterruptedException {

        HttpResponse<String> response = addTaskToServer(task, "/epic");

        assertEquals(201, response.statusCode());
        assertEquals("<Epic> задача добавлена.", response.body());
    }

    @Test
    public void saveEmptyEpic() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/epic");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("<Epic> задача отсутствует в теле запроса.", response.body());
    }

    @Test
    public void updateEpic() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");

        HttpResponse<String> response = addTaskToServer(task, "/epic?id=1");

        assertEquals(201, response.statusCode());
        assertEquals("<Epic> задача 1 обновлена.", response.body());
    }

    @Test
    public void removeEpic() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");
        HttpResponse<String> response = client.send(deleteRequest("/epic?id=1"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("<Epic> задача 1 удалена.", response.body());
    }

    @Test
    public void removeAllEpics() throws IOException, InterruptedException {

        addTaskToServer(task, "/epic");
        addTaskToServer(subtask, "/epic");

        HttpResponse<String> response = client.send(deleteRequest("/epic"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все <Epic> задачи удалены.", response.body());
    }

    @Test
    public void getAllEpics() throws IOException, InterruptedException {

        addTaskToServer(task, "/epic");
        addTaskToServer(subtask, "/epic");

        HttpResponse<String> response = client.send(getRequest("/epic"), handler);
        List<Epic> taskList = GSON.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(2, taskList.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getEpicById() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");
        epic.setId(1);

        HttpResponse<String> response = client.send(getRequest("/epic?id=1"), handler);
        Epic epic = GSON.fromJson(response.body(), Epic.class);

        assertEquals(1, epic.getId());
        assertEquals("epic", epic.getTitle());
        assertEquals("epic", epic.getDescription());
        assertEquals(task.getStatus(), epic.getStatus());
        assertEquals(task.getDuration(), epic.getDuration());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");

        HttpResponse<String> response = addTaskToServer(subtask, "/subtask");

        assertEquals(201, response.statusCode());
        assertEquals("<Subtask> задача добавлена.", response.body());
    }

    @Test
    public void emptySubtask() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("<Subtask> задача отсутствует в теле запроса.", response.body());
    }

    @Test
    public void removeSubtaskWithNullId() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");
        addTaskToServer(subtask, "/subtask");

        HttpResponse<String> response = client.send(deleteRequest("/subtask?id=2"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("<Subtask> задача не найдена.", response.body());
    }

    @Test
    public void removeAllSubtasks() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");
        addTaskToServer(subtask, "/subtask");

        HttpResponse<String> response = client.send(deleteRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все <Subtask> задачи удалены.", response.body());
    }

    @Test
    public void getAllSubtasks() throws IOException, InterruptedException {
        addTaskToServer(epic, "/epic");
        addTaskToServer(subtask, "/subtask");

        HttpResponse<String> response = client.send(getRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getSubtaskWithNullId() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");
        addTaskToServer(subtask, "/subtask");

        HttpResponse<String> response = client.send(getRequest("/subtask?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("<Subtask> задача не найдена.", response.body());
    }

    @Test
    public void getSubtasksInEpic() throws IOException, InterruptedException {

        addTaskToServer(epic, "/epic");
        addTaskToServer(subtask, "/subtask");

        HttpResponse<String> response = client.send(getRequest("/subtask/epic?id=1"), handler);

        assertEquals(200, response.statusCode());
    }

    @Test
    public void getHistory() throws IOException, InterruptedException {

        addTaskToServer(task, "/epic");
        addTaskToServer(subtask, "/subtask");
        addTaskToServer(task, "/task");
        addTaskToServer(subtask, "/task");
        client.send(getRequest("/task?id=3"), handler);
        client.send(getRequest("/task?id=4"), handler);

        HttpResponse<String> history = client.send(getRequest("/history"), handler);

        assertEquals(200, history.statusCode());
    }

    @Test
    public void getPrioritizedTasks() throws IOException, InterruptedException {

        task.setStartTime(LocalDateTime.parse("21:00 - 07.03.2019", FORMATTER));
        task.setStartTime(LocalDateTime.parse("09:30 - 07.03.2019", FORMATTER));
        addTaskToServer(task, "/task");
        addTaskToServer(task, "/task");

        HttpResponse<String> prioritized = client.send(getRequest(""), handler);

        assertEquals(200, prioritized.statusCode());
    }
}
