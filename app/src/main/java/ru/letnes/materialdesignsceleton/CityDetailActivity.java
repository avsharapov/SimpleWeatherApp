package ru.letnes.materialdesignsceleton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ru.letnes.materialdesignsceleton.adapters.DbAdapter;


public class CityDetailActivity extends AppCompatActivity {

    public Toolbar mToolbar;
    public CoordinatorLayout mCoordinatorLayout;
    public FloatingActionButton mFab;

    private BroadcastReceiver _myReceiver = new MyReceiverDetail();
    private DbAdapter mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);
        this.mDb = new DbAdapter(getApplicationContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction("UPDATE");
        this.registerReceiver(_myReceiver, filter);
        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordLayoutDetail);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(view -> {
            if(isOnline(getApplicationContext())) {
                mDb.updateCity(getIntent().getStringExtra(CityDetailFragment.ARG_ITEM_ID),false);
            }else{
                Snackbar.make(mCoordinatorLayout, getString(R.string.no_internet_message2), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(CityDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(CityDetailFragment.ARG_ITEM_ID));
            CityDetailFragment fragment = new CityDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.city_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this._myReceiver);
    }

    private class MyReceiverDetail extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle arguments = new Bundle();
            arguments.putString(CityDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(CityDetailFragment.ARG_ITEM_ID));
            CityDetailFragment fragment = new CityDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.city_detail_container, fragment)
                    .commit();
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, CityListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
