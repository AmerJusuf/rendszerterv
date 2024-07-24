package hu.bme.mit.intellibus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ACCControllerTest {

    private MockEngineController mockEngineController;
    private ACCController acc;

    private static class MockEngineController implements IEngineController {
        enum State { INCREASE_SPEED, DECREASE_SPEED, TRAVELING_WITH_ACC_SPEED }

        private State state = State.TRAVELING_WITH_ACC_SPEED;
        @Override
        public void holdSpeed() {
            state = State.TRAVELING_WITH_ACC_SPEED;
        }

        @Override
        public void increaseSpeed() {
            state = State.INCREASE_SPEED;
        }

        @Override
        public void decreaseSpeed() {
            state = State.DECREASE_SPEED;
        }

        public State getState() {
            return state;
        }
    }

    @BeforeEach
    void setup() {
        // This test class will play the role of the TestConductor

        // The conductor fulfills the roles of the commander and the sensors.
        // All signals that the conductor sends to the ACC will be mapped to acc method calls

        // As it can be seen in the TestingSystem IBD, the AC Control under test is connected to
        // a mock engine controller, which will simply remember which signal was last sent to it,
        // and return the corresponding state when getState is called

        mockEngineController = new MockEngineController();
        acc = new ACCController(mockEngineController);
    }

    @Test
    void testReq1() {
        acc.setSpeed(50);

        assertEquals(ACCController.State.OPERATING, acc.getState());
        assertEquals(acc.getAccSpeed(), 50);
    }

    @Test
    void testReq2_1() {
        acc.setSpeed(80);
        acc.currentSpeed(80);

        assertEquals(MockEngineController.State.TRAVELING_WITH_ACC_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq2_2() {
        acc.setSpeed(130);
        acc.currentSpeed(80);

        assertEquals(MockEngineController.State.INCREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq2_3() {
        acc.setSpeed(80);
        acc.currentSpeed(85);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq4AndReq2_1CurrentSpeedFirst() {
        acc.setSpeed(80);
        acc.currentSpeed(80);
        acc.currentDistance(70);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq4AndReq2_1CurrentDistanceFirst() {
        acc.setSpeed(80);
        acc.currentDistance(70);
        acc.currentSpeed(80);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq4AndReq2_2CurrentSpeedFirst() {
        acc.setSpeed(80);
        acc.currentSpeed(70);
        acc.currentDistance(70);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq4AndReq2_2CurrentDistanceFirst() {
        acc.setSpeed(80);
        acc.currentDistance(70);
        acc.currentSpeed(70);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq4AndReq2_3CurrentSpeedFirst() {
        acc.setSpeed(70);
        acc.currentSpeed(80);
        acc.currentDistance(70);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq4AndReq2_3CurrentDistanceFirst() {
        acc.setSpeed(70);
        acc.currentDistance(70);
        acc.currentSpeed(80);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq5TurningOffFromOperation() {
        acc.setSpeed(130);

        assertEquals(ACCController.State.OPERATING, acc.getState());

        acc.turnOff();

        assertEquals(ACCController.State.OFF, acc.getState());
    }

    @Test
    void testReq5StayingInStateOff() {
        assertEquals(ACCController.State.OFF, acc.getState());

        acc.turnOff();

        assertEquals(ACCController.State.OFF, acc.getState());
    }

    @Test
    void testReq6() {
        acc.setSpeed(-20);

        assertEquals(ACCController.State.OFF, acc.getState());
    }

    @Test
    void testReq7AndReq2_1() {
        acc.setSpeed(120);
        acc.currentSpeed(120);

        assertEquals(MockEngineController.State.TRAVELING_WITH_ACC_SPEED, mockEngineController.getState());

        acc.setSpeed(120);

        assertEquals(MockEngineController.State.TRAVELING_WITH_ACC_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq7AndReq2_2() {
        acc.setSpeed(50);
        acc.currentSpeed(40);

        assertEquals(MockEngineController.State.INCREASE_SPEED, mockEngineController.getState());

        acc.currentSpeed(50);
        acc.setSpeed(60);

        assertEquals(MockEngineController.State.INCREASE_SPEED, mockEngineController.getState());
    }

    @Test
    void testReq7AndReq2_3() {
        acc.setSpeed(40);
        acc.currentSpeed(50);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());

        acc.currentSpeed(40);
        acc.setSpeed(30);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }
}