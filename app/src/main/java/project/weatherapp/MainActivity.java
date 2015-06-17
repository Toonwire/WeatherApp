package project.weatherapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

    private static final String TAG_DT = "dt";

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
    private String dt;

    private static String url;

    private TextView tvTemperature;
    private TextView tvPressure;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvRain;
    private TextView tvCity;

    private ImageView imTemperature;
    private ImageView imPressure;
    private ImageView imHumidity;
    private ImageView imWindDirection;
    private ImageView imSunrise;
    private ImageView imSunset;
    private ImageView imRain;
    private ImageView imWeatherIcon;

    DecimalFormat df = new DecimalFormat("#.00");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // the format of your date

    public enum UnitSystem{
        METRIC, IMPERIAL
    }

    UnitSystem settingsUnitSystem = UnitSystem.METRIC;
    boolean settingsRain = true;
    boolean settingHumidity = true;
    boolean settingPressure = true;
    boolean settingsWind = true;
    boolean settingsSunriseSet = true;


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

        imTemperature = (ImageView) findViewById(R.id.data_view_temperature_icon);
        imPressure = (ImageView) findViewById(R.id.data_view_pressure_icon);
        imHumidity = (ImageView) findViewById(R.id.data_view_humidity_icon);
        imWindDirection = (ImageView) findViewById(R.id.data_view_wind_direction_icon);
        imSunrise = (ImageView) findViewById(R.id.data_view_sunrise_icon);
        imSunset = (ImageView) findViewById(R.id.data_view_sunset_icon);
        imRain = (ImageView) findViewById(R.id.data_view_rain_icon);
        imWeatherIcon = (ImageView) findViewById(R.id.main_weather_image);

        // Set by location in smartphone
        url = "http://api.openweathermap.org/data/2.5/weather?q=Aalborg,dk";
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

                    dt = jsonObj.getString(TAG_DT);
                    city = jsonObj.getString(TAG_CITY);

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
            String sunriseTime = sdf.format(date);

            unixSeconds = Long.parseLong(sunset);
            date = new Date(unixSeconds*1000L);
            String sunsetTime = sdf.format(date);

            Log.d("testingss", id);
            tvCity.setText(city);
            tvSunrise.setText(sunriseTime);
            tvSunset.setText(sunsetTime);
            tvWindSpeed.setText(df.format(Double.parseDouble(windSpeed)) + "m/s");
            if (rain != null)
                tvRain.setText(df.format(Double.parseDouble(rain)) + "mm");
            tvTemperature.setText(df.format(Double.parseDouble(temperature) - 273.15) + "°C");
            tvPressure.setText(Integer.parseInt(pressure) + " hPa");
            tvHumidity.setText(humidity + "%");

            imWindDirection.setRotation(Float.parseFloat(windDeg));

            Bitmap mBitmap = null;

            switch(id){
                case "200":
                case "201":
                case "202":
                case "210":
                case "211":
                case "212":
                case "221":
                case "230":
                case "231":
                case "232":

                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thunder);
                    break;

                case "300":
                case "301":
                case "302":
                case "310":
                case "311":
                case "312":
                case "321":
                case "511":
                case "520":
                case "521":
                case "522":

                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rain);
                    break;

                case "500":
                case "501":
                case "502":
                case "503":
                case "504":

                    if (day())
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.day_rain);

                    else
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.night_rain);

                    break;

                case "600":
                case "601":
                case "602":
                case "611":
                case "615":
                case "616":
                case "621":

                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snow);
                    break;

                case "701":
                case "711":
                case "721":
                case "731":
                case "741":

                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mist);
                    break;

                case "800":

                    if (day())
                       mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sun);

                    else
                       mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moon);

                    break;

                case "801":

                    if (day())
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.few_clouds_day);
                    else
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.few_clouds_night);

                    break;

                case "802":
                case "803":
                case "804":

                    if (day())
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clouds);
                    else
                        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clouds);

                    break;

                case "900":
                case "901":
                case "902":
                case "903":
                case "904":
                case "905":
                case "906":
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tornado);
                    break;

                default:
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.raindrop);

            }

            imWeatherIcon.setImageBitmap(mBitmap);

        }
        // checks if the current time is within the day time interval, created by sunrise and sunset
        private boolean day(){
            return Integer.parseInt(sunrise) < Integer.parseInt(dt) && Integer.parseInt(dt) < Integer.parseInt(sunset);
        }

    }

    public void setUnitSystem(UnitSystem unitSystem){
        settingsUnitSystem = unitSystem;
    }

    public void setRain(boolean checked){
        settingsRain = true;
    }

    public void setHumidity(boolean checked){
        settingsRain = true;
    }

    public void setPressure(boolean checked){
        settingsRain = true;
    }

    public void setSunriseSet(boolean checked){
        settingsRain = true;
    }




}
