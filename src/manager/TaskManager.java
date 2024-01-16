package manager;

import task.Epic;
import task.SubTask;
import task.Task;
import java.util.List;

 public interface TaskManager {

     List<Task> getTasks();

     List<Epic> getEpics();

     List<SubTask> getSubTasks();

     void deleteTasks();

     void deleteEpics();

     void deleteSubTasks();

     Task getTask(Integer id);

     Epic getEpic(Integer epicId);

     SubTask getSubTask(Integer subTaskId);

     void deleteTask(Integer id);

     void deleteEpic(Integer epicId);

     void deleteSubTask(Integer subTaskId);

     Task createTask(Task task);

     Epic createEpic(Epic epic);

     SubTask createSubTask(SubTask subTask);

     void updateTask(Task task);

     void updateEpic(Epic epic);

     void updateSubTask(SubTask subTask);

     List<SubTask> getSubTasksOfEpic(int epicId);

     HistoryManager getHistoryManager();

 }
