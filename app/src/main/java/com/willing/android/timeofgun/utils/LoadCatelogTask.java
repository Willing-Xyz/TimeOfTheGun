package com.willing.android.timeofgun.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.ListView;

import com.willing.android.timeofgun.adapter.CatelogAdapter;

import java.lang.ref.SoftReference;

/**
 * Created by Willing on 2016/3/18.
 */
public class LoadCatelogTask extends AsyncTask<Void, Void, Cursor>
{
    private SoftReference<Context> mContext;
    private ListView mListView;

    public LoadCatelogTask(Context context, ListView listView)
    {
        mContext = new SoftReference<Context>(context);
        mListView = listView;
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        Context context = mContext.get();
        if (context == null)
        {
            return null;
        }
        return DbHelper.getAllCatelog(context);
    }

    @Override
    protected void onPostExecute(Cursor cursor) {

        if (cursor == null || isCancelled())
        {
            return;
        }
        Context context = mContext.get();
        if (context == null)
        {
            return;
        }
        CatelogAdapter adapter = new CatelogAdapter(context, cursor,
                CatelogAdapter.from(), CatelogAdapter.to());
        mListView.setAdapter(adapter);
    }
}
