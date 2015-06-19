package project.weatherapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddLocationActivity extends ListActivity {

    private static final String TAG_RESULTS = "results";
    private static final String TAG_ADDRESS_COMPONENTS = "address_components";
    private static final String TAG_CITY_NAME = "long_name";
    private static final String TAG_COUNTRY_LONG_NAME = "long_name";
    private static final String TAG_COUNTRY_SHORT_NAME = "short_name";
    private static final String TAG_POSTAL_CODE = "long_name";
    private static final String TAG_FORMATED_ADDRESS = "formatted_address";
    private static final String TAG_GEOMETRY = "geometry";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_LONGITUDE = "lng";
    private static final String TAG_LATITUDE = "lat";

    List<Location> locations = new ArrayList<Location>();

    JSONArray resultArray;

    private static String url;
    private String countryName;
    private String cityName;
    private String address;
    private double longitude;
    private double latitude;

    ListView listView;
    LocationAdapter mAdapter;
    RelativeLayout headerView;

    private Button btAddLocation;
    private Button btCloseAddLocation;
    private EditText etCountryName;
    private EditText etCityName;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new LocationAdapter(getApplicationContext());
        listView = getListView();

        listView.setHeaderDividersEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(AddLocationActivity.this);

        headerView = (RelativeLayout) inflater.inflate(R.layout.add_location_header,null);

        listView.addHeaderView(headerView);

        btAddLocation = (Button) findViewById(R.id.add_location_in_add_location);
        etCountryName = (EditText) findViewById(R.id.country_name_input);
        etCityName = (EditText) findViewById(R.id.city_name_input);

        btAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryName = etCountryName.getText().toString();
                cityName = etCityName.getText().toString();
                url = "http://maps.google.com/maps/api/geocode/json?address=" + cityName + "-" + countryName + "&sensor=false";
                new GetAllLocations().execute();
            }
        });


        listView.setAdapter(mAdapter);

    }

    protected void onResume() {
        super.onResume();
        
    }

    private class LocationAdapter extends BaseAdapter {

        private Context mContext;

        public LocationAdapter(Context applicationContext) {
            mContext = applicationContext;
        }

        public void add(Location item) {

            locations.add(item);
            notifyDataSetChanged();

        }

        public void clear() {

            locations.clear();
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return locations.size();
        }

        @Override
        public Object getItem(int position) {
            return locations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Location location = locations.get(position);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

            RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.add_location_list,parent,false);

            final TextView nameView = (TextView) itemLayout.findViewById(R.id.location_item_another_string);
            nameView.setText(location.toString());

            Button btAddLocation = (Button) itemLayout.findViewById(R.id.location_add_this_location_btn);
            btAddLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);

                    Toast.makeText(mContext, "Added: " + location.toString(), Toast.LENGTH_SHORT).show();
                    listView.setAdapter(mAdapter);
                }
            });

            return itemLayout;
        }
    }

    /**
     * Async task class to get json by making HTTP call
     * */

    private class GetAllLocations extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AddLocationActivity.this);
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

                    resultArray = jsonObj.getJSONArray(TAG_RESULTS);

                    Log.d("loop","Is before loop");
                    for(int i = 0; i < resultArray.length(); i++) {
                        Log.d("loop", resultArray.getString(i));
                        JSONObject object = resultArray.getJSONObject(i);
                        address = object.getString(TAG_FORMATED_ADDRESS);
                        Log.d("loop", "test1");

                        JSONObject location = object.getJSONObject(TAG_GEOMETRY).getJSONObject(TAG_LOCATION);
                        Log.d("loop", "test3");

                        longitude = Double.parseDouble(location.getString(TAG_LONGITUDE));
                        Log.d("loop", "test4");

                        latitude = Double.parseDouble(location.getString(TAG_LATITUDE));

                        Log.d("loop","Before adding it");

                        locations.add(new Location(address, longitude, latitude));

                        Log.d("loop",locations.toString());
                    }
                    Log.d("loop","Is after loop");


                    /*
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
                    hum<idity = todaysMainResources.getString(TAG_HUMIDITY);

                    JSONObject todaysWindResources = jsonObj.getJSONObject(TAG_WIND);
                    windSpeed = todaysWindResources.getString(TAG_WIND_SPEED);
                    windDeg = todaysWindResources.getString(TAG_WIND_DEGREES);

                    JSONObject todaysRainResources = jsonObj.getJSONObject(TAG_RAIN);
                    rain = todaysRainResources.getString(TAG_3H);
                    Log.d("tagtest", rain);
                    */

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
            
        }

    }
    
}
