package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_STRING = "weather";

    public String geocodeURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public String darkSkyURL = "https://api.darksky.net/forecast/9fc8da73549951d7da276eb1d2984840/";

    private String googleAPIKey = "AIzaSyCF_bMN7_W7ghHIkXeiQCYc2awsUKm-Xbk";
    private String darkskyAPIKey = "9fc8da73549951d7da276eb1d2984840";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void findMe (View view) {
        // Get the text view.
        TextView addressInput = (TextView)findViewById(R.id.addressText);

        // Get the value of the text view.
        String address = addressInput.getText().toString();

        // get location coordinates from Google
        locationData addressCoord = getGeoCodeInfo(address);

        // get weather data from DarkSky
        weatherData addressWeather = getWeatherInfo(addressCoord.latitude, addressCoord.longitude);

        if(!addressCoord.error || !addressWeather.error) {
            // send data to weather activity and start it
            Intent weatherIntent = new Intent(this, WeatherActivity.class);
            double[] locationInfo = {
                    addressWeather.temp,
                    addressWeather.humidity,
                    addressWeather.windSpeed,
                    addressWeather.precepitation,
                    addressCoord.latitude,
                    addressCoord.longitude};
            weatherIntent.putExtra(WEATHER_STRING, locationInfo);
            startActivity(weatherIntent);
        }

        // API error, give error message
        else{
            Toast errorNotify = Toast.makeText(this, "Address error, please enter new address.",
                    Toast.LENGTH_SHORT);
            errorNotify.show();
        }
    }

    // Nick driving
    public locationData getGeoCodeInfo(String address) {
        String[] address_split = address.split("(?<=,)");
        String url = geocodeURL;

        for(int i = 0; i < address_split.length; i++) {
            String urlEdited = address_split[i].replace(" ", "+");
            url += urlEdited;
        }

        // Add API key
        url += "&key=" + googleAPIKey;

        // Send request to google maps to get JSON with lat/long
        OkHttpClient client = new OkHttpClient();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response  = client.newCall(request).execute();

            String jsonData = response.body().string();

            // Only need lat/long to get weather data, grab from returned JSON
            JSONObject locationData = new JSONObject(jsonData);
            JSONArray results = locationData.getJSONArray("results");

            double longitude = 0.0;
            double latitude = 0.0;
            // End up getting 2 lat and long results. Each slightly different, maybe average them for final result?
            for(int i = 0; i < results.length(); i++) {
                JSONObject geometryData = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                longitude = geometryData.getDouble("lng");
                latitude = geometryData.getDouble("lat");
            }
            return new locationData(latitude, longitude);

        } catch(Exception e) {
            e.printStackTrace();
            return new locationData(true);
        }
    }

    public weatherData getWeatherInfo(double lat, double longit) {

        String latitude = Double.toString(lat);
        String longitude = Double.toString(longit);
        String url = darkSkyURL + latitude + "," + longitude;

        // Send request to google maps to get JSON with lat/long
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response  = client.newCall(request).execute();
            String jsonData = response.body().string();
            System.out.println(jsonData);

            // Grab the relevant weather data from JSON
            JSONObject weatherData = new JSONObject(jsonData);
            JSONObject currData = weatherData.getJSONObject("currently");

            double temperature = currData.getDouble("temperature");
            double humidity = currData.getDouble("humidity");
            double windSpeed = currData.getDouble("windSpeed");
            double precipitation = currData.getDouble("precipProbability");

            return new weatherData(temperature, humidity, windSpeed, precipitation);

        } catch(Exception e) {
            e.printStackTrace();
            return new weatherData(true);
        }
    }

    // Class to hold lat and long info from Google GeoCoding API
    public class locationData{
        double latitude;
        double longitude;
        boolean error;

        locationData(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
            this.error = false;
        }

        locationData(boolean error){
            this.error = error;
        }
    }

    // Class to hold weather info from Dark Sky APIs
    public class weatherData{
        double temp;
        double humidity;
        double windSpeed;
        double precepitation;
        boolean error;

        weatherData(double temp, double humidity, double windSpeed, double precepitation){
            this.temp = temp;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.precepitation = precepitation;
            this.error = false;
        }

        weatherData(boolean error){
            this.error = error;
        }
    }

}
