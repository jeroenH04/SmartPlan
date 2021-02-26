package com.example.agenda_app;

public class Task{
    private String name;
    private String duration;
    private String intensity;
    private String difficulty;
    private String deadline;
    private String today;
    private int totalTime;

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

    // Getters of Task.class parameters
    public String getName() { return name; }
    public String getDuration() { return duration; }
    public String getIntensity() { return intensity; }
    public String getDifficulty() { return difficulty; }
    public String getDeadline() {
        return deadline;
    }
    public String getToday() { return today; }
    public int getTotalTime() { return totalTime; }

    @Override
    public String toString() {
        return "[ name=" + name + ", duration=" + duration + ", intensity=" + intensity +
                ", difficulty=" + difficulty + ", deadline=" + deadline + "]";
    }


}
