package com.example.agenda_app.algorithms.sorters;

import com.example.agenda_app.algorithms.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;


public class DeadlineSorterTask implements Comparator<Task> {
    /**
     * Compare the deadlines of two tasks
     *
     * @param o1, task 1
     * @param o2, task 2
     * @return
     */
    @Override
    public int compare(final Task o1, final Task o2) {
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return f.parse(o1.getDeadline()).compareTo(
                    f.parse(o2.getDeadline()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
