import manager.*;
import tasks.*;
import java.io.File;
public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));


        Task task = new Task("task1", "Описание Таска");
        Task task2 = new Task("task2", "Проверка 2.0");

        Epic epicasdasd = new Epic("епик проверка", "Время 3 утра....");

        Subtask subwaySurfers = new Subtask("Мы в щи", "Пора домой", 1);
        manager.addEpic(epicasdasd);
        manager.addSubtask(subwaySurfers);
        manager.addTask(task);
        manager.addTask(task2);
        manager.getEpicById(1);

        manager.getSubtasksByEpicId(1);




    }
}