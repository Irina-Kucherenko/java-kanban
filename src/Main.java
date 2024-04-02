

import manager.Managers;
import manager.TaskManager;
import server.HttpTaskServer;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        String delimiter = "*****************************************************************************************";
        Managers manager = new Managers();

        TaskManager taskManager =  manager.getDefault();
        Task task1 = new Task("Домашнее задание от мамы", "Убрать хату -> помыть посуду", LocalDateTime.now(), 5);
        task1 = taskManager.createTask(task1);

        Task task2= new Task("Новый год: докупить", "Джин -> вино -> ягермейстер", LocalDateTime.now().plusHours(1), 10);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Проект", "Разработать проект");
        epic1 = taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана -> " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(2), 10);
        subTask1 = taskManager.createSubTask(subTask1);
        epic1.addSubTask(subTask1);
        SubTask subTask2 = new SubTask(epic1.getId(), "Начать делать проект", "1. Открыть среду -> " +
                " 2.Создать файлик -> 3. На этом всё поработали", LocalDateTime.now().plusHours(3), 10);
        subTask2 = taskManager.createSubTask(subTask2);
        epic1.addSubTask(subTask2);
        taskManager.updateEpic(epic1);

        Epic epic2 = new Epic("Ужин", "Готовка");
        epic2 = taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask(epic2.getId(), "Главное блюдо", "Поставить мясо в духовку", LocalDateTime.now().plusHours(4), 10);
        subTask3 = taskManager.createSubTask(subTask3);
        epic2.addSubTask(subTask3);
        taskManager.updateEpic(epic2);

        System.out.println(delimiter);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);

        subTask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask3);

        System.out.println(delimiter);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());

        taskManager.deleteTask(task1.getId());


        System.out.println(delimiter);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());

        System.out.println(delimiter);



        System.out.println(taskManager.getEpics());

        System.out.println(delimiter);

        taskManager.deleteEpic(epic2.getId());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());

        System.out.println(delimiter);

        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getSubTasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistoryManager().getHistory()) {
           System.out.println(task);
        }

        try {
            HttpTaskServer httpTaskServer = new HttpTaskServer();
            httpTaskServer.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
