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
    
    /** Test get duration of tasks: pre is violated **/
    @Test(expected = IllegalArgumentException.class)
    public void testGetDurationMinutes2() {
        schedule.getDurationMinutes("00:60");
        fail("should have thrown" + IllegalArgumentException.class);
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
        createBasicAvailability();
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

    /** Test remove task with 2 tasks **/
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

    /** Test remove task with 1 task that is split into smaller tasks **/
    @Test()
    public void testRemoveTask5() {
        schedule.addTask("task1", "16:00","relaxed","b",
                "08-02-2020", "18-01-2020");
        assertEquals(schedule.getTaskList().size(), 8);
        schedule.removeTask("task1");
        assertEquals(schedule.getTaskList().size(), 0);
    }

    /** Test complete task with 1 task **/
    @Test()
    public void testCompleteTask() {
        createBasicAvailability();
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
        createBasicAvailability();
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
    
    /** Test add availability **/
    @Test()
    public void testAddAvailability() {
        schedule.addAvailability("26-02-2021","8:00-16:00");
        assertEquals(schedule.getNewAvailability().size(),1);
        schedule.addAvailability("28-02-2021","8:00-9:00");
        assertEquals(schedule.getNewAvailability().size(),2);
    }

    /** Test add availability: pre is violated **/
    @Test(expected = NullPointerException.class)
    public void testAddAvailability2() {
        schedule.addAvailability(null,null);
        fail("Should have thrown"+NullPointerException.class);
    }

    /** Test add availability: pre is violated **/
    @Test(expected = IllegalArgumentException.class)
    public void testAddAvailability3() {
        schedule.addAvailability("","-");
        fail("Should have thrown"+IllegalArgumentException.class);
    }

    /** Test remove availability **/
    @Test()
    public void testRemoveAvailability() {
        schedule.addAvailability("26-02-2021","8:00-16:00");
        assertEquals(schedule.getNewAvailability().size(),1);
        schedule.removeAvailability("26-02-2021","8:00-16:00");
        assertEquals(schedule.getNewAvailability().size(),0);
    }

    /** Test add availability **/
    @Test()
    public void testClearAvailability() {
        schedule.addAvailability("26-02-2021","8:00-16:00");
        assertEquals(schedule.getNewAvailability().size(),1);
        schedule.clearAvailability();
        assertEquals(schedule.getNewAvailability().size(),0);
    }

    /* Create basic availability for the scheduling algorithm */
    void createBasicAvailability() {
        schedule.addAvailability("15-02-2021", "10:00-12:00");
        schedule.addAvailability("16-02-2021", "9:00-10:00");
        schedule.addAvailability("17-02-2021", "14:00-18:00");
        schedule.addAvailability("18-02-2021", "8:50-16:50");
    }

    /* Create advanced availability for the scheduling algorithm */
    void createAdvancedAvailability() {
        schedule.addAvailability("15-02-2021", "0:00-8:00");
        schedule.addAvailability("16-02-2021", "5:00-10:00");
        schedule.addAvailability("17-02-2021", "8:00-16:00");
        schedule.addAvailability("18-02-2021", "3:15-6:15");
        schedule.addAvailability("19-02-2021", "5:30-10:30");
        schedule.addAvailability("20-02-2021", "8:00-16:00");
    }


    void showCreatedSchedule() {
        Map<String, ArrayList<Task>> createdSchedule = schedule.getSchedule();
        for (Map.Entry<String, ArrayList<Task>> entry : createdSchedule.entrySet()) {
            for (Task e : entry.getValue()) {
                System.out.println(entry.getKey() + " : "+ e.getName() + " : " + e.getDuration());
            }
        }
    }

    /** Test of creating optimal schedule with 1 task **/
    @Test()
    public void testCreateSchedule() {
        createBasicAvailability();
        schedule.addTask("task1", "3:30", "a", "b",
                "18-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "10:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "9:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:30-18:00"));
        testAvail.add(new Availability("18-02-2021", "8:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getNewAvailability();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }


    /** Test of creating optimal schedule with 2 tasks **/
    @Test()
    public void testCreateSchedule2() {
        createBasicAvailability();
        schedule.addTask("task1", "1:00","a","b",
                "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "3:00","a","b",
                "21-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "11:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "9:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "8:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getNewAvailability();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
    }
    
    /** Test of creating optimal schedule with 3 tasks **/
    @Test()
    public void testCreateSchedule3() {
        createBasicAvailability();
        schedule.addTask("task1", "1:30","a","b",
                "14-03-2021", "14-01-2021");
        schedule.addTask("task2", "0:30","a","b",
                "16-02-2021", "14-01-2021");
        schedule.addTask("task3", "3:59","a","b",
                "14-03-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "12:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "9:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:59-18:00"));
        testAvail.add(new Availability("18-02-2021", "8:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getNewAvailability();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
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
        createBasicAvailability();
        schedule.addTask("task1", "1:00","a","b",
                "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "3:00","a","b",
                "21-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "11:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "9:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "17:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "8:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getNewAvailability();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(), newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }

        schedule.addTask("task3", "3:00","a","b",
                "21-02-2021", "14-01-2021");

        ArrayList<Availability> testAvail2 = new ArrayList<>();
        testAvail2.add(new Availability("15-02-2021", "11:00-12:00"));
        testAvail2.add(new Availability("16-02-2021", "9:00-10:00"));
        testAvail2.add(new Availability("17-02-2021", "17:00-18:00"));
        testAvail2.add(new Availability("18-02-2021", "11:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail2 = schedule.getNewAvailability();
        for (Availability e : testAvail2) {
            int index = testAvail2.indexOf(e);
            assertEquals(e.getDate(),newAvail2.get(index).getDate());
            assertEquals(e.getDuration(), newAvail2.get(index).getDuration());
        }
    }
    
    /** Test of creating optimal schedule with 2 tasks on same day **/
    @Test()
    public void testCreateSchedule6() {
        createBasicAvailability();
        schedule.addTask("task1", "1:30","a","b",
                "16-02-2021", "14-01-2021");
        schedule.addTask("task2", "0:30","a","b",
                "16-02-2021", "14-01-2021");
        ArrayList<Availability> testAvail = new ArrayList<>();
        testAvail.add(new Availability("15-02-2021", "12:00-12:00"));
        testAvail.add(new Availability("16-02-2021", "9:00-10:00"));
        testAvail.add(new Availability("17-02-2021", "14:00-18:00"));
        testAvail.add(new Availability("18-02-2021", "8:50-16:50"));
        schedule.createSchedule();
        ArrayList<Availability> newAvail = schedule.getNewAvailability();
        for (Availability e : testAvail) {
            int index = testAvail.indexOf(e);
            assertEquals(e.getDate(),newAvail.get(index).getDate());
            assertEquals(e.getDuration(), newAvail.get(index).getDuration());
        }
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
        createAdvancedAvailability();
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
        createBasicAvailability();
        ArrayList<Task> unsortedList = new ArrayList<>();
        ArrayList<Task> sortedList = new ArrayList<>();
        Task task1 = new Task("task1", "2:00","intense","b",
                "25-02-2021", "14-01-2021",120);
        Task task2 = new Task("task2", "2:00","intense","b",
                "24-02-2021", "14-01-2021",120);
        Task task3 = new Task("task3", "2:00","intense","b",
                "23-02-2021", "14-01-2021",120);
        Task task4 = new Task("task4", "2:00","intense","b",
                "16-02-2021", "14-01-2021",120);
        unsortedList.add(task1); unsortedList.add(task2); unsortedList.add(task3);
        unsortedList.add(task4);
        sortedList.add(task4); sortedList.add(task3); sortedList.add(task2); sortedList.add(task1);
        unsortedList.sort(new DeadlineSorterTask());
        assertEquals(unsortedList, sortedList);
    }
}
