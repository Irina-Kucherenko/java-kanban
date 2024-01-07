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

    public int getIdentifier(){ /*Не совсем поняла, что вы имели в виду, потому что описания ошибки в ревью не было. Что конкретно не так?*/
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
        for(Epic epic : epics.values()){
            updateStatusEpic(epic);
        }

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
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Ошибка! Задача с id " + task.getId() + " не найдена.");
        }
    }

    public void updateEpic(Epic epic){
        if (epics.containsKey(epic.getId())) {
            updateStatusEpic(epic);
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Ошибка! Эпик с id " + epic.getId() + " не найден.");
        }
    }

    public void updateSubTask(SubTask subTask){
        if (subTasks.containsKey(subTask.getId())){
            updateStatusEpic(epics.get(subTask.getEpicId()));
            subTasks.put(subTask.getId(), subTask);
        } else {
            System.out.println("Ошибка! Подзадача с id " + subTask.getId() + " не найдена.");
        }

    }

    public List<SubTask> getSubTasksOfEpic(int epicId){
        return epics.get(epicId).getSubTaskList().stream()
                .filter(subTasks::containsKey)
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
