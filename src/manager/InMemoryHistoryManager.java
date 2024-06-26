package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> recentHistory = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;
    @Override
    public List<Task> getHistory() {
        List<Task> taskList = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            taskList.add(node.value);
            node = node.next;
        }
        return  taskList;
    }


    @Override
    public void addHistory(Task task) {
        if (!recentHistory.containsKey(task.getId())) {
            linkLast(task.cloneObject());
        }/*Объясняю: метод cloneObject нужен для того, чтобы сохранилась в истории
        прошлая версия объекта. Допустим, ситуация: мы захотели положить в историю объект, положили; позже нам
        захотелось изменить название и описание объекта, вызвали get метод для определённого объекта. И всё у нас
        сохранена первая версия объекта и актуальная. Руководствовалась требованиями задания, не более*/
    }

    @Override
    public void remove(int id) { /*непонятен Ваш комментарий, InMemoryHistoryManager это же
    и есть реализация интерфейса*/

        if (recentHistory.containsKey(id)) {
            if (head.value.getId() == id) {
                head = head.next;
                head.prev = null;
            }
            else if (tail.value.getId() == id) {
                tail = tail.prev;
                tail.next = null;
            }
            else {
                Node<Task> value = recentHistory.get(id);
                Node<Task> prev = value.prev;
                Node<Task> next = value.next;
                prev.next = next;
                next.prev = prev;
            }
            recentHistory.remove(id);
        }

    }

    private void linkLast(Task task) {
        final Node<Task> prevTail = tail;
        final Node<Task> newElem = new Node<>(prevTail, task, null);
        tail = newElem;
        if (prevTail == null) {
            head = newElem;
        } else {
            prevTail.next = newElem;
        }
        recentHistory.put(task.getId(), newElem);
    }



    private static class Node<Task> {

        private Node<Task> prev;
        private Node<Task> next;
        private final Task value;

        public Node(Node<Task> prev, Task value,  Node<Task> next) {
            this.prev = prev;
            this.next = next;
            this.value = value;
        }
    }
}


