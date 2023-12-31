import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int idCounter = 0;

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public int getIdentifier(){
        if(tasks.isEmpty() && epics.isEmpty() && subTasks.isEmpty()){
            idCounter = 0;
        }
        idCounter++;
        return idCounter;
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        deleteSubTasks();
        epics.clear();
    }

    public void deleteSubTasks() {
        subTasks.clear();
    }

    public Task getTask(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpic(Integer epicId) {
        return epics.get(epicId);
    }

    public SubTask getSubTask(Integer subTaskId) {
        return subTasks.get(subTaskId);
    }

    public void deleteTask(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        epic.getSubTaskList().forEach(this::deleteSubTask);
        epics.remove(epicId);
    }

    public void deleteSubTask(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }

    public Task createTask(Task task){
        task.setId(getIdentifier());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic){
        epic.setId(getIdentifier());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask){
        subTask.setId(getIdentifier());
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic){
        updateStatusEpic(epic);
        epics.put(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask){
        updateStatusEpic(epics.get(subTask.getEpicId()));
        subTasks.put(subTask.getId(), subTask);
    }

    public List<SubTask> getSubTasksOfEpic(int epicId){
        return epics.get(epicId).getSubTaskList().stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    /*Метод вытаскивает из подзадач статусы с помощью потока и проверяет их*/
    private void updateStatusEpic(Epic epic){
        var statuses = getSubTasksOfEpic(epic.getId()).stream()
                .map(Task::getStatus)
                .collect(Collectors.toList());
        if (statuses.stream().allMatch(status -> status.equals(TaskStatus.NEW))) {
            epic.setStatus(TaskStatus.NEW);

        }
        else if (statuses.stream().allMatch(status -> status.equals(TaskStatus.DONE))) {
            epic.setStatus(TaskStatus.DONE);

        }
        else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }

    }


}
