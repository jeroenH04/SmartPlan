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
        if (minutesDifference < 0) {
            minutesDifference = minutesDifference + 60;
            hourDifference--;
        }
        if (minutesDifference < 10) {
            return hourDifference + ":0" + minutesDifference;
        } else {
            return hourDifference + ":" + minutesDifference;
        }
    }

    public void updateDuration(int neededTime) {
        int totalHours1 = parseInt(duration.substring(0,duration.indexOf(":")));
        int totalHours2 = parseInt(duration.substring(duration.indexOf("-")+1,
                duration.indexOf(":", duration.indexOf(":")+1)));
        int totalMinutes1 = parseInt(duration.substring(duration.indexOf(":")+1,
                duration.indexOf("-")));
        int totalMinutes2 = parseInt(duration.substring(duration.indexOf(":",
                duration.indexOf(":")+1)+1));
        int newHours = (int) Math.floor(neededTime / 60);
        int newMinutes = neededTime - 60 * newHours;
        if (totalMinutes1 + newMinutes >= 60) {
            newHours++;
            newMinutes = totalMinutes1 + newMinutes - 60;
        } else {
            newMinutes += totalMinutes1;
        }
        newHours += totalHours1;

        String totalMinutesString1 = String.valueOf(newMinutes);
        String totalMinutesString2 = String.valueOf(totalMinutes2);

        if (totalMinutes2 == 0) {
            totalMinutesString2 = totalMinutes2 + "0";
        }
        if (newMinutes == 0) {
            totalMinutesString1 = newMinutes + "0";
        }
        this.duration = newHours + ":" + totalMinutesString1 + "-" +
                totalHours2 + ":" + totalMinutesString2;
    }

    @Override
    public String toString() {
        return "[ date=" + date + ", duration=" + duration + "]";
    }
}
