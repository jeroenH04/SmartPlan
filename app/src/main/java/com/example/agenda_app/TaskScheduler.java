package com.example.agenda_app;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import static java.lang.Integer.parseInt;

public class TaskScheduler {
    // Initialize ArrayList containing all tasks to be scheduled
    private ArrayList<Task> taskList;

    // Initialize ArrayList containing all availability of user
    private ArrayList<Availability> availabilityList;

    // Initialize map containing schedule (date,ArrayList<Task>)
    private Map<String, Map<Task,String>> schedule;

    private String name;
    private String studyMode;

    // Default intensity in minutes of different modes
    private int relaxedIntensity;
    private int normalIntensity;
    private int intenseIntensity;

    public TaskScheduler() {}

    public TaskScheduler(ArrayList<Task> taskList, ArrayList<Availability> availabilityList,
                         Map<String, Map<Task,String>> schedule, String name, String studyMode,
                         int relaxedIntensity, int normalIntensity, int intenseIntensity) {
        this.taskList = taskList;
        this.schedule = schedule;
        this.availabilityList = availabilityList;
        this.name = name; // used in the application
        this.studyMode = studyMode; // used in the application
        this.relaxedIntensity = relaxedIntensity;
        this.normalIntensity = normalIntensity;
        this.intenseIntensity = intenseIntensity;
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
    * @pre @code{name != null && name != empty && duration != null && intensity != null && difficulty != null &&
    *           deadline != null && today != null && deadline > today} && name is unique
    * @throws NullPointerException if precondition is violated
    * @throws IllegalArgumentException if @code{deadline <= today}
    * @throws IllegalArgumentException if name is not unique
    * @modifies taskList
    * @post
     */
    public void addTask(String name, String duration, String intensity, String difficulty,
                 String deadline, String today) {
        // check if no parameter is null
        if (name == null || name.isEmpty() || duration == null || intensity == null || difficulty == null ||
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

        // create a new Task object and add it to the task lists
        Task task = new Task(name, duration, intensity, difficulty, deadline, today, totalTime);
        taskList.add(task);
        combineTask(taskList);
    }

    /* Combine subtasks of a task into one task again
     *
     * @param ArrayList<Task> taskList
     * @modifies taskList
     */
    public void combineTask(ArrayList<Task> taskList) {
        for (Task task : taskList) {
            for (Task task2 : taskList) {
                String taskName = task.getName();
                String taskName2 = task2.getName();
                String newTime = updateTime(task.getDuration(), task2.getTotalTime());
                if (taskList.indexOf(task) != taskList.indexOf(task2)) {
                    // Two subtasks
                    if ( task.getName().contains(".") && task2.getName().contains(".") &&
                            taskName.substring(0, taskName.length() - 1).equals(
                            taskName2.substring(0, taskName2.length() - 1)) ) {
                        // remove old tasks
                        removeTask(taskName);
                        removeTask(taskName2);

                        // add new (merged) task
                        addTask(taskName.substring(0, taskName.length() - 2), newTime,
                                task.getIntensity(), task.getDifficulty(), task.getDeadline(),
                                task.getToday());
                        combineTask(taskList); // continue recursively
                        return;
                    // One main task, one subtask
                    } else if ( task2.getName().contains(".") &&
                            taskName.equals(taskName2.substring(0, taskName2.length() - 2)) ) {
                        // remove old tasks
                        removeTask(taskName);
                        removeTask(taskName2);

                        // add new (merged) task
                        addTask(taskName, newTime,
                                task.getIntensity(), task.getDifficulty(), task.getDeadline(),
                                task.getToday());
                        combineTask(taskList); // continue recursively
                        return;
                    }
                }
            }
        }
    }

    /* Remove task from task list
     *
     * @param String taskName, task to be removed
     * @pre @code{\exists i; taskList.contains(i); i.name == taskName}
     * @modifies taskList
     * @throws IllegalArgumentException if pre is violated
     */
    public void removeTask(String taskName) {
        ArrayList<Task> toBeRemoved = new ArrayList<>(); // list of tasks that need to be removed
        for (Task e : taskList) {   // Loop over the task list to find the correct task
            if (e.getName().equals(taskName)) {
                toBeRemoved.add(e);
            }
        }
        if (toBeRemoved.size() == 0) {
            throw new IllegalArgumentException("There exists no task in the tasklist with this name");
        }
        // remove task from taskList
        for (Task e : toBeRemoved) {
            taskList.remove(e);
        }
    }

    /* Remove task from schedule and restore availability
     *
     * @param String taskName, task to be removed
     * @pre @code{\exists i; schedule.contains(i); i.name == taskName}
     * @modifies schedule
     * @throws IllegalArgumentException if pre is violated
     */
    public void completeTask(String taskName) {
        for (Map.Entry<String, Map<Task, String>> entry : schedule.entrySet()) {
            for (Task e : entry.getValue().keySet()) {
                if (e.getName().equals(taskName)) {
                    // get the time of the task
                    String time = entry.getValue().values().toString();
                    time = time.substring(1, time.length() - 1);

                    addAvailability(entry.getKey(), time); // add back the availability
                    entry.getValue().remove(e); // remove task from the array

                    if (entry.getValue().isEmpty()) { // check if the array is now empty
                        schedule.remove(entry.getKey()); // remove date in schedule
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
     * @pre @code{date != null && time != null && date != empty && time.length() != 1}
     * @throws NullPointerException if @code{date != null && time != null}
     * @throws IllegalArgumentException if @code{date != empty && time.length() != 1}
     * @modifies @code{availabilityList}
     * @post @code{availabilityList.contains(new Availability(date, time))}
     */
    public void addAvailability(String date, String time) { // i.e. "26-02-2021","8:00-16:00"
        if (date == null || time == null) {
            throw new NullPointerException("Date or time is null");
        }
        if (date.isEmpty() || time.length() == 1) {
            throw new IllegalArgumentException("Date or time is empty");
        }
        Availability avail = new Availability(date, time);
        availabilityList.add(avail);
        combineAvailability(availabilityList);
    }

    /* Combine availability that occur on the same date and after each other
    *
    * @param ArrayList<Availability> availabilityList
    * @modifies availabilityList
     */
    public void combineAvailability(ArrayList<Availability> availabilityList) {
        for (Availability avail : availabilityList) {
            for (Availability avail2 : availabilityList) {
                // Check if the date of both availabilities are the same and the end time of the
                // first time equals the start time of the second availability
                if (availabilityList.indexOf(avail) != availabilityList.indexOf(avail2) &&
                    avail.getDate().equals(avail2.getDate()) &&
                    avail.getEndTime().equals(avail2.getStartTime())) {
                    // remove both availabilities
                    removeAvailability(avail.getDate(), avail.getDuration());
                    removeAvailability(avail2.getDate(), avail2.getDuration());
                    // merge the availabilities together
                    addAvailability(avail.getDate(), avail.getStartTime() + "-" + avail2.getEndTime());
                    combineAvailability(availabilityList); // continue recursively
                    return;
                }
            }
        }
    }

    /* Remove availability of user
     *
     * @param String date, date of availability
     * @modifies availabilityList
     * @post @code{!availabilityList.contains(new Availability(date, time))}
     */
    public void removeAvailability(String date, String time) { // i.e. "26-02-2021","8:00-16:00"
        for (Availability avail : availabilityList) {
            if (avail.getDate().equals(date) && avail.getDuration().equals(time)) {
                availabilityList.remove(avail);
                return;
            }
        }
    }

    /* Clear availability of user
     *
     * @modifies availabilityList
     * @post @code{availabilityList.size() == 0}
     */
    public void clearAvailability() {
        availabilityList.clear();
    }

    /* Clear taskList of user
     *
     * @modifies @code{taskList}
     * @post @code{tasklist.size() == 0}
     */
    public void clearTasklist() {
        taskList.clear();
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
    public void setIntensity(int relaxed, int normal, int intense) {
        if (relaxed <= 0 || normal <= 0 || intense <= 0) {
            throw new IllegalArgumentException("intensity <= 0");
        }
        this.relaxedIntensity = relaxed * 60;
        this.normalIntensity = normal * 60;
        this.intenseIntensity = intense * 60;
    }

    /* Check intensity of task and split it into smaller subtasks with a unique name
     *
     * @param Task task
     * @pre @code{task.intensity == 'relaxed' || task.intensity == 'normal'
     *              || task.intensity = 'intense'}
     * @modifies taskList
     * @post
     */
    public void checkIntensity(Task task) {
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

        if (getDurationMinutes(task.getDuration()) <= intensityNumber ) {
            return;
        }

        // calculate the new number of new tasks
        numberOfTasks = (int) Math.ceil(getDurationMinutes(task.getDuration()) /
                (double) intensityNumber);

        for (int i = 1; i <= numberOfTasks; ++i) {
            if (i < numberOfTasks || getDurationMinutes(task.getDuration()) % intensityNumber == 0 ) {
                // the new duration of the first tasks is in correspondence to the intensity
                newDuration = timeIntToString(intensityNumber);
                newTask = new Task(task.getName() + "." + i, newDuration, task.getIntensity(),
                        task.getDifficulty(), task.getDeadline(), task.getToday(), intensityNumber);
            }
            else {
                // the new duration of the last task is the remaining time (time < intensity)
                newDuration = timeIntToString(getDurationMinutes(task.getDuration()) %
                        intensityNumber);
                newTask = new Task(task.getName() + "." + i, newDuration, task.getIntensity(),
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
    public void createSchedule() {
        if (availabilityList.size() == 0) {
            throw new IllegalArgumentException("no availability has been set");
        }

        // Create ArrayList for tasks that are unplanned, i.e. no available date in availability
        ArrayList<Task> unPlannedTasks = new ArrayList<>();

        // Copy ArrayList to temporary array to avoid ConcurentModificationException
        ArrayList<Task> tasksToSplit = new ArrayList<>(taskList);
        for (Task e : tasksToSplit) {
            checkIntensity(e); // check the set intensity and split the task accordingly
        }

        taskList.sort(new DeadlineSorterTask()); // sort the task list on deadline

        // Loop over all tasks in the task list and compare it to all days in the availability
        for (Task e : taskList) {
            int neededTime = e.getTotalTime();
            int minimum = 24*60; // maximum difference possible
            int index = -1;
            String bestDate = "";
            String startTime = "";

            // Initialize map containing tasks on a certain date
            Map<Task, String> tasksOnDate = new HashMap<>();

            for (Availability avail : availabilityList) {
                // calculate the time difference between the task duration and availability
                int timeDifference = getDurationMinutes(
                        avail.getAvailableTimeInt(avail.getDuration())) - neededTime;

                if (timeDifference >= 0 && timeDifference < minimum &&
                        compareDates(e.getDeadline(), avail.getDate()) &&
                        !subtaskPlannedOnDate(avail, e) ) {

                        bestDate = avail.getDate(); // get the date of the availability
                        index = availabilityList.indexOf(avail);
                        minimum = timeDifference;
                        startTime = avail.getStartTime();
                }
            }
            if (bestDate.equals("")) { // no date available
                unPlannedTasks.add(e);
            } else {
                // Check if there is already a task planned for this date, if so, add the task
                // to the existing ArrayList
                if (schedule.containsKey(bestDate)) {
                    tasksOnDate = schedule.get(bestDate);
                }
                availabilityList.get(index).updateDuration(neededTime); // update the availability
                tasksOnDate.put(e, startTime + "-" + updateTime(startTime, neededTime) ); // add the task to the ArrayList
                schedule.put(bestDate, tasksOnDate); // add the updated ArrayList to the schedule
            }
        }
        clearTasklist();
        taskList.addAll(unPlannedTasks); // place back the unplanned tasks
    }

    /* Check if no subtasks of a task is planned on the same date
    *
    * @param Availability avail
    * @param Task e
    * @returns @code{true} if subtask of task e is planned on the same date
     */
    boolean subtaskPlannedOnDate(Availability avail, Task e) {
        for (Map.Entry<String, Map<Task, String>> entry : schedule.entrySet()) {
            if (entry.getKey().equals(avail.getDate())) { // check if the dates are the same
                for (Map.Entry<Task, String> d2 : entry.getValue().entrySet()) {
                    String taskNameOld = d2.getKey().getName();
                    String taskNameNew = e.getName();

                    // Check if a subtask of this task is planned on this day
                    // i.e. if the characters except the last match and the name ends with a dot
                    if (taskNameOld.substring(0, taskNameOld.length() - 1)
                            .equals(taskNameNew.substring(0, taskNameNew.length() - 1)) &&
                            taskNameOld.substring(taskNameOld.length() - 2,
                                    taskNameOld.length() - 1).equals(".")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /*
     * Get the total time of a task in minutes
     *
     * @param String duration ("hh:mm")
     * @pre mm < 60
     * @throws IllegalArgument if pre is violated
     * @returns int totalDuration
     */
    public int getDurationMinutes(String duration) {
        int totalHours = parseInt(duration.substring(0,duration.indexOf(":")));
        int totalMinutes = parseInt(duration.substring(duration.indexOf(":")+1));
        if (totalMinutes >= 60) {
            throw new IllegalArgumentException("minutes >= 60");
        }
        return totalHours * 60 + totalMinutes;
    }

    /*
     * Update a time variable
     *
     * @param String startTime ("hh:mm")
     * @param int duration in minutes
     * @returns String newTime
     */
    public String updateTime(String startTime, int duration) {
        int updateMinutes = duration % 60;
        int updateHours = (duration - updateMinutes) / 60;
        int startHours = parseInt(startTime.substring(0,startTime.indexOf(":")));
        int startMinutes = parseInt(startTime.substring(startTime.indexOf(":")+1));
        String newTime;

        int newMinutes = startMinutes + updateMinutes;
        int newHours = startHours + updateHours;
        if (newMinutes >= 60) {
            newMinutes -= 60;
            newHours ++;
        }
        if (newHours < 10) {
            // declare newTime, add zero in case of single digit minutes
            newTime = "0" + newHours + ":" + newMinutes + "0";
        } else {
            newTime = newHours + ":" + newMinutes + "0";
        }
        return newTime.substring(0,5);
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
    public boolean compareDates(String deadline, String today) {
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
        for (Map.Entry<String, Map<Task, String>> entry : schedule.entrySet()) {
            for (Task e : entry.getValue().keySet()) {
                if (e.getName().equals(taskName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* Reset created schedule and move all tasks back into the tasklist
     */
    public void resetSchedule() {
        ArrayList<Task> addToTaskList = new ArrayList<>();
        Map<String, Map<Task, String>> createdSchedule = getSchedule();
        for (Map.Entry<String, Map<Task, String>> entry : createdSchedule.entrySet()) {
            for (Map.Entry<Task, String> entry2 : entry.getValue().entrySet()) {
                addToTaskList.add(entry2.getKey()); // add the task to the ArrayList
            }
        }
        schedule.clear(); // clear the schedule
        for (Task task : addToTaskList) {
            addTask(task.getName(), task.getDuration(), task.getIntensity(),
                    task.getDifficulty(), task.getDeadline(), task.getToday()); // add task back to the split task list
        }
    }

    /* Get created schedule
     *
     * @returns Map<String, ArrayList<Task>> schedule
     */
    public Map<String, Map<Task, String>> getSchedule() {
        return schedule;
    }

    /* Get updated availability ordered on date
     *
     * @returns ArrayList<Availability> availabilityList
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Availability> getAvailabilityList() {
        availabilityList.sort(new DeadlineSorterAvailability());
        return availabilityList;
    }

    /* Get task taskList
     *
     * @returns ArrayList<Task> taskList
     */
    public ArrayList<Task> getTaskList() { return taskList; }

    /* Get name
     *
     * @returns String name
     */
    public String getName() { return name; }

    /* Get studyMode
     *
     * @returns String studyMode
     */
    public String getStudyMode() { return studyMode; }

    /* Get intense intensity duration
     *
     * @returns int intenseIntensity
     */
    public int getIntenseIntensity() { return intenseIntensity; }

    /* Get normal intensity duration
     *
     * @returns int normalIntensity
     */
    public int getNormalIntensity() { return normalIntensity; }

    /* Get relaxed intensity duration
     *
     * @returns int relacedIntensity
     */
    public int getRelaxedIntensity() { return relaxedIntensity; }


}
