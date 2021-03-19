package com.example.agenda_app.algorithms.sorters;

import com.example.agenda_app.algorithms.Item;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateSorterItem implements Comparator<Item> {
    /**
     * Compare the dates of two items in the schedule
     *
     * @param i1, item 1
     * @param i2, item 2
     * @return
     */
    @Override
    public int compare(final Item i1, final Item i2) {
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return f.parse(i1.getDate()).compareTo(f.parse(i2.getDate()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
