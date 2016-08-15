package ru.letnes.materialdesignsceleton;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.letnes.materialdesignsceleton.adapters.DbAdapter;
import ru.letnes.materialdesignsceleton.model.Main;
import ru.letnes.materialdesignsceleton.model.Weather;
import ru.letnes.materialdesignsceleton.model.WeatherData;
import ru.letnes.materialdesignsceleton.model.Wind;


public class CityDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private DbAdapter mDb;
     String cityName;
    private Typeface weatherFont;
     TextView cityTitle;
     TextView updatedField;
     TextView detailsField;
     TextView currentTemperatureField;
     TextView weatherIcon;
    private CollapsingToolbarLayout appBarLayout;

    public boolean mTwoPane;

    public CityDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            cityName = getArguments().getString(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

            weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
            if (getResources().getBoolean(R.bool.isTablet)) {
                mTwoPane = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.city_detail, container, false);
        cityTitle = (TextView) rootView.findViewById(R.id.city_title);
        updatedField = (TextView) rootView.findViewById(R.id.updated_field);
        detailsField = (TextView) rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);



        renderWeatherData(getContext(), cityName);
        return rootView;
    }




    public void renderWeatherData(Context mCtx, String mCity) {


        mDb = new DbAdapter(mCtx);

        ArrayList<WeatherData> city = mDb.getCity(mCity);
        Log.d("SHARAPOVVV",city.get(0).getName());

        if(mTwoPane){
            cityTitle.setText(city.get(0).getName().toUpperCase(Locale.US) +
                    ", " +
                    city.get(0).getSys().getCountry());
        }else{
            if (appBarLayout != null) {
                appBarLayout.setTitle(city.get(0).getName().toUpperCase(Locale.US) +
                        ", " +
                        city.get(0).getSys().getCountry());
            }
        }

        Weather details = city.get(0).getWeather().get(0);

        Wind wind = city.get(0).getWind();
        Main main = city.get(0).getMain();
        Double pressure = main.getPressure() * 0.750062;


        String detailText = details.getDescription().toUpperCase(Locale.getDefault()) + "\n" +
                "Влажность: " + main.getHumidity() + "%" + "\n" +
                "Ветер: " + wind.getSpeed() + "м/с, " + getFormattedWind(wind.getDeg()) + "\n" +
                "Давление: " + Math.round(pressure) + " мм.рт.ст.";


        detailsField.setText(detailText);
        String tempText = String.format("%.2f", main.getTemp()) + " ℃";
        currentTemperatureField.setText(tempText);

        Date date = new Date(city.get(0).getDt()*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        String formattedDate = "Последнее обновление: " + sdf.format(date);




        updatedField.setText(formattedDate);

        switch (city.get(0).getWeather().get(0).getIcon()){
            case "01d":
                weatherIcon.setText(R.string.wi_day_sunny);
                break;
            case "02d":
                weatherIcon.setText(R.string.wi_cloudy_gusts);
                break;
            case "03d":
                weatherIcon.setText(R.string.wi_cloud_down);
                break;
            case "10d":
                weatherIcon.setText(R.string.wi_day_rain_mix);
                break;
            case "11d":
                weatherIcon.setText(R.string.wi_day_thunderstorm);
                break;
            case "13d":
                weatherIcon.setText(R.string.wi_day_snow);
                break;
            case "01n":
                weatherIcon.setText(R.string.wi_night_clear);
                break;
            case "04d":
                weatherIcon.setText(R.string.wi_cloudy);
                break;
            case "04n":
                weatherIcon.setText(R.string.wi_night_cloudy);
                break;
            case "02n":
                weatherIcon.setText(R.string.wi_night_cloudy);
                break;
            case "03n":
                weatherIcon.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "10n":
                weatherIcon.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "11n":
                weatherIcon.setText(R.string.wi_night_rain);
                break;
            case "13n":
                weatherIcon.setText(R.string.wi_night_snow);
                break;
        }



    }
    static String getFormattedWind(double degrees) {

        String direction = "";
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = "C";
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = "СВ";
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = "В";
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = "ЮВ";
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = "Ю";
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = "ЮЗ";
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = "З";
        } else if (degrees >= 292.5 || degrees < 22.5) {
            direction = "СЗ";
        }
        return String.format(direction);
    }

}
