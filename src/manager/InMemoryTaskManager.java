package manager;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    
    private int idCounter = 0;

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getTasks() {
        ArrayList<Task> results = new ArrayList<>(tasks.values());
        for (Task result : results) {
            historyManager.addHistory(result);
        }
        return results;


    }
    @Override
    public List<Epic> getEpics() {
        ArrayList<Epic> results = new ArrayList<>(epics.values());
        for (Task result : results) {
            historyManager.addHistory(result);
        }
        return results;
    }

    @Override
    public List<SubTask> getSubTasks() {
        ArrayList<SubTask> results = new ArrayList<>(subTasks.values());
        for (Task result : results) {
            historyManager.addHistory(result);
        }
        return results;
    }

    private int getIdentifier(){
        idCounter++;
        return idCounter;
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        deleteSubTasks();
        epics.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
        }

    }

    @Override
    public Task getTask(Integer id) {
        Task task = tasks.get(id);
        if (task.equals(tasks.get(id))) {
            historyManager.addHistory(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epic.equals(epics.get(epicId))) {
            historyManager.addHistory(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTask(Integer subTaskId) {

        SubTask subTask =  subTasks.get(subTaskId);
        if (subTask.equals(subTasks.get(subTaskId))) {
            historyManager.addHistory(subTask);
        }
        return subTask;
    }

    @Override
    public void deleteTask(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epics.containsKey(epic.getId())) {
            epics.remove(epicId);
            epic.getSubTaskList().forEach(this::deleteSubTask);
        }
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {
        subTasks.remove(subTaskId);
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
        }
    }

    @Override
    public Task createTask(Task task){
        task.setId(getIdentifier());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic){
        epic.setId(getIdentifier());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask){
        subTask.setId(getIdentifier());
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    @Override
    public void updateTask(Task task){
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Ошибка! Задача с id " + task.getId() + " не найдена.");
        }
    }

    @Override
    public void updateEpic(Epic epic){
        if (epics.containsKey(epic.getId())) {
            epic.setName(epic.getName());
            epic.setDescription(epic.getDescription());
        } else {
            System.out.println("Ошибка! Эпик с id " + epic.getId() + " не найден.");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask){
        if (epics.containsKey(subTask.getEpicId())) {
            if (subTasks.containsKey(subTask.getId())) {
                updateStatusEpic(epics.get(subTask.getEpicId()));
                subTasks.put(subTask.getId(), subTask);
            } else {
                System.out.println("Ошибка! Подзадача с id " + subTask.getId() + " не найдена.");
            }
        }

    }

    @Override
    public List<SubTask> getSubTasksOfEpic(int epicId){
        List<SubTask> subTaskList = null;
        Epic epic = epics.get(epicId);
        if (epics.containsKey(epic.getId())) {
            subTaskList = epics.get(epicId).getSubTaskList().stream()
                    .filter(subTasks::containsKey)
                    .map(subTasks::get)
                    .toList();
        }
        return subTaskList;
    }

   

    

    /*Метод вытаскивает из подзадач статусы с помощью потока и проверяет их*/
    private void updateStatusEpic(Epic epic){
        var statuses = getSubTasksOfEpic(epic.getId()).stream()
                .map(Task::getStatus)
                .toList();
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