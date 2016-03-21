package com.willing.android.timeofgun.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.fragment.SettingsFragment;
import com.willing.android.timeofgun.fragment.StatisticCatelogFragment;
import com.willing.android.timeofgun.fragment.StatisticTimeFragment;
import com.willing.android.timeofgun.fragment.TimelineFragment;
import com.willing.android.timeofgun.fragment.TimingFragment;
import com.willing.android.timeofgun.model.User;
import com.willing.android.timeofgun.view.CircleImageView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String TIMING_FRAGMENT = "timing_fragment";
    private static final String SETTINGS_FRAGMENT = "settings_fragment";
    private static final String TIMELINE_FRAGMENT = "timeline_fragment";
    private static final String STATISTIC_FRAGMENT = "statistic_fragment";
    private static final String STATISTIC_CATELOG_FRAGMENT = "statistic_catelog_framgnet";
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolBar;
    private NavigationView mNavigation;
    private DrawerLayout mDrawer;


    Map<String, SoftReference<Fragment>> mCacheFragmentMap;
    private TextView mUserName;
    private CircleImageView mUserPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mCacheFragmentMap == null) {
            mCacheFragmentMap = new HashMap<>();
        }

        initView();
        setupListener();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.fragment) == null)
        {
            Fragment timingFragment = getFragmentFromCache(TIMING_FRAGMENT);
            if (timingFragment == null)
            {
                timingFragment = newFragment(TIMING_FRAGMENT);
            }

            fragmentManager.beginTransaction().add(R.id.fragment, timingFragment, TIMING_FRAGMENT).commit();
        }

        // 检查更新
        BmobUpdateAgent.silentUpdate(this);
    }


    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mNavigation = (NavigationView) findViewById(R.id.navigation);
        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
//        mUserPic = (CircleImageView) mNavigation.getHeaderView(0).findViewById(R.id.userPic);
//        mUserName = (TextView) mNavigation.getHeaderView(0).findViewById(R.id.userName);
        View view = mNavigation.inflateHeaderView(R.layout.navigation_header);
        mUserPic = (CircleImageView) view.findViewById(R.id.userPic);
        mUserName = (TextView) view.findViewById(R.id.userName);

        setSupportActionBar(mToolBar);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolBar, 0, 0);
    }

    private void setupListener() {

        mDrawer.setDrawerListener(mDrawerToggle);
//        mDrawer.addDrawerListener(mDrawerToggle);
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                int id = item.getItemId();

                String tag = getTagByMenuId(id);

                changeFragment(tag);

                item.setChecked(true);
                mDrawer.closeDrawer(GravityCompat.START);
                mToolBar.setTitle(item.getTitle());

                return true;
            }
        });

        mUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 判断用户是否已登录
                User user = User.getCurrentUser(MainActivity.this, User.class);
                if (user != null)
                {
                    // 已登录
                    mDrawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                    startActivity(intent);
                }
                else
                {
                    // 未登录
                    mDrawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, LoginOrSignupActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        BmobUser user = BmobUser.getCurrentUser(this);
        if (user != null) {
            mUserName.setText(user.getUsername());
            // TODO: 2016/3/19 下载并显示头像
        }
        else
        {
            mUserName.setText(R.string.no_login);
        }
    }

    private String getTagByMenuId(int id) {
        String tag = null;
        switch (id)
        {
            case R.id.drawer_home:
                tag = TIMING_FRAGMENT;
                break;
            case R.id.drawer_statistic:
                tag = STATISTIC_FRAGMENT;
                break;
            case R.id.drawer_timeline:
                tag = TIMELINE_FRAGMENT;
                break;
            case R.id.drawer_settings:
                tag = SETTINGS_FRAGMENT;
                break;
            case R.id.drawer_statistic_catelog:
                tag = STATISTIC_CATELOG_FRAGMENT;
                break;
        }
        return tag;
    }

    /**
     * 切换Fragment
     * 1. 如果存在Fragment，则detach它。
     * 2. 如果缓存中有Fragment，则attach它
     * 3. 如果存中没有，则创建，并add它
     */
    private void changeFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment nowFragment = fragmentManager.findFragmentById(R.id.fragment);
        if (nowFragment != null)
        {
            fragmentTransaction.detach(nowFragment);
        }
        Fragment newFragment = getFragmentFromCache(tag);
        if (newFragment != null)
        {
            fragmentTransaction.attach(newFragment);
        }
        else
        {
            newFragment = newFragment(tag);
            fragmentTransaction.add(R.id.fragment, newFragment, tag);
        }
        fragmentTransaction.commit();
    }

    /**
     * 新建Fragment，并添加到缓存
     * @param tag
     * @return
     */
    private Fragment newFragment(String tag)
    {
        Fragment fragment = null;
        switch (tag)
        {
            case TIMING_FRAGMENT:
                fragment = TimingFragment.getInstance();
                break;
            case TIMELINE_FRAGMENT:
                fragment = TimelineFragment.getInstance();
                break;
            case STATISTIC_FRAGMENT:
                fragment = StatisticTimeFragment.getInstance();
                break;
            case SETTINGS_FRAGMENT:
                fragment = SettingsFragment.getInstance();
                break;
            case STATISTIC_CATELOG_FRAGMENT:
                fragment = StatisticCatelogFragment.getInstance();
                break;
        }
        addToFragmentCache(fragment, tag);

        return fragment;
    }

    /**
     * 从缓存中获取Fragment
     * @param tag
     * @return
     */
    private Fragment getFragmentFromCache(String tag) {
        Fragment fragment = null;
        SoftReference<Fragment> softReference = mCacheFragmentMap.get(tag);
        if (softReference != null)
        {
            fragment = softReference.get();
        }
        return fragment;
    }

    /**
     * 增加Fragment到缓存中
     * @param timingFragment
     * @param tag
     */
    private void addToFragmentCache(Fragment timingFragment, String tag) {
        Fragment cacheFragment = getFragmentFromCache(tag);
        if (cacheFragment == null) {
            mCacheFragmentMap.put(tag, new SoftReference<Fragment>(timingFragment));
        }
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
