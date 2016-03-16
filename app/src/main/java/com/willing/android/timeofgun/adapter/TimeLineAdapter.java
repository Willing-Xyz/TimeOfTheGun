package com.willing.android.timeofgun.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.EventAndCatelog;
import com.willing.android.timeofgun.utils.DateUtils;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.view.CircleView;
import com.willing.android.timeofgun.view.RectView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Willing on 2016/3/11.
 */
public class TimeLineAdapter extends BaseAdapter{

    private List<EventAndCatelog> mTimeEvents;
    private final Context mContext;
    private long mMaxLength = 1;
    private int mIndex;

    public TimeLineAdapter(Context context, int index)
    {
        mContext = context;
        mIndex = index;

        new LoadEventTask().execute();
    }

    @Override
    public int getCount() {
        if (mTimeEvents == null)
        {
            return 0;
        }
        return mTimeEvents.size();
    }

    @Override
    public Object getItem(int position) {
        return mTimeEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_timeline, parent, false);
            viewHolder.circleView = (CircleView) convertView.findViewById(R.id.circle);
            viewHolder.eventName = (TextView) convertView.findViewById(R.id.eventName);
            viewHolder.rectView = (RectView) convertView.findViewById(R.id.rect);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EventAndCatelog event = mTimeEvents.get(position);
        viewHolder.circleView.setColor(event.getCatelog().getColor());
        viewHolder.eventName.setText(event.getCatelog().getName());
        viewHolder.rectView.setColor(event.getCatelog().getColor());
        int length = (int) ((event.getStopTime() - event.getStartTime()) * 100 / mMaxLength);
        viewHolder.rectView.setLength(length);
        viewHolder.time.setText(DateUtils.createText(event.getStartTime(), event.getStopTime()));


        return convertView;
    }

    class ViewHolder
    {
        CircleView circleView;
        TextView eventName;
        RectView rectView;
        TextView time;
    }

    class LoadEventTask extends AsyncTask<Void, Void, List<EventAndCatelog>>
    {
        @Override
        protected List<EventAndCatelog> doInBackground(Void... params) {
            List<EventAndCatelog> list = loadEvent(mIndex);

            EventAndCatelog event = null;
            long curLength;
            for (Iterator<EventAndCatelog> ite = list.iterator(); ite.hasNext(); )
            {
                event = ite.next();
                if ((curLength = event.getStopTime() - event.getStartTime()) > mMaxLength)
                {
                    mMaxLength = curLength;
                }
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<EventAndCatelog> eventAndCatelogs) {

            mTimeEvents = eventAndCatelogs;
            notifyDataSetChanged();
        }
    }


    private List<EventAndCatelog> loadEvent(int index) {

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, index);

        long startDate = DateUtils.getDayBegin(today.getTimeInMillis());
        long stopDate = DateUtils.getDayEnd(today.getTimeInMillis());

        Cursor cursor = DbHelper.queryEvent(mContext, startDate, stopDate);

        List<EventAndCatelog> list = new ArrayList<>(cursor.getCount());
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            EventAndCatelog event = new EventAndCatelog();
            event.setStartTime(cursor.getLong(cursor.getColumnIndex(DbHelper.START_TIME)));
            event.setStopTime(cursor.getLong(cursor.getColumnIndex(DbHelper.STOP_TIME)));
            Catelog catelog = new Catelog();
            catelog.setColor(cursor.getInt(cursor.getColumnIndex(DbHelper.CATELOG_COLOR)));
            catelog.setName(cursor.getString(cursor.getColumnIndex(DbHelper.CATELOG_NAME)));
            event.setCatelog(catelog);
            list.add(event);
        }


        return list;
    }
}
