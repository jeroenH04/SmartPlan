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

public class AvailabilityTest {

    /** Tests of getAvailableTime method **/
    // Tests with different time differences
    @Test()
    public void testGetAvailableTime() {
        Availability testAvail = new Availability("26-02-2021","9:30-10:40");
        assertEquals(testAvail.getAvailableTimeInt("9:30-10:40"), "1:10");
        assertEquals(testAvail.getAvailableTimeInt("10:40-12:30"), "1:50");
        assertEquals(testAvail.getAvailableTimeInt("0:00-10:40"), "10:40");
        assertEquals(testAvail.getAvailableTimeInt("23:01-23:02"), "0:01");
    }

    // Test with pre violated, end time > begin time (hours)
    @Test(expected = IllegalArgumentException.class)
    public void testGetAvailableTime2() {
        Availability testAvail = new Availability("26-02-2021","9:30-10:40");
        testAvail.getAvailableTimeInt("11:50-10:40");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    // Test with pre violated, end time > begin time (minutes)
    @Test(expected = IllegalArgumentException.class)
    public void testGetAvailableTime3() {
        Availability testAvail = new Availability("26-02-2021","9:30-10:40");
        testAvail.getAvailableTimeInt("10:50-10:40");
        fail("should have thrown "+ IllegalArgumentException.class);
    }

    /** Tests of updateDuration method **/
    // Test with neededTime < 60, double digit
    @Test()
    public void testUpdateDuration() {
        Availability testAvail = new Availability("26-02-2021","9:30-10:40");
        testAvail.updateDuration(50);
        assertEquals(testAvail.getDuration(),"10:20-10:40");
    }

    // Test with neededTime < 60, single digit
    @Test()
    public void testUpdateDuration2() {
        Availability testAvail = new Availability("26-02-2021","9:30-10:40");
        testAvail.updateDuration(1);
        assertEquals(testAvail.getDuration(),"9:31-10:40");
    }

    // Test with neededTime > 60
    @Test()
    public void testUpdateDuration3() {
        Availability testAvail = new Availability("26-02-2021","9:30-18:40");
        testAvail.updateDuration(135);
        assertEquals(testAvail.getDuration(),"11:45-18:40");
    }

    // Test with neededTime == 60
    @Test()
    public void testUpdateDuration4() {
        Availability testAvail = new Availability("26-02-2021","9:00-18:00");
        testAvail.updateDuration(60);
        assertEquals(testAvail.getDuration(),"10:00-18:00");
    }

}


