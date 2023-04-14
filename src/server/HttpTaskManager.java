package server;

import adapter.FileAdapter;
import adapter.HistoryManagerAdapter;
import adapter.LocalDateTimeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import manager.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;


import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class HttpTaskManager extends FileBackedTasksManager {

    public static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(File.class, new FileAdapter())
            .registerTypeAdapter(HistoryManager.class, new HistoryManagerAdapter())
            .serializeNulls().create();
    private final String key;
    private final String url;

    public HttpTaskManager(String url, String key) {
        this.url = url;
        this.key = key;
    }

    @Override
    public void save() {
        String manager = gson.toJson(this);
        new KVTaskClient(url).put(key, manager);
    }

    public static HttpTaskManager load(String url, String key) {
        String json = new KVTaskClient(url).load(key);
        if (json.isEmpty()) {
            return new HttpTaskManager(url, key);
        }
        return gson.fromJson(json, HttpTaskManager.class);
    }
}
