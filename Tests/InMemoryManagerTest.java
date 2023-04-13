import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void emptyTasks(){
        assertEquals(Collections.emptyList(), manager.getAllTask());
        assertEquals(Collections.emptyList(), manager.getAllEpics());
        assertEquals(Collections.emptyList(), manager.getAllSubtask());
    }
}