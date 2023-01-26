public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 = new Task("Таск1", "ОписаниеТаска");
        Epic epic1 = new Epic("Епик1", "Описание эпика");
        Epic epic2 = new Epic("Епик2", "Описание эпика");

        Subtask subtask = new Subtask("Сабтаск1", "Епика2", 2);

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask);

       System.out.println(epic2);
        System.out.println(manager.getSubtasksByEpicId(2));







    }
}