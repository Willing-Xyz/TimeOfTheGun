package com.willing.android.timeofgun.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.adapter.PieChartPagerAdapter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Willing on 2016/3/13.
 */
public class StatisticFragment extends  BaseFragment{

    private View mRootView;

    private ViewPager mViewPager;
    private View mDayView;
    private View mWeekView;
    private View mMonthView;
    private View mYearView;
    private PieChartPagerAdapter mPageAdapter;


    public static StatisticFragment getInstance()
    {
        StatisticFragment fragment = new StatisticFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;
        if (mRootView != null)
        {
            view = mRootView;
        }
        else
        {
            view = inflater.inflate(R.layout.fragment_statistic, container, false);
            mRootView = view;
        }

        initView();
        setupListener();

        return view;
    }

    private void initView() {
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        mDayView = mRootView.findViewById(R.id.bt_day);
        mWeekView = mRootView.findViewById(R.id.bt_week);
        mMonthView = mRootView.findViewById(R.id.bt_month);
        mYearView = mRootView.findViewById(R.id.bt_year);
    }

    private void setupListener() {


        mPageAdapter = new PieChartPagerAdapter(getActivity());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setCurrentItem(Integer.MAX_VALUE);


    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(mPageAdapter);
        // TODO: 2016/3/16 设置子标题
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(mPageAdapter);
        // TODO: 2016/3/16 取消子标题
    }
}
