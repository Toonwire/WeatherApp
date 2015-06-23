package project.weatherapp;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeatherLocation extends Fragment {

    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_WEATHER = "weather";
    private static final String TAG_ID = "id";
    private static final String TAG_MAIN = "main";
    private static final String TAG_NAME = "name";
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
    private static final String TAG_CITY = "city";
    private static final String TAG_LIST = "list";
    private static final String TAG_DAY = "day";
    private static final String TAG_COUNTRY = "country";

    private static final String CLOUDY_COLOR = "#95B4C6";
    private static final String DAWN_COLOR = "#63AED5";
    private static final String SUNNY_COLOR = "#1E8BC3";
    private static final String EVENING_COLOR = "#125275";
    private static final String NIGHT_COLOR = "#0A293B";

    Map<String, String> countries = new HashMap<>();

    private int weatherID;
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
    private String url;

    private int[] dtForecast = new int[5];
    private int[] weatherIDforecast = new int[5];
    private String[] temperatureForecast = new String[5];
    private String[] windSpeedForecast = new String[5];
    private String[] windDirectionForecast = new String[5];

    private TextView tvTemperature;
    private TextView tvPressure;
    private TextView tvHumidity;
    private TextView tvWindSpeed;
    private TextView tvSunrise;
    private TextView tvSunset;
    private TextView tvRain;
    private TextView tvCity;
    private TextView tvCityLandscapeMode;
    private TextView tvCountry;
    private TextView tvDate;

    //    private ImageView imTemperature;
    private ImageView imWindDirection;
    private ImageView imHumidity;
    private ImageView imPressure;
    private ImageView imSunrise;
    private ImageView imSunset;
    private ImageView imWeatherIcon;
    private ImageView imRain;


    private ArrayList<TextView> tvTemperatureForecastList = new ArrayList<>();
    private ArrayList<TextView> tvWindSpeedForecastList = new ArrayList<>();
    private ArrayList<TextView> tvDateForecastList = new ArrayList<>();
    private ArrayList<TextView> tvDayForecastList = new ArrayList<>();
    private ArrayList<ImageView> imWeatherIconForecastList = new ArrayList<>();
    private ArrayList<ImageView> imWindDirectionForecastList = new ArrayList<>();

    // Reference to the LocationManager and LocationListener
    private LocationManager locationManager;
    private LocationListener locationListener;
    // Current best location estimate
    private Location location;

    DecimalFormat df = new DecimalFormat("#.00");
    DecimalFormat df1 = new DecimalFormat("#.0");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd"); // June 21
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");

    private UnitSystem settingsUnitSystem = UnitSystem.METRIC;
    private boolean settingsRain = false;
    private boolean settingsHumidity = false;
    private boolean settingsPressure = false;
    private boolean settingsWind = false;
    private boolean settingsSunriseSet = false;

    private TableLayout leftOverview;
    private TableRow overviewRain;
    private TableRow overviewHumidity;
    private TableRow overviewPressure;
    private TableLayout rightOverview;
    private TableRow overviewWind;
    private TableRow overviewSunRiseSet;

    private LinearLayout currentWeatherLayout;
    private LinearLayout forecastWeatherLayout;

    public WeatherLocation(String url, boolean rain, boolean humidity, boolean pressure, boolean wind, boolean sunriseSet, UnitSystem units){
        this.url = url;
        this.settingsRain = rain;
        this.settingsHumidity = humidity;
        this.settingsPressure = pressure;
        this.settingsWind = wind;
        this.settingsSunriseSet = sunriseSet;
        this.settingsUnitSystem = units;
    }

    public WeatherLocation(String url, UnitSystem units){
        this.url = url;
        this.settingsUnitSystem = units;
    }

    public WeatherLocation(){

    }

    public float getLongitude(String url){
        return (float) Math.round(Double.parseDouble(url.split("lon=")[1].split("&")[0]));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Tess", "onCreateView()");

        if (container == null)
            return null;

        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(iso, l.getDisplayCountry());
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            currentWeatherLayout = (LinearLayout) inflater.inflate(R.layout.current_weather, container, false);

            tvTemperature = (TextView) currentWeatherLayout.findViewById(R.id.data_view_temperature);
            tvPressure = (TextView) currentWeatherLayout.findViewById(R.id.data_view_pressure);
            tvHumidity = (TextView) currentWeatherLayout.findViewById(R.id.data_view_humidity);
            tvWindSpeed = (TextView) currentWeatherLayout.findViewById(R.id.data_view_wind);
            tvSunrise = (TextView) currentWeatherLayout.findViewById(R.id.data_view_sunrise);
            tvSunset = (TextView) currentWeatherLayout.findViewById(R.id.data_view_sunset);
            tvRain = (TextView) currentWeatherLayout.findViewById(R.id.data_view_rain);
            tvCity = (TextView) currentWeatherLayout.findViewById(R.id.city_name);
            tvCountry = (TextView) currentWeatherLayout.findViewById(R.id.country_name);
            tvDate = (TextView) currentWeatherLayout.findViewById(R.id.date_text);

            imPressure = (ImageView) currentWeatherLayout.findViewById(R.id.data_view_pressure_icon);
            imHumidity = (ImageView) currentWeatherLayout.findViewById(R.id.data_view_humidity_icon);
            imWindDirection = (ImageView) currentWeatherLayout.findViewById(R.id.data_view_wind_direction_icon);
            imSunrise = (ImageView) currentWeatherLayout.findViewById(R.id.data_view_sunrise_icon);
            imSunset = (ImageView) currentWeatherLayout.findViewById(R.id.data_view_sunset_icon);
            imRain = (ImageView) currentWeatherLayout.findViewById(R.id.data_view_rain_icon);
            imWeatherIcon = (ImageView) currentWeatherLayout.findViewById(R.id.main_weather_image);

            leftOverview = (TableLayout) currentWeatherLayout.findViewById(R.id.overview_left);
            rightOverview = (TableLayout) currentWeatherLayout.findViewById(R.id.overview_right);
            overviewRain = (TableRow) currentWeatherLayout.findViewById(R.id.overview_rain);
            overviewHumidity = (TableRow) currentWeatherLayout.findViewById(R.id.overview_humidity);
            overviewPressure = (TableRow) currentWeatherLayout.findViewById(R.id.overview_pressure);
            overviewWind = (TableRow) currentWeatherLayout.findViewById(R.id.overview_wind);
            overviewSunRiseSet = (TableRow) currentWeatherLayout.findViewById(R.id.overview_sunriseset);

            return currentWeatherLayout;
        }

        forecastWeatherLayout = (LinearLayout) inflater.inflate(R.layout.forecast_weather, container, false);

        tvCityLandscapeMode = (TextView) forecastWeatherLayout.findViewById(R.id.city_name_landscape);

        TextView tvTemperatureForecast1 = (TextView) forecastWeatherLayout.findViewById(R.id.deg_text1);
        TextView tvTemperatureForecast2 = (TextView) forecastWeatherLayout.findViewById(R.id.deg_text2);
        TextView tvTemperatureForecast3 = (TextView) forecastWeatherLayout.findViewById(R.id.deg_text3);
        TextView tvTemperatureForecast4 = (TextView) forecastWeatherLayout.findViewById(R.id.deg_text4);
        TextView tvTemperatureForecast5 = (TextView) forecastWeatherLayout.findViewById(R.id.deg_text5);
        TextView tvWindSpeedForecast1 = (TextView) forecastWeatherLayout.findViewById(R.id.speed_text1);
        TextView tvWindSpeedForecast2 = (TextView) forecastWeatherLayout.findViewById(R.id.speed_text2);
        TextView tvWindSpeedForecast3 = (TextView) forecastWeatherLayout.findViewById(R.id.speed_text3);
        TextView tvWindSpeedForecast4 = (TextView) forecastWeatherLayout.findViewById(R.id.speed_text4);
        TextView tvWindSpeedForecast5 = (TextView) forecastWeatherLayout.findViewById(R.id.speed_text5);
        TextView tvDateForecast1 = (TextView) forecastWeatherLayout.findViewById(R.id.date_day1);
        TextView tvDateForecast2 = (TextView) forecastWeatherLayout.findViewById(R.id.date_day2);
        TextView tvDateForecast3 = (TextView) forecastWeatherLayout.findViewById(R.id.date_day3);
        TextView tvDateForecast4 = (TextView) forecastWeatherLayout.findViewById(R.id.date_day4);
        TextView tvDateForecast5 = (TextView) forecastWeatherLayout.findViewById(R.id.date_day5);
        TextView tvDayForecast1 = (TextView) forecastWeatherLayout.findViewById(R.id.day_day1);
        TextView tvDayForecast2 = (TextView) forecastWeatherLayout.findViewById(R.id.day_day2);
        TextView tvDayForecast3 = (TextView) forecastWeatherLayout.findViewById(R.id.day_day3);
        TextView tvDayForecast4 = (TextView) forecastWeatherLayout.findViewById(R.id.day_day4);
        TextView tvDayForecast5 = (TextView) forecastWeatherLayout.findViewById(R.id.day_day5);

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

        tvDateForecastList.add(tvDateForecast1);
        tvDateForecastList.add(tvDateForecast2);
        tvDateForecastList.add(tvDateForecast3);
        tvDateForecastList.add(tvDateForecast4);
        tvDateForecastList.add(tvDateForecast5);

        tvDayForecastList.add(tvDayForecast1);
        tvDayForecastList.add(tvDayForecast2);
        tvDayForecastList.add(tvDayForecast3);
        tvDayForecastList.add(tvDayForecast4);
        tvDayForecastList.add(tvDayForecast5);

        ImageView imWeatherIconForecast1 = (ImageView) forecastWeatherLayout.findViewById(R.id.weather_day1);
        ImageView imWeatherIconForecast2 = (ImageView) forecastWeatherLayout.findViewById(R.id.weather_day2);
        ImageView imWeatherIconForecast3 = (ImageView) forecastWeatherLayout.findViewById(R.id.weather_day3);
        ImageView imWeatherIconForecast4 = (ImageView) forecastWeatherLayout.findViewById(R.id.weather_day4);
        ImageView imWeatherIconForecast5 = (ImageView) forecastWeatherLayout.findViewById(R.id.weather_day5);
        ImageView imWindDirectionForecast1 = (ImageView) forecastWeatherLayout.findViewById(R.id.wind_direction_day1);
        ImageView imWindDirectionForecast2 = (ImageView) forecastWeatherLayout.findViewById(R.id.wind_direction_day2);
        ImageView imWindDirectionForecast3 = (ImageView) forecastWeatherLayout.findViewById(R.id.wind_direction_day3);
        ImageView imWindDirectionForecast4 = (ImageView) forecastWeatherLayout.findViewById(R.id.wind_direction_day4);
        ImageView imWindDirectionForecast5 = (ImageView) forecastWeatherLayout.findViewById(R.id.wind_direction_day5);

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

        return forecastWeatherLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            configureDataView();
            new GetWeatherToday().execute();

        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            new GetWeatherForecast().execute();
        }

    }

    private class GetWeatherForecast extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            // if doInBackgrgound takes more than 15 seconds, make the conclusion
            // that there is no internet connection and thus terminate the app and show a toast
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
                    // top level container
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    // get the corresponding city from longitude and latitude coordinates
                    JSONObject cityObj = jsonObj.getJSONObject(TAG_CITY);  // change city tag to city...
                    city = cityObj.getString(TAG_NAME);     // field


                    // extract from the list tag all the data from the individual days
                    JSONArray forecastResources = jsonObj.getJSONArray(TAG_LIST);

                    for (int i = 0; i < forecastResources.length(); i++) {

                        JSONObject forecast = forecastResources.getJSONObject(i);
                        dtForecast[i] = Integer.parseInt(forecast.getString(TAG_DT));
                        JSONObject temp = forecast.getJSONObject(TAG_TEMPERATURE);
                        temperatureForecast[i] = temp.getString(TAG_DAY);

                        JSONArray forecastWeatherResources = forecast.getJSONArray(TAG_WEATHER);
                        JSONObject forecastWeather = forecastWeatherResources.getJSONObject(0);
                        weatherIDforecast[i] = Integer.parseInt(forecastWeather.getString(TAG_ID));

                        windSpeedForecast[i] = forecast.getString(TAG_WIND_SPEED);
                        windDirectionForecast[i] = forecast.getString(TAG_WIND_DEGREES);

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

            // display current city of the 5-day forecast
            tvCityLandscapeMode.setText(city);

            // 5 being the number of days of weather forecasts
            for (int i = 0; i < 5; i++) {

                tvDateForecastList.get(i).setText(unixTimeConverter(dtForecast[i])[0]);
                tvDayForecastList.get(i).setText(unixTimeConverter(dtForecast[i])[1]);
                tvTemperatureForecastList.get(i).setText(getTemperatureAndUnit(i));
                tvWindSpeedForecastList.get(i).setText(getWindSpeedAndUnit(i));
                imWeatherIconForecastList.get(i).setImageBitmap(getWeatherIconBitmap(weatherIDforecast[i]));
                imWindDirectionForecastList.get(i).setRotation(Float.parseFloat(windDirectionForecast[i]));

            }

            Log.d("testingg", "url: " + url);
            Log.d("testingg", "Temperature: " + temperatureForecast[0]);
            Log.d("testingg", "Weather ID: " + weatherIDforecast[0]);
            Log.d("testingg", "Wind Speed: " + windSpeedForecast[0]);
            Log.d("testingg", "Wind Direction: " + windDirectionForecast[0]);


        }

    }

    public String[] unixTimeConverter(int unixSeconds) {
        Date date = new Date(unixSeconds*1000L);

        String[] dateAndDay = new String[2];
        dateAndDay[0] = dateFormat.format(date);
        dateAndDay[1] = dayFormat.format(date);
        return dateAndDay;
    }



    public void configureDataView() {


        // remove or add the data view components according to settings


        // RAIN
        if (!settingsRain) {
            if (overviewRain.getVisibility() == View.VISIBLE) {
                leftOverview.removeView(overviewRain);
                overviewRain.setVisibility(View.INVISIBLE);
            }
        } else {
            if (overviewRain.getVisibility() != View.VISIBLE) {
                leftOverview.addView(overviewRain);
                overviewRain.setVisibility(View.VISIBLE);
            }
        }

        // HUMIDITY
        if (!settingsHumidity) {
            if (overviewHumidity.getVisibility() == View.VISIBLE) {
                leftOverview.removeView(overviewHumidity);
                overviewHumidity.setVisibility(View.INVISIBLE);
            }
        } else {
            if (overviewHumidity.getVisibility() != View.VISIBLE) {
                leftOverview.addView(overviewHumidity);
                overviewHumidity.setVisibility(View.VISIBLE);
            }
        }

        // PRESSURE
        if (!settingsPressure) {
            if (overviewPressure.getVisibility() == View.VISIBLE)
                leftOverview.removeView(overviewPressure);
            overviewPressure.setVisibility(View.INVISIBLE);
        } else {
            if (overviewPressure.getVisibility() != View.VISIBLE)
                leftOverview.addView(overviewPressure);
            overviewPressure.setVisibility(View.VISIBLE);
        }

        // WIND
        if (!settingsWind) {
            if (overviewWind.getVisibility() == View.VISIBLE) {
                rightOverview.removeView(overviewWind);
                overviewWind.setVisibility(View.INVISIBLE);
            }
        } else {
            if (overviewWind.getVisibility() != View.VISIBLE) {
                rightOverview.addView(overviewWind);
                overviewWind.setVisibility(View.VISIBLE);
            }
        }

        // SUNRISE / SUNSET
        if (!settingsSunriseSet) {
            if (overviewSunRiseSet.getVisibility() == View.VISIBLE) {
                rightOverview.removeView(overviewSunRiseSet);
                overviewSunRiseSet.setVisibility(View.INVISIBLE);
            }
        } else {
            if (overviewSunRiseSet.getVisibility() != View.VISIBLE) {
                rightOverview.addView(overviewSunRiseSet);
                overviewSunRiseSet.setVisibility(View.VISIBLE);
            }
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
            pDialog = new ProgressDialog(getActivity());
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
                    city = jsonObj.getString(TAG_NAME);

                    JSONObject todaysSysResources = jsonObj.getJSONObject(TAG_SYS);
                    sunrise = todaysSysResources.getString(TAG_SUNRISE);
                    sunset = todaysSysResources.getString(TAG_SUNSET);
                    country = todaysSysResources.getString(TAG_COUNTRY);

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
            Date date = new Date(unixSeconds*1000L);
            String sunriseTime = timeFormat.format(date);

            unixSeconds = Long.parseLong(sunset);
            date = new Date(unixSeconds*1000L);
            String sunsetTime = timeFormat.format(date);

            // ~ish times for color changes
            long unixSecondsDifference = (int) (getLongitude(url) / 15) * 3600;  // 15 long = 1 hour
            unixSeconds = Long.parseLong(dt) + unixSecondsDifference;

            date = new Date(unixSeconds*1000L);
            String realTime = timeFormat.format(date);

            if (countries.get(country) != null)
                tvCountry.setText(countries.get(country));
            else
                tvCountry.setText(country);

            tvCity.setText(city);
            tvSunrise.setText(sunriseTime);
            tvSunset.setText(sunsetTime);
            tvWindSpeed.setText(getWindSpeedAndUnit());
            tvRain.setText(getRainAndUnit());
            tvTemperature.setText(getTemperatureAndUnit());
            tvPressure.setText(getPressureAndUnit());
            tvHumidity.setText(humidity + "%");
            tvDate.setText(dayFormat.format(date) + " - " + dateFormat.format(date));     // Mon - Jun 22

            if (Integer.parseInt(realTime.substring(0,2)) < 6)
                currentWeatherLayout.setBackgroundColor(Color.parseColor(DAWN_COLOR));
            else if (Integer.parseInt(realTime.substring(0,2)) > 18)
                currentWeatherLayout.setBackgroundColor(Color.parseColor(EVENING_COLOR));
            else if (Integer.parseInt(realTime.substring(0,2)) > 23)
                currentWeatherLayout.setBackgroundColor(Color.parseColor(NIGHT_COLOR));

            imWindDirection.setRotation(Float.parseFloat(windDeg));
            imWeatherIcon.setImageBitmap(getWeatherIconBitmap(weatherID));

            Log.d("ala", imWeatherIcon.getDrawable().toString());
            Log.d("testing", "url: " + url);
            Log.d("testing", "Temperature: " + temperature);
            Log.d("testing", "Weather ID: " + weatherID);
            Log.d("testing", "Wind Speed: " + windSpeed);
            Log.d("testing", "Wind Direction: " + windDeg);
            Log.d("tast", "Country: " + country);
            Log.d("tast", "Country2: " + tvCountry.getText());

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


    /*
     * getTemperatureAndUnit() and getWindSpeedAndUnit() WITHOUT parameters is used on todays forecast
     * while the ones with the int parameter are used to get the forecast for the 5-day view.
     */

    // To accomodate for default url units (Kelvin)
    // an attempt to regulate high temperatures (60 degrees celcius and above) will be made
    public String getTemperatureAndUnit() {
        if (settingsUnitSystem == UnitSystem.METRIC) {
            if (Double.parseDouble(temperature) > 60)
                return df1.format((Double.parseDouble(temperature) - 273.15)) + "°C";
            return df1.format(Double.parseDouble(temperature)) + "°C";
        }

        if (Double.parseDouble(temperature) > 140)
            return df1.format((Double.parseDouble(temperature) - 273.15) * 1.8 + 32)  + "°F";
        return df1.format(Double.parseDouble(temperature)) + "°F";
    }

    public String getWindSpeedAndUnit() {
        if (settingsUnitSystem == UnitSystem.METRIC)
            return df.format(Double.parseDouble(windSpeed)) + " m/s";
        return df.format(Double.parseDouble(windSpeed)) + " mph";
    }

    public String getTemperatureAndUnit(int i) {
        if (settingsUnitSystem == UnitSystem.METRIC) {
            if (Double.parseDouble(temperatureForecast[i]) > 60)
                return df1.format((Double.parseDouble(temperatureForecast[i]) - 273.15)) + "°C";
            return df1.format(Double.parseDouble(temperatureForecast[i])) + "°C";
        }
        if (Double.parseDouble(temperatureForecast[i]) > 140)
            return df1.format((Double.parseDouble(temperatureForecast[i]) - 273.15) * 1.8 + 32)  + "°F";
        return df1.format(Double.parseDouble(temperatureForecast[i])) + "°F";
    }

    public String getWindSpeedAndUnit(int i) {
        if (settingsUnitSystem == UnitSystem.METRIC)
            return df.format(Double.parseDouble(windSpeedForecast[i])) + " m/s";
        return df.format(Double.parseDouble(windSpeedForecast[i])) + " mph";
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
                if (day()) {
                    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
                }
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
        // the 5-day forecast doesn't need to know whether it is night or day.
        // it should always display the day-version of the weather
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        return Integer.parseInt(sunrise) < Integer.parseInt(dt) && Integer.parseInt(dt) < Integer.parseInt(sunset);
    }


}
