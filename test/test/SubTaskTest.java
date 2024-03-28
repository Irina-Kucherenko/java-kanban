package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.SubTask;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private static SubTask subTask1;
    private static SubTask subTask2;
    @BeforeEach
    public void createSubTask() {
        subTask1 = new SubTask(1, "Закончить прокрастинировать", "1. Встать с дивана; " +
                "2. Сесть за ПК", LocalDateTime.now().plusHours(1), 5);
        subTask2 = new SubTask(1, "Начать делать проект", "1. Открыть среду; " +
                " 2.Создать файлик; 3. На это всё, поработали", LocalDateTime.now().plusHours(2), 5);
    }
    @Test
    void subTaskEqualsTest() {

        subTask1.setId(2);
        subTask2.setId(2);
        assertEquals(subTask1, subTask2);

    }

    @Test
    void subTaskShouldBeEpicByItselfTestReturnException() {

        assertThrows(IllegalArgumentException.class, () -> subTask1.setId(1));
    }

}