package test;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static Epic epic1;
    private static Task task1;
    private static InMemoryTaskManager inMemoryTaskManager;

    @BeforeEach
    public void createAllFields() {
        epic1 = new Epic("Проект", "Разработать проект");
        task1 = new Task("Домашнее задание от мамы", "Убрать хату, помыть посуду");
        inMemoryTaskManager = new InMemoryTaskManager();

    }

    @Test
    void shouldAddEpicInStorageAndGetEpic() {
        epic1 = inMemoryTaskManager.createEpic(epic1);
        assertEquals(1, inMemoryTaskManager.getEpics().size());
        assertNotNull(inMemoryTaskManager.getEpic(epic1.getId()));
    }

    @Test
    void shouldAddSubTaskInStorageAndGetSubTask() {
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        subTask1 = inMemoryTaskManager.createSubTask(subTask1);
        assertEquals(1, inMemoryTaskManager.getSubTasks().size());
        assertNotNull(inMemoryTaskManager.getSubTask(subTask1.getId()));
    }

    @Test
    void shouldAddTaskInStorageAndGetTask() {
        task1 = inMemoryTaskManager.createTask(task1);
        assertEquals(1, inMemoryTaskManager.getTasks().size());
        assertNotNull(inMemoryTaskManager.getTask(task1.getId()));
    }

    @Test
    void shouldOurIdNotToArgueWithIdOfManager() {
        task1.setId(3);
        assertDoesNotThrow(() -> inMemoryTaskManager.createTask(task1));
        epic1.setId(4);
        assertDoesNotThrow(() -> inMemoryTaskManager.createEpic(epic1));
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        subTask1.setId(5);
        assertDoesNotThrow(() -> inMemoryTaskManager.createSubTask(subTask1));

    }

    @Test
    void deleteTasksTest() {
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.deleteTasks();
        assertEquals(0, inMemoryTaskManager.getTasks().size());
    }

    @Test
    void deleteEpicsTest() {
        inMemoryTaskManager.createEpic(epic1);
        inMemoryTaskManager.deleteEpics();
        assertEquals(0, inMemoryTaskManager.getEpics().size());
        assertEquals(0, inMemoryTaskManager.getSubTasks().size());
    }

    @Test
    void deleteSubTasksTest() {
        inMemoryTaskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        inMemoryTaskManager.createSubTask(subTask1);
        inMemoryTaskManager.deleteSubTasks();
        assertEquals(0, inMemoryTaskManager.getSubTasks().size());
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    void deleteTaskTest() {
        inMemoryTaskManager.createTask(task1);
        inMemoryTaskManager.deleteTask(task1.getId());
        assertEquals(0, inMemoryTaskManager.getTasks().size());
    }

    @Test
    void deleteEpicTest() {
        epic1 = inMemoryTaskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        SubTask subTask2 = new SubTask(epic1.getId(), "123", "6" +
                "12");
        inMemoryTaskManager.createSubTask(subTask1);
        inMemoryTaskManager.createSubTask(subTask2);
        epic1.addSubTask(subTask1.getId());
        epic1.addSubTask(subTask2.getId());
        inMemoryTaskManager.updateEpic(epic1);
        inMemoryTaskManager.deleteEpic(epic1.getId());
        assertEquals(0, inMemoryTaskManager.getSubTasks().size());
        assertEquals(0, inMemoryTaskManager.getEpics().size());

    }












}