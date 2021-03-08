package com.example.agenda_app;

import static java.lang.Integer.parseInt;

public class Availability {
    private String date;
    private String duration;
    private String availableTime;

    public Availability(String date, String duration) {
        this.date = date;
        this.duration = duration;
        this.availableTime = getAvailableTimeInt(duration);
    }

    public String getDate() { return date; }
    public String getDuration() { return duration; }
    public String getAvailableTime() { return availableTime; }

    public String getStartTime() { return duration.substring(0,duration.indexOf("-")); }

    /* Get total available time from string duration "hh:mm-hh:mm"
    *
    * @param String duration
    * @pre @code{(hourDifference == 0 && minutesDifference > 0) ||
    *             (hourDifference > 0)}
    * @throws IllegalArgumentException if pre is violated
    * @return @code{totalTime}
     */
    public String getAvailableTimeInt(String duration) {
        int totalHours1 = parseInt(duration.substring(0,duration.indexOf(":")));
        int totalHours2 = parseInt(duration.substring(duration.indexOf("-")+1,
                duration.indexOf(":", duration.indexOf(":")+1)));
        int totalMinutes1 = parseInt(duration.substring(duration.indexOf(":")+1,
                duration.indexOf("-")));
        int totalMinutes2 = parseInt(duration.substring(duration.indexOf(":",
                duration.indexOf(":")+1)+1));
        int hourDifference = totalHours2 - totalHours1;
        int minutesDifference = totalMinutes2 - totalMinutes1;
        if ((hourDifference == 0 && minutesDifference < 0) || (hourDifference < 0)) {
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
            return hourDifference + ":0" + minutesDifference; // add leading zero, i.e. 1 -> 01
        } else {
            return hourDifference + ":" + minutesDifference;
        }
    }

    /* Update the duration of the availability
     *
     * @param int neededTime, time needed to be removed from availability
     * @modifies this.duration
     */
    public void updateDuration(int neededTime) {
        int totalHours1 = parseInt(duration.substring(0,duration.indexOf(":")));
        int totalHours2 = parseInt(duration.substring(duration.indexOf("-")+1,
                duration.indexOf(":", duration.indexOf(":")+1)));
        int totalMinutes1 = parseInt(duration.substring(duration.indexOf(":")+1,
                duration.indexOf("-")));
        int totalMinutes2 = parseInt(duration.substring(duration.indexOf(":",
                duration.indexOf(":")+1)+1));
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
        // Make sure the numbers are of the correct format, i.e. hh:mm-hh:mm
        if (totalMinutes2 == 0) { // if first minutes are 0, add another 0. i.e. 0 -> 00
            totalMinutesString2 = totalMinutes2 + "0";
        }
        if (minutesNeeded == 0) { // if second minutes are 0, add another 0. i.e. 0 -> 00
            totalMinutesString1 = minutesNeeded + "0";
        }
        this.duration = hoursNeeded + ":" + totalMinutesString1 + "-" +
                totalHours2 + ":" + totalMinutesString2; // update duration
    }


    @Override
    public String toString() {
        return "[ date=" + date + ", duration=" + duration + "]";
    }
}
