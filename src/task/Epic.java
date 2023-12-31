package task;

import java.util.ArrayList;
import java.util.List;
public class Epic extends Task {

    private final List<Integer> subTaskList = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubTask(Integer subtask){
        subTaskList.add(subtask);
    }

    public List<Integer> getSubTaskList() {
        return subTaskList;
    }
}
