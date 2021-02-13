package com.example.agenda_app;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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

        public Task(String name, String duration, String intensity, String difficulty,
                    String deadline, String today) {
            this.name = name;
            this.duration = duration;
            this.intensity = intensity;
            this.difficulty = difficulty;
            this.deadline = deadline;
            this.today = today;
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
    *           deadline != null && today != null && deadline > today && duration > 0}
    * @throws NullPointerException if precondition is violated
    * @throws IllegalArgumentException if @code{deadline <= today || duration <= 0}
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
        int totalHours = parseInt(duration.substring(0,duration.indexOf(":")));
        int totalMinutes = parseInt(duration.substring(duration.indexOf(":")+1));
        int totalTime = totalHours * 60 + totalMinutes;
        if (yearDifference < 0 || (yearDifference == 0 && monthDifference < 0) ||
                (yearDifference == 0 && monthDifference == 0 && dayDifference < 0) ) {
            throw new IllegalArgumentException("deadline <= today");
        }
        if (totalTime <= 0) {
            throw new IllegalArgumentException("duration <= 0");
        }
        Task task = new Task(name, duration, intensity, difficulty, deadline, today);
        list.add(task);
    }

    void removeTask() {

    }

    void updateSchedule() {

    }

    ArrayList<Task> getSchedule() {
        return list;
    }


}
