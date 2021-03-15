package com.example.agenda_app;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Item {

    private String date;
    private Task task;
    private String time;

    public Item() {}

    public Item(String date, Task task, String time) {
        this.date = date;
        this.task = task;
        this.time = time;
    }

    /* Get date of item
     *
     * @returns String date
     */
    public String getDate() { return date; }

    /* Get task of item
     *
     * @returns String date
     */
    public Task getTask() { return task; }

    /* Get time of item
     *
     * @returns String date
     */
    public String getTime() { return time; }

}
