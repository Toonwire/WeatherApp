package project.weatherapp;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by daniel on 18/06/2015.
 */
public interface RestService {
    @GET("/data/2.5/forecast/daily?cnt=5&mode=json")
    void getWeather(@Query("lat")double lat,@Query("lon")double lon, Callback<Infos> callback);
}
