package com.willing.android.timeofgun.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.activity.CatelogPickerActivity;
import com.willing.android.timeofgun.adapter.BarChartPagerAdapter;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.DateUnit;
import com.willing.android.timeofgun.utils.DbHelper;

/**
 * Created by Willing on 2016/3/18.
 */
public class StatisticCatelogFragment extends StatisticFragment{


    private static final int PICK_CATELOG_REQUEST_CODE = 1;

    private Catelog mCatelog;

    public static Fragment getInstance()
    {
        return new StatisticCatelogFragment();
    }

    @Override
    protected PagerAdapter createPagerAdapter() {
        return new BarChartPagerAdapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        mDayView.setVisibility(View.GONE);
        mWeekView.setBackgroundResource(R.drawable.middle_days_left);

        if (mCatelog == null) {
            mCatelog = DbHelper.findAnyCatelogFromDb(getActivity());
        }
        if (mCatelog != null) {
            ((BarChartPagerAdapter) mPagerAdapter).setCatelog(mCatelog);
        }
        setDaysTextView(DateUnit.WEEK);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.pick_catelog)
        {
            Intent intent = new Intent(getActivity(), CatelogPickerActivity.class);
            startActivityForResult(intent, PICK_CATELOG_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_CATELOG_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            mCatelog = data.getParcelableExtra(CatelogPickerActivity.EXTRA_CATELOG);
            ((BarChartPagerAdapter)mPagerAdapter).setCatelog(mCatelog);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.stastistic_catelog);
    }
}
