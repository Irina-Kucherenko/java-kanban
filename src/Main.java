import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        String delimiter = "*****************************************************************************************";
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Домашнее задание от мамы", "Убрать хату, помыть посуду");
        task1 = taskManager.createTask(task1);

        Task task2= new Task("Новый год: докупить", "Джин, вино, ягермейстер");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Проект", "Разработать проект");
        epic1 = taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        subTask1 = taskManager.createSubTask(subTask1);
        epic1.addSubTask(subTask1.getId());
        SubTask subTask2 = new SubTask(epic1.getId(), "Начать делать проект", "1. Открыть среду; " +
                " 2.Создать файлик; 3. На это всё, поработали");
        subTask2 = taskManager.createSubTask(subTask2);
        epic1.addSubTask(subTask2.getId());
        taskManager.updateEpic(epic1);

        Epic epic2 = new Epic("Ужин", "Готовка");
        epic2 = taskManager.createEpic(epic2);
        SubTask subTask3 = new SubTask(epic2.getId(), "Главное блюдо", "Поставить мясо в духовку");
        subTask3 = taskManager.createSubTask(subTask3);
        epic2.addSubTask(subTask3.getId());
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
        taskManager.deleteEpic(epic1.getId());

        System.out.println(delimiter);
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks());

        System.out.println(delimiter);

        taskManager.deleteSubTasks();

        System.out.println(taskManager.getEpics());

        System.out.println(delimiter);

        taskManager.updateTask(task1);
    }
}
