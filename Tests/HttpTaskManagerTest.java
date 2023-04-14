import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskManager;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;
    HttpTaskManager manager = new HttpTaskManager("http://localhost:8078", "key");

    @BeforeEach
    public void startServer() throws IOException {
        kvServer = new KVServer();

        kvServer.start();
        Task task = new Task("task", "taskOne");
        manager.addTask(task);
        Task task1 = new Task("task2", "taskTwo");
        manager.addTask(task1);
        Task task2 = new Task("task3", "taskTree");
        manager.addTask(task2);

        Epic epic = new Epic("epic", "epic", LocalDateTime.now());
        manager.addEpic(epic);

        Subtask subtask = new Subtask("sub1", "sub", epic.getId(),30, LocalDateTime.now());
        manager.addSubtask(subtask);
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void sizeTasks() {

        assertEquals(3, manager.getAllTask().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtask().size());
        assertEquals(0, manager.getHistory().size());
    }

    @Test
    public void removeTasks() {
        manager.removeAllTasks();
        manager.removeAllEpic();
        manager.removeAllSubtask();

        assertEquals(0, manager.getAllTask().size());
        assertEquals(0, manager.getAllEpics().size());
        assertEquals(0, manager.getAllSubtask().size());
        assertEquals(0, manager.getHistory().size());

        assertNotNull(manager);
    }

    @Test
    public void sizeManagerEpicAndHistory() {
        manager.getEpicById(4);

        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getHistory().size());
        assertEquals(1, manager.getEpicById(4).getSubtasks().size());
    }


}