package project.weatherapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.os.AsyncTask;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONObject;

public class MainActivity extends Activity {

    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_ID = "id";

    private static final String TAG_MAIN = "main";
    private static final String TAG_TEMPERATURE = "temp";
    private static final String TAG_PRESSURE = "pressure";
    private static final String TAG_HUMIDITY = "humidity";

    private static final String TAG_SYS = "sys";
    private static final String TAG_COUNTRY = "country";
    private static final String TAG_SUNRISE = "sunrise";
    private static final String TAG_SUNSET = "sunset";

    private static final String TAG_WIND = "wind";
    private static final String TAG_WIND_SPEED = "speed";
    private static final String TAG_WIND_DEGREES = "deg";

    private static final String TAG_RAIN = "rain";
    private static final String TAG_3H = "3h";

    private static final String TAG_CITY = "name";

    private String id;
    private String country;
    private String city;
    private String temperature;
    private String pressure;
    private String humidity;
    private String sunrise;
    private String sunset;
    private String windSpeed;
    private String windDeg;
    private String rain;

    private static String url;

    private TextView tvTemperature;
    private TextView tvPressure;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvRain;
    private TextView tvCity;

    DecimalFormat df = new DecimalFormat("#.00");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // the format of your date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemperature = (TextView) findViewById(R.id.data_view_temperature);
        tvPressure = (TextView) findViewById(R.id.data_view_pressure);
        tvHumidity = (TextView) findViewById(R.id.data_view_humidity);
        tvWindSpeed = (TextView) findViewById(R.id.data_view_wind);
        tvSunrise = (TextView) findViewById(R.id.data_view_sunrise);
        tvSunset = (TextView) findViewById(R.id.data_view_sunset);
        tvRain = (TextView) findViewById(R.id.data_view_rain);
        tvCity = (TextView) findViewById(R.id.city_name);

        // Set by location in smartphone
        url = "http://api.openweathermap.org/data/2.5/weather?q=Helsingor,dk";
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+1")); // give a timezone reference for formating (see comment at the bottom

        new GetWeatherToday().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetWeatherToday extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node

                    JSONObject todaysSysResources = jsonObj.getJSONObject(TAG_SYS);
                    country = todaysSysResources.getString(TAG_COUNTRY);
                    sunrise = todaysSysResources.getString(TAG_SUNRISE);
                    sunset = todaysSysResources.getString(TAG_SUNSET);

                    JSONArray todaysWeatherResources = jsonObj.getJSONArray(TAG_WEATHER);
                    JSONObject todaysWeather = todaysWeatherResources.getJSONObject(0);
                    id = todaysWeather.getString(TAG_ID);

                    JSONObject todaysMainResources = jsonObj.getJSONObject(TAG_MAIN);
                    temperature = todaysMainResources.getString(TAG_TEMPERATURE);
                    pressure = todaysMainResources.getString(TAG_PRESSURE);
                    humidity = todaysMainResources.getString(TAG_HUMIDITY);

                    JSONObject todaysWindResources = jsonObj.getJSONObject(TAG_WIND);
                    windSpeed = todaysWindResources.getString(TAG_WIND_SPEED);
                    windDeg = todaysWindResources.getString(TAG_WIND_DEGREES);

                    JSONObject todaysRainResources = jsonObj.getJSONObject(TAG_RAIN);
                    rain = todaysRainResources.getString(TAG_3H);

                    city = jsonObj.getString(TAG_CITY);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            long unixSeconds = Long.parseLong(sunrise);
            Date date = new Date(unixSeconds*1000L);
            sunrise = sdf.format(date);

            unixSeconds = Long.parseLong(sunset);
            date = new Date(unixSeconds*1000L);
            sunset = sdf.format(date);

            tvCity.setText(city);
            tvSunrise.setText(sunrise);
            tvSunset.setText(sunset);
            tvWindSpeed.setText(df.format(Double.parseDouble(windSpeed)) + "m/s");
            if (rain != null)
                tvRain.setText(df.format(Double.parseDouble(rain)) + "mm");
            tvTemperature.setText(df.format(Double.parseDouble(temperature) - 273.15) + "°C");
            tvPressure.setText(pressure + " hPa");
            tvHumidity.setText(humidity + "%");

        }

    }

}
