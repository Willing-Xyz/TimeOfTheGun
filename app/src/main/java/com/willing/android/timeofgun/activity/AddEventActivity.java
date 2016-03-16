package com.willing.android.timeofgun.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.model.Catelog;

/**
 * Created by Willing on 2016/3/16.
 */
public class AddEventActivity extends AppCompatActivity
{
    private static final int CATELOG_PICKER_REQUEST_CODE = 1;
    private View mSelectCatelog;
    private View mSelectStopTime;
    private View mSelectStartTime;

    private Button mCatelogNameView;
    private View mCatelogColorView;
    private Button mStartTimeView;
    private Button mStopTimeView;

    private Catelog mCatelog;
    private long mStartTime;
    private long mStopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_event);

        getSupportActionBar().setTitle(R.string.add_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.acion_ok);

        initView();
        setupListener();
    }

    private void initView() {

        mSelectCatelog = findViewById(R.id.select_catelog);
        mSelectStartTime = findViewById(R.id.select_startTime);
        mSelectStopTime = findViewById(R.id.select_stopTime);

        mCatelogNameView = (Button) findViewById(R.id.bt_catelog);
        mCatelogColorView = findViewById(R.id.vw_catelogColor);

        mStartTimeView = (Button) findViewById(R.id.bt_startTime);
        mStopTimeView = (Button) findViewById(R.id.bt_stopTime);
    }

    private void setupListener()
    {
        mSelectCatelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEventActivity.this, CatelogPickerActivity.class);
                startActivityForResult(intent, CATELOG_PICKER_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATELOG_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            mCatelog = data.getParcelableExtra(CatelogPickerActivity.EXTRA_CATELOG);

            mCatelogNameView.setText(mCatelog.getName());
            mCatelogColorView.setBackgroundColor(mCatelog.getColor());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {



            finish();
        }
        return false;
    }
}
