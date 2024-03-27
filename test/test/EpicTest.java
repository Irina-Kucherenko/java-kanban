package test;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.TaskStatus;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private static Epic epic1;
    private static Epic epic2;
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void createEpic() {
        epic1 = new Epic("Проект", "Разработать проект");
        epic2 = new Epic("Ужин", "Готовка");
        taskManager = new InMemoryTaskManager(new HashMap<>(), new HashMap<>(), new HashMap<>(), 0);
    }
    @Test
    void epicEqualsTest() {
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }

    @Test
    void epicAddItselfTestReturnException() {

        epic1.setId(1);
        assertThrows(IllegalArgumentException.class, () -> epic1.addSubTask(epic1));
    }

    @Test
    void checkUpdatingEpicTest() {
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

        assertEquals(TaskStatus.NEW, epic1.getStatus());
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());

        subTask1.setStatus(TaskStatus.NEW);
        subTask2.setStatus(TaskStatus.DONE);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus());

        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);

        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        assertEquals(TaskStatus.DONE, epic1.getStatus());




    }
}