package manager;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File storageTasks;

    public FileBackedTaskManager(File file) {
        super(new HashMap<>(), new HashMap<>(), new HashMap<>(), 0);
        this.storageTasks = file;
    }

    public FileBackedTaskManager(File file, Map<Integer, Task> tasks, Map<Integer, Epic> epics,
                                 Map<Integer, SubTask> subTasks, int idCounter) {
        super(tasks, epics, subTasks, idCounter);
        this.storageTasks = file;
    }

    private void save() {
        CSV csv = new CSV();
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,start,duration,epic\n");
        for (Task task : super.getTasks()) {
            sb.append(csv.toStringTask(task));
        }
        for (Epic epic : super.getEpics()) {
            sb.append(csv.toStringEpic(epic));
        }
        for (SubTask subTask : super.getSubTasks()) {
            sb.append(csv.toStringSubTask(subTask));
        }
        sb.append(historyToString(super.getHistoryManager()));
        try {
            Files.writeString(storageTasks.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

     private static Task fromString(String elem) {
        String[] taskParams = elem.split(",");
        int id = Integer.parseInt(taskParams[0]);
        TaskTypes type = TaskTypes.valueOf(taskParams[1].toUpperCase());
        String name = taskParams[2];
        TaskStatus status = TaskStatus.valueOf(taskParams[3].toUpperCase());
        String description = taskParams[4];
        LocalDateTime startTime = LocalDateTime.parse(taskParams[5]);
        int minutes = Integer.parseInt(taskParams[6]);
        Integer epicId;
        if (type.equals(TaskTypes.SUBTASK)) {
            epicId = Integer.parseInt(taskParams[7]);
        } else {
            epicId = null;
        }

        if (type.equals(TaskTypes.SUBTASK)) {
            SubTask subTask = new SubTask(epicId, name, description, startTime, minutes);
            subTask.setId(id);
            subTask.setStatus(status);
            return subTask;

        } else if (type.equals(TaskTypes.EPIC)) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);

            return epic;
        } else {
            Task task = new Task(name, description, startTime, minutes);
            task.setId(id);
            task.setStatus(status);
            return task;
        }
    }

    static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(Task::getId)
                .map(String:: valueOf)
                .collect(Collectors.joining(","));
    }

    static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(",")).map(Integer::parseInt).toList();
    }

     public static FileBackedTaskManager loadFromFile(File file) {


        if (!file.exists()) {
            throw new ManagerCreateException("Файл не существует");
        }
        try {
            List<String> strings = Files.readAllLines(file.toPath());
            HashMap<Integer, Task> tasks = new HashMap<>();
            HashMap<Integer, Epic> epics = new HashMap<>();
            HashMap<Integer, SubTask> subTasks = new HashMap<>();
            HashMap<Integer, Task>  allTypes = new HashMap<>();
            int counter = 0;
            if (strings.isEmpty()) {
                return new FileBackedTaskManager(file);
            }
            for (int i = 1; i < strings.size() - 1; i++) {
                if (strings.get(i) != null || !strings.get(i).isBlank()) {
                    String line = strings.get(i);
                    Task task = fromString(line);

                    if (counter < task.getId()) {
                        counter = task.getId();
                    }
                    if (task instanceof SubTask subTask) {
                        subTasks.put(subTask.getId(), subTask);
                    } else if (task instanceof Epic epic) {
                        epics.put(epic.getId(), epic);
                    } else {
                        tasks.put(task.getId(), task);
                    }
                    allTypes.put(task.getId(), task);

                }
            }

            var manager = new FileBackedTaskManager(file, tasks, epics, subTasks, counter);
            loadHistory(manager, allTypes, strings.getLast());
            return manager;

        } catch (IOException e) {
            throw new ManagerCreateException(e.getMessage());
        }
    }



    static void loadHistory(FileBackedTaskManager manager, HashMap<Integer, Task> allTypes, String history) {
        var historyManager = manager.getHistoryManager();

        for (Integer id : historyFromString(history)) {
            if (allTypes.containsKey(id)) {
                historyManager.addHistory(allTypes.get(id));
            }
        }
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(Integer epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubTask(Integer subTaskId) {
        super.deleteSubTask(subTaskId);
        save();
    }

    @Override
    public Task createTask(Task task) {
        var result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public Epic createEpic(Epic epic) {
        var result = super.createEpic(epic);
        save();
        return result;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        var result = super.createSubTask(subTask);
        save();
        return result;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }
}
