package ru.letnes.materialdesignsceleton.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import ru.letnes.materialdesignsceleton.R;
import ru.letnes.materialdesignsceleton.model.CityTips;
import ru.letnes.materialdesignsceleton.model.List;
import ru.letnes.materialdesignsceleton.service.APIservice;
import rx.Observable;


public class WeatherAutoCompleteAdapter extends BaseAdapter implements Filterable {
    private static final int MAX_RESULTS = 20;
    private final Context mContext;
    private ArrayList<List> resultList = new ArrayList<List>();
    public WeatherAutoCompleteAdapter(Context context) {
        mContext = context;


    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index).getName() + ", " + resultList.get(index).getSys().getCountry();
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
                    findWeatherData(mContext, constraint.toString());

                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (results != null && results.count > 0) {
                    resultList = (ArrayList<List>) results.values;
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
    private void findWeatherData(Context mContext, String cityTitle) {

        Log.d("SHARAPOV", cityTitle);

        APIservice apiService = APIservice.retrofit.create(APIservice.class);
        Observable<CityTips> call = apiService.findCity(cityTitle,"like","ru", "population", "30","json", mContext.getString(R.string.open_weather_maps_app_id));
        call.subscribe(cityTips -> {
                    Log.d("SHARAPOV", cityTips.getList().get(0).getName());
                    resultList = cityTips.getList();
                });
    }
}


