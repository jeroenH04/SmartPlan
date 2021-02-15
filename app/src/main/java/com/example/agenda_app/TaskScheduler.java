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
    private final ArrayList<Task> list = new ArrayList<>();

    public static class Task {
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
    * @modifies list
    * @post
     */
    void addTask(String name, String duration, String intensity, String difficulty,
                 String deadline, String today) {
        if (name == null || duration == null || intensity == null || difficulty == null ||
                deadline == null || today == null) {
            throw new NullPointerException("precondition is validated");
        }
        int yearDifference = parseInt(deadline.substring(6))-parseInt(today.substring(6));
        int monthDifference = parseInt(deadline.substring(3,5))-parseInt(today.substring(3,5));
        int dayDifference = parseInt(deadline.substring(0,2))-parseInt(today.substring(0,2));
        int totalTime = getDurationMinutes(duration);
        if (yearDifference < 0 || (yearDifference == 0 && monthDifference < 0) ||
                (yearDifference == 0 && monthDifference == 0 && dayDifference < 0) ) {
            throw new IllegalArgumentException("deadline <= today");
        }

        Task task = new Task(name, duration, intensity, difficulty, deadline, today, totalTime);
        list.add(task);
    }

    void removeTask() {

    }

    void updateSchedule() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    Map<String, String> createSchedule() {
        /* TO DO: change availability getter */
        Map<String, String> availability = new HashMap<>();
        availability.put("15-02-2021", "2:00");
        availability.put("16-02-2021", "1:00");
        availability.put("17-02-2021", "4:00");
        availability.put("18-02-2021", "8:00");

        for (Task e : list) {
            int neededTime = e.totalTime;
            System.out.println("needed time for task:"+neededTime);
            int minimum = 10000;
            String newTime = "";
            String bestDate = "";

            for (Map.Entry<String, String> entry : availability.entrySet()) {
                int timeDifference = getDurationMinutes(entry.getValue()) - neededTime;
                System.out.println("difference: "+timeDifference);
                if (timeDifference >= 0 && timeDifference < minimum) {
                    bestDate = entry.getKey();
                    newTime = (timeDifference - (timeDifference % 60)) / 60+":"+(timeDifference % 60);
                    minimum = timeDifference;
                    System.out.println("new min difference: "+timeDifference);
                }
            }
            availability.replace(bestDate, newTime);
        }
        return availability;
    }

    /*
     * Get the total time of a task in minutes
     *
     * @param String duration
     * @pre @code{duration > 0}
     * @throws IllegalArgumentException if @code{duration <= 0}
     */
    int getDurationMinutes(String duration) {
        int totalHours = parseInt(duration.substring(0,duration.indexOf(":")));
        int totalMinutes = parseInt(duration.substring(duration.indexOf(":")+1));
        int totalTime = totalHours * 60 + totalMinutes;
        if (totalTime < 0) {
            throw new IllegalArgumentException("duration <= 0");
        }
        return totalTime;
    }

    ArrayList<Task> getSchedule() {
        return list;
    }


}
