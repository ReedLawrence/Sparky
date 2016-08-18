package com.example.reed.sparky;

/**
 * Created by reed_ on 8/18/2016.
 */
public class CurrentWeather {
    private String mIcon;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;

    public  CurrentWeather() {
        setIcon(null);
        setTime(0);
        setTemperature(0.0);
        setHumidity(0.0);
        setPrecipChance(0.0);
        setSummary(null);
    }

    public CurrentWeather(String icon, long time, double temperature, double humidity, double precipChance, String summary){
        setIcon(icon);
        setTime(time);
        setTemperature(temperature);
        setHumidity(humidity);
        setPrecipChance(precipChance);
        setSummary(summary);
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public double getTemperature() {
        return mTemperature;
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

    public double getPrecipChance() {
        return mPrecipChance;
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
