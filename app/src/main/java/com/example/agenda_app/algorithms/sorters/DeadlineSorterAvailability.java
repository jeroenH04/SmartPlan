package com.example.agenda_app.algorithms.sorters;

import com.example.agenda_app.algorithms.Availability;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DeadlineSorterAvailability implements Comparator<Availability> {
    /**
     * Compare the dates of two availabilities
     *
     * @param o1, availability 1
     * @param o2, availability 2
     * @return
     */
    @Override
    public int compare(final Availability o1, final Availability o2) {
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
