package test;

import manager.Managers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    private static Managers manager;

    @BeforeAll
    public static void createManagers() {
        manager = new Managers();
    }

    @Test
    void getDefaultNotNullTest() {
        assertNotNull(manager.getDefault());
    }

    @Test
    void getDefaultHistoryNotNullTest() {
        assertNotNull(Managers.getDefaultHistory());
    }
}