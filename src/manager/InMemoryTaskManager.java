package manager;

import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id;

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager;
    private final Comparator<Task> comparing = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(comparing);


    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }


    private void addNewPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        checkIntersections();
    }


    private void checkIntersections() {
        List<Task> tasks = getPrioritizedTasks();

        for (int i = 1; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            boolean intersection = task.getStartTime().isBefore(tasks.get(i - 1).getEndTime());

            if (intersection) {
                throw new ManagerSaveException("Обнаружено пересечение между: " + task.getId() + " и " + tasks.get(i - 1) + ".");
            }
        }
    }

    private List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }


    // Получение Task / Epic / Subtask
    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(subtasks.values());
    }

    // ---------------------------------------------------//
    /* TASK */
    @Override
    public void addTask(Task task) {
        task.setId(++id);
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTaskById(Integer getId) {
        return tasks.get(getId);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTaskById(Integer id) {
        tasks.remove(id);
        prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), id));
    }


    // ---------------------------------------------------//
    /* EPIC */
    @Override
    public void addEpic(Epic epic) {                 // Добавление Епика
        epic.setId(++id);
        epics.put(epic.getId(), epic);

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
            prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
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
    public void addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        subtask.setId(++id);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(subtask.getEpicId());

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
        prioritizedTasks.remove(subtask);
        epic.removeSubtask(subtask);
        subtasks.remove(subtaskId);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(Integer epicId) {

        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            // выбросить исключение или вернуть пустой список
            return subtasksOfEpic;
        }
        for (Integer subtaskId : epic.getSubtasks()) {
            subtasksOfEpic.add(subtasks.get(subtaskId));
            historyManager.add(subtasks.get(subtaskId));
        }

        return subtasksOfEpic;
    }

    // ---------------------------------------------------
    /* Удаление Task / Epic / Subtask */
    @Override
    public void removeAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            for (int subtaskId : epic.getSubtasks()) {
                Subtask subtask = subtasks.get(subtaskId);
                prioritizedTasks.remove(subtask);
            }
        }

    }

    // Получение истории
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

}

