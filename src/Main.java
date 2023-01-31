import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.*;
public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

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
        manager.getEpicById(1);
        manager.getEpicById(2);
        manager.getSubtasksByEpicId(1);
        System.out.println(manager.getHistory());


    }
}