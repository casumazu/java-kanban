import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> epicSubtasks;
    public Epic(String title, String description) {
        super(title, description, "");
        epicSubtasks = new ArrayList<>();
    }


    public void setEpicSubtasks(ArrayList<Integer> epicSubtasks) {
        this.epicSubtasks = epicSubtasks;
    }

    public ArrayList<Integer> getEpicSubtasks() {
        return epicSubtasks;
    }
}

