package project.weatherapp;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by daniel on 18/06/2015.
 */
public class Infos {
    private ArrayList<WeatherData> list;

    public ArrayList<WeatherData> getList() {
        return list;
    }

    public static class WeatherData{
        double speed;
        double temp;


    }
}
