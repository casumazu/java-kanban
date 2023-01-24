import java.util.HashMap;

public class Manager{

    HashMap<String, String> task;
    HashMap<String, String> epic;
    HashMap<String, String> subtask;

    void addTask(String name, String description){
        task.put(name, description);
    }

    void addEpic(String name, String description){
        epic.put(name, description);
    }

    void addSubtask(String name, String description){
        subtask.put(name, description);

    }

    void printEpicSubtaskbyId(){

    }

}
