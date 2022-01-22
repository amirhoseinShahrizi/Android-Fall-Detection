package com.example.fall_detection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetectedFall {

    private String current_time, current_date;

    public DetectedFall() {

        SimpleDateFormat df_date = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        SimpleDateFormat df_time = new SimpleDateFormat("h:mm a");

        setCurrent_time(df_time.format(Calendar.getInstance().getTime()));
        setCurrent_date(df_date.format(Calendar.getInstance().getTime()));

    }

    public DetectedFall(String date, String time) {

        setCurrent_date(date);
        setCurrent_time(time);
    }


    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    public void setCurrent_date(String current_date) {
        this.current_date = current_date;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public String getCurrent_date() {
        return current_date;
    }
}
