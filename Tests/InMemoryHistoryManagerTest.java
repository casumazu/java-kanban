import manager.InMemoryHistoryManager;

import org.junit.jupiter.api.Test;

import tasks.Task;

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    InMemoryHistoryManager manager = new InMemoryHistoryManager();



    @Test
    public void taskToHistory(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        Task task1 = new Task("Test2", "Test TaskWork", LocalDateTime.now(), 30);
        task.setId(0);
        manager.add(task);
        task1.setId(1);
        manager.add(task1);
        assertEquals(List.of(task,task1), manager.getHistory());
    }

    @Test
    public void emptyList(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        Task task1 = new Task("Test2", "Test TaskWork", LocalDateTime.now(), 30);
        task.setId(0);
        manager.add(task);
        task1.setId(1);
        manager.add(task1);
        manager.remove(task.getId());
        manager.remove(task1.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void removeTask(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        Task task1 = new Task("Test2", "Test TaskWork", LocalDateTime.now(), 30);
        task.setId(0);
        manager.add(task);
        task1.setId(1);
        manager.add(task1);

        manager.remove(task1.getId());
        assertEquals(List.of(task), manager.getHistory());
    }

    @Test
    public void notRemoveTask(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        task.setId(0);
        manager.add(task);

        manager.remove(0);
        assertNotEquals(List.of(task), manager.getHistory());
    }

    @Test
    public void getHistoryEqualsGetTasks(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        Task task1 = new Task("Test2", "Test TaskWork", LocalDateTime.now(), 30);
        task.setId(0);
        manager.add(task);
        task1.setId(1);
        manager.add(task1);

        assertEquals(manager.getTasks(), manager.getHistory());
    }

    @Test
    public void isEmpty(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        Task task1 = new Task("Test2", "Test TaskWork", LocalDateTime.now(), 30);
        task.setId(0);
        manager.add(task);
        task1.setId(1);
        manager.add(task1);

        assertFalse(manager.getHistory().isEmpty());

        manager.remove(0);
        manager.remove(1);

        assertTrue(manager.getHistory().isEmpty());
    }
}
