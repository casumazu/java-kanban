import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;

import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) throws IOException {

        new KVServer().start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078", "key");


//
//
//        Task task = new Task("task", "taskOne");
//        manager.addTask(task);
//        Task task1 = new Task("task2", "taskTwo");
//        manager.addTask(task1);
//        Task task2 = new Task("task3", "taskTree");
//        manager.addTask(task2);
//        LocalDate date = LocalDate.of(2023, Month.MAY,25);
//        LocalTime time = LocalTime.of(12,30);
//
//        Epic epic = new Epic("epic", "epic", LocalDateTime.now());
//        manager.addEpic(epic);
//
//        Subtask subtask = new Subtask("sub1", "sub", epic.getId(),30, LocalDateTime.now());
//        manager.addSubtask(subtask);
//
//        System.out.println("Задачи ->");
//        System.out.println(manager.getAllTask());
//        System.out.println("Епики ->");
//        System.out.println(manager.getAllEpics());
//        System.out.println("Subtask ->");
//        System.out.println(manager.getAllSubtask());
//        System.out.println("History ->");
//        System.out.println(manager.getHistory());
//        System.out.println("Приоритетные задачи ->");
//        System.out.println(manager.getPrioritizedTasks());

    }
}