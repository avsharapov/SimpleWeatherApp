package ru.letnes.materialdesignsceleton;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;


import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import android.widget.AdapterView;

import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import ru.letnes.materialdesignsceleton.adapters.CityRVAdapter;
import ru.letnes.materialdesignsceleton.adapters.DbAdapter;
import ru.letnes.materialdesignsceleton.adapters.WeatherAutoCompleteAdapter;
import ru.letnes.materialdesignsceleton.helper.OnRecyclerListChangedListener;
import ru.letnes.materialdesignsceleton.helper.OnStartDragListener;
import ru.letnes.materialdesignsceleton.helper.SimpleItemTouchHelperCallback;
import ru.letnes.materialdesignsceleton.model.WeatherData;


public class CityListActivity extends AppCompatActivity implements OnStartDragListener,OnRecyclerListChangedListener {

    private DbAdapter mDb;
    private CityRVAdapter mmAdapter;
    private RecyclerView recyclerView;
    private CoordinatorLayout coordLayout;
    private WeatherAutoCompleteAdapter wacAdapter;
    private DelayAutoCompleteTextView input;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private BroadcastReceiver _myReceiver = new MyReceiver();
    private ItemTouchHelper mItemTouchHelper;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public static final String LIST_OF_SORTED_DATA = "json_list_sorted_data";
    public final static String PREFERENCE_FILE = "weather_sort_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        IntentFilter filter = new IntentFilter();
        filter.addAction("SWAP");
        this.registerReceiver(_myReceiver, filter);
        this.mDb = new DbAdapter(getApplicationContext());

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isOnline(getApplicationContext())) {
                    mDb.updateCitys();
                }else{
                    Snackbar.make(coordLayout, "Нет подключения к интернету", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });



        coordLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline(getApplicationContext())) {
                    showInputDialog(view);
                }else{
                    Snackbar.make(coordLayout, "Для добавления городов подключитесь к интернету...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        View vrecyclerView = findViewById(R.id.city_list);

        assert vrecyclerView != null;
        mSharedPreferences = this.getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        setupRecyclerView((RecyclerView) vrecyclerView);

        if(isOnline(getApplicationContext())) {
            mSwipeRefreshLayout.setRefreshing(true);
            mDb.updateCitys();
        }else{
            Snackbar.make(coordLayout, "Нет подключения к интернету", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


    }




    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
            super.onRestoreInstanceState(savedInstanceState);
        }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
        }
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this._myReceiver);
    }
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            mmAdapter.swap(getSortData());
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    public void showInputDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Добавить город");

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View formView = layoutInflater.inflate(R.layout.form, null, false);
        input = (DelayAutoCompleteTextView) formView.findViewById(R.id.weather_title);

        wacAdapter = new WeatherAutoCompleteAdapter(formView.getContext());
        input.setThreshold(3);
        input.setAdapter(wacAdapter);
        ProgressBar progressBar2 = (android.widget.ProgressBar) formView.findViewById(R.id.loading_indicator);
        progressBar2.getIndeterminateDrawable().setColorFilter(0xFF0099CC, android.graphics.PorterDuff.Mode.MULTIPLY);
        input.setLoadingIndicator(progressBar2);
        input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectItem = (String) adapterView.getItemAtPosition(position);
                String city = selectItem.split(",")[0];
                input.setText(city);

            }
        });
        builder.setView(formView);
        builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSwipeRefreshLayout.setRefreshing(true);
                mDb.setCity(input.getText().toString());
            }
        });
        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weathermenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_city) {

            mSwipeRefreshLayout.setRefreshing(true);
            mDb.addTestData();

            Snackbar.make(coordLayout, "Тестовые данные добавлены", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        if (item.getItemId() == R.id.del_city) {
            mSwipeRefreshLayout.setRefreshing(true);
            mDb.deleteCitys();
            Snackbar.make(coordLayout, "База очищена!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        return false;
    }
    private void setupRecyclerView(@NonNull RecyclerView vrecyclerView) {

        mmAdapter = new CityRVAdapter(getSupportFragmentManager(), getApplicationContext(), vrecyclerView, getSortData(), this, this);

        recyclerView = vrecyclerView;

        recyclerView.setAdapter(mmAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mmAdapter);

        mItemTouchHelper = new ItemTouchHelper(callback);

        mItemTouchHelper.attachToRecyclerView(recyclerView);


    }


    @Override
    public void onNoteListChanged(List<WeatherData> weathersData) {

        List<String> listOfSortedCityName = new ArrayList<String>();

        for (WeatherData weatherData: weathersData){
            listOfSortedCityName.add(weatherData.getName());
        }

        Gson gson = new Gson();
        String jsonListOfSortedCityName = gson.toJson(listOfSortedCityName);


        //save to SharedPreference
        mEditor.putString(LIST_OF_SORTED_DATA, jsonListOfSortedCityName).commit();
        mEditor.commit();
    }


    private ArrayList<WeatherData> getSortData(){

        ArrayList<WeatherData> weathersData = mDb.getCitys();
        ArrayList<WeatherData> sortedCitys = new ArrayList<WeatherData>();
        String jsonListOfSortedCityName = mSharedPreferences.getString(LIST_OF_SORTED_DATA, "");
        if (!jsonListOfSortedCityName.isEmpty()){


            Gson gson = new Gson();
            List<String> listOfSortedCityName = gson.fromJson
                    (jsonListOfSortedCityName, new TypeToken<List<String>>(){}.getType());


            if (listOfSortedCityName != null && listOfSortedCityName.size() > 0){
                for (String name: listOfSortedCityName){
                    for (WeatherData weatherData: weathersData){
                        if (weatherData.getName().equals(name)){
                            sortedCitys.add(weatherData);
                            weathersData.remove(weatherData);
                            break;
                        }
                    }
                }
            }

            if (weathersData.size() > 0){
                sortedCitys.addAll(weathersData);
            }

            return sortedCitys;
        }else {
            return weathersData;
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

}
