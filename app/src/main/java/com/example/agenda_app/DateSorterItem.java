package com.example.agenda_app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateSorterItem implements Comparator<Item>
{
    @Override
    public int compare(Item i1, Item i2) {
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return f.parse(i1.getDate()).compareTo(f.parse(i2.getDate()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
