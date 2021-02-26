package com.example.agenda_app;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TaskSchedulerTest {
    TaskScheduler schedule = new TaskScheduler();

    /** Test get duration of tasks **/
    @Test()
    public void testGetDurationMinutes() {
        assertEquals(schedule.getDurationMinutes("100:30"), 6030);
        assertEquals(schedule.getDurationMinutes("5:11"), 311);
        assertEquals(schedule.getDurationMinutes("0:01"), 1);
    }

    /** Test get string value of integer time **/
    @Test()
    public void testTimeIntToString() {
        assertEquals(schedule.timeIntToString(180), "3:00");
        assertEquals(schedule.timeIntToString(181), "3:01");
        assertEquals(schedule.timeIntToString(192), "3:12");
    }

    /** Test compare dates **/
    @Test()
    public void testCompareDates() {
        assertFalse(schedule.compareDates("15-02-2021", "16-02-2021"));
        assertFalse(schedule.compareDates("15-02-2021", "15-02-2021"));
        assertTrue(schedule.compareDates("15-02-2021", "14-02-2021"));
        assertTrue(schedule.compareDates("15-02-2021", "16-01-2021"));
        assertTrue(schedule.compareDates("15-02-2021", "16-02-2020"));
    }

    /** Test unique names in the task list **/
    @Test()
    public void testUniqueNames() {
        schedule.addTask("task1", "2:30","a","b",
                "08-01-2020", "01-01-2020");
        assertFalse(schedule.checkUniqueName("task1"));
        assertTrue(schedule.checkUniqueName("task"));
    }

    /** Test unique names in the schedule list **/
    @Test()
    public void testUniqueNames2() {
        schedule.setAvailability(createBasicAvailability());
        schedule.addTask("task1", "2:30","a","b",
                "20-02-2021", "01-01-2020");
        schedule.createSchedule();
        assertFalse(schedule.checkUniqueName("task1"));
        assertTrue(schedule.checkUniqueName("task"));
    }

    /** Test unique names in the schedule list **/
    @Test(expected = NullPointerException.class)
    public void testUniqueNames3() {
        schedule.checkUniqueName(null);
        fail("should have thrown "+ NullPointerException.class);
    }

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
        assertEquals(schedule.getTaskList().size(), 1);
    }

    /** Test add task with 2 tasks **/
    @Test()
    public void testAddTask12() {
        schedule.addTask("task1", "2:30","a","b",
                "08-02-2020", "18-01-2020");
        schedule.addTask("task2", "2:30","a","b",
                "09-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 2);
    }

    /** Test remove task with 1 task **/
    @Test()
    public void testRemoveTask() {
        schedule.addTask("task1", "2:30","a","b",
                "08-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 1);
        schedule.removeTask("task1");
        assertEquals(schedule.getTaskList().size(), 0);
    }

    /** Test remove task with 1 task **/
    @Test()
    public void testRemoveTask2() {
        schedule.addTask("task1", "2:30","a","b",
                "08-02-2020", "18-01-2020");
        schedule.addTask("task2", "2:30","a","b",
                "09-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 2);
        schedule.removeTask("task1");
        schedule.removeTask("task2");
        assertEquals(schedule.getTaskList().size(), 0);
    }

    /** Test remove task with invalid task name**/
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveTask3() {
        schedule.removeTask("task");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Test complete task with 1 task **/
    @Test()
    public void testCompleteTask() {
        schedule.setAvailability(createBasicAvailability());
        schedule.addTask("task1", "2:30","a","b",
                "08-03-2021", "18-01-2020");
        schedule.createSchedule();
        assertEquals(schedule.getSchedule().size(), 1);
        schedule.completeTask("task1");
        assertEquals(schedule.getSchedule().size(), 0);
    }

    /** Test complete task with 2 tasks **/
    @Test()
    public void testCompleteTask2() {
        schedule.setAvailability(createBasicAvailability());
        schedule.addTask("task1", "1:00","a","b",
                "16-02-2021", "18-01-2020");
        schedule.addTask("task2", "1:00","a","b",
                "16-02-2021", "18-01-2020");
        schedule.createSchedule();
        assertEquals(schedule.getSchedule().size(), 1);
        schedule.completeTask("task1");
        assertEquals(schedule.getSchedule().size(), 1);
        schedule.completeTask("task2");
        assertEquals(schedule.getSchedule().size(), 0);
    }
    /** Test complete task with invalid task name**/
    @Test(expected = IllegalArgumentException.class)
    public void testCompleteTask3() {
        schedule.completeTask("task");
        fail("should have thrown "+ IllegalArgumentException.class);
    }


    /* Create basic availability for the scheduling algorithm */
    Map<String, String> createBasicAvailability() {
        Map<String, String> input = new HashMap<>();
        input.put("15-02-2021", "2:00");
        input.put("16-02-2021", "1:00");
        input.put("17-02-2021", "4:00");
        input.put("18-02-2021", "8:00");
        return input;
    }

    /* Create advanced availability for the scheduling algorithm */
    Map<String, String> createAdvancedAvailability() {
        Map<String, String> input = new HashMap<>();
        input.put("15-02-2021", "8:00");
        input.put("16-02-2021", "5:00");
        input.put("17-02-2021", "8:00");
        input.put("18-02-2021", "3:00");
        input.put("19-02-2021", "5:00");
        input.put("20-02-2021", "8:00");
        return input;
    }

    void showCreatedSchedule() {
        Map<String, ArrayList<Task>> createdSchedule = schedule.getSchedule();
        for (Map.Entry<String, ArrayList<Task>> entry : createdSchedule.entrySet()) {
            for (Task e : entry.getValue()) {
                System.out.println(entry.getKey() + " : "+ e.name + " : " + e.duration);
            }
        }
    }

    /** Test of creating optimal schedule with 1 task **/
    @Test()
    public void testCreateSchedule() {
        schedule.setAvailability(createBasicAvailability());
        schedule.addTask("task1", "3:30", "a", "b",
                "18-02-2021", "14-01-2021");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("15-02-2021", "2:00");
        testMap.put("16-02-2021", "1:00");
        testMap.put("17-02-2021", "0:30");
        testMap.put("18-02-2021", "8:00");
        schedule.createSchedule();
        assertEquals(schedule.getNewAvailability(), testMap);
    }

    /** Test of creating optimal schedule with 2 tasks **/
    @Test()
    public void testCreateSchedule2() {
        schedule.setAvailability(createBasicAvailability());
        schedule.addTask("task1", "1:00","a","b",
                "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "3:00","a","b",
                "21-02-2021", "14-01-2021");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("15-02-2021", "1:00");
        testMap.put("16-02-2021", "1:00");
        testMap.put("17-02-2021", "1:00");
        testMap.put("18-02-2021", "8:00");
        schedule.createSchedule();
        assertEquals(schedule.getNewAvailability(), testMap);
    }

    /** Test of creating optimal schedule with 3 tasks **/
    @Test()
    public void testCreateSchedule3() {
        schedule.setAvailability(createBasicAvailability());
        schedule.addTask("task1", "1:30","a","b",
                "14-03-2021", "14-01-2021");
        schedule.addTask("task2", "0:30","a","b",
                "16-02-2021", "14-01-2021");
        schedule.addTask("task3", "3:59","a","b",
                "14-03-2021", "14-01-2021");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("15-02-2021", "0:00");
        testMap.put("16-02-2021", "1:00");
        testMap.put("17-02-2021", "0:01");
        testMap.put("18-02-2021", "8:00");
        schedule.createSchedule();
        assertEquals(schedule.getNewAvailability(), testMap);
    }

    /** Test of creating optimal schedule with no set availability **/
    @Test(expected = IllegalArgumentException.class)
    public void testCreateSchedule4() {
        schedule.addTask("task1", "1:30","a","b",
                "14-03-2021", "14-01-2021");
        schedule.createSchedule();
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Test of creating optimal schedule with 3 tasks **/
    @Test()
    public void testCreateSchedule5() {
        schedule.setAvailability(createBasicAvailability());
        schedule.addTask("task1", "1:00","a","b",
                "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "3:00","a","b",
                "21-02-2021", "14-01-2021");
        Map<String, String> testMap = new HashMap<>();
        testMap.put("15-02-2021", "1:00");
        testMap.put("16-02-2021", "1:00");
        testMap.put("17-02-2021", "1:00");
        testMap.put("18-02-2021", "8:00");
        schedule.createSchedule();
        assertEquals(schedule.getNewAvailability(), testMap);

        schedule.addTask("task3", "3:00","a","b",
                "21-02-2021", "14-01-2021");

        Map<String, String> testMap2 = new HashMap<>();
        testMap2.put("15-02-2021", "1:00");
        testMap2.put("16-02-2021", "1:00");
        testMap2.put("17-02-2021", "1:00");
        testMap2.put("18-02-2021", "5:00");
        schedule.createSchedule();
        assertEquals(schedule.getNewAvailability(), testMap2);
    }

    /** Test of intensity setter, invalid pre **/
    @Test(expected = IllegalArgumentException.class)
    public void testSetIntensity() {
        schedule.setIntensity(0,4,8);
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Test of intensity checker: intensity = relaxed, duration < relaxed duration (2 hours)**/
    @Test()
    public void testIntensityChecker() {
        schedule.addTask("task1", "1:00","relaxed","b",
                "16-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 1);
    }

    /** Test of intensity checker: intensity = relaxed, duration > relaxed duration (2 hours)**/
    @Test()
    public void testIntensityChecker2() {
        schedule.addTask("task1", "6:00","relaxed","b",
                "16-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 3);
    }

    /** Test of intensity checker: intensity = normal, duration < normal duration (4 hours)**/
    @Test()
    public void testIntensityChecker3() {
        schedule.addTask("task1", "1:00","normal","b",
                "16-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 1);
    }

    /** Test of intensity checker: intensity = normal, duration > normal duration (4 hours)**/
    @Test()
    public void testIntensityChecker4() {
        schedule.setAvailability(createAdvancedAvailability());
        schedule.addTask("task1", "15:00","normal","b",
                "23-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 4);
        schedule.createSchedule();
        showCreatedSchedule();
    }

    /** Test of intensity checker: intensity = intense, duration < normal duration (8 hours)**/
    @Test()
    public void testIntensityChecker5() {
        schedule.addTask("task1", "1:00","intense","b",
                "16-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 1);
    }

    /** Test of intensity checker: intensity = intense, duration > normal duration (8 hours)**/
    @Test()
    public void testIntensityChecker6() {
        schedule.addTask("task1", "16:00","intense","b",
                "16-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 2);
    }

    /** Test of intensity checker: set intensity**/
    @Test()
    public void testIntensityChecker7() {
        schedule.setIntensity(1,2,3);
        schedule.addTask("task1", "16:00","relaxed","b",
                "16-02-2021", "14-01-2021");
        assertEquals(schedule.getTaskList().size(), 16);
    }

    /** Test of deadline sorter **/
    @Test()
    public void testDeadlineSorter() {
        schedule.setAvailability(createBasicAvailability());
        ArrayList<Task> unsortedList = new ArrayList<>();
        ArrayList<Task> sortedList = new ArrayList<>();
        Task task1 = new Task("task1", "2:00","intense","b",
                "25-02-2021", "14-01-2021", 120);
        Task task2 = new Task("task2", "2:00","intense","b",
                "24-02-2021", "14-01-2021", 120);
        Task task3 = new Task("task3", "2:00","intense","b",
                "23-02-2021", "14-01-2021", 120);
        Task task4 = new Task("task4", "2:00","intense","b",
                "16-02-2021", "14-01-2021", 120);
        unsortedList.add(task1); unsortedList.add(task2); unsortedList.add(task3);
        unsortedList.add(task4);
        sortedList.add(task4); sortedList.add(task3); sortedList.add(task2); sortedList.add(task1);
        unsortedList.sort(new DeadlineSorter());
        assertEquals(unsortedList, sortedList);
    }
}