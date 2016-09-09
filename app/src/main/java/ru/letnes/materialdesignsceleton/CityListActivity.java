package ru.letnes.materialdesignsceleton;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    public SwipeRefreshLayout mSwipeRefreshLayout;
    public CoordinatorLayout mCoordinatorLayout;
    public Toolbar mToolbar;
    public FloatingActionButton mFab;
    public View mRecyclerView;
    public ProgressBar mProgressBar;

    private DbAdapter mDb;
    private CityRVAdapter mAdapter;
    private BroadcastReceiver _myReceiver = new MyReceiver();
    private ItemTouchHelper mItemTouchHelper;
    private SharedPreferences mSharedPreferences;
    private DelayAutoCompleteTextView mInput;
    protected SharedPreferences.Editor mEditor;

    public static final String LIST_OF_SORTED_DATA = "json_list_sorted_data";
    public final static String PREFERENCE_FILE = "weather_sort_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);
        IntentFilter filter = new IntentFilter();
        filter.addAction("SWAP");
        filter.addAction("INUSE");
        filter.addAction("EXIST");
        this.registerReceiver(_myReceiver, filter);
        this.mDb = new DbAdapter(getApplicationContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(isOnline(getApplicationContext())) {
                mDb.updateCitys();
            }else{
                Snackbar.make(mCoordinatorLayout, getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(view -> {
            if(isOnline(getApplicationContext())) {
                showInputDialog(view);
            }else{
                Snackbar.make(mCoordinatorLayout, getString(R.string.no_internet_message1), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mRecyclerView = findViewById(R.id.city_list);
        assert mRecyclerView != null;
        mSharedPreferences = this.getApplicationContext()
                .getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
        setupRecyclerView((RecyclerView) mRecyclerView);
        //updateWeather();
    }

    private void updateWeather() {
        mSwipeRefreshLayout.setRefreshing(true);


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
            switch (intent.getAction()){
                case "SWAP":
                    mAdapter.swap(getSortData());
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case "EXIST":
                    Snackbar.make(mCoordinatorLayout, getString(R.string.test_city_already_exist), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case "INUSE":
                    Snackbar.make(mCoordinatorLayout, getString(R.string.city_already_exist), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }

        }
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showInputDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.add_city));
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View formView = layoutInflater.inflate(R.layout.form, null, false);
        WeatherAutoCompleteAdapter wacAdapter = new WeatherAutoCompleteAdapter(formView.getContext());
        mInput = (DelayAutoCompleteTextView) formView.findViewById(R.id.weather_title);
        mInput.setThreshold(3);
        mInput.setAdapter(wacAdapter);
        mProgressBar = (android.widget.ProgressBar) formView.findViewById(R.id.loading_indicator);
        mProgressBar.getIndeterminateDrawable().setColorFilter(0xFF0099CC, android.graphics.PorterDuff.Mode.MULTIPLY);
        mInput.setLoadingIndicator(mProgressBar);
        mInput.setOnItemClickListener((adapterView, view1, position, id) -> {
            String selectItem = (String) adapterView.getItemAtPosition(position);
            String city = selectItem.split(",")[0];
            mInput.setText(selectItem);

        });
        builder.setView(formView);
        builder.setPositiveButton(getString(R.string.add_city), (dialog, which) -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mDb.setCity(mInput.getText().toString());
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
            Snackbar.make(mCoordinatorLayout, getString(R.string.test_data_added), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        if (item.getItemId() == R.id.del_city) {
            mSwipeRefreshLayout.setRefreshing(true);
            mDb.deleteCitys();
            Snackbar.make(mCoordinatorLayout, getString(R.string.database_clear), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        return false;
    }
    private void setupRecyclerView(@NonNull RecyclerView vrecyclerView) {
        mAdapter = new CityRVAdapter(getSupportFragmentManager(), getApplicationContext(), vrecyclerView, getSortData(), this, this);
        vrecyclerView.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(vrecyclerView);
    }

    @Override
    public void onNoteListChanged(List<WeatherData> weathersData) {
        List<String> listOfSortedCityName = new ArrayList<String>();
        for (WeatherData weatherData: weathersData){
            listOfSortedCityName.add(weatherData.getName());
        }
        Gson gson = new Gson();
        String jsonListOfSortedCityName = gson.toJson(listOfSortedCityName);
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
