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

    private ImageView imTemperature;
    private ImageView imWindDirection;
    private ImageView imHumidity;
    private ImageView imPressure;
    private ImageView imSunrise;
    private ImageView imSunset;
    private ImageView imWeatherIcon;
    private ImageView imRain;

    private ArrayList<TextView> tvTemperatureForecastList = new ArrayList<>();
    private ArrayList<TextView> tvWindSpeedForecastList= new ArrayList<>();
    private ArrayList<ImageView> imWeatherIconForecastList= new ArrayList<>();
    private ArrayList<ImageView> imWindDirectionForecastList= new ArrayList<>();

    DecimalFormat df = new DecimalFormat("#.00");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // the format of your date

    public MainActivity() {
    }

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

        TextView tvTemperatureForecast1 = (TextView) findViewById(R.id.deg_text1);
        TextView tvTemperatureForecast2 = (TextView) findViewById(R.id.deg_text2);
        TextView tvTemperatureForecast3 = (TextView) findViewById(R.id.deg_text3);
        TextView tvTemperatureForecast4 = (TextView) findViewById(R.id.deg_text4);
        TextView tvTemperatureForecast5 = (TextView) findViewById(R.id.deg_text5);
        TextView tvWindSpeedForecast1 = (TextView) findViewById(R.id.speed_text1);
        TextView tvWindSpeedForecast2 = (TextView) findViewById(R.id.speed_text2);
        TextView tvWindSpeedForecast3 = (TextView) findViewById(R.id.speed_text3);
        TextView tvWindSpeedForecast4 = (TextView) findViewById(R.id.speed_text4);
        TextView tvWindSpeedForecast5 = (TextView) findViewById(R.id.speed_text5);

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

        ImageView imWeatherIconForecast1 = (ImageView) findViewById(R.id.weather_day1);
        ImageView imWeatherIconForecast2 = (ImageView) findViewById(R.id.weather_day2);
        ImageView imWeatherIconForecast3 = (ImageView) findViewById(R.id.weather_day3);
        ImageView imWeatherIconForecast4 = (ImageView) findViewById(R.id.weather_day4);
        ImageView imWeatherIconForecast5 = (ImageView) findViewById(R.id.weather_day5);
        ImageView imWindDirectionForecast1 = (ImageView) findViewById(R.id.wind_direction_day1);
        ImageView imWindDirectionForecast2 = (ImageView) findViewById(R.id.wind_direction_day2);
        ImageView imWindDirectionForecast3 = (ImageView) findViewById(R.id.wind_direction_day3);
        ImageView imWindDirectionForecast4 = (ImageView) findViewById(R.id.wind_direction_day4);
        ImageView imWindDirectionForecast5 = (ImageView) findViewById(R.id.wind_direction_day5);

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

        Button settingsButton = (Button) findViewById(R.id.settings_button);
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

// Acquire reference to the LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            Log.i("TESTING","No location found.");
            finish();
        }
        // Get best last location measurement
        Location location = null;
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        // Display last reading information


        // Set by location in smartphone

        // setup current weather from data extracted from the API
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d("testing123", "portrait-mode working");
            if (null != location) {
                Log.i("TESTING", location.getLongitude() + " --- " + location.getLatitude());
                url = "http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&units="+settingsUnitSystem;
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

                    for (int i = 0; i < forecastResources.length(); i++) {

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


                        tvTemperatureForecastList.get(i).setText(getTemperatureAndUnit());
                        tvWindSpeedForecastList.get(i).setText(getWindSpeedAndUnit());
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            TextView tvCityLandscapeMode = (TextView) findViewById(R.id.city_name_landscape);

            tvCityLandscapeMode.setText("city..");

            Log.d("testingg", "Temperature: " + temperature);
            Log.d("testingg", "Weather ID: " + weatherID);
            Log.d("testingg", "Wind Speed: " + windSpeed);
            Log.d("testingg", "Wind Direction: " + windDeg);

        }


    }

    public void loadSettings() {
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);

        boolean settingsRain = settings.getBoolean("rain", false);
        boolean settingsHumidity = settings.getBoolean("humidity", false);
        boolean settingsPressure = settings.getBoolean("pressure", false);
        boolean settingsSunriseSet = settings.getBoolean("sunriseset", false);
        boolean settingsWind = settings.getBoolean("wind",false);

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
            tvWindSpeed.setText(getWindSpeedAndUnit());
            tvRain.setText(getRainAndUnit());
            tvTemperature.setText(getTemperatureAndUnit());
            tvPressure.setText(getPressureAndUnit());
            tvHumidity.setText(humidity + "%");

            imWindDirection.setRotation(Float.parseFloat(windDeg));
            imWeatherIcon.setImageBitmap(getWeatherIconBitmap(weatherID));
        }

    }

    public String getPressureAndUnit() {
        if (settingsUnitSystem == UnitSystem.METRIC){
            return (int)Double.parseDouble(pressure) + " hPa";
        }
        return (int)(Double.parseDouble(pressure)*0.0145037737955) + " psi";
    }

    public String getRainAndUnit() {
        if (rain != null)
            return df.format(Double.parseDouble(rain)) + "mm";
        return "No rain";
    }

    public String getTemperatureAndUnit() {
        if (settingsUnitSystem == UnitSystem.METRIC)
            return df.format(Double.parseDouble(temperature)) + "°C";
        return df.format(Double.parseDouble(temperature)) + "°F";
    }

    public String getWindSpeedAndUnit() {
        if (settingsUnitSystem == UnitSystem.METRIC)
            return df.format(Double.parseDouble(windSpeed)) + " m/s";
        return df.format(Double.parseDouble(windSpeed)) + " mph";
    }

    public Bitmap getWeatherIconBitmap(int weatherID){

        Bitmap mBitmap;

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
