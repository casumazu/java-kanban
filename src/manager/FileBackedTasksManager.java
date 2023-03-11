package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;
    private String fileName;

    private HashMap<Integer, String> allTasks = new HashMap<>();


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
                int epicId = Integer.parseInt(epicIdString);
                return new Subtask(id, name, description, status, fileBackedTasksManager.epics.get(epicId).getId());
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
                String[] lineData = line.split(",");
                if (lineData.length < 2) {
                    continue;
                }
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
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Файл не найден.", e);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла - " + file.getName());
        }

        return manager;
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {

            List<Task> history = new ArrayList<>(super.getHistory());
            List<Task> allTaskList = new ArrayList<>();

            writer.write("id,type,name,status,description,epic\n");

            allTaskList.addAll(super.getAllTask());
            allTaskList.addAll(super.getAllEpics());
            allTaskList.addAll(super.getAllSubtask());

            allTaskList.sort(Comparator.comparing(Task::getId));

            for (Task task : allTaskList) {
                writer.write(String.format("%s\n", task.toStringFromFile()));
            }
            for (String value : allTasks.values()) {
                writer.write(String.format("%s\n", value));
            }
            writer.write("\n");

            history.sort(Comparator.comparing(Task::getId));

            for (Task value : history) {
                writer.write(String.format("%s\n", value.toStringFromFile()));
            }
            writer.write("\n");

        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка записи файла %s.", file.getName()));
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
