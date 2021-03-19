package com.example.agenda_app.algorithms;

public class Item {
    private String date;
    private Task task;
    private String time;

    /**
     * Public constructor that takes no arguments, necessary for Firestore.
     */
    public Item() { }

    /**
     * Class that represents an item in the schedule
     *
     * @param date  date of the item
     * @param task  task details of the item
     * @param time  time of the item
     */
    public Item(final String date, final Task task, final String time) {
        this.date = date;
        this.task = task;
        this.time = time;
    }

    /**
     *  Get date of item
     *
     * @return String date
     */
    public String getDate() {
        return date;
    }

    /**
     *  Get task of item
     *
     * @return String date
     */
    public Task getTask() {
        return task;
    }

    /**
     * Get time of item
     *
     * @return String date
     */
    public String getTime() {
        return time;
    }
}
