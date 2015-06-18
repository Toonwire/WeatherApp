package project.weatherapp;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocationsActivity extends ListActivity {

    private Context mContext;
    ArrayList<String> cities = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new LocationAdapter());

        cities.add("Lyngby");
        cities.add("London");
        cities.add("New York");
        cities.add("Hong Kong");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.activity_locations,cities);

        setListAdapter(adapter);

    }

    private class LocationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public Object getItem(int position) {
            return cities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.activity_locations,parent,false);
            TextView textView = (TextView) rowView.findViewById(R.id.location_item_string);
            Button btDelete = (Button) rowView.findViewById(R.id.location_delete_btn);

            btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cities.remove(position);
                }
            });

            textView.setText(cities.get(position));

            return rowView;
        }
    }

    public class LocationsData {
        String cityName;
        float latitude;
        float longitude;
    }
}
