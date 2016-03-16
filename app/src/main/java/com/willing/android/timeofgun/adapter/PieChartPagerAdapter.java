package com.willing.android.timeofgun.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.willing.android.timeofgun.event.UnitChangeEvent;
import com.willing.android.timeofgun.model.DateUnit;
import com.willing.android.timeofgun.utils.DateUtils;
import com.willing.android.timeofgun.utils.DbHelper;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Willing on 2016/3/16.
 */
public class PieChartPagerAdapter extends PagerAdapter
{
    private Context mContext;

    private PieChart mPieChartCache;
    private DateUnit mUnit;

    private HashMap<Integer, PieChart> mPieCharMap;

    public PieChartPagerAdapter(Context context)
    {
        mContext = context;
        mUnit = DateUnit.DAY;
        mPieCharMap = new HashMap<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position - Integer.MAX_VALUE + 1; // 因为从0开始


        View view = null;

        if (mPieChartCache != null)
        {
            view = mPieChartCache;
            mPieChartCache = null;
        }
        else {
            PieChart pieChart = new PieChart(mContext);

            pieChart.setRotationEnabled(false);
            pieChart.setDescription("");

            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

                }

                @Override
                public void onNothingSelected() {

                }
            });

            view = pieChart;
        }

        mPieCharMap.put(position, (PieChart) view);
        new LoadPieChartDataTask(position).execute(index);


        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if (mPieChartCache == null) {
            mPieChartCache = (PieChart) object;
        }
        mPieCharMap.remove(position);

        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Subscribe
    public void onUnitChange(UnitChangeEvent event)
    {
        mUnit = event.getUnit();
    }

    private PieData getPieDataset(int index)
    {
       Calendar cal = Calendar.getInstance();
        long startTime = 0;
        long stopTime = 0;

        switch (mUnit)
        {
            case DAY:
                cal.add(Calendar.DAY_OF_MONTH, index);
                startTime = DateUtils.getDayBegin(cal.getTimeInMillis());
                stopTime = DateUtils.getDayEnd(cal.getTimeInMillis());
                break;
            case WEEK:
                cal.add(Calendar.DAY_OF_MONTH, index * 7);
                startTime = DateUtils.getWeekBegin(cal.getTimeInMillis());
                stopTime = DateUtils.getWeekEnd(cal.getTimeInMillis());
                break;
            case MONTH:
                cal.add(Calendar.MONTH, index);
                startTime = DateUtils.getMonthBegin(cal.getTimeInMillis());
                stopTime = DateUtils.getMonthEnd(cal.getTimeInMillis());
                break;
            case YEAR:
                cal.add(Calendar.YEAR, index);
                startTime = DateUtils.getYearBegin(cal.getTimeInMillis());
                stopTime = DateUtils.getYearEnd(cal.getTimeInMillis());
                break;
        }


        Cursor cursor = DbHelper.queryEvent(mContext,startTime,stopTime);

        if (cursor == null || cursor.getCount() == 0)
        {
            return null;
        }

        ArrayList<Integer> colors = new ArrayList<>();

        LinkedHashMap<String, Entry> catelogNameAndTime = new LinkedHashMap<>();

        String catelogName;
        float catelogTime;
        int catelogColor;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            catelogName = cursor.getString(cursor.getColumnIndex(DbHelper.CATELOG_NAME));
            startTime = cursor.getLong(cursor.getColumnIndex(DbHelper.START_TIME));
            stopTime = cursor.getLong(cursor.getColumnIndex(DbHelper.STOP_TIME));
            catelogTime = DateUtils.convertToHour(stopTime - startTime);
            Log.i("test", "catelogTime: " + catelogTime);
            catelogColor = cursor.getInt(cursor.getColumnIndex(DbHelper.CATELOG_COLOR));

            Entry entry;
            if (catelogNameAndTime.containsKey(catelogName))
            {
                entry = catelogNameAndTime.get(catelogName);
                entry = new Entry(entry.getVal() + catelogTime, entry.getXIndex());
                catelogNameAndTime.put(catelogName, entry);
            }
            else
            {
                entry = new Entry(catelogTime, catelogNameAndTime.size());
                catelogNameAndTime.put(catelogName, entry);

                colors.add(catelogColor);
            }
        }

        ArrayList<String> catelogs = new ArrayList<>();
        ArrayList<Entry> times = new ArrayList<>();
        Map.Entry<String, Entry> mapEntry;
        for (Iterator<Map.Entry<String, Entry>> ite = catelogNameAndTime.entrySet().iterator(); ite.hasNext(); )
        {
            mapEntry = ite.next();
            catelogs.add(mapEntry.getKey());
            times.add(mapEntry.getValue());
        }

        PieDataSet dataSet = new PieDataSet(times, "类别");
        dataSet.setColors(colors);
        PieData data = new PieData(catelogs, dataSet);

        return data;
    }

    private class LoadPieChartDataTask extends AsyncTask<Integer, Void, PieData>
    {
        private final int mPos;

        public LoadPieChartDataTask(int position)
        {
            mPos = position;
        }

        @Override
        protected PieData doInBackground(Integer... params) {
            return getPieDataset(params[0]);
        }

        @Override
        protected void onPostExecute(PieData pieData) {
            PieChart chart = mPieCharMap.get(mPos);
            if (chart != null && (View)chart.getParent() != null)
            {
                chart.setData(pieData);
                chart.requestLayout();
            }
        }
    }
}
