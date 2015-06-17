package project.weatherapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.ToggleButton;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Created by Jesper on 16/06/15.
 */
public class Settings extends MainActivity {

    private Switch rain;
    private Switch humidity;
    private RadioButton metric;
    private RadioButton imperial;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);

        rain = (Switch) findViewById(R.id.rain_switch);
        humidity = (Switch) findViewById(R.id.humidity_switch);
        metric = (RadioButton) findViewById(R.id.metric_radio_button);
        imperial = (RadioButton) findViewById(R.id.imperial_radio_button);

        rain.setChecked(true);
        humidity.setChecked(true);

        rain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.setRain(isChecked);
            }
        });

        humidity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.setHumidity(isChecked);
            }
        });

        RadioGroup group = (RadioGroup) findViewById(R.id.unit_radio_group);

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //TODO Make setUnit
                MainActivity.setUnit(isChecked);
            }
        });

    }

    public void onResume() {

    }

    public void onUnitRadioButtonClicked(View view) {
        boolean metricChecked = ((RadioButton) view).isChecked();
        boolean imperialChecked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.imperial_radio_button:
                if(imperialChecked) {
                    metricChecked = false;

                }
                break;
            case R.id.metric_radio_button:
                if(metricChecked) {
                    imperialChecked = false;
                }
                break;
        }
    }


    public void goToManageLocations() {
        Intent intent = new Intent(Settings.this,Locations.class);
        EditText edittext = (EditText) findViewById(R.id.manage_location);
        String message = edittext.getText().toString();
        intent.putExtra(null,message);
        startActivity(intent);
    }
}
