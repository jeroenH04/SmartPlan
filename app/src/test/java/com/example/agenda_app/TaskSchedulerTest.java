package com.example.agenda_app;

import junit.framework.TestCase;

import org.junit.Test;

import static org.junit.Assert.fail;

public class TaskSchedulerTest {

    /** Test add task with invalid precondition: name = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask() {
        TaskScheduler schedule = new TaskScheduler();
        schedule.addTask(null, "2:30","a","b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: duration = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask2() {
        TaskScheduler schedule = new TaskScheduler();
        schedule.addTask("task1", null,"a","b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: intensity = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask3() {
        TaskScheduler schedule = new TaskScheduler();
        schedule.addTask("task1", "2:30",null,"b",
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: difficulty = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask4() {
        TaskScheduler schedule = new TaskScheduler();
        schedule.addTask("task1", "2:30","a",null,
                "08-01-2020", "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: deadline = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask5() {
        TaskScheduler schedule = new TaskScheduler();
        schedule.addTask("task1", "2:30","a","b",
                null, "01-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: today = null **/
    @Test(expected = NullPointerException.class)
    public void testAddTask6() {
        TaskScheduler schedule = new TaskScheduler();
        schedule.addTask("task1", "2:30","a","b",
                "08-01-2020", null);
        fail("should have thrown "+ NullPointerException.class);
    }

    /** Test add task with invalid precondition: today <= deadline **/
    @Test(expected = NullPointerException.class)
    public void testAddTask7() {
        TaskScheduler schedule = new TaskScheduler();
        schedule.addTask("task1", "2:30","a","b",
                "08-01-2020", "07-01-2020");
        fail("should have thrown "+ NullPointerException.class);
    }
}