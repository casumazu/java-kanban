package tasks;

import manager.TaskType;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(int id, String title, String description) {
        super(id, title, description, null);
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(int id, String title, TaskStatus status, String description) {
        super(id, title, description, null);
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.status = status;

    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
    }


    @Override
    public String toString() {
        return "Epic{" +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status + ", " +
                "subtasks=" + subtasks +
                '}';
    }
}