import manager.TaskManager;
import tasks.*;
public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Epic epic1 = new Epic("Епик1", "Описание эпика");
        Subtask subtask = new Subtask("Сабтаск1", "Епика1", 1);
        Subtask subtask1 = new Subtask("Сабтаск2", "Епика1", 1);

        Epic epic2 = new Epic("Епик2", "Описание эпика");


        Task task1 = new Task("Таск1", "ОписаниеТаска");
        Task task2 = new Task("Таск2", "ОписаниеТаска2");

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addTask(task1);
        manager.addTask(task2);

        manager.updateEpicStatus(1);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubtask());

        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);


        System.out.println(manager.getAllEpics());


        manager.removeTaskById(3);
        System.out.println(manager.getEpicById(1));
        System.out.println(manager.getTaskById(3));
        System.out.println(manager.getAllSubtask());




    }
}