package project.weatherapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends Activity {

    // DEBUGGING
    private boolean debugging = true;


    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_ID = "id";

    private static final String TAG_MAIN = "main";
    private static final String TAG_TEMPERATURE = "temp";
    private static final String TAG_PRESSURE = "pressure";
    private static final String TAG_HUMIDITY = "humidity";

    private static final String TAG_SYS = "sys";
    private static final String TAG_SUNRISE = "sunrise";
    private static final String TAG_SUNSET = "sunset";

    private static final String TAG_WIND = "wind";
    private static final String TAG_WIND_SPEED = "speed";
    private static final String TAG_WIND_DEGREES = "deg";

    private static final String TAG_RAIN = "rain";
    private static final String TAG_3H = "3h";

    private static final String TAG_DT = "dt";

    private static final String TAG_CITY = "name";

    private static final String TAG_LIST = "list";

    private static final String TAG_DAY = "day";

    private int weatherID;
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

    private ArrayList<TextView> tvTemperatureForecastList;
    private TextView tvTemperatureForecast1;
    private TextView tvTemperatureForecast2;
    private TextView tvTemperatureForecast3;
    private TextView tvTemperatureForecast4;
    private TextView tvTemperatureForecast5;

    private ArrayList<TextView> tvWindSpeedForecastList;
    private TextView tvWindSpeedForecast1;
    private TextView tvWindSpeedForecast2;
    private TextView tvWindSpeedForecast3;
    private TextView tvWindSpeedForecast4;
    private TextView tvWindSpeedForecast5;

    private ImageView imTemperature;
    private ImageView imPressure;
    private ImageView imHumidity;
    private ImageView imWindDirection;
    private ImageView imSunrise;
    private ImageView imSunset;
    private ImageView imRain;
    private ImageView imWeatherIcon;

    private ArrayList<ImageView> imWeatherIconForecastList;
    private ImageView imWeatherIconForecast1;
    private ImageView imWeatherIconForecast2;
    private ImageView imWeatherIconForecast3;
    private ImageView imWeatherIconForecast4;
    private ImageView imWeatherIconForecast5;

    private ArrayList<ImageView> imWindDirectionForecastList;
    private ImageView imWindDirectionForecast1;
    private ImageView imWindDirectionForecast2;
    private ImageView imWindDirectionForecast3;
    private ImageView imWindDirectionForecast4;
    private ImageView imWindDirectionForecast5;

    // Reference to the LocationManager and LocationListener
    private LocationManager locationManager;
    private LocationListener locationListener;

    // Current best location estimate
    private Location location;

    DecimalFormat df = new DecimalFormat("#.00");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // the format of your date

    public enum UnitSystem {
        METRIC, IMPERIAL;

        public static UnitSystem StringToEnum (String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                // For error cases
                return METRIC;
            }
        }
    }

    private UnitSystem settingsUnitSystem = UnitSystem.METRIC;
    private boolean settingsRain = false;
    private boolean settingsHumidity = false;
    private boolean settingsPressure = false;
    private boolean settingsWind = false;
    private boolean settingsSunriseSet = false;

    private Button settingsButton;


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

        tvTemperatureForecast1 = (TextView) findViewById(R.id.deg_text1);
        tvTemperatureForecast2 = (TextView) findViewById(R.id.deg_text2);
        tvTemperatureForecast3 = (TextView) findViewById(R.id.deg_text3);
        tvTemperatureForecast4 = (TextView) findViewById(R.id.deg_text4);
        tvTemperatureForecast5 = (TextView) findViewById(R.id.deg_text5);
        tvWindSpeedForecast1 = (TextView) findViewById(R.id.speed_text1);
        tvWindSpeedForecast2 = (TextView) findViewById(R.id.speed_text2);
        tvWindSpeedForecast3 = (TextView) findViewById(R.id.speed_text3);
        tvWindSpeedForecast4 = (TextView) findViewById(R.id.speed_text4);
        tvWindSpeedForecast5 = (TextView) findViewById(R.id.speed_text5);

        tvTemperatureForecastList.add(tvTemperatureForecast1);
        tvTemperatureForecastList.add(tvTemperatureForecast2);
        tvTemperatureForecastList.add(tvTemperatureForecast3);
        tvTemperatureForecastList.add(tvTemperatureForecast4);
        tvTemperatureForecastList.add(tvTemperatureForecast5);

        tvWindSpeedForecastList.add(tvWindSpeedForecast1);
        tvWindSpeedForecastList.add(tvWindSpeedForecast2);
        tvWindSpeedForecastList.add(tvWindSpeedForecast3);
        tvWindSpeedForecastList.add(tvWindSpeedForecast4);
        tvWindSpeedForecastList.add(tvWindSpeedForecast5);



        imTemperature = (ImageView) findViewById(R.id.data_view_temperature_icon);
        imPressure = (ImageView) findViewById(R.id.data_view_pressure_icon);
        imHumidity = (ImageView) findViewById(R.id.data_view_humidity_icon);
        imWindDirection = (ImageView) findViewById(R.id.data_view_wind_direction_icon);
        imSunrise = (ImageView) findViewById(R.id.data_view_sunrise_icon);
        imSunset = (ImageView) findViewById(R.id.data_view_sunset_icon);
        imRain = (ImageView) findViewById(R.id.data_view_rain_icon);
        imWeatherIcon = (ImageView) findViewById(R.id.main_weather_image);

        imWeatherIconForecast1 = (ImageView) findViewById(R.id.weather_day1);
        imWeatherIconForecast2 = (ImageView) findViewById(R.id.weather_day2);
        imWeatherIconForecast3 = (ImageView) findViewById(R.id.weather_day3);
        imWeatherIconForecast4 = (ImageView) findViewById(R.id.weather_day4);
        imWeatherIconForecast5 = (ImageView) findViewById(R.id.weather_day5);
        imWindDirectionForecast1 = (ImageView) findViewById(R.id.wind_direction_day1);
        imWindDirectionForecast2 = (ImageView) findViewById(R.id.wind_direction_day2);
        imWindDirectionForecast3 = (ImageView) findViewById(R.id.wind_direction_day3);
        imWindDirectionForecast4 = (ImageView) findViewById(R.id.wind_direction_day4);
        imWindDirectionForecast5 = (ImageView) findViewById(R.id.wind_direction_day5);

        imWeatherIconForecastList.add(imWeatherIconForecast1);
        imWeatherIconForecastList.add(imWeatherIconForecast2);
        imWeatherIconForecastList.add(imWeatherIconForecast3);
        imWeatherIconForecastList.add(imWeatherIconForecast4);
        imWeatherIconForecastList.add(imWeatherIconForecast5);

        imWindDirectionForecastList.add(imWindDirectionForecast1);
        imWindDirectionForecastList.add(imWindDirectionForecast2);
        imWindDirectionForecastList.add(imWindDirectionForecast3);
        imWindDirectionForecastList.add(imWindDirectionForecast4);
        imWindDirectionForecastList.add(imWindDirectionForecast5);



        // Acquire reference to the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.i("TESTING","No location found.");
            finish();
        }
        // Get best last location measurement
        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        // Display last reading information


        // Set by location in smartphone
//        url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk";

        // setup current weather from data extracted from the API
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("testing123", "portrait-mode working");
            if (null != location) {
                Log.i("TESTING", location.getLongitude() + " --- " + location.getLatitude());
                url = "http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+1")); // give a timezone reference for formatting (see comment at the bottom)

            } else {
                Log.i("TESTING", "No Initial Reading Available");
            }
                new GetWeatherToday().execute();

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("testing123", "landscape-mode working");
            if (null != location) {
                Log.i("TESTING", location.getLongitude() + " --- " + location.getLatitude());
                url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&cnt=5&mode=json";
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+1")); // give a timezone reference for formatting (see comment at the bottom)
            } else {
                Log.i("TESTING", "No Initial Reading Available");
            }
            new GetWeatherForecast().execute();
        }

        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
                Log.d("testing123", "settings button working");
                startActivity(settingsIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettings();
    }

    private class GetWeatherForecast extends AsyncTask<Void, Void, Void> {

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

                    JSONArray forecastResources = jsonObj.getJSONArray(TAG_LIST);
                    for (int i = 0; i < 5; i++) {
                        JSONObject forecast = forecastResources.getJSONObject(i);
                        dt = jsonObj.getString(TAG_DT);
                        JSONObject tempResources = forecast.getJSONObject(TAG_TEMPERATURE);
                        JSONObject dayTempResource = tempResources.getJSONObject(TAG_DAY);
                        temperature = dayTempResource.toString();


                        JSONArray forecastWeatherResources = jsonObj.getJSONArray(TAG_WEATHER);
                        JSONObject forecastWeather = forecastWeatherResources.getJSONObject(0);
                        weatherID = Integer.parseInt(forecastWeather.getString(TAG_ID));


                        JSONObject forecastWindResources = jsonObj.getJSONObject(TAG_WIND);
                        windSpeed = forecastWindResources.getString(TAG_WIND_SPEED);
                        windDeg = forecastWindResources.getString(TAG_WIND_DEGREES);


                        tvTemperatureForecastList.get(i).setText(temperature);
                        tvWindSpeedForecastList.get(i).setText(windSpeed);
                        imWeatherIconForecastList.get(i).setImageBitmap(getWeatherIconBitmap(weatherID));
                        imWindDirectionForecastList.get(i).setRotation(Float.parseFloat(windDeg));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

    }

    public void loadSettings() {
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);

        settingsRain = settings.getBoolean("rain", false);
        settingsHumidity = settings.getBoolean("humidity", false);
        settingsPressure = settings.getBoolean("pressure", false);
        settingsSunriseSet = settings.getBoolean("sunriseset", false);
        settingsWind = settings.getBoolean("wind",false);

        switch (UnitSystem.StringToEnum(settings.getString("unit", "METRIC"))) {
            case METRIC:
                settingsUnitSystem = UnitSystem.METRIC;
                break;
            case IMPERIAL:
                settingsUnitSystem = UnitSystem.IMPERIAL;
                break;
        }
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
                    sunrise = todaysSysResources.getString(TAG_SUNRISE);
                    sunset = todaysSysResources.getString(TAG_SUNSET);

                    JSONArray todaysWeatherResources = jsonObj.getJSONArray(TAG_WEATHER);
                    JSONObject todaysWeather = todaysWeatherResources.getJSONObject(0);
                    weatherID = Integer.parseInt(todaysWeather.getString(TAG_ID));

                    JSONObject todaysMainResources = jsonObj.getJSONObject(TAG_MAIN);
                    temperature = todaysMainResources.getString(TAG_TEMPERATURE);
                    pressure = todaysMainResources.getString(TAG_PRESSURE);
                    humidity = todaysMainResources.getString(TAG_HUMIDITY);

                    JSONObject todaysWindResources = jsonObj.getJSONObject(TAG_WIND);
                    windSpeed = todaysWindResources.getString(TAG_WIND_SPEED);
                    windDeg = todaysWindResources.getString(TAG_WIND_DEGREES);

                    JSONObject todaysRainResources = jsonObj.getJSONObject(TAG_RAIN);
                    rain = todaysRainResources.getString(TAG_3H);
                    Log.d("tagtest", rain);


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
            Date date = new Date(unixSeconds * 1000L);
            String sunriseTime = sdf.format(date);

            unixSeconds = Long.parseLong(sunset);
            date = new Date(unixSeconds * 1000L);
            String sunsetTime = sdf.format(date);

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


            // checks if the current time is within the day time interval, created by sunrise and sunset


            imWeatherIcon.setImageBitmap(getWeatherIconBitmap(weatherID));
        }


        private String getPressureAndUnit() {
            if (settingsUnitSystem == UnitSystem.METRIC){
                return Double.parseDouble(pressure) + "hPa";
            }
            return df.format(Double.parseDouble(pressure)*0.0145037737955) + "psi";
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

    public Bitmap getWeatherIconBitmap(int weatherID){

        Bitmap mBitmap = null;

        switch(weatherID){
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thunder);
                break;
            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 321:
            case 511:
            case 520:
            case 521:
            case 522:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rain);
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                if (day())
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.day_rain);
                else
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.night_rain);
                break;
            case 600:
            case 601:
            case 602:
            case 611:
            case 615:
            case 616:
            case 621:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.snow);
                break;
            case 701:
            case 711:
            case 721:
            case 731:
            case 741:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mist);
                break;
            case 800:
                if (day())
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
                else
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moon);
                break;
            case 801:
                if (day())
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.few_clouds_day);
                else
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.few_clouds_night);
                break;
            case 802:
            case 803:
            case 804:
                if (day())
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clouds);
                else
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clouds);
                break;
            case 900:
            case 901:
            case 902:
            case 903:
            case 904:
            case 905:
            case 906:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tornado);
                break;
            default:
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.raindrop);
        }

        return mBitmap;

    }

    public boolean day(){
        return Integer.parseInt(sunrise) < Integer.parseInt(dt) && Integer.parseInt(dt) < Integer.parseInt(sunset);
    }
}
