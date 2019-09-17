package com.example.catty.comp448_assignmenttwo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment{
    private EditText searchContent;
    private Button searchButton;
    private Button shareButton;
    private ImageView weatherIcon;
    private FloatingActionButton fab;
    private DatabaseHelper db;
    private View rootView;

    private Handler handler;
    private String currCity;
    private String currDetails;
    private String currTemp;
    private int currIconID;

    public SearchFragment(){
        handler = new Handler();
    }
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override //Save state
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("CURR_CITY", currCity);
        outState.putString("CURR_DETAILS",currDetails);
        outState.putString("CURR_TEMP", currTemp);
        outState.putInt("CURR_ICON", currIconID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_search, container, false);

            db = new DatabaseHelper(getContext());
            searchContent = (EditText) rootView.findViewById(R.id.search_bar) ;
            searchButton = (Button) rootView.findViewById(R.id.search_button);
            fab = (FloatingActionButton) rootView.findViewById(R.id.floatingButton);
            shareButton = (Button) rootView.findViewById(R.id.share_button);

            if(savedInstanceState != null){
                //Load in saved state if saved
                currCity = savedInstanceState.getString("CURR_CITY");
                currDetails = savedInstanceState.getString("CURR_DETAILS");
                currTemp = savedInstanceState.getString("CURR_TEMP");
                currIconID = savedInstanceState.getInt("CURR_ICON");
                if(currCity != null)
                    setItems();
            }

            //Setting appropriate button clicks
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeCity(v);
                }
            });
            searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(actionId == EditorInfo.IME_ACTION_SEARCH){
                        changeCity(v);
                        return true;
                    }
                    return false;
                }
            });
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareContent();
                }
            });
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCity(currCity);
                }
            });


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(searchFromList);
    }

    //Set relevant views and display buttons
    private void setItems(){
        TextView cityField = (TextView) rootView.findViewById(R.id.cityField);
        TextView detailsField = (TextView) rootView.findViewById(R.id.detailsField);
        TextView tempField = (TextView) rootView.findViewById(R.id.current_temperature);
        weatherIcon = (ImageView) rootView.findViewById(R.id.weatherIcon);

        cityField.setText(currCity);
        detailsField.setText(currDetails);
        tempField.setText(currTemp);
        setWeatherIcon(currIconID);

        shareButton.setVisibility(View.VISIBLE);
        fab.show();
    }

    private void addCity(String city){
        List<City> favouriteCitiesList = db.getAllCities();
        Snackbar snackbar;
        boolean listContains = false;

        //Check if list already contains city
        for(City w: favouriteCitiesList){
            if(w.getCity().equals(city)){
                listContains = true;
            }
        }

        //Only add to the favourites if it doesn't already exist
        if(!listContains){
            db.insertCity(currCity);
            currCity = city;
            //Broadcast message to update list
            Intent intent = new Intent("UPDATE_LIST");
            getActivity().sendBroadcast(intent);
            snackbar = Snackbar.make(rootView, R.string.added_to_favourites, Snackbar.LENGTH_LONG);
        }else{
            snackbar = Snackbar.make(rootView, R.string.already_in_favourites, Snackbar.LENGTH_LONG);
        }
        //Show relevant snackbar message
        snackbar.show();
    }

    //Hides the keyboard from view
    public static void hideKeyboardForm(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Share content with another app
    private void shareContent(){
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String sharingContent = "The current weather conditions in " + currCity + " are " +  currDetails
                +" and the temperature is also " + currTemp;
        sharingIntent.putExtra(Intent.EXTRA_TEXT, sharingContent);
        startActivity(Intent.createChooser(sharingIntent, getText(R.string.share_send_text)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if(Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            if (intent.getType().equals("text/plain")) {
                updateWeatherData(intent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(searchFromList, new IntentFilter("SEARCH"));

    }

    //Fetch weather data from API if no weather data found alert user otherwise renderWeather to show data
    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = FetchData.getJSON(getActivity(), city);
                if(json == null){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(rootView, R.string.place_not_found, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setItems();
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    //Extract the weather data from the json then display it
    private void renderWeather(JSONObject json){
        try {
            currCity = json.getString("name")+ "," + json.getJSONObject("sys").getString("country");

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");

            currDetails = details.getString("description").toUpperCase(Locale.US) +
                                    "\n" + "Humidity: " + main.getString("humidity") + "%" +
                                    "\n" + "Pressure: " + main.getString("pressure") + " hPa";

            Double kelvinTemp = main.getDouble("temp");
            Double celTemp = kelvinTemp - 273.15;
            currTemp = String.format("%.2f", celTemp) + "℃";
            setWeatherIcon(details.getInt("id"));

            setItems();
        }catch (Exception e){
            Log.e("WeatherApp", "One or more fields was not found in the JSON");
        }
    }

    //Set the icon image based on the ID
    private void setWeatherIcon(int actualID){
        int tempID = actualID/100;
        int icon = 0;
        if(actualID == 800) {
            icon = R.drawable.weather_sunny;
        }else if(actualID == 801) {
            icon = R.drawable.weather_cloudy;
        }else if(actualID == 802){
            icon = R.drawable.weather_scatteredcloud;
        }else{
            switch(tempID){
                case 2 : icon = R.drawable.weather_thunderstorm;
                break;
                case 3 : icon = R.drawable.weather_shower;
                break;
                case 5 : icon = R.drawable.weather_rain;
                break;
                case 6 : icon = R.drawable.weather_snow;
                break;
                case 7 : icon = R.drawable.weather_foggy;
                break;
                case 8 : icon = R.drawable.weather_brokencloud;
                break;
            }
        }
        weatherIcon.setImageResource(icon);
        currIconID = actualID;
    }

    //Change the city, and hide the keyboard
    public void changeCity(View rootView){
        String city = searchContent.getText().toString();
        updateWeatherData(city);
        hideKeyboardForm(getContext(), rootView);
    }

    //Update weather when called from the favourites fragment
    private BroadcastReceiver searchFromList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateWeatherData(intent.getStringExtra("CITY"));
        }
    };
}
