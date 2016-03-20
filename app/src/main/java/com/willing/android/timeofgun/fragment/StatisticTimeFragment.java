package com.willing.android.timeofgun.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.adapter.PieChartPagerAdapter;

/**
 * Created by Willing on 2016/3/18.
 */
public class StatisticTimeFragment extends StatisticFragment{

    public static Fragment getInstance()
    {
        return new StatisticTimeFragment();
    }

    @Override
    protected PagerAdapter createPagerAdapter() {

        return new PieChartPagerAdapter(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.statistic);
    }
}
