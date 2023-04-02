import manager.FileBackedTasksManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class FileBackedTasksManagerTest {

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(Path.of("files/data.csv"));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void saveAndLoadEmptyTasks() {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));
        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask());
    }
    @Test
    public void CorrectlySaveAndLoad() {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));
        Task task = new Task("Test1", "Test TaskWork", LocalDateTime.now(), 30);

        manager.addTask(task);
        Epic epic2 = new Epic("Epic", "Описание 2", LocalDateTime.now());
        manager.addEpic(epic2);

        assertEquals(List.of(task), manager.getAllTask());
        assertEquals(List.of(epic2), manager.getAllEpics());
    }

    @Test
    public void SaveAndLoadEmptyTask() {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));

        assertEquals(Collections.EMPTY_LIST, manager.getAllTask());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubtask());
    }
    @Test
    public void LoadHistoryEmptyHistory() {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}
