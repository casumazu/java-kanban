import manager.InMemoryHistoryManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tasks.Task;

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    InMemoryHistoryManager manager;
    Task task;
    Task task1;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryHistoryManager();
        task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        task1 = new Task("Test2", "Test TaskWork", LocalDateTime.now(), 30);
        task.setId(0);
        task1.setId(1);
    }

    @DisplayName("addTaskToHistory")
    @Test
    public void taskToHistory(){
        manager.add(task);
        manager.add(task1);
        assertEquals(List.of(task,task1), manager.getHistory());
    }

    @Test
    public void emptyList(){
        manager.add(task);
        manager.add(task1);
        manager.remove(task.getId());
        manager.remove(task1.getId());
        assertEquals(Collections.emptyList(), manager.getHistory());
    }

    @Test
    public void removeTask(){
        manager.add(task);
        manager.add(task1);
        manager.remove(task1.getId());
        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    public void notRemoveTask(){
        manager.add(task);
        manager.remove(0);
        assertEquals(Collections.emptyList(), manager.getHistory());
    }

    @Test
    public void getHistoryEqualsGetTasks(){
        manager.add(task);
        manager.add(task1);
        assertEquals(manager.getTasks(), manager.getHistory());
    }

    @Test
    public void isEmpty(){
        manager.add(task);
        manager.add(task1);
        assertFalse(manager.getHistory().isEmpty());
        manager.remove(0);
        manager.remove(1);
        assertTrue(manager.getHistory().isEmpty());
    }
}
