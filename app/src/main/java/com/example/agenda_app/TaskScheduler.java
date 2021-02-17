package com.example.agenda_app;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.lang.Integer.min;
import static java.lang.Integer.parseInt;

public class TaskScheduler {
    private final ArrayList<Task> taskList = new ArrayList<>();
    private final Map<String, String> availability = new HashMap<>();
    private final Map<String, Task> schedule = new HashMap<>();

    public class Task {
        public String name;
        public String duration;
        public String intensity;
        public String difficulty;
        public String deadline;
        public String today;
        public int totalTime;

        public Task(String name, String duration, String intensity, String difficulty,
                    String deadline, String today, int totalTime) {
            this.name = name;
            this.duration = duration;
            this.intensity = intensity;
            this.difficulty = difficulty;
            this.deadline = deadline;
            this.today = today;
            this.totalTime = totalTime;
        }
    }

    /*
    * Add task to the schedule
    *
    * @param String name
    * @param String duration
    * @param String intensity
    * @param String difficulty
    * @param String deadline
    * @param String today
    * @pre @code{name != null && duration != null && intensity != null && difficulty != null &&
    *           deadline != null && today != null && deadline > today}
    * @throws NullPointerException if precondition is violated
    * @throws IllegalArgumentException if @code{deadline <= today}
    * @modifies taskList
    * @post
     */
    void addTask(String name, String duration, String intensity, String difficulty,
                 String deadline, String today) {
        if (name == null || duration == null || intensity == null || difficulty == null ||
                deadline == null || today == null) {
            throw new NullPointerException("precondition is validated");
        }
        int totalTime = getDurationMinutes(duration);
        if (!compareDates(deadline, today)) {
            throw new IllegalArgumentException("deadline <= today");
        }

        Task task = new Task(name, duration, intensity, difficulty, deadline, today, totalTime);
        taskList.add(task);
    }

    /* Set availability of user
     *
     * @param String taskName, task to be removed
     * @pre @code{\exists i; taskList.contains(i); i.name == taskName}
     * @modifies taskList
     * @throws IllegalArgumentException if pre is violated
     */
    void removeTask(String taskName) {
        for (Task e : taskList) {   // Loop over the task list to find the correct task
            if (e.name.equals(taskName)) {
                taskList.remove(e);
                return;
            }
        }
        throw new IllegalArgumentException("precondition is validated");
    }

    void updateSchedule() {
    }

    /* Set availability of user
     *
     */
    void setAvailability(Map<String, String> input) {
        for (Map.Entry<String, String> entry : input.entrySet()) {
            availability.put(entry.getKey(), entry.getValue());
        }
    }

    /* Create schedule of tasks based on availability
    *
    * @pre @code{availability.size() != 0}
    * @throws IllegalArgumentException if @code{availability.size() == 0}
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    Map<String, String> createSchedule() {
        if (availability.size() == 0) {
            throw new IllegalArgumentException("precondition is validated");
        }
        for (Task e : taskList) {
            int neededTime = e.totalTime;
            int minimum = 10000;
            String newTime = "";
            String bestDate = "";

            for (Map.Entry<String, String> entry : availability.entrySet()) {
                int timeDifference = getDurationMinutes(entry.getValue()) - neededTime;
                if (timeDifference >= 0 && timeDifference < minimum &&
                        compareDates(e.deadline, entry.getKey())) {
                    bestDate = entry.getKey();
                    newTime = timeIntToString(timeDifference);
                    minimum = timeDifference;
                }
            }
            if (bestDate == "") { // no date available
                System.out.println("no date available for task: " + e.name);
            } else {
                schedule.put(bestDate, e);
                availability.replace(bestDate, newTime);
            }
        }
        taskList.clear();
        return availability;
    }

    /*
     * Get the total time of a task in minutes
     *
     * @param String duration
     */
    int getDurationMinutes(String duration) {
        int totalHours = parseInt(duration.substring(0,duration.indexOf(":")));
        int totalMinutes = parseInt(duration.substring(duration.indexOf(":")+1));
        return totalHours * 60 + totalMinutes;
    }

    /*
     * Replace the integer time value (minutes) to a string time value "h:mm"
     *
     * @param int time
     */
    String timeIntToString(int time) {
        int minutes = time % 60;
        int hours = (time - minutes)/60;
        if (minutes < 10) {
            return hours + ":0" + minutes;
        } else {
            return hours + ":" + minutes;
        }
    }

    /*
     * Replace the integer time value (minutes) to a string time value "h:mm"
     *
     * @param String deadline
     * @param String today
     * @post deadline > today
     */
    boolean compareDates(String deadline, String today) {
        int yearDifference = parseInt(deadline.substring(6))-parseInt(today.substring(6));
        int monthDifference = parseInt(deadline.substring(3,5))-parseInt(today.substring(3,5));
        int dayDifference = parseInt(deadline.substring(0,2))-parseInt(today.substring(0,2));
        return yearDifference >= 0 && (yearDifference != 0 || monthDifference >= 0) &&
                (yearDifference != 0 || monthDifference != 0 || dayDifference > 0);
    }

    /* Get created schedule
     *
     */
    Map<String, Task> getSchedule() {
        return schedule;
    }

    /* Get task taskList
     *
     */
    ArrayList<Task> getTaskList() { return taskList; }

}
