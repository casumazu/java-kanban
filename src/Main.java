import manager.*;
import tasks.*;
import java.io.File;
public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));

        Epic epic256 = new Epic("Епик1", "Описание эпика");
        Epic epic2526 = new Epic("Епик1", "Описание эпика");
        Subtask subtask = new Subtask("Сабтаск1", "Епика1", 1);
        Subtask subtask1 = new Subtask("Сабтаск2", "Епика1", 1);
        Subtask subtask2 = new Subtask("Сабтаск3", "Епика1", 1);

        Epic epic2 = new Epic("Епик2", "Описание эпика");


        Task task1 = new Task("Таск1", "ОписаниеТаска");
        Task task2 = new Task("Таск2", "ОписаниеТаска2");

        manager.addEpic(epic256);
        manager.addEpic(epic2526);
        manager.addEpic(epic2);
        manager.addTask(task1);
        manager.addSubtask(subtask);
        manager.addTask(task2);


        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        System.out.println(manager.getEpicById(1));
        System.out.println(manager.getEpicById(2));
        System.out.println(manager.getTaskById(4));
    }
}