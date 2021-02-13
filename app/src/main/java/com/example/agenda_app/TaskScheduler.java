package com.example.agenda_app;

import java.util.HashMap;

public class TaskScheduler {

    public static class Task {
        public String name;
        public String duration;
        public String intensity;
        public String difficulty;
        public String deadline;

        public Task(String name, String duration, String intensity, String difficulty,
                    String deadline) {
            this.name = name;
            this.duration = duration;
            this.intensity = intensity;
            this.difficulty = difficulty;
            this.deadline = deadline;
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
    * @post
    *
    *
     */
    void addTask(String name, String duration, String intensity, String difficulty,
                 String deadline, String today) {
        Task task = new Task(name, duration, intensity, difficulty, deadline);
        System.out.println(task);
    }

    void removeTask() {

    }

    void updateSchedule() {

    }


}
