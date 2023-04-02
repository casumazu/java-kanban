import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryManagerTest {
    TaskManager manager = Managers.getDefault();


    @BeforeEach
    void beforeEach() {
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);
        LocalDate date = LocalDate.of(2023, Month.MAY, 25);
        LocalTime time = LocalTime.of(12, 30);
        Epic epic = new Epic("Epic", "Описание 1", LocalDateTime.of(date, time));
        Epic epic2 = new Epic("Epic", "Описание 2", LocalDateTime.now());

        Subtask sub = new Subtask("sub1", "subbbs", 2);
        Subtask subtask = new Subtask("sub2", "subbbs2", 2, 30, LocalDateTime.now());
        Subtask subtask1 = new Subtask("Сабтаск3", "Описание сабтаска3", 3, 50, LocalDateTime.now());
        Subtask sub2 = new Subtask("sub3", "ОписаниеSub2", 2, 50, LocalDateTime.of(date, time));

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(sub);
        manager.addSubtask(sub2);

    }

    @Test
    void getHist() {
        List<Task> list = manager.getHistory();

        for (Task task : list) {
            assertNotNull(task, "Лист пустой");
        }
    }

    @Test
    void getTasks() {
        List<Task> tasks = manager.getAllTask();
        List<Epic> epic = manager.getAllEpics();
        List<Subtask> subtasks = manager.getAllSubtask();
        assertNotNull(tasks);
        assertNotNull(epic);
        assertNotNull(subtasks);
    }
    @Test
    void addTask() {
        Task task8123 = new Task("Название таска8123", "Описание 23");
        manager.addTask(task8123);

        Task task = manager.getTaskById(task8123.getId());

        assertNotNull(task, "Задача не создалась");


        Epic epic2356 = new Epic("Название epic2356", "Описание epic56");
        manager.addEpic(epic2356);

        Epic epic = manager.getEpicById(epic2356.getId());

        assertNotNull(epic, "Задача не создалась");
    }



}