package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {


    int compare(Task o1, Task o2);

    // Получение ЗАДАЧ / ЕПИКОВ / САБТАСКОВ
    public ArrayList<Task> getAllTask();

    public ArrayList<Epic> getAllEpics();

    public ArrayList<Subtask> getAllSubtask();

    // ---------------------------------------------------//
    /* TASK */
    public void addTask(Task task);

    public Task getTaskById(Integer getId);

    public void updateTask(Task task);

    public void removeTaskById(Integer id);

// ---------------------------------------------------//
    /* ЭПИКИ */

    public void addEpic(Epic epic);

    public Epic getEpicById(Integer getId);

    public void updateEpic(Epic epic);

    public void removeEpicById(Integer epicId);


    public void updateEpicStatus(Integer epicId);


// ---------------------------------------------------//
    /* SUBTASK */

    public void addSubtask(Subtask subtask);

    public void updateSubtask(Subtask subtask);

    public void removeSubtaskById(Integer subtaskId);

    public ArrayList<Subtask> getSubtasksByEpicId(Integer epicId);

// ---------------------------------------------------
    /* Удаление всех задач */

    public void removeAllTasks();

    public void removeAllEpic();

    public void removeAllSubtask();

    // ----------------------------------------------------
    public List<Task> getHistory();

    public void remove(int id);
}
