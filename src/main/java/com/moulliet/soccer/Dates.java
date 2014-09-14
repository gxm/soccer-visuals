package com.moulliet.soccer;

import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Dates {
    public static final int CURRENT_YEAR = 2014;
    private static final Dates instance = new Dates();
    private Map<Integer, DatePair> yearDateMap = new HashMap<Integer, DatePair>();

    public Dates() {
        yearDateMap.put(2014, new DatePair(new LocalDate(2014, 8, 22), new LocalDate()));
        yearDateMap.put(2013, new DatePair(new LocalDate(2013, 8, 25), new LocalDate(2013, 11, 10)));
        yearDateMap.put(2012, new DatePair(new LocalDate(2012, 8, 19), new LocalDate(2012, 11, 4)));
        yearDateMap.put(2011, new DatePair(new LocalDate(2011, 8, 21), new LocalDate(2011, 11, 6)));
        yearDateMap.put(2010, new DatePair(new LocalDate(2010, 8, 22), new LocalDate(2010, 11, 7)));
        yearDateMap.put(2009, new DatePair(new LocalDate(2009, 9, 1), new LocalDate(2009, 11, 7)));
    }

    public static Dates get() {
        return instance;
    }

    /**
     * Use these to start stepping through the season by week.
     *
     * @param year
     * @return
     */
    public DatePair getStartDate(int year) {
        return yearDateMap.get(year);
    }

    class DatePair {
        LocalDate startDate;
        LocalDate endDate;

        DatePair(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
