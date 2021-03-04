package com.example.agenda_app;

public class Task{
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

    @Override
    public String toString() {
        return "[ name=" + name + ", duration=" + duration + ", intensity=" + intensity +
                ", difficulty=" + difficulty + ", deadline=" + deadline + "]";
    }

    public String getDeadline() {
        return deadline;
    }
}
