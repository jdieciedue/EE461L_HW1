package com.example.homework1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WeatherActivity extends AppCompatActivity {

    private static final String WEATHER_STRING = "weather";
    private static final String COORDS_STRING = "coords";
    private double[] locationInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        updateWeatherDetails();
    }

    public void updateWeatherDetails(){
        TextView temperature = (TextView)findViewById(R.id.tempTV);
        TextView humidity = (TextView)findViewById(R.id.humidityTV);
        TextView windspeed = (TextView)findViewById(R.id.windSpeedTV);
        TextView precipitation = (TextView)findViewById(R.id.precipitationTV);

        this.locationInfo = getIntent().getDoubleArrayExtra(WEATHER_STRING);

        temperature.setText(Double.toString(locationInfo[0]));
        humidity.setText(Double.toString(locationInfo[1]));
        windspeed.setText(Double.toString(locationInfo[2]));
        precipitation.setText(Double.toString(locationInfo[3]));
    }

    public void showMap(View view){
        Intent mapIntent = new Intent(this, MapsActivity.class);
        double[] coords = {locationInfo[4], locationInfo[5]};
        mapIntent.putExtra(COORDS_STRING, coords);
        startActivity(mapIntent);
    }
}
