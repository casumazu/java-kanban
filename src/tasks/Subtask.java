package tasks;


import manager.TaskType;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(Integer id, String title, String description, TaskStatus status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }
    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "subtask{" +
                "№=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", id, taskType, title, status, description, epicId);
    }

}