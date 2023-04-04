import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    public void returnEmptyHistory() {
        assertEquals(Collections.emptyList(), manager.getHistory());
    }

    @Test
    public void createTask() {
        Task task = addTask();
        manager.addTask(task);
        List<Task> tasks = manager.getAllTask();
        assertNotNull(task.getStatus());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void createEpic() {
        Epic epic = addEpic();
        manager.addEpic(epic);
        List<Epic> epics = manager.getAllEpics();
        assertNotNull(epic.getStatus());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(Collections.emptyList(), epic.getSubtasks());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void createSubtask() {
        Epic epic = addEpic();
        manager.addEpic(epic);
        Subtask subtask = addSubtask(epic);
        manager.addSubtask(subtask);
        List<Subtask> subtasks = manager.getAllSubtask();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubtasks());
    }

    @Test
    public void statusToInProgress() {
        Task task = addTask();
        manager.addTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(task.getId()).getStatus());


        Epic epic = addEpic();
        manager.addEpic(epic);
        epic.setStatus(TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());


        Subtask subtask = addSubtask(epic);
        manager.addSubtask(subtask);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
    }


    @Test
    public void statusToInDone() {
        Task task = addTask();
        manager.addTask(task);
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);
        assertEquals(TaskStatus.DONE, manager.getTaskById(task.getId()).getStatus());

        Epic epic = addEpic();
        manager.addEpic(epic);
        epic.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, manager.getEpicById(epic.getId()).getStatus());

        Epic epic1 = addEpic();
        manager.addEpic(epic1);
        Subtask subtask = addSubtask(epic1);
        manager.addSubtask(subtask);
        subtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask);
        assertEquals(TaskStatus.DONE, manager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    public void deleteAllTasksAndSubtasks() {
        Task task = addTask();
        manager.addTask(task);
        manager.removeAllTasks();
        assertEquals(Collections.emptyList(), manager.getAllTask());

        Epic epic = addEpic();
        manager.addEpic(epic);
        manager.removeAllEpic();
        assertEquals(Collections.emptyList(), manager.getAllEpics());

        Epic epic1 = addEpic();
        manager.addEpic(epic1);
        Subtask subtask = addSubtask(epic1);
        manager.addSubtask(subtask);
        manager.removeAllSubtask();
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void deleteById(){
        Task task = addTask();
        manager.addTask(task);
        manager.removeTaskById(task.getId());
        assertEquals(Collections.emptyList(), manager.getAllTask());

        Epic epic = addEpic();
        manager.addEpic(epic);
        manager.removeEpicById(epic.getId());
        assertEquals(Collections.emptyList(), manager.getAllEpics());

        Epic epic1 = addEpic();
        manager.addEpic(epic1);
        Subtask subtask = addSubtask(epic1);
        manager.addSubtask(subtask);
        for(Subtask sub: manager.getAllSubtask()){
            manager.removeSubtaskById(sub.getId());
        }
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    public void emptyListSubtasksNoSubtasks() {
        assertTrue(manager.getAllTask().isEmpty());
        assertTrue(manager.getAllEpics().isEmpty());
        assertTrue(manager.getAllSubtask().isEmpty());
    }



    @Test
    public void emptyHistory() {
        assertEquals(Collections.emptyList(), manager.getHistory());
    }


}
