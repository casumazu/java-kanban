package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    protected HashMap<Integer, Node<Task>> mapTaskHistory;

    public InMemoryHistoryManager() {
        this.mapTaskHistory = new HashMap<>();
    }

    private Node<Task> head;

    private Node<Task> tail;

    private int size = 0;


    @Override
    public void add(Task task) {
        if (mapTaskHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
            linkLast(task);

    }

    // Добавление задачи в начало списка
    public void linkFirst(Task element) {
        final Node<Task> oldHead = head;
        final Node<Task> newNode = new Node<>(null, element, oldHead);
        head = newNode;
        mapTaskHistory.put(element.getId(), newNode);
        if (oldHead == null) tail = newNode;
        else oldHead.prev = newNode;
        size++;
    }


    // Добавление задачи в конец списка
    public void linkLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        mapTaskHistory.put(element.getId(), newNode);
        if (oldTail == null) head = newNode;
        else oldTail.next = newNode;
        size++;
    }

    public Task getFirst() {
        final Node<Task> curHead = head;
        if (curHead == null) throw new NoSuchElementException();
        return head.data;
    }

    public Task getLast() {
        final Node<Task> curHead = tail;
        if (curHead == null) throw new NoSuchElementException();
        return tail.data;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void removeNode(Node<Task> taskNode) {
        if (taskNode != null) {
            final Node<Task> next = taskNode.next;
            final Node<Task> prev = taskNode.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                taskNode.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                taskNode.next = null;
            }
            size--;
        }
    }

    @Override
    public void remove(int id) {
        removeNode(mapTaskHistory.get(id));
    }

    public int size() {
        return this.size;
    }

    // Отображение всех подзадач
    public void print() {
        Node<Task> temp = head;

        while (temp != null) {
            System.out.println(temp.data);
            temp = temp.next;
        }
    }

    public LinkedList<Task> getTasks() {
        Node<Task> temp = head;
        LinkedList<Task> listTask = new LinkedList<>();
        while (temp != null) {
            listTask.add(temp.data);
            temp = temp.next;
        }
        return listTask;
    }

    public List<Task> getHistory() {
        return getTasks();
    }

}