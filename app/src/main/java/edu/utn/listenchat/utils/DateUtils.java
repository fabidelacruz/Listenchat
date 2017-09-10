package edu.utn.listenchat.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fabian on 9/10/17.
 */

public class DateUtils {

    private static final DateFormat UNTIL_SECOND_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateFormat UNTIL_MINUTE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private static final DateFormat UNTIL_DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    public static String toStringUntilMinute(Date date) {
        return UNTIL_MINUTE_FORMAT.format(date);
    }

    public static String toStringUntilDay(Date date) {
        return UNTIL_MINUTE_FORMAT.format(date);
    }

    public static Date toDate(String stringDate) {
        try {
            return UNTIL_SECOND_FORMAT.parse(stringDate);
        } catch(ParseException e) {
            return null;
        }
    }

}
