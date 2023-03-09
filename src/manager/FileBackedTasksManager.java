package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private File file;
    private String fileName;

    public FileBackedTasksManager(File file) {
        this.file = file;

        fileName = "./files/data.csv";
        file = new File(fileName);
        if (!file.isFile()) {
            try {
                Files.createFile(Paths.get(fileName));
            } catch (IOException e) {
                throw new ManagerSaveException("Ошибка создания файла.");
            }
        }
    }


//    private static Task fromString(String value) {
//        String[] parts = value.split(",");
//        int id = Integer.parseInt(parts[0]);
//        TaskType type = TaskType.valueOf(parts[1]);
//        String name = parts[2];
//        TaskStatus status = TaskStatus.valueOf(parts[3]);
//        String description = parts[4];
//        int epicId = Integer.parseInt(parts[5]);
//
//        if (type == TaskType.TASK) {
//            return new Task(id, name, status, description, epicId);
//        } else if (type == TaskType.EPIC) {
//            return new Epic(id, name, status, description);
//        } else {
//            return new Subtask(id, name, status, description, epicId);
//        }
//    }
    private static String toString(HistoryManager manager) {
        List<String> str = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            str.add(String.valueOf(task.getId()));
        }
        return String.join(",", str);
    }
    private String toString(Task task) {
        return task.getId() + "," +
                task.getId() + "," +
                task.getTaskType() + "," +
                task.getStatus() + "," +
                task.getDescription() + "," +
                task.getTitle();
    }

    /*
    private void save() {
        try (Writer writer = new FileWriter(new File(fileName))) {

            writer.write("id,type,name,status,description,epic\n");

            for (Epic epic : getAllEpics()) {
                writer.write(epic.getId() + ",");
                writer.write(epic.getTaskType() + ",");
                writer.write(epic.getTitle() + ",");
                writer.write(epic.getStatus() + ",");
                writer.write(epic.getDescription() + "," + "\n");
            }

            for (Task task : getAllTask()) {
                writer.write(task.getId() + ",");
                writer.write(task.getTaskType() + ",");
                writer.write(task.getTitle() + ",");
                writer.write(task.getStatus() + ",");
                writer.write(task.getDescription() + "," + "\n");
                for (Subtask subtask : getAllSubtask()) {
                    writer.write(subtask.getId() + ",");
                    writer.write(subtask.getTaskType() + ",");
                    writer.write(subtask.getTitle() + ",");
                    writer.write(subtask.getStatus() + ",");
                    writer.write(subtask.getDescription() + ",");
                    writer.write(subtask.getEpicId() + "," + "\n");

                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка save");
        }
    }
    */
    private static Task fromString(String str, TaskType taskType, FileBackedTasksManager fileBackedTasksManager) {
        String[] dataOfTask = str.split(",", 6);
        int id = Integer.parseInt(dataOfTask[0]);
        String name = dataOfTask[2];
        TaskStatus status = TaskStatus.valueOf(dataOfTask[3]);
        String description = dataOfTask[4];
        String epicIdString = dataOfTask[5].trim();

        switch (taskType) {
            case TASK -> {
                return new Task(id, name, description, status);
            }
            case EPIC -> {
                return new Epic(id, name, status, description);
            }
            case SUBTASK -> {
                if (epicIdString.isEmpty()) {
                    return null;
                }
                return new Subtask(id, name, description, status,
                        fileBackedTasksManager.epics.get(Integer.valueOf(epicIdString)).getId());
            }
            default -> {
                return null;
            }
        }
    }
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                TaskType taskType = TaskType.valueOf(line.split(",")[1].toUpperCase());
                Task task = fromString(line, taskType, manager);
                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    if (task != null) {
                        manager.addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла.");
        }

        return manager;
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            HashMap<Integer, String> allTasks = new HashMap<>();
            for (Task task : super.getAllTask()) {
                allTasks.put(task.getId(), task.toStringFromFile());
                for (Epic epic : super.getAllEpics()) {
                    allTasks.put(epic.getId(), epic.toStringFromFile());
                } for (Subtask subtask : getAllSubtask()){
                    allTasks.put(subtask.getId(), subtask.toStringFromFile());
                }
            }
            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }
            writer.write("\n");

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи файла.");
        }
    }



    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(Integer id) {
        ArrayList<Subtask> subtask = super.getSubtasksByEpicId(id);
        save();
        return subtask;
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }
}
