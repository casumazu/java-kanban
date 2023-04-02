import manager.*;
import tasks.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) {
        FileBackedTasksManager manager = FileBackedTasksManager.loadFromFile(new File(("files/data.csv")));

        LocalDate date = LocalDate.of(2023, Month.MAY,25);
        LocalTime time = LocalTime.of(12,30);

        Epic epic = new Epic("Epic", "Описание епика1", LocalDateTime.of(date,time));
        Epic epic2 = new Epic("Epic", "Описание епика2", LocalDateTime.now());

        Subtask sub = new Subtask("sub1", "subbbs", 1);
        Subtask subtask = new Subtask("sub2", "subbbs2",2, 30, LocalDateTime.now());
        Subtask subtask1 = new Subtask("Сабтаск3", "Описание сабтаска3", 2, 50, LocalDateTime.now());
        Subtask sub2 = new Subtask("sub3", "ОписаниеSub2", 1, 50, LocalDateTime.of(date,time));

        manager.addEpic(epic);
        manager.addEpic(epic2);
        manager.addSubtask(subtask);
        manager.addSubtask(subtask1);
        manager.addSubtask(sub);
        manager.addSubtask(sub2);


        System.out.println(manager.getSubtasksByEpicId(2));
        System.out.println(epic2.getEndTime());
        System.out.println(subtask1.getEndTime());

    }
}