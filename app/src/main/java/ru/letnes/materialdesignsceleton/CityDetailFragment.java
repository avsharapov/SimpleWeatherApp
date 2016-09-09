package ru.letnes.materialdesignsceleton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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

    public TextView mCityTitle;
    public TextView mUpdatedField;
    public TextView mDetailsField;
    public TextView mCurrentTemperatureField;
    public TextView mWeatherIcon;
    public CollapsingToolbarLayout mAppBarLayout;

    private Typeface weatherFont;
    private boolean mTwoPane;
    private String cityName;

    public static final String ARG_ITEM_ID = "item_id";

    public CityDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            cityName = getArguments().getString(ARG_ITEM_ID);
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
        mAppBarLayout = (CollapsingToolbarLayout) this.getActivity().findViewById(R.id.toolbar_layout);
        mCityTitle = (TextView) rootView.findViewById(R.id.city_title);
        mUpdatedField = (TextView) rootView.findViewById(R.id.updated_field);
        mDetailsField = (TextView) rootView.findViewById(R.id.details_field);
        mCurrentTemperatureField = (TextView) rootView.findViewById(R.id.current_temperature_field);
        mWeatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);
        mWeatherIcon.setTypeface(weatherFont);
        renderWeatherData(getContext(), cityName);
        return rootView;
    }

    public void renderWeatherData(Context mCtx, String mCity) {
        DbAdapter db = new DbAdapter(mCtx);
        ArrayList<WeatherData> city = db.getCity(mCity);
        if(mTwoPane){
            mCityTitle.setText(String.format(getString(R.string.city_title),city.get(0).getName().toUpperCase(Locale.US),city.get(0).getSys().getCountry()));
        }else{
            if (mAppBarLayout != null) {
                mAppBarLayout.setTitle(String.format(getString(R.string.city_title),city.get(0).getName().toUpperCase(Locale.US),city.get(0).getSys().getCountry()));
            }
        }
        Weather details = city.get(0).getWeather().get(0);
        Wind wind = city.get(0).getWind();
        Main main = city.get(0).getMain();
        Double pressure = Integer.parseInt(main.getPressure()) * 0.750062;
        String detailText = details.getDescription().toUpperCase(Locale.getDefault()) + "\n" +
                "Влажность: " + main.getHumidity() + "%" + "\n" +
                "Ветер: " + wind.getSpeed() + "м/с, " + getFormattedWind(Double.parseDouble(wind.getDeg())) + "\n" +
                "Давление: " + Math.round(pressure) + " мм.рт.ст.";
        mDetailsField.setText(detailText);
        String tempText = String.format(getString(R.string.city_temp), main.getTemp());
        mCurrentTemperatureField.setText(tempText);
        Date date = new Date(city.get(0).getDt()*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String formattedDate = "Последнее обновление: " + sdf.format(date);
        mUpdatedField.setText(formattedDate);
        switch (city.get(0).getWeather().get(0).getIcon()){
            case "01d":
                mWeatherIcon.setText(R.string.wi_day_sunny);
                break;
            case "02d":
                mWeatherIcon.setText(R.string.wi_cloudy_gusts);
                break;
            case "03d":
                mWeatherIcon.setText(R.string.wi_cloud_down);
                break;
            case "10d":
                mWeatherIcon.setText(R.string.wi_day_rain_mix);
                break;
            case "11d":
                mWeatherIcon.setText(R.string.wi_day_thunderstorm);
                break;
            case "13d":
                mWeatherIcon.setText(R.string.wi_day_snow);
                break;
            case "01n":
                mWeatherIcon.setText(R.string.wi_night_clear);
                break;
            case "04d":
                mWeatherIcon.setText(R.string.wi_cloudy);
                break;
            case "04n":
                mWeatherIcon.setText(R.string.wi_night_cloudy);
                break;
            case "02n":
                mWeatherIcon.setText(R.string.wi_night_cloudy);
                break;
            case "03n":
                mWeatherIcon.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "10n":
                mWeatherIcon.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "11n":
                mWeatherIcon.setText(R.string.wi_night_rain);
                break;
            case "13n":
                mWeatherIcon.setText(R.string.wi_night_snow);
                break;
        }
    }
    static String getFormattedWind(Double degrees) {
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
        return direction;
    }
}
