package project.weatherapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lukas on 21/06/2015.
 */
public class PagerAdapter extends FragmentPagerAdapter{

    FragmentManager fragmentManager;

    private List<WeatherLocation> locations;
    public PagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        this.locations = new ArrayList<WeatherLocation>();
    }
    public void addFragment(WeatherLocation location) {
        locations.add(location);
    }
    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        return this.locations.get(position);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.locations.size();
    }

    public void removeAllFragments() {
        if (locations != null) {
            for (WeatherLocation location : locations)
                fragmentManager.beginTransaction().remove(location).commit();
            locations.clear();
            notifyDataSetChanged();
        }
    }
}
