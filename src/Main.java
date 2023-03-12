import manager.*;
import tasks.*;
import java.io.File;
public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));

        Epic epic = new Epic("Epic", "Описание епика1");
        Epic epic2 = new Epic("Epic", "Описание епика2");

        Subtask sub = new Subtask("sub1", "subbbs", 1);

        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addSubtask(sub);

        manager.getSubtasksByEpicId(1);
        manager.getEpicById(1);
    }
}