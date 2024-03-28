package test;

import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    protected static Epic epic1;
    protected static Task task1;
    protected T taskManager;


    void beforeEachTest() {
        epic1 = new Epic("Проект", "Разработать проект");
        task1 = new Task("Домашнее задание от мамы", "Убрать хату, помыть посуду", LocalDateTime.now().plusHours(1), 5);

    }

    @Test
    void shouldAddEpicInStorageAndGetEpic() {
        epic1 = taskManager.createEpic(epic1);
        assertEquals(1, taskManager.getEpics().size());
        assertNotNull(taskManager.getEpic(epic1.getId()));
    }

    @Test
    void shouldAddSubTaskInStorageAndGetSubTask() {
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(2), 5);
        subTask1 = taskManager.createSubTask(subTask1);
        assertEquals(1, taskManager.getSubTasks().size());
        assertNotNull(taskManager.getSubTask(subTask1.getId()));
    }

    @Test
    void shouldAddTaskInStorageAndGetTask() {
        task1 = taskManager.createTask(task1);
        assertEquals(1, taskManager.getTasks().size());
        assertNotNull(taskManager.getTask(task1.getId()));
    }

    @Test
    void shouldOurIdNotToArgueWithIdOfManager() {
        task1.setId(3);
        assertDoesNotThrow(() -> taskManager.createTask(task1));
        epic1.setId(4);
        assertDoesNotThrow(() -> taskManager.createEpic(epic1));
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(3), 5);
        subTask1.setId(5);
        assertDoesNotThrow(() -> taskManager.createSubTask(subTask1));

    }

    @Test
    void deleteTasksTest() {
        taskManager.createTask(task1);
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteEpicsTest() {
        taskManager.createEpic(epic1);
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    void deleteSubTasksTest() {
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(4), 5);
        taskManager.createSubTask(subTask1);
        taskManager.deleteSubTasks();
        assertEquals(0, taskManager.getSubTasks().size());
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    void deleteTaskTest() {
        taskManager.createTask(task1);
        taskManager.deleteTask(task1.getId());
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteEpicTest() {
        epic1 = taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(5), 5);
        SubTask subTask2 = new SubTask(epic1.getId(), "123", "6" +
                "12", LocalDateTime.now().plusHours(6), 5);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        taskManager.updateEpic(epic1);
        taskManager.deleteEpic(epic1.getId());
        assertEquals(0, taskManager.getSubTasks().size());
        assertEquals(0, taskManager.getEpics().size());

    }

    @Test
    void checkTimeIntersectionTest() {
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(1), 10);
        taskManager.createSubTask(subTask1);

        assertThrows(IllegalArgumentException.class, () -> taskManager.createSubTask(subTask1));
    }

    @Test
    void checkPrioritizedListWithTimeTest() {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        taskManager.createTask(task1);
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void checkPrioritizedListWithoutTimeTest() {
        assertEquals(0, taskManager.getPrioritizedTasks().size());
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        taskManager.createSubTask(subTask1);
        assertEquals(0, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void checkSubTaskShouldNotHaveEpic() {

        assertThrows(NullPointerException.class, () -> new SubTask(null,  "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК"));

    }

}
