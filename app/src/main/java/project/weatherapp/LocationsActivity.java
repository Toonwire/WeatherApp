package project.weatherapp;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LocationsActivity extends ListActivity {

    LocationAdapter mAdapter;
    TextView footerView;
    RelativeLayout headerView;
    ListView listView;

    Button btCloseLocationsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new LocationAdapter(getApplicationContext());
        listView = getListView();

        listView.setFooterDividersEnabled(true);
        listView.setHeaderDividersEnabled(true);

        LayoutInflater inflater = LayoutInflater.from(LocationsActivity.this);

        footerView = (TextView) inflater.inflate(R.layout.footerview_addlocation_button,null);
        headerView = (RelativeLayout) inflater.inflate(R.layout.headerview_location,null);

        listView.addHeaderView(headerView);
        listView.addFooterView(footerView);

        btCloseLocationsActivity = (Button) findViewById(R.id.locations_close_button);

        btCloseLocationsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.add(new Location("Helsingør", 12.00, 56.00));
                Intent intent = new Intent(LocationsActivity.this, AddLocationActivity.class);
                startActivity(intent);
            }
        });

        mAdapter.add(new Location("Tønder", 12.00, 56.00));
        mAdapter.add(new Location("Thy", 12.00, 56.00));
        mAdapter.add(new Location("Moscow", 12.00, 56.00));

        listView.setAdapter(mAdapter);

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

            final Location location = mLocations.get(position);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

            RelativeLayout itemLayout = (RelativeLayout) inflater.inflate(R.layout.activity_locations,parent,false);

            final TextView nameView = (TextView) itemLayout.findViewById(R.id.location_item_string);
            nameView.setText(location.toString());

            Button btDeleteLocation = (Button) itemLayout.findViewById(R.id.location_delete_btn);
            btDeleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // TODO add undo

                    mLocations.remove(location);
                    Toast.makeText(mContext, "Deleted: " + location.toString(), Toast.LENGTH_SHORT).show();
                    listView.setAdapter(mAdapter);
                }
            });

            return itemLayout;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri contactUri = data.getData();

        }
    }
}