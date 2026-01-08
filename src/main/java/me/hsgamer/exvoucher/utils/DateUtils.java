package me.hsgamer.exvoucher.utils;

import me.hsgamer.exvoucher.configs.Setting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public final class DateUtils {

    private static final TimeZone ZONE = TimeZone.getDefault();

    private DateUtils() {
    }

    public static boolean isExpired(String date) {
        Calendar cur = Calendar.getInstance(ZONE), parse;
        SimpleDateFormat format = new SimpleDateFormat(Setting.getDateFormat());
        format.setTimeZone(ZONE);

        try {
            parse = Calendar.getInstance(ZONE);
            parse.setTime(format.parse(date));
            return cur.after(parse);
        } catch (ParseException ignored) {
        }

        return false;
    }

}