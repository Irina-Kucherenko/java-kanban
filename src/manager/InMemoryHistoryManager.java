package manager;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> recentHistory = new LinkedList<>();
    @Override
    public List<Task> getHistory() {
        return recentHistory;
    }


    @Override
    public void addHistory(Task task) {
        if (recentHistory.size() > 9) {
            recentHistory.remove(0);
        }
        recentHistory.add(task.cloneObject());
    }
}
