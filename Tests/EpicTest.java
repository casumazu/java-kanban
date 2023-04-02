package tasks;

import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager manager = new InMemoryTaskManager();


    @BeforeEach
    void beforeEach(){
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        LocalDate date = LocalDate.of(2023, Month.MAY,25);
        LocalTime time = LocalTime.of(12,30);
        Epic epic = new Epic("Epic", "Описание 1", LocalDateTime.of(date,time));
        Epic epic2 = new Epic("Epic", "Описание 2", LocalDateTime.now());

        Subtask sub = new Subtask("sub1", "subbbs", 2);
        Subtask subtask = new Subtask("sub2", "subbbs2",2, 30, LocalDateTime.now());
        Subtask subtask1 = new Subtask("Сабтаск3", "Описание сабтаска3", 3, 50, LocalDateTime.now());
        Subtask sub2 = new Subtask("sub3", "ОписаниеSub2", 2, 50, LocalDateTime.of(date,time));

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(sub);
        manager.addSubtask(sub2);

    }

    @Test
    void add() {
        final List<Task> history = manager.getHistory();
        assertNotNull(history, "История не пустая.");
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
        Task task = manager.getTaskById(1);
        TaskStatus savedTask = task.getStatus();
        assertNotNull(savedTask, "Статус не задан");
    }

    @Test
    void statusEpic(){
        Epic epic = manager.getEpicById(2);
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
    void removeEpicForSubtask(){
        manager.removeEpicById(3);
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(3);
        for (Subtask subtask: subtasks) {
            assertNotNull(subtask);
        }
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
        Task task = manager.getTaskById(1);
        task.setDuration(60);
        Assertions.assertEquals(task.getDuration(), 60);
    }

    @Test
    void getDurationEpic(){
        Epic epic = manager.getEpicById(2);
        int duration = 0;
        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(2);
        for (Subtask subtask: subtasks) {
            duration += subtask.getDuration();
        }
        Assertions.assertEquals(duration, epic.getDuration());
    }

    @Test
    void setDurationSubtask(){
        Task task = manager.getTaskById(1);
        task.setDuration(60);
        Assertions.assertEquals(task.getDuration(), 60);
    }


    @Test
    void getTitle(){
        Task task = manager.getTaskById(1);
        assertNotNull(task.getTitle(), "Нет названия");

        Epic epic = manager.getEpicById(2);
        assertNotNull(epic.getTitle(), "Нет названия");

        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(2);
        for (Subtask subtask: subtasks) {
            assertNotNull(subtask.getTitle(),"Нет названия");
        }
    }

    @Test
    void getDesc(){
        Task task = manager.getTaskById(1);
        assertNotNull(task.getDescription(), "Нет описания");

        Epic epic = manager.getEpicById(2);
        assertNotNull(epic.getDescription(), "Нет описания");

        ArrayList<Subtask> subtasks = manager.getSubtasksByEpicId(2);
        for (Subtask subtask: subtasks) {
            assertNotNull(subtask.getDescription(),"Нет описания");
        }
    }


    @Test
    void setStartTime(){
        Task task = manager.getTaskById(1);
        LocalDate date = LocalDate.of(2024, Month.MARCH,25);
        LocalTime time = LocalTime.of(12,30);

        Epic  epic = manager.getEpicById(2);

        task.setStartTime(LocalDateTime.of(date,time));
        epic.setStartTime(LocalDateTime.of(date,time));
        assertEquals(task.getStartTime(), LocalDateTime.parse("2024-03-25T12:30"));
        assertEquals(epic.getStartTime(), LocalDateTime.parse("2024-03-25T12:30"));
    }

    @Test
    void sumOnStartTimeAndDuration(){
        Task task = manager.getTaskById(1);


        LocalDate date = LocalDate.of(2024, Month.MARCH,25);
        LocalTime time = LocalTime.of(12,30);
        task.setStartTime(LocalDateTime.of(date,time));


        LocalDateTime sumTime = LocalDateTime.of(date,time).plusMinutes(task.getDuration());
        Assertions.assertEquals(sumTime,LocalDateTime.parse("2024-03-25T13:00") );

    }

    @Test
    void testId(){
        Task task = manager.getTaskById(1);
        assertEquals(task.getId(), 1);

        Epic epic = manager.getEpicById(2);
        assertEquals(epic.getId(), 2);

        task.setId(21);
        epic.setId(26);
        assertEquals(task.getId(), 21);
        assertEquals(epic.getId(), 26);
    }

    @Test
    void setStatus(){
        Task task = manager.getTaskById(1);
        task.setStatus(TaskStatus.DONE);

        Epic epic = manager.getEpicById(2);
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