import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        id = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();

    }

    public int getNextId() {
        return ++id;
    }

    // Получение ЗАДАЧ / ЕПИКОВ / САБТАСКОВ
    public HashMap<Integer, Task> getAllTask() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubtask() {
        return subtasks;
    }
// ---------------------------------------------------//
            /* TASK */
    public Task addTask(Task task) {      // Создание Задачи
        task.setId(getNextId());
        tasks.put(task.getId(), task);

        return task;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void removeTaskById(Integer id) {
        tasks.remove(id);
    }


// ---------------------------------------------------//
                 /* ЭПИКИ */

    public Epic addEpic(Epic epic) {                 // создание Епика
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);

        return epic;
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void removeEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
        }

        epics.remove(epicId);
    }


    public void updateEpicStatus(Integer epicId) {
        int newCounter = 0;
        int doneCounter = 0;
        Epic epic = epics.get(epicId);
        if (epic.getSubtasks().isEmpty()){
            epic.setStatus(TaskStatus.NEW);
        }
        for(Integer subId: epic.getSubtasks()) {
            if (subtasks.get(subId).getStatus() == TaskStatus.NEW) {
                newCounter += 1;
            }
            if (subtasks.get(subId).getStatus() == TaskStatus.DONE) {
                newCounter += 1;
            }
        }

            if(epic.getSubtasks().size() == newCounter){
                epic.setStatus(TaskStatus.NEW);
            }
            if (epic.getSubtasks().size() == doneCounter){
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
    }


// ---------------------------------------------------//
                   /* SUBTASK */

        public Subtask addSubtask (Subtask subtask){         // создание Сабтаска
            Epic epic = epics.get(subtask.getEpicId());

            subtask.setId(getNextId());
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtask(subtask);
            updateEpicStatus(subtask.getEpicId());

            return subtask;
        }

        public void updateSubtask (Subtask subtask){
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }

    public void removeSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());

        epic.removeSubtask(subtask);
        subtasks.remove(subtaskId);
        updateEpicStatus(subtask.getEpicId());
    }

    public ArrayList<Subtask> getSubtasksByEpicId(Integer epicId) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);

        for (Integer subtaskId : epic.getSubtasks()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
        }

        return subtasksOfEpic;
    }

// ---------------------------------------------------
        /* Удаление всех задач */

    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

}
