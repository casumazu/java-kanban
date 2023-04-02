import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task addTask() {
        return new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
    }
    protected Epic addEpic() {

        return new Epic("Epic", "Описание 2", LocalDateTime.now());
    }
    protected Subtask addSubtask(Epic epic) {
        return new Subtask("sub2", "subbbs2", epic.getId(), 30, LocalDateTime.now());
    }


    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldCreateTask() {
        Task task = addTask();
        manager.addTask(task);
        List<Task> tasks = manager.getAllTask();
        assertNotNull(task.getStatus());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }
}
