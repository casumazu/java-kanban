package manager;

import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }


    // Получение ЗАДАЧ / ЕПИКОВ / САБТАСКОВ
    @Override
    public ArrayList<Task> getAllTask() {
        ArrayList<Task> tasksss = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksss.add(task);
        }
        return tasksss;
    }
    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> arEpic = new ArrayList<>();
        arEpic.addAll(epics.values());
        return arEpic;
    }
    @Override
    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> arSub = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            arSub.add(subtask);
        }
        return arSub;
    }

    // ---------------------------------------------------//
    /* TASK */
    @Override
    public Task addTask(Task task) {      // Создание Задачи
        task.setId(++id);
        tasks.put(task.getId(), task);

        return task;
    }
    @Override
    public Task getTaskById(Integer getId) {
        Task task = tasks.get(getId);
        return task;
    }
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }
    @Override
    public void removeTaskById(Integer id) {
        tasks.remove(id);
    }


// ---------------------------------------------------//
    /* ЭПИКИ */
    @Override
    public Epic addEpic(Epic epic) {                 // создание Епика
        epic.setId(++id);
        epics.put(epic.getId(), epic);

        return epic;
    }
    @Override
    public Epic getEpicById(Integer getId) {
        Epic epic = epics.get(getId);
        historyManager.add(epic);
        return epic;
    }
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }
    @Override
    public void removeEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(epicId);
    }

    @Override
    public void updateEpicStatus(Integer epicId) {
        int newCounter = 0;
        int doneCounter = 0;
        Epic epic = epics.get(epicId);
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            System.out.println("Нет подзадач, статус остаётся прежним.");
        }
        for (Integer subId : epic.getSubtasks()) {
            if (subtasks.get(subId).getStatus() == TaskStatus.NEW) {
                newCounter += 1;
            }
            if (subtasks.get(subId).getStatus() == TaskStatus.DONE) {
                newCounter += 1;
            }
        }

        if (epic.getSubtasks().size() == newCounter) {
            epic.setStatus(TaskStatus.NEW);
        }
        if (epic.getSubtasks().size() == doneCounter) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }


// ---------------------------------------------------//
    /* SUBTASK */
    @Override
    public Subtask addSubtask(Subtask subtask) {         // создание Сабтаска
        Epic epic = epics.get(subtask.getEpicId());

        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(subtask.getEpicId());

        return subtask;
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }
    @Override
    public void removeSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtask(subtask);
        subtasks.remove(subtaskId);
        updateEpicStatus(subtask.getEpicId());
    }
    @Override
    public Subtask getSubtasksByEpicId(Integer epicId) {
        Subtask subtask = subtasks.get(epicId);
        historyManager.add(subtask);
//        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
//        Epic epic = epics.get(epicId);
//
//        for (Integer subtaskId : epic.getSubtasks()) {
//            subtasksOfEpic.add(subtasks.get(subtaskId));
//        }
          return subtask;
    }

// ---------------------------------------------------
    /* Удаление всех задач */
    @Override
    public void removeAllTasks() {
        tasks.clear();
    }
    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }
    @Override
    public void removeAllSubtask() {
        subtasks.clear();
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}

