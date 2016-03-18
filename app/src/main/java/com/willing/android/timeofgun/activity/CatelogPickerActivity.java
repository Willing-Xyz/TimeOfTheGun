package com.willing.android.timeofgun.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.utils.LoadCatelogTask;

/**
 * Created by Willing on 2015/11/16 0016.
 */
public class CatelogPickerActivity extends AppCompatActivity
{

    private static final int ADD_CATELOG_REQUEST_CODE = 1;

    public static final String EXTRA_CATELOG = "extra_catelog";

    private ListView mCatelogListView;

    private LoadCatelogTask mLoadCatelogTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_catelog_picker);

        getSupportActionBar().setTitle(R.string.pick_catelog);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        initView();
        setupListener();


    }

    private void initView()
    {
        mCatelogListView = (ListView) findViewById(R.id.lv_catelog);

    }

    private void setupListener()
    {

        mCatelogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Cursor cursor = (Cursor) mCatelogListView.getItemAtPosition(position);

                Catelog catelog = new Catelog();
                catelog.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                catelog.setName(cursor.getString(cursor.getColumnIndex(DbHelper.CATELOG_NAME)));
                catelog.setColor(cursor.getInt(cursor.getColumnIndex(DbHelper.CATELOG_COLOR)));
                catelog.setCatelogId(cursor.getLong(cursor.getColumnIndex(DbHelper.CATELOG_ID)));

                intent.putExtra(EXTRA_CATELOG, catelog);

                setResult(Activity.RESULT_OK, intent);

                CatelogPickerActivity.this.finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        mLoadCatelogTask = new LoadCatelogTask(this, mCatelogListView);
        mLoadCatelogTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mLoadCatelogTask.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_catelog, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_catelog)
        {
            Intent intent = new Intent(CatelogPickerActivity.this, AddCatelogActivity.class);

            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home)
        {
            setResult(RESULT_CANCELED);
            finish();
        }
        return false;
    }




}
