package com.example.agenda_app;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TaskSchedulerTest {
    TaskScheduler schedule = new TaskScheduler();

    /** Test add task with invalid precondition: name = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask() {
        schedule.addTask(null, "2:30","a","b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: duration = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask2() {
        schedule.addTask("task1", null,"a","b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: intensity = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask3() {
        schedule.addTask("task1", "2:30",null,"b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: difficulty = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask4() {
        schedule.addTask("task1", "2:30","a",null,
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: deadline = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask5() {
        schedule.addTask("task1", "2:30","a","b",
                null, "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: today = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask6() {
        schedule.addTask("task1", "2:30","a","b",
                "08-01-2020", null);
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: today <= deadline **/
    @Test(expected = IllegalArgumentException.class)
    public void testAddTask7() {
        schedule.addTask("task1", "2:30","a","b",
                "08-01-2020", "09-01-2020");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Test add task with invalid precondition: today <= deadline **/
    @Test(expected = IllegalArgumentException.class)
    public void testAddTask8() {
        schedule.addTask("task1", "2:30","a","b",
                "08-01-2019", "07-02-2020");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Test add task with invalid precondition: today <= deadline **/
    @Test(expected = IllegalArgumentException.class)
    public void testAddTask9() {
        schedule.addTask("task1", "2:30","a","b",
                "08-01-2020", "08-02-2020");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Test add task with no task **/
    @Test()
    public void testAddTask10() {
        assertEquals(schedule.getSchedule().size(), 0);
    }

    /** Test add task with 1 task **/
    @Test()
    public void testAddTask11() {
        schedule.addTask("task1", "2:30","a","b",
                "08-02-2020", "18-01-2020");
        assertEquals(schedule.getSchedule().size(), 1);
    }

    /** Test add task with 2 tasks **/
    @Test()
    public void testAddTask12() {
        schedule.addTask("task1", "2:30","a","b",
                "08-02-2020", "18-01-2020");
        schedule.addTask("task2", "2:30","a","b",
                "09-02-2020", "18-01-2020");
        assertEquals(schedule.getSchedule().size(), 2);
    }

    /** Test get duration of tasks **/
    @Test()
    public void testGetDurationMinutes() {
        assertEquals(schedule.getDurationMinutes("100:30"), 6030);
        assertEquals(schedule.getDurationMinutes("5:11"), 311);
        assertEquals(schedule.getDurationMinutes("0:01"), 1);
    }

    /** Test get duration of a task: invalid duration **/
    @Test(expected = IllegalArgumentException.class)
    public void testGetDurationMinutes2() {
        schedule.getDurationMinutes("-1:00");
        fail("should have thrown " + IllegalArgumentException.class);
    }

    /** Test of creating optimal schedule with 1 task **/
    @Test()
    public void testCreateSchedule() {
        schedule.addTask("task1", "3:30","a","b",
                "08-02-2020", "18-01-2020");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("15-02-2021", "2:00");
        testMap.put("16-02-2021", "1:00");
        testMap.put("17-02-2021", "0:30");
        testMap.put("18-02-2021", "8:00");
        assertEquals(schedule.createSchedule(), testMap);
    }

    /** Test of creating optimal schedule with 2 tasks **/
    @Test()
    public void testCreateSchedule2() {
        schedule.addTask("task1", "1:00","a","b",
                "08-02-2020", "18-01-2020");
        schedule.addTask("task2", "3:00","a","b",
                "09-02-2020", "18-01-2020");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("15-02-2021", "2:00");
        testMap.put("16-02-2021", "0:0");
        testMap.put("17-02-2021", "1:0");
        testMap.put("18-02-2021", "8:00");
        assertEquals(schedule.createSchedule(), testMap);
    }

    /** Test of creating optimal schedule with 3 tasks **/
    @Test()
    public void testCreateSchedule3() {
        schedule.addTask("task1", "1:00","a","b",
                "08-02-2020", "18-01-2020");
        schedule.addTask("task2", "7:50","a","b",
                "09-02-2020", "18-01-2020");
        schedule.addTask("task3", "3:01","a","b",
                "09-02-2020", "18-01-2020");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("15-02-2021", "2:00");
        testMap.put("16-02-2021", "0:0");
        testMap.put("17-02-2021", "0:59");
        testMap.put("18-02-2021", "0:10");
        assertEquals(schedule.createSchedule(), testMap);
    }
}