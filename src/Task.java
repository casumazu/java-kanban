import java.util.ArrayList;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected String status;
    private ArrayList<Integer> tasks;

    public Task(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}