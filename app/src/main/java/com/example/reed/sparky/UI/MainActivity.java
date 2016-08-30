package com.example.reed.sparky.UI;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reed.sparky.R;
import com.example.reed.sparky.weather.Current;
import com.example.reed.sparky.weather.Day;
import com.example.reed.sparky.weather.Forecast;
import com.example.reed.sparky.weather.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private Forecast mForecast;
    private TextView mTemperatureLabel;
    private TextView mTimeLabel;
    //private TextView mLocationLabel;  //This is currently hard coded to a single location
    private ImageView mIconImageView;
    private TextView mHumidityValue;
    private TextView mPrecipValue;
    private TextView mSummary;
    private ImageView mRefreshImageView;
    private ProgressBar mRefreshProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTemperatureLabel = (TextView)findViewById(R.id.temperatureLabel);
        mTimeLabel = (TextView)findViewById(R.id.timeLabel);
        //mLocationLabel = (TextView)findViewById(R.id.locationLabel);
        mIconImageView = (ImageView)findViewById(R.id.iconImageView);
        mHumidityValue = (TextView)findViewById(R.id.humidityValue);
        mPrecipValue = (TextView)findViewById(R.id.precipValue);
        mSummary = (TextView) findViewById(R.id.summary);
        mRefreshImageView = (ImageView) findViewById(R.id.refreshImageView);
        mRefreshProgressBar = (ProgressBar) findViewById(R.id.refreshProgressBar);

        mRefreshProgressBar.setVisibility(View.INVISIBLE);

        final double latitude = 30.4998;
        final double longitude = -97.8082;

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForcast(latitude, longitude);
            }
        });

        getForcast(latitude, longitude);
    }

    private void getForcast(double latitude, double longitude) {
        String APIKey = "c994a0aea3f6916e2b1c6a0c497e9da8";
        String forecastURL = "https://api.forecast.io/forecast/" + APIKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            toggleRefresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    errorAlert();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            errorAlert();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception Caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.networkUnavaibleToast, Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if(mRefreshProgressBar.getVisibility() == View.INVISIBLE) {
            mRefreshProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mRefreshProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        mTemperatureLabel.setText(mForecast.getCurrent().getTemperature() + "");
        mTimeLabel.setText("At " + mForecast.getCurrent().getFormattedTime() + " it will be");
        mHumidityValue.setText(mForecast.getCurrent().getHumidity() + "");
        mPrecipValue.setText(mForecast.getCurrent().getPrecipChance() + "%");
        mSummary.setText(mForecast.getCurrent().getSummary());

        Drawable drawable = getResources().getDrawable(mForecast.getCurrent().getIconID());
        mIconImageView.setImageDrawable(drawable);
    }

    private Forecast parseForecastDetails( String jsonData) throws JSONException{
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));
        return forecast;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];
        for(int i = 0; i<data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            days[i].setTimeZone(forecast.getString("timezone"));
            days[i].setIcon(jsonDay.getString("icon"));
            days[i].setTime(jsonDay.getLong("time"));
            days[i].setSummary(jsonDay.getString("summary"));
            days[i].setTemperatureMax(jsonDay.getDouble("temperatureMax"));
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];
        for(int i=0; i<data.length(); i++){
            JSONObject jsonHour = data.getJSONObject(i);
            hours[i].setTime(jsonHour.getLong("time"));
            hours[i].setIcon(jsonHour.getString("icon"));
            hours[i].setTemperature(jsonHour.getDouble("temperature"));
            hours[i].setSummary(jsonHour.getString("summary"));
            hours[i].setTimeZone(forecast.getString("timezone"));
        }
        return hours;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        /*
        For initial testing purposes
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);
        */

        JSONObject currently = forecast.getJSONObject("currently");
        return new Current(
                currently.getString("icon"),
                currently.getLong("time"),
                currently.getDouble("temperature"),
                currently.getDouble("humidity"),
                currently.getDouble("precipProbability"),
                currently.getString("summary"),
                forecast.getString("timezone"));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void errorAlert() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
