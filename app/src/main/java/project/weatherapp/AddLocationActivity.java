package project.weatherapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private static final String TAG_FORMATED_ADDRESS = "formatted_address";
    private static final String TAG_GEOMETRY = "geometry";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_LONGITUDE = "lng";
    private static final String TAG_LATITUDE = "lat";

    List<Location> locations = new ArrayList<>();

    private static Context mContext;

    JSONArray resultArray;

    private static String url;
    private String cityName;
    private String fixedCityName;
    private String address;
    private double longitude;
    private double latitude;

    ListView listView;
    LocationAdapter mAdapter;
    LinearLayout headerView;

    private Button btShowLocations;
    private Button btCloseAddLocation;
    private EditText etCityName;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();
        mAdapter = new LocationAdapter(mContext);
        listView = getListView();

        listView.setHeaderDividersEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(AddLocationActivity.this);

        headerView = (LinearLayout) inflater.inflate(R.layout.add_location_header,null);

        listView.addHeaderView(headerView);

        btShowLocations = (Button) findViewById(R.id.show_locations_in_add_location);
        btCloseAddLocation =(Button) findViewById(R.id.add_locations_close_button);
        etCityName = (EditText) findViewById(R.id.city_name_input);

        btShowLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    cityName = etCityName.getText().toString();

                    // Removing common url crashes
                    cityName = cityName.replace(" ", "");
                    cityName = cityName.replace("!","");
                    cityName = cityName.replace("%","");
                    cityName = cityName.replace("?","");
                    cityName = cityName.replace("\"","");

                    // Will crash if the user uses characters not allowed in url

                    url = "http://maps.google.com/maps/api/geocode/json?address=" + cityName + "&sensor=false";
                    locations.clear();
                    mAdapter.notifyDataSetChanged();
                    new GetAllLocations().execute();
                } catch (Exception e){
                    Toast.makeText(mContext,"Please don't do that",Toast.LENGTH_SHORT).show();
                }
            }
        });


        etCityName.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                for (int i = s.length(); i > 0; i--) {
                    if (s.subSequence(i - 1, i).toString().equals("\n"))
                        s.replace(i - 1, i, "");
                }
            }
        });

        btCloseAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setAdapter(mAdapter);

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
        public View getView(final int position, View convertView, ViewGroup parent) {

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

                    intent.putExtra("longitude", location.getLongitude());

                    Log.d("arraywithlocations", location.getLongitude().toString());

                    intent.putExtra("latitude", location.getLatitude());

                    Log.d("arraywithlocations", location.getLatitude().toString());

                    intent.putExtra("locationName", location.getLocationName());

                    Log.d("arraywithlocations", location.getLocationName());

                    setResult(RESULT_OK, intent);

                    Toast.makeText(mContext, "Added: " + location.toString(), Toast.LENGTH_SHORT).show();

                    Log.d("arraywithlocations", location.getLocationName());

                    finish();

                    Log.d("arraywithlocations", "after finish()");

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

            if (locations.size() == 0) {
                Toast.makeText(mContext,"Could not find the location.",Toast.LENGTH_SHORT).show();
            }

            mAdapter.notifyDataSetChanged();

            /**
             * Updating parsed JSON data into ListView
             * */

            
        }

    }
    
}
