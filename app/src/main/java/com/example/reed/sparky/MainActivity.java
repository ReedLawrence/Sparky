package com.example.reed.sparky;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private CurrentWeather mCurrentWeather;
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
                            mCurrentWeather = getCurrentDetails(jsonData);
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
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummary.setText(mCurrentWeather.getSummary());

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconID());
        mIconImageView.setImageDrawable(drawable);
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        /*
        For initial testing purposes
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);
        */

        JSONObject currently = forecast.getJSONObject("currently");
        return new CurrentWeather(
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
