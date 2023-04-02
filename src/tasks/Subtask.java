package tasks;


import manager.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
        startTime = LocalDateTime.now();
    }

    public Subtask(Integer id, String title, String description, TaskStatus status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String title, String description, Integer epicId, int duration, LocalDateTime startTime){
        super(title, description, startTime, duration);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer epicId, int duration, LocalDateTime startTime, LocalDateTime endDate){
        super(id, name,description,status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
        this.duration = duration;
        this.startTime = startTime;
        endDate = getEndTime();
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    public LocalDateTime getEndTime(){
        return getStartTime().plusMinutes(duration);
    }

    @Override
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", id, taskType, title, status, description, epicId, duration, startTime, getEndTime());
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", taskType=" + taskType +
                '}';
    }
}