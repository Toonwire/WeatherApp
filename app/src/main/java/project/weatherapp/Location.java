package project.weatherapp;

import android.content.Intent;

/**
 * Created by Jesper on 18/06/15.
 */
public class Location {

    private String mCityName;
    private String mCountry;
    private Double mLongitude;
    private Double mLatitude;

    Location(String cityName, String country , Double longitude, Double latitude) {
        this.mCityName = cityName;
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mCountry = country;
    }


    public String getCityName() {
        return mCityName;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public String getCountry() {
        return mCountry;
    }

    public String toString() {
        return mCityName + ", " + mCountry;
    }
}
