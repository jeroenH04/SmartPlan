package com.example.agenda_app;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Integer.parseInt;

public class TaskScheduler {
    // Initialize arraylist containing all tasks to be scheduled
    private final ArrayList<Task> taskList = new ArrayList<>();

    // Initialize map containing availability ("date","hours")
    private final Map<String, String> availability = new HashMap<>();

    // Initialize map containing schedule ("date",tasks)
    private final Map<String, ArrayList<Task>> schedule = new HashMap<>();

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
    *           deadline != null && today != null && deadline > today} && name is unique
    * @throws NullPointerException if precondition is violated
    * @throws IllegalArgumentException if @code{deadline <= today}
    * @throws IllegalArgumentException if name is not unique
    * @modifies taskList
    * @post
     */
    void addTask(String name, String duration, String intensity, String difficulty,
                 String deadline, String today) {
        // check if no parameter is null
        if (name == null || duration == null || intensity == null || difficulty == null ||
                deadline == null || today == null) {
            throw new NullPointerException("precondition is validated");
        }
        // check if the deadline has not passed yet
        if (!compareDates(deadline, today)) {
            throw new IllegalArgumentException("deadline <= today");
        }
        // check if the given name is not used before
        if (!checkUniqueName(name)) {
            throw new IllegalArgumentException("name is not unique");
        }

        // calculate the total time in minutes of the task
        int totalTime = getDurationMinutes(duration);
        Task task = new Task(name, duration, intensity, difficulty, deadline, today, totalTime);
        taskList.add(task);
        checkIntensity(task); // check the set intensity and split the task accordingly
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
     * @param Map<String, String> input, map containing availability per day
     */
    void setAvailability(Map<String, String> input) {
        for (Map.Entry<String, String> entry : input.entrySet()) {
            availability.put(entry.getKey(), entry.getValue());
        }
    }

    /* Check intensity of task and split it into smaller tasks
     *
     * @param Task task
     * @pre @code{task.intensity == 'relaxed' || task.intensity == 'normal'
     *              || task.intensity = 'intense'}
     * @modifies taskList
     * @post
     */
    void checkIntensity(Task task) {
        int numberOfTasks;
        int intensityNumber;
        String newDuration;
        Task newTask;
        switch (task.intensity) {
            case "relaxed":
                intensityNumber = 120; // 2 hours
                break;
            case "normal":
                intensityNumber = 240; // 4 hours
                break;
            case "intense":
                intensityNumber = 480; // 8 hours
                break;
            default:
                return;
        }
        // calculate the new number of tasks
        numberOfTasks = (int) Math.ceil(getDurationMinutes(task.duration) /
                (double) intensityNumber);
        for (int i = 1; i <= numberOfTasks; ++i) {
            if (i < numberOfTasks) {
                // the new duration of the first tasks is in correspondence to the intensity
                newDuration = timeIntToString(intensityNumber);
                newTask = new Task(task.name, newDuration, task.intensity, task.difficulty,
                        task.deadline, task.today, intensityNumber);
            }
            else {
                // the new duration of the last task is the remaining time (time < intensity)
                newDuration = timeIntToString(getDurationMinutes(task.duration) % intensityNumber);
                newTask = new Task(task.name, newDuration, task.intensity, task.difficulty,
                        task.deadline, task.today,
                        getDurationMinutes(task.duration) % intensityNumber);
            }
            taskList.add(newTask); // add the new task to the task list
        }
        taskList.remove(task); // remove the old task from the task list
    }


    /* Create schedule of tasks based on availability
    *
    * @pre @code{availability.size() != 0}
    * @throws IllegalArgumentException if @code{availability.size() == 0}
     */
    @RequiresApi(api = Build.VERSION_CODES.N) // needed to use .replace()
    void createSchedule() {
        if (availability.size() == 0) {
            throw new IllegalArgumentException("precondition is validated");
        }
        for (Task e : taskList) {
            int neededTime = e.totalTime;
            int minimum = 10000;
            String newTime = "";
            String bestDate = "";
            ArrayList<Task> tasksOnDate = new ArrayList<>();

            for (Map.Entry<String, String> entry : availability.entrySet()) {
                int timeDifference = getDurationMinutes(entry.getValue()) - neededTime;
                if (timeDifference >= 0 && timeDifference < minimum &&
                        compareDates(e.deadline, entry.getKey())) {
                    bestDate = entry.getKey();
                    newTime = timeIntToString(timeDifference);
                    minimum = timeDifference;
                }
            }
            if (bestDate.equals("")) { // no date available
                System.out.println("no date available for task: " + e.name + ":" + e.duration);
            } else {
                // Check if there is already a task planned for this date, if so, add the task
                // to the existing arraylist
                if (schedule.containsKey(bestDate)) {
                    tasksOnDate = schedule.get(bestDate);
                }
                tasksOnDate.add(e);
                schedule.put(bestDate, tasksOnDate);
                availability.replace(bestDate, newTime);
            }
        }
        taskList.clear(); // all tasks are planned (if possible), remove all tasks from the list
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
     * @returns deadline > today
     */
    boolean compareDates(String deadline, String today) {
        int yearDifference = parseInt(deadline.substring(6))-parseInt(today.substring(6));
        int monthDifference = parseInt(deadline.substring(3,5))-parseInt(today.substring(3,5));
        int dayDifference = parseInt(deadline.substring(0,2))-parseInt(today.substring(0,2));
        return yearDifference >= 0 && (yearDifference != 0 || monthDifference >= 0) &&
                (yearDifference != 0 || monthDifference != 0 || dayDifference > 0);
    }

    /*
    * Check if the task name is unique; i.e. not used in the schedule or task list
    *
    * @param taskName
    * @pre @code{taskName != null}
    * @returns @code{(! \exists i; taskList.contains(i); i.name == taskName) &&
    *                   (! \exists j; schedule.contains(j); j.name == taskName)}
     */
    boolean checkUniqueName(String taskName) {
        if (taskName == null) {
            throw new NullPointerException("precondition is validated");
        }
        for (Task e: taskList) {
            if (e.name.equals(taskName)) {
                return false;
            }
        }
        for (Map.Entry<String, ArrayList<Task>> entry : schedule.entrySet()) {
            for (Task e : entry.getValue()) {
                if (e.name.equals(taskName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* Get created schedule
     *
     * @returns Map<String, ArrayList<Task>> schedule
     */
    Map<String, ArrayList<Task>> getSchedule() {
        return schedule;
    }

    /* Get updated availability
     *
     * @returns Map<String, String> availability
     */
    Map<String, String> getNewAvailability() {
        return availability;
    }

    /* Get task taskList
     *
     * @returns ArrayList<Task> taskList
     */
    ArrayList<Task> getTaskList() { return taskList; }

}
