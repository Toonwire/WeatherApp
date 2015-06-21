package project.weatherapp;

import android.content.Intent;

/**
 * Created by Jesper on 18/06/15.
 */
public class Location {

    private String mLocationName;
    private double mLongitude;
    private double mLatitude;

    Location(String locationName, double longitude, double latitude) {
        this.mLocationName = locationName;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
    }


    public String getLocationName() {
        return mLocationName;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public String toString() {
        return mLocationName;
    }
}
