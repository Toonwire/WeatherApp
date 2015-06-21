package project.weatherapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FragmentActivity {



    private static String url;


    // Reference to the LocationManager and LocationListener
    private LocationManager locationManager;
    private LocationListener locationListener;
    // Current best location estimate
    private Location location;

    private UnitSystem settingsUnitSystem = UnitSystem.METRIC;
    private boolean settingsRain = false;
    private boolean settingsHumidity = false;
    private boolean settingsPressure = false;
    private boolean settingsWind = false;
    private boolean settingsSunriseSet = false;

    private PagerAdapter mPagerAdapter;
    private ViewPager pager;

    private Button settingsButton;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mPagerAdapter != null)
            mPagerAdapter.removeAllFragments();

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        Log.d("Tess", "onCreate()");
        // Acquire reference to the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) {
            Log.i("TESTING", "No location found - Shutting down");
            finish();
        }
        // Get best last location measurement
        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSettings();
        Log.d("Tess", "onResume()");


        mPagerAdapter = new PagerAdapter(this.getSupportFragmentManager());

        // Display last reading information
//        if (location != null) {
//            Log.i("TESTING", location.getLongitude() + " --- " + location.getLatitude());
//
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//                url = "http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&units=" + settingsUnitSystem.toString();
//                mPagerAdapter.addFragment(new WeatherLocation(url, settingsRain, settingsHumidity, settingsPressure, settingsWind, settingsSunriseSet, settingsUnitSystem));
//            }
//            else {
//                url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&cnt=5&mode=json" + "&units=" + settingsUnitSystem.toString();
//                mPagerAdapter.addFragment(new WeatherLocation(url, settingsUnitSystem));
//            }
//        } else
//            Log.i("TESTING", "No Initial Reading Available");


        // LOAD SAVED LOCATIONS, USING TODAY WEATHER URL
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPagerAdapter.addFragment(new WeatherLocation("http://api.openweathermap.org/data/2.5/weather?q=London,uk&units=metric", settingsRain, settingsHumidity, settingsPressure, settingsWind, settingsSunriseSet, settingsUnitSystem, "Port1"));
            mPagerAdapter.addFragment(new WeatherLocation("http://api.openweathermap.org/data/2.5/weather?q=Helsingør,dk&units=metric", settingsRain, settingsHumidity, settingsPressure, settingsWind, settingsSunriseSet, settingsUnitSystem, "Port2"));
            Log.d("Tess", "Portrait");
        }

        // LOAD SAVED LOCATIONS, USING FORECAST WEATHER URL
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Tess", "Landscape start");
            mPagerAdapter.addFragment(new WeatherLocation("http://api.openweathermap.org/data/2.5/forecast/daily?q=London,uk&mode=json&units=metric&cnt=5", settingsUnitSystem, "Land1"));
            mPagerAdapter.addFragment(new WeatherLocation("http://api.openweathermap.org/data/2.5/forecast/daily?q=Helsingør,denmark&mode=json&units=metric&cnt=5", settingsUnitSystem, "Land2"));
            Log.d("Tess", "Landscape");
        }
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(mPagerAdapter);
    }

    public void loadSettings() {
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);

        settingsRain = settings.getBoolean("rain", false);
        settingsHumidity = settings.getBoolean("humidity", false);
        settingsPressure = settings.getBoolean("pressure", false);
        settingsSunriseSet = settings.getBoolean("sunriseset", false);
        settingsWind = settings.getBoolean("wind", false);

        Log.d("Test", "" + settingsRain);
        Log.d("Test", "" + settingsHumidity);
        Log.d("Test", "" + settingsPressure);
        Log.d("Test", "" + settingsSunriseSet);
        Log.d("Test", "" + settingsWind);

        switch (UnitSystem.StringToEnum(settings.getString("unit", "METRIC"))) {
            case METRIC:
                settingsUnitSystem = UnitSystem.METRIC;
                break;
            case IMPERIAL:
                settingsUnitSystem = UnitSystem.IMPERIAL;
                break;
        }
    }
}
