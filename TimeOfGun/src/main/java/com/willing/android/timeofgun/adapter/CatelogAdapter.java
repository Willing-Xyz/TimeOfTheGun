package com.willing.android.timeofgun.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.utils.DbHelper;

/**
 * Created by Willing on 2015/11/16 0016.
 */
public class CatelogAdapter extends SimpleCursorAdapter
{
    private final LayoutInflater mInflater;
    private boolean mActionModeStarted;

    public CatelogAdapter(Context context, Cursor c, String[] from, int[] to)
    {
        super(context, R.layout.catelog_item, c, from, to);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.catelog_item, null);
            holder = new ViewHolder();
            holder.catelogColor = convertView.findViewById(R.id.vw_catelogColor);
            holder.catelogName = (TextView) convertView.findViewById(R.id.tv_catelogName);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_checked);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mActionModeStarted)
        {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.checkBox.setVisibility(View.GONE);
        }

        final Cursor cursor = (Cursor) getItem(position);

        holder.catelogColor.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(DbHelper.CATELOG_COLOR)));
        holder.catelogName.setText(cursor.getString(cursor.getColumnIndex(DbHelper.CATELOG_NAME)));

        return convertView;
    }

    public static String[] from()
    {
        return new String[]{
                DbHelper.CATELOG_NAME,
                DbHelper.CATELOG_COLOR
        };
    }

    public static int[] to()
    {
        return new int[]{
                R.id.vw_catelogColor,
                R.id.tv_catelogName
        };
    }

    public void setActionModeStarted(boolean b) {

        mActionModeStarted = b;
    }

    class ViewHolder
    {
        View catelogColor;
        TextView catelogName;
        CheckBox checkBox;
    }
}
