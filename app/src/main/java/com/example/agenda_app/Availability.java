package com.example.agenda_app;

public class Availability {
    private String date;
    private String duration;

    public Availability(String name, String duration) {
        this.date = name;
        this.duration = duration;
    }

    public String getDate() { return date; }
    public String getDuration() { return duration; }

    @Override
    public String toString() {
        return "[ date=" + date + ", duration=" + duration + "]";
    }
}
