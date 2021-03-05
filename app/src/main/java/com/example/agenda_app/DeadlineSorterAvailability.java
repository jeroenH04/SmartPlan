package com.example.agenda_app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DeadlineSorterAvailability implements Comparator<Availability>
{
    @Override
    public int compare(Availability o1, Availability o2) {
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
