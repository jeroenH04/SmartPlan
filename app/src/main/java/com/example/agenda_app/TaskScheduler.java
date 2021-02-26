package com.example.agenda_app;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Integer.parseInt;

public class TaskScheduler {
    // @TODO: enable time availability

    // Initialize arraylist containing all tasks to be scheduled
    private final ArrayList<Task> taskList = new ArrayList<>();

    // Initialize arraylist containing all availability
    private final ArrayList<Availability> availabilityList = new ArrayList<>();

    // Initialize map containing schedule ("date",tasks)
    private final Map<String, ArrayList<Task>> schedule = new HashMap<>();

    // default intensity in minutes of different modes
    private int relaxedIntensity = 120;
    private int normalIntensity = 240;
    private int intenseIntensity = 480;

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

    /* Remove task from task list
     *
     * @param String taskName, task to be removed
     * @pre @code{\exists i; taskList.contains(i); i.name == taskName}
     * @modifies taskList
     * @throws IllegalArgumentException if pre is violated
     */
    void removeTask(String taskName) {
        for (Task e : taskList) {   // Loop over the task list to find the correct task
            if (e.getName().equals(taskName)) {
                taskList.remove(e);
                return;
            }
        }
        throw new IllegalArgumentException("There exists no task in the tasklist with this name");
    }

    /* Remove task from schedule
     *
     * @param String taskName, task to be removed
     * @pre @code{\exists i; schedule.contains(i); i.name == taskName}
     * @modifies schedule
     * @throws IllegalArgumentException if pre is violated
     */
    void completeTask(String taskName) {
        for (Map.Entry<String, ArrayList<Task>> entry : schedule.entrySet()) {
            for (Task e : entry.getValue()) {
                if (e.getName().equals(taskName)) {
                    entry.getValue().remove(e);
                    if (entry.getValue().isEmpty()) {
                        schedule.remove(entry.getKey());
                    }
                    return;
                }
            }
        }
        throw new IllegalArgumentException("There exists no task in the schedule with this name");
    }

    /* Add availability of user
     *
     * @param String date, date of availability
     * @param String time, time of availability
     * @modifies availabilityList
     * @post @code{availabilityList.contains(new Availability(date, time))}
     */
    void addAvailability(String date, String time) { // i.e. "26-02-2021","8:00-16:00"
        Availability avail = new Availability(date, time);
        availabilityList.add(avail);
    }

    /* Clear availability of user
     *
     * @modifies availabilityList
     * @post @code{availabilityList.size() == 0}
     */
    void clearAvailability() {
        availabilityList.clear();
    }

    /* Set the intensity of the different modes
    *
    * @param integer relaxed, duration for relaxed mode in hours
    * @param integer normal, duration for normal mode in hours
    * @param integer intense, duration for intense mode in hours
    * @pre @code{relaxed > 0 && normal > 0 && intense > 0}
    * @throws IllegalArgumentException if pre is violated
    * @modifies relaxedIntensity, normalIntensity, intenseIntensity
     */
    void setIntensity(int relaxed, int normal, int intense) {
        if (relaxed <= 0 || normal <= 0 || intense <= 0) {
            throw new IllegalArgumentException("intensity <= 0");
        }
        this.relaxedIntensity = relaxed * 60;
        this.normalIntensity = normal * 60;
        this.intenseIntensity = intense * 60;
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
        switch (task.getIntensity()) {
            case "relaxed":
                intensityNumber = relaxedIntensity;
                break;
            case "normal":
                intensityNumber = normalIntensity;
                break;
            case "intense":
                intensityNumber = intenseIntensity;
                break;
            default:
                return;
        }
        // calculate the new number of tasks
        numberOfTasks = (int) Math.ceil(getDurationMinutes(task.getDuration()) /
                (double) intensityNumber);
        for (int i = 1; i <= numberOfTasks; ++i) {
            if (i < numberOfTasks) {
                // the new duration of the first tasks is in correspondence to the intensity
                newDuration = timeIntToString(intensityNumber);
                newTask = new Task(task.getName(), newDuration, task.getIntensity(),
                        task.getDifficulty(), task.getDeadline(), task.getToday(), intensityNumber);
            }
            else {
                // the new duration of the last task is the remaining time (time < intensity)
                newDuration = timeIntToString(getDurationMinutes(task.getDuration()) %
                        intensityNumber);
                newTask = new Task(task.getName(), newDuration, task.getIntensity(),
                        task.getDifficulty(), task.getDeadline(), task.getToday(),
                        getDurationMinutes(task.getDuration()) % intensityNumber);
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
    @RequiresApi(api = Build.VERSION_CODES.N) // needed for sort
    void createSchedule() {
        if (availabilityList.size() == 0) {
            throw new IllegalArgumentException("no availability has been set");
        }
        taskList.sort(new DeadlineSorter()); // sort the task list on deadline
        for (Task e : taskList) {
            int neededTime = e.getTotalTime();
            int minimum = 10000;
            int index = -1;
            int newTime = 0;
            int timeUsed = 0;
            String bestDate = "";
            ArrayList<Task> tasksOnDate = new ArrayList<>();

            for (Availability avail : availabilityList) {
                int timeDifference = getDurationMinutes(
                        avail.getAvailableTimeInt(avail.getDuration())) - neededTime;
                if (timeDifference >= 0 && timeDifference < minimum &&
                        compareDates(e.getDeadline(), avail.getDate())) {
                    bestDate = avail.getDate();
                    index = availabilityList.indexOf(avail);
                    newTime = timeDifference;
                    timeUsed = neededTime;
                    minimum = timeDifference;
                }
            }
            if (bestDate.equals("")) { // no date available
                System.out.println("no date available for task: " + e.getName() + ":" + e.getDuration());
            } else {
                // Check if there is already a task planned for this date, if so, add the task
                // to the existing arraylist
                if (schedule.containsKey(bestDate)) {
                    tasksOnDate = schedule.get(bestDate);
                }
                tasksOnDate.add(e);
                schedule.put(bestDate, tasksOnDate);
                availabilityList.get(index).updateDuration(neededTime);
              //  Availability newAvail = new Availability(bestDate, newTime);
              //  availabilityList.set(index,newAvail);
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
            if (e.getName().equals(taskName)) {
                return false;
            }
        }
        for (Map.Entry<String, ArrayList<Task>> entry : schedule.entrySet()) {
            for (Task e : entry.getValue()) {
                if (e.getName().equals(taskName)) {
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
     * @returns ArrayList<Availability> availabilityList
     */
    ArrayList<Availability> getNewAvailability() {
        return availabilityList;
    }

    /* Get task taskList
     *
     * @returns ArrayList<Task> taskList
     */
    ArrayList<Task> getTaskList() { return taskList; }

}
