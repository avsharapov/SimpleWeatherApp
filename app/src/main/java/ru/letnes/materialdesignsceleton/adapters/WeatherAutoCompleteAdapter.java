package ru.letnes.materialdesignsceleton.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.letnes.materialdesignsceleton.R;
import ru.letnes.materialdesignsceleton.model.WeatherData;
import ru.letnes.materialdesignsceleton.service.APIservice;


public class WeatherAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 20;
    private final Context mContext;
    private ArrayList<String> resultList = new ArrayList<String>();
    WeatherData weatherData;
    DbAdapter mDb;
    public WeatherAutoCompleteAdapter(Context context) {
        mContext = context;


    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.dropdown_item, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position));

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {


                    // Assign the data to the FilterResults
                    resultList.clear();
                    resultList = findWeatherData(mContext, constraint.toString());

                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    resultList = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};

        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private ArrayList<String> findWeatherData(Context mContext, String cityTitle) {

        final ArrayList<String> arres = new ArrayList<String>();
        ResponseBody responseBody;
        APIservice apiService = APIservice.retrofit.create(APIservice.class);
        Call<ResponseBody> call = apiService.findCity(cityTitle,"like","ru", "population", "30","json", mContext.getString(R.string.open_weather_maps_app_id));
        try {

            responseBody = call.execute().body();
            JSONObject data = new JSONObject(responseBody.string());
            JSONArray list = data.getJSONArray("list");
            for(int i=0;i<list.length();i++){
                arres.add(list.getJSONObject(i).getString("name") + ", " + list.getJSONObject(i).getJSONObject("sys").getString("country"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


        return arres;
    }
}


