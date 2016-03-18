package com.willing.android.timeofgun.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.willing.android.timeofgun.activity.ModifyEventActivity;
import com.willing.android.timeofgun.event.UpdateEventEvent;
import com.willing.android.timeofgun.model.EventAndCatelog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Willing on 2016/3/11.
 */
public class TimeLinePagerAdapter extends PagerAdapter {


    private final Context mContext;
    private ListView mListViewCache;
    private HashMap<Integer, ListView> mListViewMap;

    public TimeLinePagerAdapter(Context context)
    {
        mContext = context;
        mListViewMap = new HashMap<>();
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int index = position - Integer.MAX_VALUE + 1; // 因为从0开始


        ListView view = null;


            if (mListViewCache != null)
            {
                view = mListViewCache;
                mListViewCache = null;
            }
            else {
                view = new ListView(mContext);
                view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position != parent.getCount() - 1) {
                            Intent intent = new Intent(mContext, ModifyEventActivity.class);
                            EventAndCatelog event = (EventAndCatelog) parent.getAdapter().getItem(position);
                            intent.putExtra(ModifyEventActivity.EXTRA_EVENT, event);

                            EventBus.getDefault().register(TimeLinePagerAdapter.this);
                            mContext.startActivity(intent);
                        }

                    }
                });
            }

            view.setAdapter(new TimeLineListAdapter(mContext, index));

        mListViewMap.put(position, view);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if (mListViewCache == null) {
            mListViewCache = (ListView) object;
        }
        mListViewMap.remove(position);

        container.removeView((View) object);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateEventEvent event)
    {
        Set<Map.Entry<Integer, ListView>> set = mListViewMap.entrySet();
        Map.Entry<Integer, ListView> entry = null;
        for (Iterator<Map.Entry<Integer, ListView>> ite = set.iterator(); ite.hasNext(); )
        {
            entry = ite.next();
            entry.getValue().setAdapter(new TimeLineListAdapter(mContext, entry.getKey() - Integer.MAX_VALUE + 1));
        }

        EventBus.getDefault().unregister(this);
    }
}
