package tasks;

import manager.TaskType;

import java.time.LocalDateTime;

public class Task {
    protected Integer id;
    protected String title;
    protected String description;
    protected TaskStatus status;
    protected int duration;
    protected LocalDateTime startTime;

    protected TaskType taskType;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
    }

    public Task(int id, String title, String description, TaskStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.taskType = TaskType.TASK;
    }

    public Task(String title, String description, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = TaskStatus.NEW;
    }
    public Task(String title, String description, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.status = TaskStatus.NEW;
    }


    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return this.duration;
    }
    public LocalDateTime getEndTime(){
        return getStartTime().plusMinutes(duration);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", taskType=" + taskType +
                '}';
    }

    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", id, taskType, title, status, description, " ", duration, startTime, getEndTime());
    }

    public TaskType getTaskType() {
        return taskType;
    }

}