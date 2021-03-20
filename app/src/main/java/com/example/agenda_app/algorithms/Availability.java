package com.example.agenda_app.algorithms;

import static java.lang.Integer.parseInt;

public class Availability {
    private String date;
    private String duration;
    private String availableTime;
    private String startTime;
    private String endTime;

    /**
     * Public constructor that takes no arguments, necessary for Firestore.
     */
    public Availability() { }

    /**
     * Class that represents an availability.
     *
     * @param date      date of the availability
     * @param duration  duration of the availability
     */
    public Availability(final String date, final String duration) {
        this.date = date;
        this.duration = duration;
        this.availableTime = getAvailableTimeInt(duration);
        this.startTime = getStartTime();
        this.endTime = getEndTime();
    }

    /**
     * Return the date of the availability object.
     *
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * Return the duration of the availability object.
     *
     * @return duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Return the available time of the availability object.
     *
     * @return availableTime
     */
    public String getAvailableTime() {
        return availableTime;
    }

    /**
     * Return the start time of the availability object.
     *
     * @return startTime
     */
    public String getStartTime() {
        return duration.substring(0, duration.indexOf("-"));
    }

    /**
     * Return the end time of the availability object.
     *
     * @return endTime
     */
    public String getEndTime() {
        return duration.substring(duration.indexOf("-") + 1);
    }

    /**
     * Get total available time from string duration "hh:mm-hh:mm".
     *
     * @param duration
     * @pre @code{(hourDifference == 0 && minutesDifference > 0) ||
     * (hourDifference > 0)}
     * @throws IllegalArgumentException if pre is violated
     * @return @code{totalTime}
     */
    public String getAvailableTimeInt(final String duration) {
        int totalHours1 = parseInt(duration.substring(0,
                duration.indexOf(":")));
        int totalHours2 = parseInt(duration.substring(duration.indexOf("-") + 1,
                duration.indexOf(":", duration.indexOf(":") + 1)));
        int totalMinutes1 = parseInt(duration.substring(
                duration.indexOf(":") + 1, duration.indexOf("-")));
        int totalMinutes2 = parseInt(duration.substring(duration.indexOf(":",
                duration.indexOf(":") + 1) + 1));
        int hourDifference = totalHours2 - totalHours1;
        int minutesDifference = totalMinutes2 - totalMinutes1;
        if ((hourDifference == 0 && minutesDifference < 0)
                || (hourDifference < 0)) {
            throw new IllegalArgumentException("negative time available");
        }
        if (totalHours1 >= 24 || totalHours2 >= 24) {
            throw new IllegalArgumentException("invalid time");
        }
        // If minutesDifference < 0, decrease the hourDifference
        if (minutesDifference < 0) {
            minutesDifference = minutesDifference + 60;
            hourDifference--;
        }
        if (minutesDifference < 10) {
            // add leading zero, i.e. 1 -> 01
            return hourDifference + ":0" + minutesDifference;
        } else {
            return hourDifference + ":" + minutesDifference;
        }
    }

    /** Update the duration of the availability.
     *
     * @param neededTime, time needed to be removed from availability
     * @modifies this.duration
     */
    public void updateDuration(final int neededTime) {
        int totalHours1 = parseInt(duration.substring(0, duration.indexOf(":")));
        int totalHours2 = parseInt(duration.substring(duration.indexOf("-") + 1,
                duration.indexOf(":", duration.indexOf(":") + 1)));
        int totalMinutes1 = parseInt(duration.substring(duration.indexOf(":")
                        + 1, duration.indexOf("-")));
        int totalMinutes2 = parseInt(duration.substring(
                duration.indexOf(":",duration.indexOf(":") + 1)
                        + 1));
        int hoursNeeded = (int) Math.floor(neededTime / 60.0);
        int minutesNeeded = neededTime - 60 * hoursNeeded;
        if (totalMinutes1 + minutesNeeded >= 60) {
            hoursNeeded++; // increase hours as new minutes >= 60
            minutesNeeded = totalMinutes1 + minutesNeeded - 60;
        } else {
            minutesNeeded += totalMinutes1;
        }
        hoursNeeded += totalHours1;

        String totalMinutesString1 = String.valueOf(minutesNeeded);
        String totalMinutesString2 = String.valueOf(totalMinutes2);
        String hoursNeededString = String.valueOf(hoursNeeded);
        String totalHoursString = String.valueOf(totalHours2);
        // Make sure the numbers are of the correct format, i.e. hh:mm-hh:mm
        if (totalMinutes2 == 0) {
            // if first minutes are 0, add another 0. i.e. 0 -> 00
            totalMinutesString2 = totalMinutes2 + "0";
        }
        if (minutesNeeded < 10) {
            // if second minutes are 0, add another 0. i.e. 0 -> 00
            totalMinutesString1 = "0" + minutesNeeded;
        }
        if (hoursNeeded < 10) {
            hoursNeededString = "0" + hoursNeeded;
        }
        if (totalHours2 < 10) {
            totalHoursString = "0" + totalHours2;
        }
        System.out.println(totalMinutesString1);
        this.duration = hoursNeededString + ":" + totalMinutesString1 + "-"
                + totalHoursString + ":" + totalMinutesString2; // update duration
    }

    @Override
    public String toString() {
        return "[ date=" + date + ", duration=" + duration + "]";
    }
}
