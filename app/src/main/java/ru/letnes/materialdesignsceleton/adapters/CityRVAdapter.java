package ru.letnes.materialdesignsceleton.adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;



import ru.letnes.materialdesignsceleton.CityDetailActivity;
import ru.letnes.materialdesignsceleton.CityDetailFragment;
import ru.letnes.materialdesignsceleton.R;
import ru.letnes.materialdesignsceleton.helper.ItemTouchHelperAdapter;
import ru.letnes.materialdesignsceleton.helper.ItemTouchHelperViewHolder;
import ru.letnes.materialdesignsceleton.helper.OnRecyclerListChangedListener;
import ru.letnes.materialdesignsceleton.helper.OnStartDragListener;
import ru.letnes.materialdesignsceleton.model.WeatherData;


public class CityRVAdapter extends RecyclerView.Adapter<CityRVAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private FragmentManager mFragmentManager;
    private ArrayList<WeatherData> mValues;
    private Typeface weatherFont;
    private Context mCtx;
    private RecyclerView mrecyclerView;
    private View rootView;
    public boolean mTwoPane;
    private final OnStartDragListener mDragStartListener;
    private DbAdapter mDb;
    private OnRecyclerListChangedListener mListChangedListener;
    public CityRVAdapter(FragmentManager fm, Context Ctx, RecyclerView recyclerView, ArrayList<WeatherData> items, OnStartDragListener dragStartListener, OnRecyclerListChangedListener listChangedListener) {
        mDragStartListener = dragStartListener;
        mFragmentManager = fm;
        mCtx = Ctx;
        this.mDb = new DbAdapter(mCtx);
        mValues = items;
        mListChangedListener = listChangedListener;
        mrecyclerView = recyclerView;
        LayoutInflater layoutInflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = layoutInflater.inflate(R.layout.city_list, null, false);
        if (mCtx.getResources().getBoolean(R.bool.isTablet)) {
            mTwoPane = true;
        }
    }

    public void swap(ArrayList<WeatherData> mValue) {

        if (mValues != null) {

            mValues.clear();
            mValues.addAll(mValue);

        } else {

            mValues = mValue;

        }

        notifyDataSetChanged();
        mrecyclerView.setAdapter(this);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        holder.iconId.setTypeface(weatherFont);
        holder.cityTitle.setText(holder.mItem.getName());
        holder.cityDesc.setText("температура: " + holder.mItem.getMain().getTemp().toString() + " ℃");

        Date date = new Date(holder.mItem.getDt()*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        // sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));
        String formattedDate = sdf.format(date);
        holder.timeUpdate.setText(formattedDate.toString());


        //"обновлялось: " + formattedDate.toString());


        switch (holder.mItem.getWeather().get(0).getIcon()){
            case "01d":
                holder.iconId.setText(R.string.wi_day_sunny);
                break;
            case "02d":
                holder.iconId.setText(R.string.wi_cloudy_gusts);
                break;
            case "03d":
                holder.iconId.setText(R.string.wi_cloud_down);
                break;
            case "10d":
                holder.iconId.setText(R.string.wi_day_rain_mix);
                break;
            case "11d":
                holder.iconId.setText(R.string.wi_day_thunderstorm);
                break;
            case "13d":
                holder.iconId.setText(R.string.wi_day_snow);
                break;
            case "01n":
                holder.iconId.setText(R.string.wi_night_clear);
                break;
            case "04d":
                holder.iconId.setText(R.string.wi_cloudy);
                break;
            case "04n":
                holder.iconId.setText(R.string.wi_night_cloudy);
                break;
            case "02n":
                holder.iconId.setText(R.string.wi_night_cloudy);
                break;
            case "03n":
                holder.iconId.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "10n":
                holder.iconId.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "11n":
                holder.iconId.setText(R.string.wi_night_rain);
                break;
            case "13n":
                holder.iconId.setText(R.string.wi_night_snow);
                break;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {

                    Bundle arguments = new Bundle();
                    arguments.putString(CityDetailFragment.ARG_ITEM_ID, holder.mItem.getName());
                    CityDetailFragment fragment = new CityDetailFragment();
                    fragment.setArguments(arguments);

                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    ft.replace(R.id.city_detail_container, fragment)
                            .commit();
                } else {

                    Context context = v.getContext();
                    Intent intent = new Intent(context, CityDetailActivity.class);
                    intent.putExtra(CityDetailFragment.ARG_ITEM_ID, holder.mItem.getName());

                    context.startActivity(intent);
                }
            }
        });
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mDb.deleteCity(mValues.get(position).getName());
        mValues.remove(position);
        notifyItemRemoved(position);

    }
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mValues, fromPosition, toPosition);
        mListChangedListener.onNoteListChanged(mValues);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        CardView cv;
        TextView cityTitle;
        TextView cityDesc;
        TextView timeUpdate;
        TextView iconId;
        public final View mView;
        public WeatherData mItem;
        public final ImageView handleView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            weatherFont = Typeface.createFromAsset(mCtx.getAssets(), "fonts/weather.ttf");
            cv = (CardView) itemView.findViewById(R.id.city_card_view);

            timeUpdate = (TextView) itemView.findViewById(R.id.upd_time);
            cityTitle = (TextView) itemView.findViewById(R.id.city_title);
            cityDesc = (TextView) itemView.findViewById(R.id.city_desc);
            iconId = (TextView) itemView.findViewById(R.id.city_icon);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            iconId.setTypeface(weatherFont);
        }
        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }

    }
}
