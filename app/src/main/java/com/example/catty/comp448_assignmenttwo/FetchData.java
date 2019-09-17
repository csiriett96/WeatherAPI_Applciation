package com.example.catty.comp448_assignmenttwo;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchData {

    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?q=";


   // private static final String OPEN_WEATHER_MAP_API =
     //       "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    //Fetch jsonobject from api based on the city specified
    public static JSONObject getJSON(Context context, String city){
        try{
            String urlString = OPEN_WEATHER_MAP_API+ city;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",context.getString(R.string.weather_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String temp = "";
            while((temp = reader.readLine()) != null){
                json.append(temp).append("\n");
            }
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                return null; //Catches 404 value
            }
            return data;

        }catch(Exception e){
            return null;
        }
    }





}
