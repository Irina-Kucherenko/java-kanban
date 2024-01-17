package manager;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int SIZE_OF_LIST = 9;
    private final List<Task> recentHistory = new LinkedList<>();
    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(recentHistory);
    }


    @Override
    public void addHistory(Task task) {
        if (recentHistory.size() > SIZE_OF_LIST) {
            recentHistory.remove(0);
        }
        recentHistory.add(task.cloneObject()); /*Объясняю: метод cloneObject нужен для того, чтобы сохранилась в истории
        прошлая версия объекта. Допустим, ситуация: мы захотели положить в историю объект, положили; позже нам
        захотелось изменить название и описание объекта, вызвали get метод для определённого объекта. И всё у нас
        сохранена первая версяя объекта и актуальная. Руководствовалась требованиями задания, не более*/
    }
}
