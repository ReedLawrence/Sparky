package com.example.reed.sparky.weather;

import com.example.reed.sparky.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Reed Lawrence on 8/18/2016.
 */
public class Current {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public Current() {
        setIcon(null);
        setTime(0);
        setTemperature(0.0);
        setHumidity(0.0);
        setPrecipChance(0.0);
        setSummary(null);
        setTimeZone(null);
    }

    public Current(String icon, long time, double temperature, double humidity, double precipChance, String summary, String timeZone){
        setIcon(icon);
        setTime(time);
        setTemperature(temperature);
        setHumidity(humidity);
        setPrecipChance(precipChance);
        setSummary(summary);
        setTimeZone(timeZone);
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconID() {
        //clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconID = R.drawable.clear_day;
        if(getIcon().equals("clear-day")){
            iconID = R.drawable.clear_day;
        } else if(getIcon().equals("clear-night")){
            iconID = R.drawable.clear_night;
        } else if(getIcon().equals("rain")){
            iconID = R.drawable.rain;
        } else if(getIcon().equals("snow")){
            iconID = R.drawable.snow;
        } else if(getIcon().equals("sleet")){
            iconID = R.drawable.sleet;
        } else if(getIcon().equals("wind")){
            iconID = R.drawable.wind;
        } else if(getIcon().equals("fog")){
            iconID = R.drawable.fog;
        } else if(getIcon().equals("cloudy")){
            iconID = R.drawable.cloudy;
        } else if(getIcon().equals("partly-cloudy-day")){
            iconID = R.drawable.partly_cloudy;
        } else if(getIcon().equals("partly-cloudy-night")){
            iconID = R.drawable.cloudy_night;
        }
        return iconID;
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        return formatter.format(new Date(getTime() * 1000));
        /*
        String timeString = formatter.format(new Date(getTime() * 1000));
        return timeString;
        */
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        return (int) Math.round(mPrecipChance*100);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }
}
