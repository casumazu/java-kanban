package manager;


import server.HttpTaskManager;

public class Managers {

    public static TaskManager getDefault(String url, String key) {
        return new HttpTaskManager(url, key);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}