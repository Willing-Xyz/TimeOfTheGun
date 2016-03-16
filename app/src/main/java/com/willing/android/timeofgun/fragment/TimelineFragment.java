package com.willing.android.timeofgun.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.adapter.TimeLinePagerAdapter;
import com.willing.android.timeofgun.utils.DateUtils;

import java.util.Calendar;

/**
 * Created by Willing on 2016/3/13.
 */
public class TimelineFragment extends BaseFragment{
    private View mRootView;
    private ViewPager mViewPager;
    private TimeLinePagerAdapter mPageAdapter;

    public static TimelineFragment getInstance()
    {
        TimelineFragment fragment = new TimelineFragment();
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
            view = inflater.inflate(R.layout.fragment_timeline, container, false);
            mRootView = view;
        }

        initView();
        setupListener();

        return view;
    }

    private void initView() {


        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);


    }

    private void setupListener() {

        mPageAdapter = new TimeLinePagerAdapter(getActivity());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setCurrentItem(Integer.MAX_VALUE);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int index = position - Integer.MAX_VALUE + 1;
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, index);

                String dateAndWeek = DateUtils.formatDateAndWeek(cal.getTimeInMillis());
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(dateAndWeek);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        int index = mViewPager.getCurrentItem() - Integer.MAX_VALUE + 1;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, index);

        String dateAndWeek = DateUtils.formatDateAndWeek(cal.getTimeInMillis());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(dateAndWeek);

    }

    @Override
    public void onStop() {
        super.onStop();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);

    }



}
