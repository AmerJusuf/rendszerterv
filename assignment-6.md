# Assignment 6 - Verification & Validation

## Task T1 - Requirements

### Overview

The initial high-level requirements for the Automatic Cruise Control are the following:

- **REQ1**: The user shall be able to turn on the ACC.

- **REQ2**: When turned on the ACC shall maintain the target speed set by the driver.

- **REQ3**: If the current speed is lower than the target speed and ACC does not detect a car in front in 90 meters,
then the ACC shall command the engine to accelerate.

- **REQ4**: If ACC detects a car in front, then it shall decrease the speed of the car.

While these requirements are a good starting point, there are certain ambiguities and inconsistencies. Revision is needed, in order to provide consistent and sufficient requirements.

### Ambiguities, inconsistencies

Some requirements need refinement to alleviate ambiguity. We should also stick to the *Subject, Auxiliary, Verb, Object, Conditions* pattern.

#### REQ1 

We should refrain from using the word "user" as the subject of this requirement and rather use "driver", as we did in REQ2. A user could just as well be a passenger.

The conditions of this requirement are unclear. When can the driver turn the ACC on?

What happens when the vehicle is turned off or the vehicle is not in motion? 

Should we consider using a required minimum speed when the driver can turn on the ACC? 

Intuitively, the driver is only able to turn on the ACC if the vehicle is in forward motion. Should we also add reverse motion as a feature?

We could revise this requirement as: 
**"The driver shall be able to turn on the ACC if the vehicle is in forward motion, going with the required minimum speed."**

#### REQ2

The words "maintain" and "set" are ambiguous in this context.

What does it mean to "set" the speed? Does it mean that the ACC should aim to maintain the current speed of the vehicle, or should the driver manually compute the desired velocity? 

Which values should be permitted in the former case?

As for maintain, what happens if the set speed is different from the current speed? 

We should rephrase this requirement so that it conveys the idea that the ACC is able to accelerate or decelerate in order to reach the desired velocity.

We should also replace the auxiliary "shall" with "should", as REQ4 shows that the ACC should decelerate (even from the desired speed) when another object is ahead of the vehicle.

We could revise this requirement as: 
**"The ACC should control velocity to reach the speed set by the driver."**

#### REQ3

REQ3 is realized by REQ2 and REQ4, because when the current speed is lower than the target speed then based on REQ2, the vehicle will increase it's velocity. However, if there is another vehicle within braking distance, it will decrease its velocity based on REQ4. Hence, the following requirement: *"REQ3: If the current speed is lower than the target speed and ACC does not detect a car in front in 90 meters, then the ACC shall command the engine to accelerate."* is fully realized by REQ2 and REQ4.


Hence, REQ3 is redundant.

**We should consider removing this requirement or splitting it up into 2-3 others.**

#### REQ4

If the ACC detects a car in front of it, it should only decrease it's speed, if the vehicle is within the braking distance.

We could revise this requirement as **"The ACC shall command the engine to decrease the speed of the vehicle if it detects a car in front in braking distance."**

### Additional requirements

While the revised requirements show a clearer picture, we should extend the list with additional requirements.

- The requirements for accelerating or decelerating only mention cars. We should revise these requirements to include other traffic participants as well, or add new ones to account for these scenarios.

- There is a requirement that specifies that the driver should be able to turn on the ACC. We should also include one that allows the driver to turn it off.

- While REQ3 and REQ4 mentions how the system should behave when it detects another vehicle ahead, there is no explicit requirement that specifies this capability. We should add a requirement along the lines of **"The ACC shall detect objects in braking distance"**.

- If we stick with braking distance (according to these recommendations), the ACC shall be able to calculate braking distance.

- While multiple requirements require the ACC to align the vehicle's speed to the desired velocity, we do not yet have a requirement that explicitly says that the ACC should be able to measure the vehicle's speed or that it shall be able to receive such a signal.

- We should add requirements that specify whether the driver can change the speed while the ACC is turned on.

- We should add safety-related requirements that specify how the ACC should behave in high-risk scenarios, such as when it is incapable of detecting vehicles ahead.

All in all, we should work on augmenting the list of requirements to resolve conflicts and account for more aspects of the ACC's functionality.

Questions such as distance measurement, traffic participants, deceleration and the mechanism for setting the velocity await clarification and may require further discussion.

## Task T2 - Testing


### **Requirement overview**

- **REQ1**

    The driver shall be able to turn on the ACC if the vehicle is in forward motion, going with the required minimum speed.

- **REQ2**

    The ACC should control velocity to reach the speed set by the driver.

    - **REQ2.1**
    
        When turned on, the ACC *should* maintain the vehicle's speed if it is equal to the speed set by the driver.

    - **REQ2.2**
    
        The ACC shall command the engine to accelerate if the current speed is lower than the target speed and the ACC does not detect a car in braking distance.

    - **REQ2.3**

        The ACC shall command the engine to decrease the speed of the vehicle if the speed is greater than the one set by the driver.

- **REQ4**

    The ACC shall command the engine to decrease the speed of the vehicle if it detects a car in front in braking distance.

    We define multiple tests for REQ4 so that the functionality is tested with every combination of REQ2's children. We also check if the behavior stays the same if the commands come in different orders.

    **NOTE**: We used a constant 90m as the braking distance when writing the tests, but as mentioned in F1, we should consider adaptive measurements to improve safety and efficiency.

- **REQ5**

    The driver shall be able to turn off the ACC.

- **REQ6**

    The ACC shall not turn on if the vehicle does not reach the minimum velocity.

- **REQ7**

    The driver can override the set speed while the ACC is operating.



### **REQ1**

The driver shall be able to turn on the ACC if the vehicle is in forward motion, going with the required minimum speed.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req1.svg)


**SEQUENCE DIAGRAM DESCRIPTION**:

For REQ1, we test if the ACC turns operational if we set its speed to 50 km/h.
We call the AC Control's SetSpeed method with value `50`.

**TEST DESCRIPTION**:

As mentioned in the sequence diagram's description, we set the speed to 50 and evaluate the `ACCController`'s state.

The test passes if the assertions for the controller state and the ACC's speed both succeed.

**CODE**:

~~~
@Test
void testReq1() {
    acc.setSpeed(50);

    assertEquals(ACCController.State.OPERATING, acc.getState());
    assertEquals(acc.getAccSpeed(), 50);
}
~~~

### **REQ2**

The ACC should control velocity to reach the speed set by the driver.

We break REQ2 down into three distinct requirements to make the requirements more explicit and improve testability.

#### **REQ2.1**: When turned on, the ACC *should* maintain the vehicle's speed if it is equal to the speed set by the driver.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req2_1.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We test that the ACC maintains the vehicle's speed if the ACC's speed is set to 80 and the current speed is 80 as well.

First, we set the desired speed to `80` with the ACC's `SetSpeed` method, then set the vehicle's current speed to the same value using the ACC's `CurrentSpeed` method.

Then, we call `Evaluate` on `MockEngineController` to check if the AC Control prompted it to hold the speed.

**TEST DESCRIPTION**:

As described in the sequence diagram's description, we call `setSpeed` and `currentSpeed` both with value `80`.

Then, we make an assertion on the `MockEngineController` to check if its tate is `TRAVELING_WITH_ACC_SPEED`. The test passes if the assertion succeeds.

**CODE**:

    ~~~
    @Test
    void testReq2_1() {
        acc.setSpeed(80);
        acc.currentSpeed(80);

        assertEquals(MockEngineController.State.TRAVELING_WITH_ACC_SPEED, mockEngineController.getState());
    }
    ~~~

#### **REQ2.2**

The ACC shall command the engine to accelerate if the current speed is lower than the target speed and the ACC does not detect a car in braking distance.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req2_2.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We test REQ2.2 to check if the AC Control prompts the MockEngineController to accelerate if the current speed is lower than the desired speed.

We set the desired speed to `130` through the `SetSpeed` command, then set the vehicle's current speed to `80` through the `CurrentSpeed` command. 

We then evaluate `MockEngineController` to see if `increaseSpeed` has been called.

**TEST DESCRIPTION**:

As mentioned, we call `setSpeed` with value 130 and `currentSpeed` with value 80.

The test passes if the `MockEngineController`'s state is equal to `INCREASE_SPEED`.

**CODE**:

    ~~~
    @Test
    void testReq2_2() {
        acc.setSpeed(130);
        acc.currentSpeed(80);

        assertEquals(MockEngineController.State.INCREASE_SPEED, mockEngineController.getState());
    }
    ~~~

#### **REQ2.3**

The ACC shall command the engine to decrease the speed of the vehicle if the speed is greater than the one set by the driver.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req2_3.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We test REQ2.3 to check if the AC Control prompts the MockEngineController to decelerate if the current speed is higher than the desired speed.

We set the desired speed to `80` through the `SetSpeed` command, then set the vehicle's current speed to `85` through the `CurrentSpeed` command. 

We then evaluate `MockEngineController` to see if `decreaseSpeed` has been called.

**TEST DESCRIPTION**:

As mentioned, we call `setSpeed` with value 80 and `currentSpeed` with value 85.

The test passes if the `MockEngineController`'s state is equal to `DECRESE_SPEED`.

**CODE**:

    ~~~
    @Test
    void testReq2_3() {
        acc.setSpeed(80);
        acc.currentSpeed(85);

        assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
    }
    ~~~

### **REQ4**

The ACC shall command the engine to decrease the speed of the vehicle if it detects a car in front in braking distance.

We define multiple tests for REQ4 so that the functionality is tested with every combination of REQ2's children. We also check if the behavior stays the same if the commands come in different orders.

**NOTE**: We used a constant 90m as the braking distance when writing the tests, but as mentioned in F1, we should consider adaptive measurements to improve safety and efficiency.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req4AndReq2_1CurrentSpeedFirst.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

This test verifies that the AC Control prompts the `MockEngineController` to decelerate if there is another vehicle in braking distance.

We set both the desired speed and the actual speed to `80` through the `SetSpeed` and `CurrentSpeed` commands, then set the distance to `70` through the `CurrentDistance` method.

We then evaluate `MockEngineController` to see if it is still decreasing speed.


**TEST DESCRIPTION**:

As mentioned, we call `setSpeed` with value 80 and `currentSpeed` with value 80.

The test passes if the `MockEngineController`'s state is equal to `DECRESE_SPEED`.

**CODE**:

~~~
@Test
void testReq4AndReq2_1CurrentSpeedFirst() {
    acc.setSpeed(80);
    acc.currentSpeed(80);
    acc.currentDistance(70);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req4AndReq2_1CurrentDistanceFirst.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

The sequence diagram for this test case begins with the ACC system setting the target speed to 80. Then, the current distance to the vehicle ahead is set to 70, followed by setting the current speed to 80. The system evaluates these inputs and determines that the speed should be decreased to maintain a safe distance from the vehicle ahead.

We need this test case to verify that the vehicle does not accelerate even after we get a new signal for the vehicle's current speed.

**TEST DESCRIPTION**:

This test validates that the Adaptive Cruise Control (ACC) system correctly commands a speed decrease when the current distance to the vehicle in front is set first, and the current speed is less than the target speed but still requiring speed adjustment for safety.

We call `setSpeed` with value `80`, followed by setting the current distance to `70`, then setting the vehicle's current speed to `80`.

The test passes if the `MockEngineController`'s state is `DECREASE_SPEED` after the operations.

**CODE**:

~~~
@Test
void testReq4AndReq2_1CurrentDistanceFirst() {
    acc.setSpeed(80);
    acc.currentDistance(70);
    acc.currentSpeed(80);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req4AndReq2_2CurrentSpeedFirst.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

The sequence diagram for this test case begins with the ACC system setting the target speed to 80. Then, the current speed is set to 70, followed by setting the current distance of the vehicle ahead to 70. The system evaluates these inputs and determines that the speed should be decreased to maintain a safe distance from the vehicle ahead.

**TEST DESCRIPTION**:

This test validates that the Adaptive Cruise Control (ACC) system correctly commands a speed decrease when the target speed and current speed are set first, followed by the current distance.

We call `setSpeed` with value `80`, followed setting the vehicle's current speed to `80`, then  setting the current distance to `70`.

The test passes if the `MockEngineController`'s state is `DECREASE_SPEED` after the operations.

**CODE**:

~~~
@Test
void testReq4AndReq2_2CurrentSpeedFirst() {
    acc.setSpeed(80);
    acc.currentSpeed(70);
    acc.currentDistance(70);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req4AndReq2_2CurrentDistanceFirst.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

The sequence diagram for this test case begins with the ACC system setting the target speed to 80. Then, the current distance to the vehicle ahead is set to 70, followed by setting the current speed to 70. The system evaluates these inputs and determines that the speed should be decreased to maintain a safe distance from the vehicle ahead.

**TEST DESCRIPTION**:

This test validates that the Adaptive Cruise Control (ACC) system correctly commands a speed decrease when the target speed and current distance are set first, followed by the current speed.

We call `setSpeed` with value `80`, followed setting the current distance to `70`, then  setting the vehicle's current speed to `70`.

The test passes if the `MockEngineController`'s state is `DECREASE_SPEED` after the operations.

**CODE**:

~~~
@Test
void testReq4AndReq2_2CurrentDistanceFirst() {
    acc.setSpeed(80);
    acc.currentDistance(70);
    acc.currentSpeed(70);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req4AndReq2_3CurrentSpeedFirst.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

In this sequence diagram, the ACC is initially set to a target speed of 70. Subsequently, the current speed is set to 80, followed by setting the current distance to 70. This indicates a scenario where the vehicle is moving faster than the target speed and needs to slow down to maintain the set speed and a safe distance.

**TEST DESCRIPTION**:

This test validates that the Adaptive Cruise Control (ACC) system correctly commands a speed decrease when the target speed and current speed are set first, followed by the distance of the vehicle ahead.

We call `setSpeed` with value `80`, followed setting the vehicle's current speed to `80`, then setting the speed of the vehicle ahead to `70`.

The test passes if the `MockEngineController`'s state is `DECREASE_SPEED` after the operations.


**CODE**:

~~~
@Test
void testReq4AndReq2_3CurrentSpeedFirst() {
    acc.setSpeed(70);
    acc.currentSpeed(80);
    acc.currentDistance(70);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req4AndReq2_3CurrentDistanceFirst.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

In this sequence diagram, the ACC is initially set to a target speed of 70. Subsequently, the current distance is set to 70, followed by setting the vehicle's current speed to 80. This indicates a scenario where the vehicle is moving faster than the target speed and needs to slow down to maintain the set speed and a safe distance, while also having to decelrate because of the vehicle ahead.

**TEST DESCRIPTION**:

This test validates that the Adaptive Cruise Control (ACC) system correctly commands a speed decrease when the target speed current distance are set first, followed by the vehicle's current speed. The vehicle has to decelerate both because the current speed is higher than the target speed and because there is a vehicle ahead.

We call `setSpeed` with value `70`, followed by setting the vehicle distance to `70` and setting the current speed to `80`.

The test passes if the `MockEngineController`'s state is `DECREASE_SPEED` after the operations.

**CODE**:

~~~
@Test
void testReq4AndReq2_3CurrentDistanceFirst() {
    acc.setSpeed(70);
    acc.currentDistance(70);
    acc.currentSpeed(80);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
}
~~~

### **REQ5**

The driver shall be able to turn off the ACC.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req5TurningOffFromOperation.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We test that the ACC can be turned off when we call its `TurnOff` method while in motion (and the ACC is turned on).

We first call the AC Control's `SetSpeed` method with value `130`, check if its state is `Operating`, then turn it off.

**TEST DESCRIPTION**:

After setting the desired speed, we assert that the `ACCController` is operational.
We then turn off the controller and assert that it has indeed been turned off.  

**CODE**:

~~~
@Test
void testReq5TurningOffFromOperation() {
    acc.setSpeed(130);

    assertEquals(ACCController.State.OPERATING, acc.getState());

    acc.turnOff();

    assertEquals(ACCController.State.OFF, acc.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req5StayingInStateOff.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We verify that the AC Control stays turned off when we call its `TurnOff` method.

**TEST DESCRIPTION**:

We assert that the controller is in state `OFF` in the beginning. Afterwards, we call its `turnOff` method and make the same assertion again.

**CODE**:

~~~
@Test
void testReq5StayingInStateOff() {
    assertEquals(ACCController.State.OFF, acc.getState());

    acc.turnOff();

    assertEquals(ACCController.State.OFF, acc.getState());
}
~~~

### **REQ6**

The ACC shall not turn on if the vehicle does not reach the minimum velocity.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req6.svg)


**SEQUENCE DIAGRAM DESCRIPTION**:

We test if the AC Control turns on ifs speed is set to an invalid value. We call the ACC's `SetSpeed` method with value `-20`.

**TEST DESCRIPTION**:

After calling the `setSpeed` method with value `-20`, we make the assertion that the `ACCController` stays turned off.

**CODE**:

~~~
@Test
void testReq6() {
    acc.setSpeed(-20);

    assertEquals(ACCController.State.OFF, acc.getState());
}
~~~

### **REQ7**
The driver can override the set speed while the ACC is operating.

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req7AndReq2_1.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We set both the vehicle's desired speed and current speed to `120`, then verify that the `MockEngineController` is prompted to hold its speed.
Afterwards, we call the `SetSpeed` method with the value of `120` again, and verify that the `MockEngineController` is still holding its speed.

**TEST DESCRIPTION**:

First, we call `setSpeed` and `currentSpeed` with value `120` and make an assertion abou the engine controller's state.
We then call `setSpeed` with the same value and make the same assertion.

**CODE**:

~~~
@Test
void testReq7AndReq2_1() {
    acc.setSpeed(120);
    acc.currentSpeed(120);

    assertEquals(MockEngineController.State.TRAVELING_WITH_ACC_SPEED, mockEngineController.getState());

    acc.setSpeed(120);

    assertEquals(MockEngineController.State.TRAVELING_WITH_ACC_SPEED, mockEngineController.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req7AndReq2_2.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We test if the target speed can be set again after initially prompting the engine controller to increase its speed.

We call the AC Control's `SetSpeed` method with speed `50`, and set the current speed to `40` by calling the control's `CurrentSpeed` method.

We evaluate the `MockEngineController`'s state to verify that it has to increase the speed.

Afterwards, we set the current speed so that it reaches the desired velocity and set the target speed to  60. 

We again evaluate the `MockEngineController`'s state to verify that it has to increase the speed.

**TEST DESCRIPTION**:

First, we call the ACC's `setSpeed` method with value `50`, then its `currentSpeed` method with value `40`.
This should prompt the `MockEngineController` to increase the speed.

We then set the `currentSpeed` to the desired speed of 50, then call tell the ACC to increase the speed to `60`.

We assert that the engine controller is prompted to increase the speed.

**CODE**:

~~~
@Test
void testReq7AndReq2_2() {
    acc.setSpeed(50);
    acc.currentSpeed(40);

    assertEquals(MockEngineController.State.INCREASE_SPEED, mockEngineController.getState());

    acc.currentSpeed(50);
    acc.setSpeed(60);

    assertEquals(MockEngineController.State.INCREASE_SPEED, mockEngineController.getState());
}
~~~

**SEQUENCE_DIAGRAM**:

![diagram](../../../raw/renders/Model.ACC_V_V.Test.TestingSystem.test_Req7AndReq2_3.svg)

**SEQUENCE DIAGRAM DESCRIPTION**:

We test if the target speed can be set again after initially prompting the engine controller to decrease its speed.

We call the AC Control's `SetSpeed` method with speed `40`, and set the current speed to `50` by calling the control's `CurrentSpeed` method.

We evaluate the `MockEngineController`'s state to verify that it has to decrease the speed.

Afterwards, we set the current speed so that it reaches the desired velocity and set the target speed to even lower, 30. 

We again evaluate the `MockEngineController`'s state to verify that it has to decrease the speed.

**TEST DESCRIPTION**:

First, we call the ACC's `setSpeed` method with value `40`, then its `currentSpeed` method with value `50`.
This should prompt the `MockEngineController` to decrease the speed.

We then set the `currentSpeed` to the desired speed of 40, then call tell the ACC to decrease the speed to `30`.

We assert that the engine controller is prompted to decrease the speed.

**CODE**:

~~~
@Test
void testReq7AndReq2_3() {
    acc.setSpeed(40);
    acc.currentSpeed(50);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());

    acc.currentSpeed(40);
    acc.setSpeed(30);

    assertEquals(MockEngineController.State.DECREASE_SPEED, mockEngineController.getState());
}
~~~

## Failing Tests

The system has shown faulty behavior on some of the test scenarios. These are the following:

- **REQ4**: The system malfunctions when the vehicle should slow down because of traffic ahead. If the ACC receives a currentSpeed signal after the initial prompt to slow down the vehicle, it stops decelerating.
    - In the case of `testReq4AndReq2_1CurrentDistanceFirst`, it resumes driving with the target speed.
    - In the case of `testReq4AndReq2_2CurrentDistanceFirst`, it starts to increase the speed again.

- **REQ6**: We can set a negative value for the target speed. Since there is currently no check for prohibited values, this test fails. Based on the refined requirements, we should consider adding this to the implementation.

- **REQ7**: When the vehicle has reached the target speed and we want to set it to an even higher/lower value, the ACC will omit the opposite command.
    - In the case of `testReq7AndReq2_2`, the engine controller will be prompted to decrease the speed instead of further increasing it.
    - In the case of `testReq4AndReq2_2CurrentDistanceFirst`, the engine controller will be prompted to increase the speed instead of further decreasing it.

### Suggestions

As for the Java implementation, the following sections have to be changed to fix failing tests:

**REQ4**:

We suggest using a method that controls the EngineController because calling currentSpeed and CurrentDistance, might lead to unexpected behaviour.

```
public void currentSpeed(int value) {
    if(state==State.OPERATING) {
        lastSpeed = value;
        if(lastSpeed == accSpeed) engineController.holdSpeed();
        else if (lastSpeed < accSpeed) engineController.increaseSpeed();
        else if(lastSpeed > accSpeed) engineController.decreaseSpeed();
    }
}

public void currentDistance(int value) {
    setLastDistanceAndSendSignals(value);
}
private void setLastDistanceAndSendSignals(int currentDistanceValue) {
    lastDistance = currentDistanceValue;
    if(lastDistance < distanceThreshold) engineController.decreaseSpeed();
}
```


**REQ7**:


```
public void setSpeed(int value) {
    if(state == State.OPERATING)
        setAccSpeedAndSendSignals(value); // change
    else if(state == State.OFF) {
        state = State.OPERATING;
        accSpeed = value;
    }
}

private void setAccSpeedAndSendSignals(int value) {
    accSpeed = value;
    if(accSpeed < lastSpeed) engineController.increaseSpeed(); // change
    else if(accSpeed == lastSpeed) engineController.holdSpeed();
    else if(accSpeed > lastSpeed) engineController.decreaseSpeed(); // change
}
```

## Revised Test Objectives

Based on the requirement recommendations and the tests that are failing, we made a comprehensive list about new test objectives.

---

### Testing for Extended Traffic Participant Recognition
Objective: Ensure the ACC system can accurately detect and respond to a variety of traffic participants beyond cars, including motorcycles, bicycles, and pedestrians.

### Braking Distance Calculation Testing
Objective: Validate that the ACC system accurately calculates the braking distance adaptively.

### Testing Speed Adjustment Capability While ACC is Active
Objective: Assess whether the driver can safely and effectively adjust the vehicle's speed while the ACC is engaged, without compromising the system's functionality.

### Safety Protocol Testing in High-Risk Scenarios
Objective: Evaluate the ACC system's response in high-risk scenarios, particularly when it fails to detect vehicles ahead. This includes testing the system's fail-safes and alerts to the driver if such functionality is implemented.

---

### Correcting ACC Deceleration Malfunction (REQ4)

#### Objective for Test Scenario testReq4AndReq2_1CurrentDistanceFirst

Ensure that the ACC system continues to decelerate the vehicle appropriately upon receiving a currentSpeed signal after the initial slow-down prompt, rather than resuming driving at the target speed.

#### Objective for Test Scenario testReq4AndReq2_2CurrentDistanceFirst

Confirm that the ACC system does not erroneously increase the speed after receiving a currentSpeed signal post an initial slow-down instruction.


### Validating Target Speed Value Range (REQ6)

Objective: Implement and test a validation mechanism in the ACC system to prevent the setting of negative target speeds, ensuring that only permissible values are accepted.

### Setting Multiple Targets (REQ7)

#### Objective for Test Scenario testReq7AndReq2_2

Verify that when the target speed is set to a higher value while the vehicle is already at a target speed, the ACC system correctly prompts the engine controller to increase, not decrease, the speed.

#### Objective for a scenario testReq4AndReq2_2CurrentDistanceFirst

Ensure that when the target speed is set to a lower value while the vehicle is at a target speed, the ACC system properly instructs the engine controller to decrease, not increase, the speed.
