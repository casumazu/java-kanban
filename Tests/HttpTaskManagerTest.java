import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskManager;
import server.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    protected KVServer kvServer;

    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = new HttpTaskManager("http://localhost:8078", "key");
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void sizeTasks() {

        manager.addTask(new Task("task", "task"));

        manager.addEpic(new Epic("epic","epic"));
        manager.addEpic(new Epic("epic2","epic2"));

        manager.addSubtask(new Subtask("sub", "subtask", 2));

        assertEquals(1, manager.getAllTask().size());
        assertEquals(2, manager.getAllEpics().size());
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
        manager.addEpic(new Epic("epic","epic"));
        manager.addSubtask(new Subtask("sub", "subtask", 1));
        manager.getEpicById(1);

        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getHistory().size());
        assertEquals(1, manager.getEpicById(1).getSubtasks().size());
    }


}