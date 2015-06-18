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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends ListActivity {

    LocationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new LocationAdapter(getApplicationContext());

        getListView().setFooterDividersEnabled(true);

        TextView footerView = null;
        LayoutInflater inflater = LayoutInflater.from(LocationsActivity.this);
        footerView = (TextView) inflater.inflate(R.layout.footerview_addlocation_button,null);

        getListView().addFooterView(footerView);

        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getListView().setAdapter(mAdapter);

    }

    private class LocationAdapter extends BaseAdapter {

        private final List<Location> mLocations = new ArrayList<Location>();
        private Context mContext;

        public LocationAdapter(Context applicationContext) {
            mContext = applicationContext;
        }

        public void add(Location item) {

            mLocations.add(item);
            notifyDataSetChanged();

        }

        public void clear() {

            mLocations.clear();
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return mLocations.size();
        }

        @Override
        public Object getItem(int position) {
            return mLocations.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final Location location = (Location) getItem(position);

            RelativeLayout itemLayout = null;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            itemLayout = (RelativeLayout) inflater.inflate(R.layout.activity_locations,parent,false);

            final TextView nameView = (TextView) itemLayout.findViewById(R.id.location_item_string);
            nameView.setText(location.toString());

            Button btDeleteLocation = (Button) itemLayout.findViewById(R.id.location_delete_btn);
            btDeleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            return itemLayout;
        }
    }
}