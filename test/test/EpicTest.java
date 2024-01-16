package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private static Epic epic1;
    private static Epic epic2;

    @BeforeEach
    public void createEpic() {
        epic1 = new Epic("Проект", "Разработать проект");
        epic2 = new Epic("Ужин", "Готовка");
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
        Integer epicId = epic1.getId();
        assertThrows(IllegalArgumentException.class, () -> epic1.addSubTask(epicId));


    }
}