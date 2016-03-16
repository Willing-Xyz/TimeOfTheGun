package com.willing.android.timeofgun.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by Willing on 2016/3/11.
 */
public class TimeLinePagerAdapter extends PagerAdapter {


    private final Context mContext;
    private ListView mListViewCache;

    public TimeLinePagerAdapter(Context context)
    {
        mContext = context;
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
                        // TODO: 2016/3/16 点击修改事件
                        if (position != parent.getCount() - 1) {

                        }

                    }
                });
            }

            view.setAdapter(new TimeLineListAdapter(mContext, index));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        if (mListViewCache == null) {
            mListViewCache = (ListView) object;
        }


        container.removeView((View) object);
    }

}
