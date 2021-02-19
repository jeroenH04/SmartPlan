package com.example.agenda_app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DeadlineSorter implements Comparator<Task>
{
    @Override
    public int compare(Task o1, Task o2) {
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return f.parse(o1.getDeadline()).compareTo(f.parse(o2.getDeadline()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
