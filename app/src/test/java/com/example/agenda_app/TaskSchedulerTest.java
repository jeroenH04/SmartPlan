package com.example.agenda_app;

import com.example.agenda_app.algorithms.Availability;
import com.example.agenda_app.algorithms.Item;
import com.example.agenda_app.algorithms.Task;
import com.example.agenda_app.algorithms.TaskScheduler;
import com.example.agenda_app.algorithms.sorters.DeadlineSorterTask;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TaskSchedulerTest {
    TaskScheduler schedule = new TaskScheduler(
            new ArrayList<Task>(),
            new ArrayList<Availability>(),
            new ArrayList<Item>(), "Tester","Off",
            120, 240, 480,
            0, "");

    /** Tests of the getDurationMinutes method */
    // Tests with different input
    @Test()
    public void testGetDurationMinutes() {
        assertEquals(schedule.getDurationMinutes("100:30"), 6030);
        assertEquals(schedule.getDurationMinutes("5:11"), 311);
        assertEquals(schedule.getDurationMinutes("0:01"), 1);
    }
    
    // Test where pre is violated
    @Test(expected = IllegalArgumentException.class)
    public void testGetDurationMinutes2() {
        schedule.getDurationMinutes("00:60");
        fail("should have thrown" + IllegalArgumentException.class);
    }

    /** Tests of the timeIntToString method */
    // Tests with different input
    @Test()
    public void testTimeIntToString() {
        assertEquals(schedule.timeIntToString(180), "3:00");
        assertEquals(schedule.timeIntToString(181), "3:01");
        assertEquals(schedule.timeIntToString(192), "3:12");
    }

    /** Tests of the compareDates method */
    // Tests with different input
    @Test()
    public void testCompareDates() {
        assertFalse(schedule.compareDates("15-02-2021",
                "16-02-2021"));
        assertFalse(schedule.compareDates("15-02-2021",
                "15-02-2021"));
        assertTrue(schedule.compareDates("15-02-2021",
                "14-02-2021"));
        assertTrue(schedule.compareDates("15-02-2021",
                "16-01-2021"));
        assertTrue(schedule.compareDates("15-02-2021",
                "16-02-2020"));
    }

    /** Tests of the uniqueNames method */
    // Tests of the addTask method
    @Test()
    public void testUniqueNames() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-01-2020", "01-01-2020");
        assertFalse(schedule.checkUniqueName("task1"));
        assertTrue(schedule.checkUniqueName("task"));
    }

    // Test with invalid precondition: taskName is null
    @Test(expected = NullPointerException.class)
    public void testUniqueNames2() {
        schedule.checkUniqueName(null);
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Tests of the addTask method */
    // Test with invalid precondition: name = null
    @Test(expected = NullPointerException.class)
    public void testAddTask() {
        schedule.addTask(null, "2:30","a","b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    // Test with invalid precondition: duration = null
    @Test(expected = NullPointerException.class)
    public void testAddTask2() {
        schedule.addTask("task1", null,"a","b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    // Test with invalid precondition: intensity = null
    @Test(expected = NullPointerException.class)
    public void testAddTask3() {
        schedule.addTask("task1", "2:30",null,
                "b", "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    // Test with invalid precondition: difficulty = null
    @Test(expected = NullPointerException.class)
    public void testAddTask4() {
        schedule.addTask("task1", "2:30","a",
                null, "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    // Test with invalid precondition: deadline = null
    @Test(expected = NullPointerException.class)
    public void testAddTask5() {
        schedule.addTask("task1", "2:30","a",
                "b", null, "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    // Test with invalid precondition: today = null
    @Test(expected = NullPointerException.class)
    public void testAddTask6() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-01-2020", null);
        fail("should have thrown "+ NullPointerException.class);
    }

    // Test with invalid precondition: today <= deadline
    @Test(expected = IllegalArgumentException.class)
    public void testAddTask7() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-01-2020", "09-01-2020");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    // Test with invalid precondition: today <= deadline (year difference)
    @Test(expected = IllegalArgumentException.class)
    public void testAddTask8() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-01-2019", "07-02-2020");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    // Test with invalid precondition: today <= deadline (month difference)
    @Test(expected = IllegalArgumentException.class)
    public void testAddTask9() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-01-2020", "08-02-2020");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    // Test with 1 task
    @Test()
    public void testAddTask11() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 1);
    }

    // Test add task with 2 tasks
    @Test()
    public void testAddTask12() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-02-2020", "18-01-2020");
        schedule.addTask("task2", "2:30","a",
                "b", "09-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 2);
    }

    /** Tests of the combineTask method */
    // Test with 3 subtasks of 1 task
    @Test()
    public void testCombineTask() {
        schedule.addTask("homework1.1", "2:00","normal",
                "b", "08-02-2020", "18-01-2020");
        schedule.addTask("homework1.2", "2:00","normal",
                "b", "09-02-2020", "18-01-2020");
        schedule.addTask("homework1.3", "0:30","normal",
                "b", "09-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 1);
    }

    // Test with 2 subtasks and 1 main task of 1 task
    @Test()
    public void testCombineTask2() {
        schedule.addTask("homework1", "2:00","normal",
                "b", "08-02-2020", "18-01-2020");
        schedule.addTask("homework1.2", "2:00","normal",
                "b", "09-02-2020", "18-01-2020");
        schedule.addTask("homework1.3", "0:30","normal",
                "b", "09-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 1);
    }

    /** Tests of the removeTask method */
    // Test with 1 task
    @Test()
    public void testRemoveTask() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 1);
        schedule.removeTask("task1");
        assertEquals(schedule.getTaskList().size(), 0);
    }

    // Test with 2 tasks
    @Test()
    public void testRemoveTask2() {
        schedule.addTask("task1", "2:30","a",
                "b", "08-02-2020", "18-01-2020");
        schedule.addTask("task2", "2:30","a",
                "b", "09-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 2);
        schedule.removeTask("task1");
        schedule.removeTask("task2");
        assertEquals(schedule.getTaskList().size(), 0);
    }

    // Test with invalid task name
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTask3() {
        schedule.removeTask("task");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Test of the clearTaskList method */
    @Test()
    public void testClearTaskList() {
        schedule.addTask("task1", "16:00","relaxed",
                "b", "08-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 1);
        schedule.clearTasklist();
        assertEquals(schedule.getTaskList().size(), 0);
    }

    /** Tests of the completeTask method */
    // Test with 1 task **/
    @Test()
    public void testCompleteTask() {
        createBasicAvailability();
        schedule.addTask("task1", "2:30","a",
                "b", "08-03-2021", "18-01-2020");
        schedule.createSchedule();
        assertEquals(schedule.getSchedule().size(), 1);
        showCreatedSchedule();
        schedule.completeTask("task1");
        showCreatedSchedule();
        assertEquals(schedule.getSchedule().size(), 0);

        // Check that the availability has been reset
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "10:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "14:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "08:50-16:50"));
        schedule.createSchedule();
        System.out.println(schedule.getAvailabilityList());
        // check the new availability
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    // Test with 2 tasks
    @Test()
    public void testCompleteTask2() {
        createBasicAvailability();
        schedule.addTask("task1", "4:00","relaxed",
                "b", "20-02-2021", "18-01-2020");
        schedule.addTask("task2", "1:00","a",
                "b", "16-02-2021", "18-01-2020");
        schedule.createSchedule();
        showCreatedSchedule();
        assertEquals(schedule.getSchedule().size(), 3);
        schedule.completeTask("task1.2");
        assertEquals(schedule.getSchedule().size(), 2);
        schedule.completeTask("task2");
        assertEquals(schedule.getSchedule().size(), 1);
        schedule.completeTask("task1.1");
        assertEquals(schedule.getSchedule().size(), 0);

        // Check that the availability has been reset
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "10:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "14:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "08:50-16:50"));
        schedule.createSchedule();
        // check the new availability
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    // Test with invalid task name
    @Test(expected = IllegalArgumentException.class)
    public void testCompleteTask3() {
        schedule.completeTask("task");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Tests of the addAvailability method */
    @Test()
    public void testAddAvailability() {
        schedule.addAvailability("26-02-2021","08:00-16:00");
        assertEquals(schedule.getAvailabilityList().size(),1);
        schedule.addAvailability("28-02-2021","08:00-09:00");
        assertEquals(schedule.getAvailabilityList().size(),2);
    }

    /** Tests of the clearAvailability method */
    @Test()
    public void testClearAvailability() {
        schedule.addAvailability("26-02-2021","08:00-16:00");
        assertEquals(schedule.getAvailabilityList().size(),1);
        schedule.clearAvailability();
        assertEquals(schedule.getAvailabilityList().size(),0);
    }

    /** Tests of the combineAvailability method */
    // Test with 3 concurrent availabilities
    @Test()
    public void testCombineAvailability() {
        schedule.addAvailability("26-02-2021","08:00-09:00");
        schedule.addAvailability("26-02-2021","09:00-12:15");
        schedule.addAvailability("26-02-2021","12:15-18:00");
        assertEquals(schedule.getAvailabilityList().size(),1);
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("26-02-2021", "08:00-18:00"));
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    // Test with no concurrent availabilities, different dates
    @Test()
    public void testCombineAvailability2() {
        schedule.addAvailability("26-02-2021","08:00-09:00");
        schedule.addAvailability("27-02-2021","09:00-12:15");
        schedule.addAvailability("28-02-2021","12:15-18:00");
        assertEquals(schedule.getAvailabilityList().size(),3);
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("26-02-2021", "08:00-09:00"));
        testAvail.add(new Availability("27-02-2021","09:00-12:15"));
        testAvail.add(new Availability("28-02-2021","12:15-18:00"));
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    /** Tests of the combineAvailability method */
    // Test with 3 concurrent availabilities
    @Test()
    public void testCombineAvailability3() {
        schedule.addAvailability("26-02-2021","16:30-18:00");
        schedule.addAvailability("26-02-2021","14:00-16:30");
        assertEquals(schedule.getAvailabilityList().size(),1);
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("26-02-2021", "14:00-18:00"));
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    /* Create basic availability for the scheduling algorithm */
    void createBasicAvailability() {
        schedule.addAvailability("15-02-2021", "10:00-12:00");
        schedule.addAvailability("16-02-2021", "09:00-10:00");
        schedule.addAvailability("17-02-2021", "14:00-18:00");
        schedule.addAvailability("18-02-2021", "08:50-16:50");
    }

    /* Create advanced availability for the scheduling algorithm */
    void createAdvancedAvailability() {
        schedule.addAvailability("15-02-2021", "00:00-08:00");
        schedule.addAvailability("16-02-2021", "05:00-10:00");
        schedule.addAvailability("17-02-2021", "08:00-16:00");
        schedule.addAvailability("18-02-2021", "03:15-06:15");
        schedule.addAvailability("19-02-2021", "05:30-10:30");
        schedule.addAvailability("20-02-2021", "08:00-16:00");
    }

    void showCreatedSchedule() {
        ArrayList<Item> createdSchedule = schedule.getSchedule();
        for (Item i : createdSchedule) {
            System.out.println(i.getDate() + ", " + i.getTask().getName()
                    + ", " + i.getTime());
        }
    }

    /** Tests of the createSchedule method */
    // Test with 1 task
    @Test()
    public void testCreateSchedule() {
        createBasicAvailability();
        schedule.addTask("task1", "3:30", "a",
                "b", "18-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "10:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:30-18:00"));
        testAvail.add(new Availability("18-02-2021", "08:50-16:50"));
        schedule.createSchedule();
        // check the new availability
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    // Test with 2 tasks
    @Test()
    public void testCreateSchedule2() {
        createBasicAvailability();
        schedule.addTask("task1", "1:00","a",
                "b", "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "3:00","a",
                "b", "21-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "11:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "08:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }
    
    // Test with 3 tasks
    @Test()
    public void testCreateSchedule3() {
        createBasicAvailability();
        schedule.addTask("task1", "1:30","a",
                "b", "14-03-2021", "14-01-2021");
        schedule.addTask("task2", "0:30","a",
                "b", "16-02-2021", "14-01-2021");
        schedule.addTask("task3", "3:59","a",
                "b", "14-03-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "12:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:59-18:00"));
        testAvail.add(new Availability("18-02-2021", "08:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    // Test with no set availability **/
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSchedule4() {
        schedule.addTask("task1", "1:30","a",
                "b", "14-03-2021", "14-01-2021");
        schedule.createSchedule();
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    // Test  with 3 tasks
    @Test()
    public void testCreateSchedule5() {
        createBasicAvailability();
        schedule.addTask("task1", "1:00","a",
                "b", "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "3:00","a",
                "b", "21-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "11:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "08:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(), newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }

        schedule.addTask("task3", "3:00","a",
                "b", "21-02-2021", "14-01-2021");

        ArrayList<Availability> testAvail2 = new ArrayList<>();
        testAvail2.add(new Availability("15-02-2021", "11:00-12:00"));
        testAvail2.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail2.add(new Availability("17-02-2021", "17:00-18:00"));
        testAvail2.add(new Availability("18-02-2021", "11:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail2 = schedule.getAvailabilityList();
        for (Availability e : testAvail2) {
            int index = testAvail2.indexOf(e);
            assertEquals(e.getDate(),newAvail2.get(index).getDate());
            assertEquals(e.getDuration(), newAvail2.get(index).getDuration());
        }
    }
    
    // Test with 2 tasks on same day
    @Test()
    public void testCreateSchedule6() {
        createBasicAvailability();
        schedule.addTask("task1", "1:30","a",
                "b", "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "0:30","a",
                "b", "16-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "12:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "09:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "14:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "08:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getAvailabilityList();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }

    // Test with unplanned task
    @Test()
    public void testCreateSchedule7() {
        createBasicAvailability();
        schedule.addTask("task1", "100:30","a",
                "b", "16-02-2021", "14-01-2021");
        schedule.createSchedule();
        assertEquals(schedule.getSchedule().size(), 0);
        assertEquals(schedule.getTaskList().size(), 1);
    }

    /** Tests of the setIntensity method */
    // Test with invalid pre, relaxed == 0
    @Test(expected = IllegalArgumentException.class)
    public void testSetIntensity() {
        schedule.setIntensity(0,4,8);
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    // Test with invalid pre, normal == 0
    @Test(expected = IllegalArgumentException.class)
    public void testSetIntensity2() {
        schedule.setIntensity(1,0,8);
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    // Test with invalid pre, intense == 0
    @Test(expected = IllegalArgumentException.class)
    public void testSetIntensity3() {
        schedule.setIntensity(1,4,0);
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Tests of the intensityChecker method */
    // Test with intensity = relaxed, duration < relaxed duration (2 hours)
    @Test()
    public void testIntensityChecker() {
        schedule.addTask("task1", "1:00","relaxed",
                "b", "16-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 1);
    }

    // Test with intensity = relaxed, duration > relaxed duration (2 hours)
    @Test()
    public void testIntensityChecker2() {
        Task task = new Task("task1", "6:00","relaxed",
                "b", "16-02-2021", "14-01-2021",
                360);
        schedule.checkIntensity(task);
        assertEquals(schedule.getTaskList().size(), 3);
    }

    // Test with intensity = normal, duration < normal duration (4 hours)
    @Test()
    public void testIntensityChecker3() {
        Task task = new Task("task1", "1:00","normal",
                "b", "16-02-2021", "14-01-2021",
                60);
        schedule.checkIntensity(task);
        assertEquals(schedule.getTaskList().size(), 0);
    }

    // Test with intensity = normal, duration > normal duration (4 hours)
    @Test()
    public void testIntensityChecker4() {
        Task task = new Task("task1", "15:00","normal",
                "b", "23-02-2021", "14-01-2021",
                15*60);
        schedule.checkIntensity(task);
        assertEquals(schedule.getTaskList().size(), 4);
    }

    // Test with intensity = intense, duration < normal duration (8 hours)
    @Test()
    public void testIntensityChecker5() {
        Task task = new Task("task1", "1:00","intense",
                "b", "16-02-2021", "14-01-2021",
                60);
        schedule.checkIntensity(task);
        assertEquals(schedule.getTaskList().size(), 0);
    }

    // Test with intensity = intense, duration > normal duration (8 hours)
    @Test()
    public void testIntensityChecker6() {
        Task task = new Task("task1", "16:00","intense",
                "b", "16-02-2021", "14-01-2021",
                16*60);
        schedule.checkIntensity(task);
        assertEquals(schedule.getTaskList().size(), 2);
    }
    
    // Test with a set intensity
    @Test()
    public void testIntensityChecker7() {
        schedule.setIntensity(1,2,3);
        Task task = new Task("task1", "16:00","relaxed",
                "b", "16-02-2021", "14-01-2021",
                16*60);
        schedule.checkIntensity(task);
        assertEquals(schedule.getTaskList().size(), 16);
    }

    /** Tests of the deadlineSorter method of the DeadlineSorterTask.class */
    @Test()
    public void testDeadlineSorter() {
        createBasicAvailability();
        ArrayList<Task> unsortedList = new ArrayList<>();
        ArrayList<Task> sortedList = new ArrayList<>();
        Task task1 = new Task("task1", "2:00","intense",
                "b", "25-02-2021", "14-01-2021",
                120);
        Task task2 = new Task("task2", "2:00","intense",
                "b", "24-02-2021", "14-01-2021",
                120);
        Task task3 = new Task("task3", "2:00","intense",
                "b", "23-02-2021", "14-01-2021",
                120);
        Task task4 = new Task("task4", "2:00","intense",
                "b", "16-02-2021", "14-01-2021",
                120);
        unsortedList.add(task1); unsortedList.add(task2); unsortedList.add(task3);
        unsortedList.add(task4);
        sortedList.add(task4); sortedList.add(task3); sortedList.add(task2);
        sortedList.add(task1);
        unsortedList.sort(new DeadlineSorterTask());
        assertEquals(unsortedList, sortedList);
    }

    /** Tests of the updateTime method */
    @Test()
    public void testUpdateTime() {
        assertEquals(schedule.updateTime("10:30",20),
                "10:50");
        assertEquals(schedule.updateTime("10:30",40),
                "11:10");
        assertEquals(schedule.updateTime("10:30",60),
                "11:30");
        assertEquals(schedule.updateTime("09:30",15),
                "09:45");
    }

    /** Tests of the resetSchedule method */
    // Test with 2 un-split tasks
    @Test()
    public void testResetSchedule() {
        createBasicAvailability();
        schedule.addTask("task1", "1:30","a",
                "b", "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "5:30","a",
                "b", "19-02-2021", "14-01-2021");
        schedule.createSchedule();
        assertEquals(schedule.getTaskList().size(), 0);
        assertEquals(schedule.getSchedule().size(), 2);
        schedule.resetSchedule();
        assertEquals(schedule.getTaskList().size(), 2);
        assertEquals(schedule.getSchedule().size(), 0);
    }

    // Test with 1 split task
    @Test()
    public void testResetSchedule2() {
        createAdvancedAvailability();
        schedule.addTask("task1", "8:30","normal",
                "b", "28-02-2021", "14-01-2021");
        schedule.createSchedule();
        assertEquals(schedule.getTaskList().size(), 0);
        schedule.resetSchedule();
        assertEquals(schedule.getTaskList().size(), 1);
        assertEquals(schedule.getSchedule().size(), 0);
        System.out.println(schedule.getTaskList());
    }

    // Test with 2 split tasks
    @Test()
    public void testResetSchedule3() {
        createAdvancedAvailability();
        schedule.addTask("task1", "8:30","normal",
                "b", "28-02-2021", "14-01-2021");
        schedule.addTask("task12", "5:00","relaxed",
                "b", "28-02-2021", "14-01-2021");
        schedule.createSchedule();
        showCreatedSchedule();
        assertEquals(schedule.getTaskList().size(), 0);
        schedule.resetSchedule();
        assertEquals(schedule.getTaskList().size(), 2);
        assertEquals(schedule.getSchedule().size(), 0);
    }
}
