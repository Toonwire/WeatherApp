package project.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.CompoundButton;

public class Settings extends MainActivity {

    private Switch swRain;
    private Switch swHumidity;
    private Switch swPressure;
    private Switch swSunriseSet;
    private Button btManageLocation;
    private RadioGroup rgGroup;
    private RadioButton rbMetric;
    private RadioButton rbImperial;

//    private boolean rain;
//    private boolean humidity;
//    private boolean pressure;
//    private boolean sunriseSet;
//    private UnitSystem unitSystem;

//    MainActivity main = new MainActivity();


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);

        swRain = (Switch) findViewById(R.id.rain_switch);
        swHumidity = (Switch) findViewById(R.id.humidity_switch);
        swPressure = (Switch) findViewById(R.id.pressure_switch);
//        sunriseSet = (Switch) findViewById(R.id.)

        rgGroup = (RadioGroup) findViewById(R.id.unit_radio_group);
        rbMetric = (RadioButton) findViewById(R.id.metric_radio_button);
        rbImperial = (RadioButton) findViewById(R.id.imperial_radio_button);

        btManageLocation = (Button) findViewById(R.id.manage_location);



        // Restore preferences
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        swRain.setChecked(settings.getBoolean("rain", false));
        swHumidity.setChecked(settings.getBoolean("humidity", false));
        swPressure.setChecked(settings.getBoolean("pressure", false));
        swSunriseSet.setChecked(settings.getBoolean("sunriseset", false));

        switch (UnitSystem.StringToEnum(settings.getString("unit", "METRIC"))) {
            case METRIC:
                rbMetric.setChecked(true);
                break;
            case IMPERIAL:
                rbImperial.setChecked(true);
                break;
        }


//
//        swRain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                rain = isChecked;
//            }
//        });
//
//        swHumidity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                humidity = isChecked;
//            }
//        });
//
//        swPressure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                pressure = isChecked;
//            }
//        });
//
//        swSunriseSet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sunriseSet = isChecked;
//            }
//        });

//        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if (checkedId == R.id.metric_radio_button) {
//                    unitSystem = UnitSystem.METRIC;
//                    //main.setUnitSystem(UnitSystem.METRIC);
//                } else if (checkedId == R.id.imperial_radio_button) {
//                    unitSystem = UnitSystem.IMPERIAL;
//                    //main.setUnitSystem(UnitSystem.IMPERIAL);
//                } else {
//                    unitSystem = UnitSystem.METRIC;
//                    //main.setUnitSystem(UnitSystem.METRIC);
//                }
//            }
//        });

        btManageLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goToManageLocations();
            }
        });

        // TODO add listeners for all attributes

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences("SETTINGS", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("rain", swRain.isChecked());
        editor.putBoolean("humidity", swHumidity.isChecked());
        editor.putBoolean("pressure", swPressure.isChecked());
        editor.putBoolean("sunriseset", swSunriseSet.isChecked());
        editor.putString("unit", getUnit().toString());

        // Commit the edits!
        editor.commit();

        finish();
    }

    public UnitSystem getUnit() {
        switch (rgGroup.getCheckedRadioButtonId()) {
            case R.id.metric_radio_button: {
                return UnitSystem.METRIC;
            }
            case R.id.imperial_radio_button: {
                return UnitSystem.IMPERIAL;
            }
            default: {
                return UnitSystem.METRIC;
            }
        }
    }

    public void goToManageLocations() {
        // TODO start Locations with array of all locations
        Intent intent = new Intent(Settings.this,Locations.class);
        startActivity(intent);
    }
}
