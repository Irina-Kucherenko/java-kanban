package manager;

import task.Epic;
import task.SubTask;
import task.Task;

public class CSV {
    protected String toStringTask(Task task) {
        return String.format("%s,%s,%s,%s,%s,%n", task.getId(), TaskTypes.TASK, task.getName(),
                task.getStatus(), task.getDescription());
    }

    protected String toStringEpic(Epic epic) {
        return String.format("%s,%s,%s,%s,%s,%n", epic.getId(), TaskTypes.EPIC, epic.getName(),
                epic.getStatus(), epic.getDescription());
    }

    protected String toStringSubTask(SubTask subTask) {
        return String.format("%s,%s,%s,%s,%s,%s%n", subTask.getId(), TaskTypes.SUBTASK, subTask.getName(),
                subTask.getStatus(), subTask.getDescription(), subTask.getEpicId());
    }
}
