package com.example.agenda_app.algorithms;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.agenda_app.algorithms.sorters.DateSorterItem;
import com.example.agenda_app.algorithms.sorters.DeadlineSorterAvailability;
import com.example.agenda_app.algorithms.sorters.DeadlineSorterTask;

import java.util.ArrayList;
import static java.lang.Integer.parseInt;

public class TaskScheduler {
    private ArrayList<Task> taskList;
    private ArrayList<Availability> availabilityList;
    private ArrayList<Item> schedule;
    private String name;
    private String studyMode;
    private int schedulerHashcode;
    private String dateOfLastUpdate;
    private int relaxedIntensity;
    private int normalIntensity;
    private int intenseIntensity;

    /**
     * Public constructor that takes no arguments, necessary for Firestore.
     */
    public TaskScheduler() { }

    /**
     * Class that handles the creation of the schedule.
     *
     * @param taskList          list containing all tasks
     * @param availabilityList  list containing availability
     * @param schedule          list containing the created schedule
     * @param name              name of the user
     * @param studyMode         current studymode
     * @param relaxedIntensity  duration of the relaxed intensity
     * @param normalIntensity   duration of the normal intensity
     * @param intenseIntensity  duration of the intense intensity
     * @param schedulerHashcode current hash code
     * @param dateOfLastUpdate  date the last update has been made
     */
    public TaskScheduler(final ArrayList<Task> taskList,
                         final ArrayList<Availability> availabilityList,
                         final ArrayList<Item> schedule, final String name,
                         final String studyMode, final int relaxedIntensity,
                         final int normalIntensity, final int intenseIntensity,
                         final int schedulerHashcode,
                         final String dateOfLastUpdate) {
        this.taskList = taskList;
        this.schedule = schedule;
        this.availabilityList = availabilityList;
        this.name = name; // used in the application
        this.studyMode = studyMode; // used in the application
        this.relaxedIntensity = relaxedIntensity;
        this.normalIntensity = normalIntensity;
        this.intenseIntensity = intenseIntensity;
        this.schedulerHashcode = schedulerHashcode;
        this.dateOfLastUpdate = dateOfLastUpdate;
    }

    /**
     * Add task to the schedule.
     *
     * @param name, name of the task
     * @param duration, duration of the task
     * @param intensity, intensity of the task
     * @param difficulty, difficulty of the task
     * @param deadline, deadline of the task
     * @param today, today's date
     * @pre @code{name != null && name != empty && duration != null &&
     * intensity != null && difficulty != null && deadline != null
     * && today != null && deadline > today} && name is unique
     * @throws NullPointerException if precondition is violated
     * @throws IllegalArgumentException if @code{deadline <= today}
     * || name is not unique
     * @modifies taskList
     * @post @code{taskList.contains(new task(name, duration, intensity,
     * difficulty, deadline, today))]
     */
    public void addTask(final String name, final String duration,
                        final String intensity, final String difficulty,
                        final String deadline, final String today) {
        // check if no parameter is null
        if (name == null || name.isEmpty() || duration == null
                || intensity == null || difficulty == null
                || deadline == null || today == null) {
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
        Task task = new Task(name, duration, intensity, difficulty, deadline,
                today, totalTime);
        taskList.add(task);
        combineTask(taskList);
    }

    /**
     * Combine subtasks of a task into one task again.
     *
     * @param taskList, list containing all tasks
     * @modifies taskList
     */
    public void combineTask(final ArrayList<Task> taskList) {
        for (Task task : taskList) {
            for (Task task2 : taskList) {
                String taskName = task.getName();
                String taskName2 = task2.getName();
                String newTime = updateTime(task.getDuration(),
                        task2.getTotalTime());
                if (taskList.indexOf(task) != taskList.indexOf(task2)) {
                    // Check if the two tasks are from the same parent task
                    if (task.getName().contains(".")
                            && task2.getName().contains(".")
                            && taskName.substring(0, taskName.length() - 1)
                            .equals(taskName2.substring(0,
                                    taskName2.length() - 1))) {
                        // remove old tasks
                        removeTask(taskName);
                        removeTask(taskName2);

                        // add new (merged) task
                        addTask(taskName.substring(0, taskName.length() - 2),
                                newTime, task.getIntensity(),
                                task.getDifficulty(), task.getDeadline(),
                                task.getToday());
                        combineTask(taskList); // continue recursively
                        return;
                    // Check if the task is from the parent task
                    } else if (task2.getName().contains(".")
                            && taskName.equals(taskName2.substring(0,
                                    taskName2.length() - 2))) {
                        // remove old tasks
                        removeTask(taskName);
                        removeTask(taskName2);

                        // add new (merged) task
                        addTask(taskName, newTime,
                                task.getIntensity(), task.getDifficulty(),
                                task.getDeadline(),
                                task.getToday());
                        combineTask(taskList); // continue recursively
                        return;
                    }
                }
            }
        }
    }

    /**
     * Remove task from task list.
     *
     * @param taskName, task to be removed
     * @pre @code{\exists i; taskList.contains(i); i.name == taskName}
     * @modifies taskList
     * @throws IllegalArgumentException if pre is violated
     */
    public void removeTask(final String taskName) {
        // Create a list of tasks that need to be removed
        ArrayList<Task> toBeRemoved = new ArrayList<>();
        for (Task e : taskList) {
            if (e.getName().equals(taskName)) {
                toBeRemoved.add(e); // add a new task to the list
            }
        }
        if (toBeRemoved.size() == 0) {
            throw new IllegalArgumentException(
                    "There exists no task in the tasklist with this name");
        }
        // remove task from taskList
        for (Task e : toBeRemoved) {
            taskList.remove(e);
        }
    }

    /**
     * Remove item from schedule.
     *
     * @param date, date of item to be removed
     * @param taskName, name of the item to be removed
     * @param time, time of the item to be removed
     * @pre @code{\exists i; schedule.contains(i); i.date == date
     * && i.name == taskName && i.time == time}
     * @modifies taskList
     * @throws IllegalArgumentException if pre is violated
     */
    public void removeItem(final String date, final String taskName,
                           final String time) {
        // Create a list of items that need to be removed
        ArrayList<Item> toBeRemoved = new ArrayList<>();
        for (Item i : schedule) {
            if (i.getDate().equals(date) && i.getTime().equals(time)
                    && i.getTask().getName().equals(taskName)) {
                toBeRemoved.add(i);
            }
        }
        if (toBeRemoved.size() == 0) {
            throw new IllegalArgumentException(
                    "There exists no item in the schedule with this name");
        }
        // remove task from schedule
        for (Item i : toBeRemoved) {
            schedule.remove(i);
        }
    }

    /**
     * Remove task from schedule and restore availability.
     *
     * @param taskName, task to be removed
     * @pre @code{\exists i; schedule.contains(i); i.name == taskName}
     * @modifies schedule
     * @throws IllegalArgumentException if pre is violated
     */
    public void completeTask(final String taskName) {
        for (Item i : schedule) {
            if (i.getTask().getName().equals(taskName)) {
                String time = i.getTime();
                addAvailability(i.getDate(), time); // add back the availability
                removeItem(i.getDate(), i.getTask().getName(), i.getTime());
                return;
            }
        }
        throw new IllegalArgumentException(
                "There exists no task in the schedule with this name");
    }

    /**
     * Add availability of user.
     *
     * @param date, date of availability
     * @pre @code{date != null && time != null && date != empty &&
     * time.length() != 1}
     * @throws NullPointerException if @code{date != null && time != null}
     * @throws IllegalArgumentException if @code{date != empty && time.length()
     * != 1}
     * @modifies @code{availabilityList}
     * @post @code{availabilityList.contains(new Availability(date, time))}
     */
    public void addAvailability(final String date, final String time) {
        if (date == null || time == null) {
            throw new NullPointerException("Date or time is null");
        }
        if (date.isEmpty() || time.length() == 1 || time.length() < 11
                || time.charAt(2) != ':' || time.charAt(8) != ':') {
            throw new IllegalArgumentException("Date input is incorrect");
        }
        Availability availability = new Availability(date, time);
        availabilityList.add(availability);
        combineAvailability(availabilityList);
    }

    /**
     * Combine availability that occur on the same date and after each other.
     *
     * @param availabilityList, list containing all availability
     * @modifies availabilityList
     */
    public void combineAvailability(final ArrayList<Availability>
                                            availabilityList) {
        for (Availability avail : availabilityList) {
            for (Availability avail2 : availabilityList) {
                // Check if the date of both availabilities are the same and
                // the end time of the first time equals the start time of the
                // second availability
                if (availabilityList.indexOf(avail) != availabilityList
                        .indexOf(avail2) && avail.getDate().equals(avail2
                        .getDate()) && avail.getEndTime().equals(avail2
                        .getStartTime())) {
                    // remove both availabilities
                    removeAvailability(avail.getDate(), avail.getDuration());
                    removeAvailability(avail2.getDate(), avail2.getDuration());
                    // merge the availabilities together
                    addAvailability(avail.getDate(), avail.getStartTime()
                            + "-" + avail2.getEndTime());
                    // continue recursively
                    combineAvailability(availabilityList);
                    return;
                } else if (availabilityList.indexOf(avail) != availabilityList
                        .indexOf(avail2) && avail.getDate().equals(avail2
                        .getDate()) && avail.getStartTime().equals(avail2
                        .getEndTime())) {
                    // remove both availabilities
                    removeAvailability(avail.getDate(), avail.getDuration());
                    removeAvailability(avail2.getDate(), avail2.getDuration());
                    // merge the availabilities together
                    addAvailability(avail.getDate(), avail2.getStartTime()
                            + "-" + avail.getEndTime());
                    combineAvailability(availabilityList);
                    // continue recursively
                    return;
                }
            }
        }
    }

    /**
     * Remove availability of user.
     *
     * @param date, date of availability
     * @modifies availabilityList
     * @post @code{!availabilityList.contains(new Availability(date, time))}
     */
    public void removeAvailability(final String date, final String time) {
        for (Availability avail : availabilityList) {
            if (avail.getDate().equals(date) && avail.getDuration()
                    .equals(time)) {
                availabilityList.remove(avail);
                return;
            }
        }
    }

    /**
     * Clear availability of user.
     *
     * @modifies @code{availabilityList}
     * @post @code{availabilityList.size() == 0}
     */
    public void clearAvailability() {
        availabilityList.clear();
    }

    /**
     * Clear taskList of user.
     *
     * @modifies @code{taskList}
     * @post @code{tasklist.size() == 0}
     */
    public void clearTasklist() {
        taskList.clear();
    }

    /**
     * Set the intensity of the different modes.
     *
     * @param relaxed, duration for relaxed mode in hours
     * @param normal, duration for normal mode in hours
     * @param intense, duration for intense mode in hours
     * @pre @code{relaxed > 0 && normal > 0 && intense > 0}
     * @throws IllegalArgumentException if pre is violated
     * @modifies relaxedIntensity, normalIntensity, intenseIntensity
     */
    public void setIntensity(final int relaxed, final int normal,
                             final int intense) {
        if (relaxed <= 0 || normal <= 0 || intense <= 0) {
            throw new IllegalArgumentException("intensity <= 0");
        }
        this.relaxedIntensity = relaxed * 60;
        this.normalIntensity = normal * 60;
        this.intenseIntensity = intense * 60;
    }

    /**
     * Check intensity of task and split it into smaller subtasks with a
     * unique name.
     *
     * @param task, task to be split into subtasks
     * @pre @code{task.intensity == 'relaxed' || task.intensity == 'normal'
     *              || task.intensity = 'intense'}
     * @modifies taskList
     * @post
     */
    public void checkIntensity(final Task task) {
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

        if (getDurationMinutes(task.getDuration()) <= intensityNumber) {
            return;
        }

        // calculate the new number of new tasks
        numberOfTasks = (int) Math.ceil(getDurationMinutes(task.getDuration())
                / (double) intensityNumber);

        for (int i = 1; i <= numberOfTasks; ++i) {
            if (i < numberOfTasks || getDurationMinutes(task.getDuration())
                    % intensityNumber == 0) {
                // the new duration of the first tasks is in correspondence to
                // the intensity
                newDuration = timeIntToString(intensityNumber);
                newTask = new Task(task.getName() + "." + i, newDuration,
                        task.getIntensity(), task.getDifficulty(),
                        task.getDeadline(), task.getToday(), intensityNumber);
            } else {
                // the new duration of the last task is the remaining time
                newDuration = timeIntToString(getDurationMinutes(task
                        .getDuration()) % intensityNumber);
                newTask = new Task(task.getName() + "." + i, newDuration,
                        task.getIntensity(), task.getDifficulty(),
                        task.getDeadline(), task.getToday(),
                        getDurationMinutes(task.getDuration())
                                % intensityNumber);
            }
            taskList.add(newTask); // add the new task to the task list
        }
        taskList.remove(task); // remove the old task from the task list
    }

    /**
     *  Create schedule of tasks based on availability.
     *
     *  @pre @code{availability.size() != 0}
     *  @throws IllegalArgumentException if @code{availability.size() == 0}
     */
    @RequiresApi(api = Build.VERSION_CODES.N) // needed for sort
    public void createSchedule() {
        if (availabilityList.size() == 0) {
            throw new IllegalArgumentException("no availability has been set");
        }
        // Create ArrayList for tasks that are unplanned,
        // i.e. no available date in availability
        ArrayList<Task> unPlannedTasks = new ArrayList<>();

        // Copy ArrayList to temporary array to avoid
        // ConcurentModificationException
        ArrayList<Task> tasksToSplit = new ArrayList<>(taskList);
        for (Task e : tasksToSplit) {
            // check the set intensity and split the task accordingly
            checkIntensity(e);
        }

        taskList.sort(new DeadlineSorterTask());

        // Loop over all tasks in the task list and compare it to all days in
        // the availability
        for (Task e : taskList) {
            int neededTime = e.getTotalTime();
            int minimum = 24 * 60; // maximum difference possible
            int index = -1;
            String bestDate = "";
            String startTime = "";

            for (Availability avail : availabilityList) {
                // calculate the time difference between the task duration and
                // availability
                int timeDifference = getDurationMinutes(
                        avail.getAvailableTimeInt(avail.getDuration()))
                        - neededTime;
                if (timeDifference >= 0 && timeDifference < minimum
                        && compareDates(e.getDeadline(), avail.getDate())
                        && !subtaskPlannedOnDate(avail, e)) {
                        bestDate = avail.getDate();
                        index = availabilityList.indexOf(avail);
                        minimum = timeDifference;
                        startTime = avail.getStartTime();
                }
            }
            if (bestDate.equals("")) { // no date available
                unPlannedTasks.add(e); // add the task to the unplanned list
            } else {
                // update the availability
                availabilityList.get(index).updateDuration(neededTime);
                schedule.add(new Item(bestDate, e, startTime + "-"
                        + updateTime(startTime, neededTime)));
            }
        }
        clearTasklist();
        taskList.addAll(unPlannedTasks); // place back the unplanned tasks
        schedule.sort(new DateSorterItem()); // sort the task list on deadline
    }

    /**
     *  Check if no subtasks of a task is planned on the same date.
     *
     *  @param avail, availability to check subtasks on
     *  @param e, task to check
     *  @return @code{true} if subtask of task e is planned on the same date
     */
    boolean subtaskPlannedOnDate(final Availability avail, final Task e) {
        for (Item i : schedule) {
            if (i.getDate().equals(avail.getDate())) {
                String taskNameOld = i.getTask().getName();
                String taskNameNew = e.getName();

                // Check if a subtask of this task is planned on this day
                // i.e. if the characters except the last match and the name
                // ends with a dot
                if (taskNameOld.substring(0, taskNameOld.length() - 1)
                        .equals(taskNameNew.substring(0,
                                taskNameNew.length() - 1))
                        && taskNameOld.substring(taskNameOld.length() - 2,
                                taskNameOld.length() - 1).equals(".")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the total time of a task in minutes.
     *
     * @param duration ("hh:mm")
     * @pre mm < 60
     * @throws IllegalArgumentException if pre is violated
     * @return int totalDuration
     */
    public int getDurationMinutes(final String duration) {
        int totalHours = parseInt(duration.substring(0, duration.indexOf(":")));
        int totalMinutes = parseInt(duration.substring(duration.indexOf(":")
                + 1));
        if (totalMinutes >= 60) {
            throw new IllegalArgumentException("minutes >= 60");
        }
        return totalHours * 60 + totalMinutes;
    }

    /**
     * Update a time variable.
     *
     * @param startTime ("hh:mm")
     * @param duration in minutes
     * @return String newTime
     */
    public String updateTime(final String startTime, final int duration) {
        int updateMinutes = duration % 60;
        int updateHours = (duration - updateMinutes) / 60;
        int startHours = parseInt(startTime.substring(0, startTime.indexOf(":")));
        int startMinutes = parseInt(startTime.substring(startTime.indexOf(":")
                + 1));
        String newTime;

        int newMinutes = startMinutes + updateMinutes;
        int newHours = startHours + updateHours;
        if (newMinutes >= 60) {
            newMinutes -= 60;
            newHours++;
        }
        if (newHours < 10) {
            // declare newTime, add zero in case of single digit minutes
            newTime = "0" + newHours + ":" + newMinutes + "0";
        } else {
            newTime = newHours + ":" + newMinutes + "0";
        }
        return newTime.substring(0, 5);
    }

    /**
     * Replace the integer time value (minutes) to a string time value "h:mm".
     *
     * @param time, time that needs to be converted from int to string
     * @return timeString
     */
    public String timeIntToString(final int time) {
        int minutes = time % 60;
        int hours = (time - minutes) / 60;
        if (minutes < 10) {
            return hours + ":0" + minutes;
        } else {
            return hours + ":" + minutes;
        }
    }

    /**
     * Replace the integer time value (minutes) to a string time value "h:mm"
     *
     * @param deadline
     * @param  today
     * @return deadline > today
     */
    public boolean compareDates(final String deadline, final String today) {
        int yearDifference = parseInt(deadline.substring(6))
                - parseInt(today.substring(6));
        int monthDifference = parseInt(deadline.substring(3, 5))
                - parseInt(today.substring(3, 5));
        int dayDifference = parseInt(deadline.substring(0, 2))
                - parseInt(today.substring(0, 2));
        return yearDifference >= 0 && (yearDifference != 0
                || monthDifference >= 0) && (yearDifference != 0
                || monthDifference != 0 || dayDifference > 0);
    }

    /**
     * Check if the task name is unique; i.e. not used in the schedule or
     * task list.
     *
     * @param taskName
     * @pre @code{taskName != null}
     * @return @code{(! \exists i; taskList.contains(i); i.name == taskName) &&
     * (! \exists j; schedule.contains(j); j.name == taskName)}
     */
    public boolean checkUniqueName(final String taskName) {
        if (taskName == null) {
            throw new NullPointerException("precondition is validated");
        }
        for (Task e: taskList) {
            if (e.getName().equals(taskName)) {
                return false;
            }
        }
        for (Item i : schedule) {
            if (i.getTask().getName().equals(taskName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Reset created schedule and move all tasks back into the tasklist
     *
     * @modifies @code{taskList, schedule}
     */
    public void resetSchedule() {
        ArrayList<Task> addToTaskList = new ArrayList<>();
        ArrayList<Item> createdSchedule = getSchedule();
        for (Item i : createdSchedule) {
            addToTaskList.add(i.getTask());
        }
        for (Task task : addToTaskList) {
            completeTask(task.getName());
            // add task back to the split task list
            addTask(task.getName(), task.getDuration(), task.getIntensity(),
                    task.getDifficulty(), task.getDeadline(), task.getToday());
        }
        schedule.clear(); // clear the schedule
    }

    /**
     * Get created schedule.
     *
     * @return schedule
     */
    public ArrayList<Item> getSchedule() {
        return schedule;
    }

    /**
     *  Get updated availability ordered on date.
     *
     * @return availabilityList
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Availability> getAvailabilityList() {
        availabilityList.sort(new DeadlineSorterAvailability());
        return availabilityList;
    }

    /**
     *  Get task taskList.
     *
     * @return taskList
     */
    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    /**
     *  Get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     *  Get studyMode.
     *
     * @return studyMode
     */
    public String getStudyMode() {
        return studyMode;
    }

    /**
     *  Set studyMode.
     *
     * @param studyMode
     */
    public void setStudyMode(final String studyMode) {
        this.studyMode = studyMode;
    }

    /**
     *  Get intense intensity duration.
     *
     * @return intenseIntensity
     */
    public int getIntenseIntensity() {
        return intenseIntensity;
    }

    /**
     *  Get normal intensity duration.
     *
     * @return normalIntensity
     */
    public int getNormalIntensity() {
        return normalIntensity;
    }

    /**
     *  Get relaxed intensity duration.
     *
     * @return relaxedIntensity
     */
    public int getRelaxedIntensity() {
        return relaxedIntensity;
    }

    /**
     *  Get hashcode of the scheduler currently wanting to edit database or 0
     *  if there is none.
     *
     * @return schedulerHashcode
     */
    public int getSchedulerHashcode() {
        return schedulerHashcode;
    }

    /**
     *  Set schedulerHashcode.
     *
     * @param schedulerHashcode
     */
    public void setSchedulerHashcode(final int schedulerHashcode) {
        this.schedulerHashcode = schedulerHashcode;
    }

    /** Get the date and time of last update.
     *
     * @return String dateOfLastUpdate
     */
    public String getDateOfLastUpdate() {
        return dateOfLastUpdate;
    }

    /**
     *  Set dateOfLastUpdate.
     *
     * @param dateOfLastUpdate
     */
    public void setDateOfLastUpdate(final String dateOfLastUpdate) {
        this.dateOfLastUpdate = dateOfLastUpdate;
    }
}
