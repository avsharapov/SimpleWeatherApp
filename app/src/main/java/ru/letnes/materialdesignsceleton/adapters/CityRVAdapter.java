package ru.letnes.materialdesignsceleton.adapters;


import android.annotation.SuppressLint;
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
    private Typeface mWeatherFont;
    private Context mCtx;
    private RecyclerView mRecyclerView;
    private boolean mTwoPane;
    private final OnStartDragListener mDragStartListener;
    private DbAdapter mDb;
    private OnRecyclerListChangedListener mListChangedListener;


    public CityRVAdapter(FragmentManager fm, Context Ctx, RecyclerView recyclerView, ArrayList<WeatherData> items, OnStartDragListener dragStartListener, OnRecyclerListChangedListener listChangedListener) {
        mDragStartListener = dragStartListener;
        mFragmentManager = fm;
        mCtx = Ctx;
        mWeatherFont = Typeface.createFromAsset(mCtx.getAssets(), "fonts/weather.ttf");
        this.mDb = new DbAdapter(mCtx);
        mValues = items;
        mListChangedListener = listChangedListener;
        mRecyclerView = recyclerView;
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
        mRecyclerView.setAdapter(this);
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
        holder.mIconId.setTypeface(mWeatherFont);
        holder.mCityTitle.setText(holder.mItem.getName());
        holder.mCityDesc.setText(String.format(mCtx.getResources().getString(R.string.city_desk),holder.mItem.getMain().getTemp().toString()));
        Date date = new Date(holder.mItem.getDt()*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String formattedDate = sdf.format(date);
        holder.mTimeUpdate.setText(formattedDate);
        switch (holder.mItem.getWeather().get(0).getIcon()){
            case "01d":
                holder.mIconId.setText(R.string.wi_day_sunny);
                break;
            case "02d":
                holder.mIconId.setText(R.string.wi_cloudy_gusts);
                break;
            case "03d":
                holder.mIconId.setText(R.string.wi_cloud_down);
                break;
            case "10d":
                holder.mIconId.setText(R.string.wi_day_rain_mix);
                break;
            case "11d":
                holder.mIconId.setText(R.string.wi_day_thunderstorm);
                break;
            case "13d":
                holder.mIconId.setText(R.string.wi_day_snow);
                break;
            case "01n":
                holder.mIconId.setText(R.string.wi_night_clear);
                break;
            case "04d":
                holder.mIconId.setText(R.string.wi_cloudy);
                break;
            case "04n":
                holder.mIconId.setText(R.string.wi_night_cloudy);
                break;
            case "02n":
                holder.mIconId.setText(R.string.wi_night_cloudy);
                break;
            case "03n":
                holder.mIconId.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "10n":
                holder.mIconId.setText(R.string.wi_night_cloudy_gusts);
                break;
            case "11n":
                holder.mIconId.setText(R.string.wi_night_rain);
                break;
            case "13n":
                holder.mIconId.setText(R.string.wi_night_snow);
                break;
        }
        holder.mView.setOnClickListener(v -> {
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
        });
        holder.mHandleView.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder);
            }
            return false;
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

        public CardView mCardView;
        public TextView mCityTitle;
        public TextView mCityDesc;
        public TextView mTimeUpdate;
        public TextView mIconId;
        public final View mView;
        public WeatherData mItem;
        public final ImageView mHandleView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mCardView = (CardView) itemView.findViewById(R.id.city_card_view);
            mTimeUpdate = (TextView) itemView.findViewById(R.id.upd_time);
            mCityTitle = (TextView) itemView.findViewById(R.id.city_title);
            mCityDesc = (TextView) itemView.findViewById(R.id.city_desc);
            mIconId = (TextView) itemView.findViewById(R.id.city_icon);
            mHandleView = (ImageView) itemView.findViewById(R.id.handle);
            mIconId.setTypeface(mWeatherFont);
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
