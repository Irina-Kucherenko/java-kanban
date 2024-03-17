package test;

import manager.FileBackedTaskManager;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

 class FileBackedTaskManagerTest {
    private static Epic epic1;
    private static Task task1;
    private static FileBackedTaskManager fileBackedTaskManager;
    private  static File tempFile;

    @BeforeEach
    public void createAllFields() throws IOException {
        epic1 = new Epic("Проект", "Разработать проект");
        task1 = new Task("Домашнее задание от мамы", "Убрать хату, помыть посуду");

        tempFile = Files.createTempFile("test", ".tmp").toFile();
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);

    }
    @Test
    void loadFileTest() throws IOException {
        var manager = FileBackedTaskManager.loadFromFile(tempFile);
        Files.readAllLines(tempFile.toPath());
        assertNotNull(manager);
        assertTrue(Files.readAllLines(tempFile.toPath()).isEmpty());
    }

    @Test
    void shouldAddEpicInStorageAndGetEpic() {
        epic1 = fileBackedTaskManager.createEpic(epic1);
        assertEquals(1, fileBackedTaskManager.getEpics().size());
        assertNotNull(fileBackedTaskManager.getEpic(epic1.getId()));
    }

    @Test
    void shouldAddSubTaskInStorageAndGetSubTask() {
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        subTask1 = fileBackedTaskManager.createSubTask(subTask1);
        assertEquals(1, fileBackedTaskManager.getSubTasks().size());
        assertNotNull(fileBackedTaskManager.getSubTask(subTask1.getId()));
    }

    @Test
    void shouldAddTaskInStorageAndGetTask() {
        task1 = fileBackedTaskManager.createTask(task1);
        assertEquals(1, fileBackedTaskManager.getTasks().size());
        assertNotNull(fileBackedTaskManager.getTask(task1.getId()));
    }

    @Test
    void shouldOurIdNotToArgueWithIdOfManager() {
        task1.setId(3);
        assertDoesNotThrow(() -> fileBackedTaskManager.createTask(task1));
        epic1.setId(4);
        assertDoesNotThrow(() -> fileBackedTaskManager.createEpic(epic1));
        SubTask subTask1 = new SubTask(-1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        subTask1.setId(5);
        assertDoesNotThrow(() -> fileBackedTaskManager.createSubTask(subTask1));

    }

    @Test
    void deleteTasksTest() {
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.deleteTasks();
        assertEquals(0, fileBackedTaskManager.getTasks().size());
    }

    @Test
    void deleteEpicsTest() {
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.deleteEpics();
        assertEquals(0, fileBackedTaskManager.getEpics().size());
        assertEquals(0, fileBackedTaskManager.getSubTasks().size());
    }

    @Test
    void deleteSubTasksTest() {
        fileBackedTaskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        fileBackedTaskManager.createSubTask(subTask1);
        fileBackedTaskManager.deleteSubTasks();
        assertEquals(0, fileBackedTaskManager.getSubTasks().size());
        assertEquals(TaskStatus.NEW, epic1.getStatus());
    }

    @Test
    void deleteTaskTest() {
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.deleteTask(task1.getId());
        assertEquals(0, fileBackedTaskManager.getTasks().size());
    }

    @Test
    void deleteEpicTest() {
        epic1 = fileBackedTaskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(epic1.getId(), "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК");
        SubTask subTask2 = new SubTask(epic1.getId(), "123", "6" +
                "12");
        fileBackedTaskManager.createSubTask(subTask1);
        fileBackedTaskManager.createSubTask(subTask2);
        epic1.addSubTask(subTask1.getId());
        epic1.addSubTask(subTask2.getId());
        fileBackedTaskManager.updateEpic(epic1);
        fileBackedTaskManager.deleteEpic(epic1.getId());
        assertEquals(0, fileBackedTaskManager.getSubTasks().size());
        assertEquals(0, fileBackedTaskManager.getEpics().size());

    }
}
