import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
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
        manager.updateEpic(epic);
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
        assertEquals(TaskStatus.DONE, subtask.getStatus());
    }

    @Test
    public void deleteAllTasksAndSubtasks() {
        testDeleteAll(Task.class);
        assertEquals(Collections.emptyList(), manager.getAllTask());

        testDeleteAll(Epic.class);
        assertEquals(Collections.emptyList(), manager.getAllEpics());

        testDeleteAll(Subtask.class);
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    private void testDeleteAll(Class<?> taskClass) {
        if (taskClass.equals(Task.class)) {
            Task task = addTask();
            manager.addTask(task);
            manager.removeTaskById(task.getId());
        } else if(taskClass.equals(Epic.class)){
            Epic epic = addEpic();
            manager.addEpic(epic);
            manager.removeEpicById(epic.getId());
            assertEquals(Collections.emptyList(), manager.getAllEpics());
        } else if(taskClass.equals(Subtask.class)){
            Task task = addTask();
            manager.addTask(task);
            manager.removeTaskById(task.getId());
        }
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

    @Test
    void addNewTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void statusTask(){
        Task task = addTask();
        TaskStatus savedTask = task.getStatus();
        assertNotNull(savedTask, "Статус не задан");
    }

    @Test
    void statusEpic(){
        Epic epic = addEpic();
        TaskStatus savedTask = epic.getStatus();
        assertNotNull(savedTask, "Статус не задан");
    }

    @Test
    void removeTask(){
        manager.removeTaskById(1);
        Task task = manager.getTaskById(1);
        assertNull(task, "Задача не удалена");
    }

    @Test
    void durationSub(){
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(3);
        for (Subtask subtask: subtasks) {
            assertNotNull(subtask.getDuration(), "Задано время");
        }
    }

    @Test
    void startTimeSub(){
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(3);
        for (Subtask subtask: subtasks) {
            assertNotNull(subtask.getStartTime());
        }
    }

    @Test
    void getEndTimeSub(){
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(3);
        for (Subtask subtask: subtasks) {
            assertNotNull(subtask.getEndTime());
        }
    }

    @Test
    void setDurationTask(){
        Task task = addTask();
        task.setDuration(60);
        Assertions.assertEquals(task.getDuration(), 60);
    }

    @Test
    void getDurationEpic(){
        Epic epic = addEpic();
        manager.addEpic(epic);
        epic.setId(1);
        Subtask subtask = addSubtask(epic);
        manager.addSubtask(subtask);
        int duration = 0;
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(1);
        for (Subtask sub: subtasks) {
            duration += sub.getDuration();
        }
        Assertions.assertEquals(duration, epic.getDuration());
    }

    @Test
    void setDurationSubtask(){
        Task task = addTask();
        task.setDuration(60);
        Assertions.assertEquals(task.getDuration(), 60);
    }

    @Test
    void getTitle(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        assertNotNull(task.getTitle(), "Нет названия");

        Epic epic = addEpic();
        manager.addEpic(epic);
        assertNotNull(epic.getTitle(), "Нет названия");

        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(0);
        for (Subtask subtask: subtasks) {
            assertNotNull(subtask.getTitle(),"Нет названия");
        }
    }

    @Test
    void getDesc(){
        Task task = addTask();
        assertNotNull(task.getDescription(), "Нет описания");

        Epic epic = addEpic();
        manager.addEpic(epic);
        assertNotNull(epic.getDescription(), "Нет описания");

        Subtask subtask = addSubtask(epic);
        manager.addSubtask(subtask);
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(0);
        for (Subtask sub: subtasks) {
            assertNotNull(sub.getDescription(),"Нет описания");
        }
    }

    @Test
    void setStatus(){
        Task task = addTask();
        manager.addTask(task);
        task.setStatus(TaskStatus.DONE);

        Epic epic = addEpic();
        manager.addEpic(epic);
        epic.setStatus(TaskStatus.DONE);

        assertEquals(task.getStatus(), TaskStatus.DONE);
        assertEquals(epic.getStatus(), TaskStatus.DONE);
    }

    @Test
    void getEpicId(){
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(2);
        for (Subtask subtask: subtasks) {
            assertEquals(subtask.getEpicId(),2);
        }
    }
}
