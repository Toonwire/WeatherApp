package project.weatherapp;

import android.content.Intent;

/**
 * Created by Jesper on 18/06/15.
 */
public class Location {

    private String mCityName = new String();
    private String mCountry = new String();
    private Float mLongitude;
    private Float mLatitude;

    Location(String cityName, String country , Float longitude, Float latitude) {
        this.mCityName = cityName;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mCountry = country;
    }


    public String getCityName() {
        return mCityName;
    }

    public Float getLongitude() {
        return mLongitude;
    }

    public Float getLatitude() {
        return mLatitude;
    }

    public String getCountry() {
        return mCountry;
    }

    public String toString() {
        return mCityName + ", " + mCountry;
    }
}
