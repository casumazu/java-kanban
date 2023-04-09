package server;

import adapter.LocalDateTimeAdapter;
import adapter.SubtaskAdapter;
import adapter.TaskAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HTTPTaskManager extends FileBackedTasksManager {

    protected final String uri;
    private final KVTaskClient kvTaskClient;

    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Task.class, new TaskAdapter())
            .registerTypeAdapter(Subtask.class, new SubtaskAdapter())
            .create();

    public HTTPTaskManager(String uri) {
        kvTaskClient = new KVTaskClient(uri);

            load();

        this.uri = uri;
    }


    private void load() {
        ArrayList<Task> tasksJson = gson.fromJson(kvTaskClient.load("tasks"),
                new TypeToken<ArrayList<Task>>() {
                }.getType());
        ArrayList<Epic> epicJson = gson.fromJson(kvTaskClient.load("epics"),
                new TypeToken<ArrayList<Epic>>() {
                }.getType());
        ArrayList<Subtask> subtaskJson = gson.fromJson(kvTaskClient.load("subtasks"),
                new TypeToken<ArrayList<Subtask>>() {
                }.getType());
        ArrayList<Integer> historyJson = gson.fromJson(kvTaskClient.load("history"),
                new TypeToken<ArrayList<Integer>>() {
                }.getType());
        if (tasksJson != null) {
            for (Task task : tasksJson) {
                int idTask = Integer.parseInt(String.valueOf(task.getId()));
                if (idTask > id) {
                    id = idTask;
                }
                tasks.put(idTask, task);
                prioritizedTasks.add(task);
            }
        }
        if (epicJson != null) {
            for (Epic e : epicJson) {
                int idTask = Integer.parseInt(String.valueOf(e.getId()));
                if (idTask > id) {
                    id = idTask;
                }
                epics.put(idTask, e);
            }
        }
        if (subtaskJson != null) {
            for (Subtask subtask : subtaskJson) {
                int idTask = Integer.parseInt(String.valueOf(subtask.getId()));
                if (idTask > id) {
                    id = idTask;
                }
                subtasks.put(idTask, subtask);
                prioritizedTasks.add(subtask);
            }
        }
        if (historyJson != null) {
            for (Integer i : historyJson) {
                if (tasks.containsKey(i)) {
                    historyManager.add(tasks.get(i));
                } else if (epics.containsKey(i)) {
                    historyManager.add(epics.get(i));
                } else {
                    historyManager.add(subtasks.get(i));
                }
            }
        }
    }
}
