package project.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.CompoundButton;

public class Settings extends MainActivity {

    private Switch rain;
    private Switch humidity;
    private RadioButton metric;
    private RadioButton imperial;
    private Button manageLocations;
    private RadioGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rain = (Switch) findViewById(R.id.rain_switch);
        humidity = (Switch) findViewById(R.id.humidity_switch);
        metric = (RadioButton) findViewById(R.id.metric_radio_button);
        imperial = (RadioButton) findViewById(R.id.imperial_radio_button);
        manageLocations = (Button) findViewById(R.id.manage_location);

        rain.setChecked(true);
        humidity.setChecked(true);

        rain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //MainActivity.setRain(isChecked);
            }
        });

        humidity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //MainActivity.setHumidity(isChecked);
            }
        });

        group = (RadioGroup) findViewById(R.id.unit_radio_group);
        group.clearCheck();

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // TODO set MainActivity unit to the checkedId
                // MainActivity.setUnit(checkedId);
            }
        });

        manageLocations.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goToManageLocations();
            }
        });
    }

    public void onResume() {

    }


    public void goToManageLocations() {
        Intent intent = new Intent(Settings.this,Locations.class);
        startActivity(intent);
    }
}
