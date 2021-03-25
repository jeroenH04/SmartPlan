package com.example.agenda_app.algorithms;

public class Task {
    private String name;
    private String duration;
    private String intensity;
    private String difficulty;
    private String deadline;
    private String today;
    private int totalTime;

    /**
     * Public constructor that takes no arguments, necessary for Firestore.
     */
    public Task() {
    }

    /**
     * Class that represents a task.
     *
     * @param name       name of the task
     * @param duration   duration of the task
     * @param intensity  intensity of the task
     * @param difficulty difficulty of the task
     * @param deadline   deadline of the task
     * @param today      today's date
     * @param totalTime  total time of the task in minutes
     */
    public Task(final String name, final String duration,
                final String intensity,
                final String difficulty, final String deadline,
                final String today, final int totalTime) {
        this.name = name;
        this.duration = duration;
        this.intensity = intensity;
        this.difficulty = difficulty;
        this.deadline = deadline;
        this.today = today;
        this.totalTime = totalTime;
    }

    /**
     * Get the name of the task.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the duration of the task.
     *
     * @return duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Get the intensity of the task.
     *
     * @return intensity
     */
    public String getIntensity() {
        return intensity;
    }

    /**
     * Get the difficulty of the task.
     *
     * @return difficulty
     */
    public String getDifficulty() {
        return difficulty;

    }

    /**
     * Get the deadline of the task.
     *
     * @return deadline
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Get today's date.
     *
     * @return today
     */
    public String getToday() {
        return today;
    }

    /**
     * Get the total time of the task in minutes.
     *
     * @return total time
     */
    public int getTotalTime() {
        return totalTime;
    }

    @Override
    public String toString() {
        return "[ name=" + name + ", duration=" + duration + ", intensity=" +
                intensity + ", difficulty=" + difficulty + ", deadline="
                + deadline + "]";
    }

}
