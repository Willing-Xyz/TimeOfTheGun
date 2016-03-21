package com.willing.android.timeofgun.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.willing.android.timeofgun.event.UnitChangeEvent;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.DateUnit;
import com.willing.android.timeofgun.utils.DateUtils;
import com.willing.android.timeofgun.utils.DbHelper;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Willing on 2016/3/18.
 */
public class BarChartPagerAdapter extends PagerAdapter
{

    private Context mContext;

    private BarChart mBarChartCache;
    private DateUnit mUnit;

    private Catelog mCatelog = new Catelog("金的", 0xffff0000, 1458111686207L);

    private HashMap<Integer, BarChart> mBarChartMap;
    private static final String[] mWeeks = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private static final String[] mMonths = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    private static final String[] mYears = {"1月", "2月", "3月","4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};

    public BarChartPagerAdapter(Context context)
    {
        mContext = context;
        mUnit = DateUnit.WEEK;
        mBarChartMap = new HashMap<>();
    }

    public void setCatelog(Catelog catelog)
    {
        mCatelog = catelog;
        invalidateViewPager();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position - Integer.MAX_VALUE + 1; // 因为从0开始


        View view = null;

        if (mBarChartCache != null)
        {
            view = mBarChartCache;
            mBarChartCache = null;
        }
        else {
            BarChart barChart = new BarChart(mContext);
            barChart.setDescription("");
            barChart.getAxisRight().setEnabled(false);
            barChart.getAxisLeft().setAxisMinValue(0f);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setSpaceBetweenLabels(2);

            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

                }

                @Override
                public void onNothingSelected() {

                }
            });

            view = barChart;
        }

        mBarChartMap.put(position, (BarChart) view);
        new LoadBarChartDataTask(position).execute(index);


        container.addView(view);

        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if (mBarChartCache == null) {
            mBarChartCache = (BarChart) object;
        }
        mBarChartMap.remove(position);

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
        invalidateViewPager();
    }

    private void invalidateViewPager()
    {
        Set<Map.Entry<Integer, BarChart>> set = mBarChartMap.entrySet();
        Map.Entry<Integer, BarChart> entry = null;
        for (Iterator<Map.Entry<Integer, BarChart>> ite = set.iterator(); ite.hasNext(); )
        {
            entry = ite.next();
            new LoadBarChartDataTask(entry.getKey()).execute(entry.getKey() - Integer.MAX_VALUE + 1);
        }
    }

    private BarData getBarDataset(int index)
    {
        Calendar cal = Calendar.getInstance();
        long startTime = 0;
        long stopTime = 0;
        long skipTime = 0; // 间隔时间

        ArrayList<BarEntry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        String[] xValStr = null;
        int length = 0;


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
                xValStr = mWeeks;
                skipTime = 24 * 60 * 60 * 1000 - 1;
                break;
            case MONTH:
                cal.add(Calendar.MONTH, index);
                startTime = DateUtils.getMonthBegin(cal.getTimeInMillis());
                stopTime = DateUtils.getMonthEnd(cal.getTimeInMillis());
                xValStr = mMonths;
                skipTime = 24 * 60 * 60 * 1000 - 1;
                break;
            case YEAR:
                cal.add(Calendar.YEAR, index);
                startTime = DateUtils.getYearBegin(cal.getTimeInMillis());
                stopTime = DateUtils.getYearEnd(cal.getTimeInMillis());
                xValStr = mYears;
                break;
        }

        for (int i = 0; i < xValStr.length; ++i)
        {
            xVals.add(xValStr[i]);
        }

        Cursor cursor = DbHelper.queryEventByCatelog(mContext, startTime, stopTime,mCatelog.getCatelogId());

        if (cursor == null || cursor.getCount() == 0)
        {
            return null;
        }


        long curStartTime = startTime;
        long curStopTime;
        if (mUnit == DateUnit.YEAR) {
            curStopTime = DateUtils.getMonthEnd(curStartTime);
        }
        else
        {
            curStopTime = curStartTime + skipTime;
        }
        float curCatelogTime = 0;
        float sumCatelogTime = 0;
        int curIndex = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            startTime = cursor.getLong(cursor.getColumnIndex(DbHelper.START_TIME));
            stopTime = cursor.getLong(cursor.getColumnIndex(DbHelper.STOP_TIME));
            curCatelogTime = DateUtils.convertToHour(stopTime - startTime);

            while (startTime > curStopTime)
            {
                if (sumCatelogTime != 0)
                {
                    yVals.add(new BarEntry(sumCatelogTime, curIndex++));
                    sumCatelogTime = 0;
                    curStartTime = curStopTime + 1;
                    if (mUnit == DateUnit.YEAR) {
                        curStopTime = DateUtils.getMonthEnd(curStartTime);
                    }
                    else
                    {
                        curStopTime = curStartTime + skipTime;
                    }
                    continue;
                }

                yVals.add(new BarEntry(0, curIndex++));
                curStartTime = curStopTime + 1;
                if (mUnit == DateUnit.YEAR) {
                    curStopTime = DateUtils.getMonthEnd(curStartTime);
                }
                else
                {
                    curStopTime = curStartTime + skipTime;
                }

            }
            sumCatelogTime += curCatelogTime;
        }

        if (sumCatelogTime != 0)
        {
            yVals.add(new BarEntry(sumCatelogTime, curIndex++));
        }



        ArrayList<BarDataSet> sets = new ArrayList<>(1);
        BarDataSet set = new BarDataSet(yVals, mCatelog.getName());
        set.setColor(mCatelog.getColor());
        sets.add(set);

        BarData data = new BarData(xVals, set);
        data.setValueTextSize(10f);

        return data;
    }



    private class LoadBarChartDataTask extends AsyncTask<Integer, Void, BarData>
    {
        private final int mPos;

        public LoadBarChartDataTask(int position)
        {
            mPos = position;
        }

        @Override
        protected BarData doInBackground(Integer... params) {
            return getBarDataset(params[0]);
        }

        @Override
        protected void onPostExecute(BarData barData) {
            BarChart chart = mBarChartMap.get(mPos);
            if (chart != null && (View)chart.getParent() != null)
            {
                chart.clear();

                chart.clear();
                chart.setData(barData);
                chart.getParent().requestLayout();
            }
        }
    }
}
