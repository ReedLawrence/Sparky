package com.example.reed.sparky;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
    private CurrentWeather mCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String APIKey = "c994a0aea3f6916e2b1c6a0c497e9da8";
        double latitude = 30.4998;
        double longitude = -97.8082;
        String forecastURL = "https://api.forecast.io/forecast/" + APIKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
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

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException{
        JSONObject forecast = new JSONObject(jsonData);
        /*
        For initial testing purposes
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);
        */

        JSONObject currently = forecast.getJSONObject("currently");
        CurrentWeather today = new CurrentWeather(
                currently.getString("icon"),
                currently.getLong("time"),
                currently.getDouble("temperature"),
                currently.getDouble("humidity"),
                currently.getDouble("precipProbability"),
                currently.getString("summary"));
        return today;
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
