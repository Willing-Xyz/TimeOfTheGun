package com.willing.android.timeofgun;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolBar;
    private NavigationView mNavigation;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setupListener();
    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mNavigation = (NavigationView) findViewById(R.id.navigation);
        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        setSupportActionBar(mToolBar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolBar, 0, 0);
    }

    private void setupListener() {

        mDrawer.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        mDrawerToggle.onOptionsItemSelected(item);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }
}
