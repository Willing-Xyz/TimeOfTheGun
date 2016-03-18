package com.willing.android.timeofgun.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.event.UnitChangeEvent;
import com.willing.android.timeofgun.model.DateUnit;
import com.willing.android.timeofgun.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

/**
 * Created by Willing on 2016/3/13.
 */
public abstract class StatisticFragment extends  BaseFragment implements View.OnClickListener {


    protected View mRootView;

    protected ViewPager mViewPager;
    protected View mDayView;
    protected View mWeekView;
    protected View mMonthView;
    protected View mYearView;

    protected PagerAdapter mPagerAdapter;

    protected DateUnit mUnit = DateUnit.DAY;

    protected int mCurPage = Integer.MAX_VALUE - 1;





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

        setHasOptionsMenu(true);

        return view;
    }

    private void initView() {
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        mDayView = mRootView.findViewById(R.id.bt_day);
        mWeekView = mRootView.findViewById(R.id.bt_week);
        mMonthView = mRootView.findViewById(R.id.bt_month);
        mYearView = mRootView.findViewById(R.id.bt_year);

        setDaysTextView(DateUnit.DAY);
    }

    private void setupListener() {


        mPagerAdapter = createPagerAdapter();
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(Integer.MAX_VALUE,false);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                mCurPage = position;

                int index = position - Integer.MAX_VALUE + 1;
                Calendar cal = Calendar.getInstance();
                cal = getAdjustDate(cal, index);

                String date = parseDate(cal);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(date);
            }
        });

        mDayView.setOnClickListener(this);
        mWeekView.setOnClickListener(this);
        mMonthView.setOnClickListener(this);
        mYearView.setOnClickListener(this);
    }

    protected  abstract PagerAdapter createPagerAdapter();




    private Calendar getAdjustDate(Calendar cal, int index) {
        switch (mUnit) {
            case DAY:
                cal.add(Calendar.DAY_OF_MONTH, index);
                break;
            case WEEK:
                cal.add(Calendar.DAY_OF_MONTH, index * 7);
                break;
            case MONTH:
                cal.add(Calendar.MONTH, index);
                break;
            case YEAR:
                cal.add(Calendar.YEAR, index);
                break;
        }
        return cal;
    }

    private String parseDate(Calendar cal) {
        StringBuilder builder = new StringBuilder();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        switch (mUnit)
        {
            case DAY:
                builder.append(DateUtils.formatDate(cal.getTimeInMillis()));
                break;
            case WEEK:
                builder.append(cal.get(Calendar.WEEK_OF_YEAR)).append("周");
                builder.append("(");
                builder.append(DateUtils.formatDate(DateUtils.getWeekBegin(cal.getTimeInMillis())));
                builder.append("至");
                builder.append(DateUtils.formatDate(DateUtils.getWeekEnd(cal.getTimeInMillis())));
                builder.append(")");
                break;
            case MONTH:
                builder.append(DateUtils.formatDateMonth(cal.getTimeInMillis()));
                break;
            case YEAR:
                builder.append(cal.get(Calendar.YEAR));
                break;
        }

        return builder.toString();
    }

    protected void setDaysTextView(DateUnit day)
    {
        mDayView.setSelected(false);
        mWeekView.setSelected(false);
        mMonthView.setSelected(false);
        mYearView.setSelected(false);
        mUnit = day;
        switch (day)
        {
            case DAY:
                mDayView.setSelected(true);
                break;
            case WEEK:
                mWeekView.setSelected(true);
                break;
            case MONTH:
                mMonthView.setSelected(true);
                break;
            case YEAR:
                mYearView.setSelected(true);
                break;
        }
        EventBus.getDefault().post(new UnitChangeEvent(day));

        int index = mCurPage - Integer.MAX_VALUE + 1;
        Calendar cal = Calendar.getInstance();
        cal = getAdjustDate(cal, index);

        String date = parseDate(cal);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(date);
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(mPagerAdapter);
        // TODO: 2016/3/16 设置子标题
        String date = parseDate(Calendar.getInstance());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(date);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(mPagerAdapter);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(null);
    }

    @Override
    public void onClick(View v) {
        if (v == mDayView)
        {
            setDaysTextView(DateUnit.DAY);
        }
        else if (v == mWeekView)
        {
            setDaysTextView(DateUnit.WEEK);
        }
        else if (v == mMonthView)
        {
            setDaysTextView(DateUnit.MONTH);
        }
        else
        {
            setDaysTextView(DateUnit.YEAR);
        }
    }
}
