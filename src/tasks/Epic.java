package tasks;

import manager.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {


    private final ArrayList<Integer> subtasks;

    public List<Subtask> getSubtaskList() {
        return subtaskList;
    }

    private final List<Subtask> subtaskList;

    public Epic(String title, String description) {
        super(title, description);
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.status = TaskStatus.NEW;
        startTime = LocalDateTime.now();
        subtaskList = new ArrayList<>();
    }
    public Epic(int id, String title, TaskStatus status, String description) {
        super(id, title, description, null);
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.status = status;
        startTime = LocalDateTime.now();
        subtaskList = new ArrayList<>();
    }

    public Epic(String title, String description, LocalDateTime startTime){
        super(title, description, startTime);
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
        this.status = TaskStatus.NEW;
        subtaskList = new ArrayList<>();
    }


    public LocalDateTime getEndTime() {

        int totalDurationMinutes = 0;
        for (Subtask subtask :subtaskList) {
            totalDurationMinutes += subtask.getDuration();
        }
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
//        LocalDateTime dateTime = getStartTime().plusMinutes(totalDurationMinutes);
        return getStartTime().plusMinutes(totalDurationMinutes);
    }

    public int getDuration(){
        int totalDurationMinutes = 0;
        for (Subtask subtask :subtaskList) {
            totalDurationMinutes += subtask.getDuration();
        }
        return totalDurationMinutes;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
        subtaskList.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask.getId());
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + getDuration() +
                ", startTime=" + startTime +
                '}';
    }
}