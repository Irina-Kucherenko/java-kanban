package manager;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final TreeSet<Task> priorityTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    
    private int idCounter;

    public InMemoryTaskManager(Map<Integer, Task> tasks, Map<Integer, Epic> epics, Map<Integer, SubTask> subTasks,
                               int idCounter) {
        this.tasks = tasks;
        this.epics = epics;
        this.subTasks = subTasks;
        this.idCounter = idCounter;
        updatePriorityTasks();
    }

    private void updatePriorityTasks() {
        this.tasks.values().forEach(task -> {
            if (task.getStartTime() != null) {
                priorityTasks.add(task);
            }
        });
        this.subTasks.values().forEach(subTask -> {
            if (subTask.getStartTime() != null) {
                priorityTasks.add(subTask);
            }
        });
    }

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
        priorityTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        if (epics.containsKey(epic.getId())) {
            epics.remove(epicId);
            epic.getSubTaskList().forEach(s -> deleteSubTask(s.getId()));
        }
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {
        priorityTasks.remove(subTasks.get(subTaskId));
        subTasks.remove(subTaskId);
        for (Epic epic : epics.values()) {
            updateStatusEpic(epic);
        }

    }

    @Override
    public Task createTask(Task task){
         priorityListCheckOfIntersection(task);
        task.setId(getIdentifier());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            priorityTasks.add(task);
        }
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
        priorityListCheckOfIntersection(subTask);
        subTask.setId(getIdentifier());
        subTasks.put(subTask.getId(), subTask);
        if (subTask.getStartTime() != null) {
            priorityTasks.add(subTask);
        }

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
                    .filter(subTask -> subTasks.containsKey(subTask.getId()))
                    .map(subTask -> subTasks.get(subTask.getId()))
                    .toList();
        }
        return subTaskList;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return priorityTasks.stream().toList();
    }


    private boolean checkTimeIntersection(Task t1, Task t2) {
        LocalDateTime start;
        LocalDateTime end;
        if (t1.getStartTime().isBefore(t2.getStartTime())) {
            start = t1.getEndTime();
            end = t2.getStartTime();
        }
        else {
            start = t2.getEndTime();
            end = t1.getStartTime();
        }
        return start.isAfter(end);
    }

    private void priorityListCheckOfIntersection(Task t1) {
        boolean checkInterSection = priorityTasks.stream().anyMatch(task -> checkTimeIntersection(t1, task));
        if (!priorityTasks.isEmpty() && checkInterSection) {
            throw new IllegalArgumentException("Задача имеет временное пересечение с другими задачами.");
        }
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
