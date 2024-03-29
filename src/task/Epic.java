package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Epic extends Task {

    private final List<SubTask> subTaskList = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description, LocalDateTime.now(), 0);
    }


    public void addSubTask(Task subtask){

        if (super.getId() != subtask.getId()) {
            subTaskList.add((SubTask) subtask);
            subTaskList.sort(Comparator.comparing(Task::getStartTime));
        }
        else {
            throw new IllegalArgumentException("Эпик не может быть добавлен сам в себя");
        }
    }


    public List<SubTask> getSubTaskList() {
        return subTaskList;
    }

    @Override
    public LocalDateTime getEndTime() {
        return subTaskList.getLast().getEndTime();
    }

    @Override
    public Duration getDuration() { //в комментарии нет конкретики, что собственно не так, и почему это должно вычисляться в мэнеджере
        int minutes = 0;
        for (SubTask subTask : subTaskList) {
            minutes += subTask.getDuration().toMinutes();
        }
        return Duration.ofMinutes(minutes);
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subTaskList.isEmpty()) {
            return super.getStartTime();
        }
        return subTaskList.getFirst().getStartTime();
    }
}
