package com.willing.android.timeofgun.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.event.AddEventEvent;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.Event;
import com.willing.android.timeofgun.utils.DateUtils;
import com.willing.android.timeofgun.utils.EventUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

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

    private TextView mInfoView;

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

        mInfoView = (TextView) findViewById(R.id.info);
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

        mSelectStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                date.setTime(date.getTime());
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener()
                        {
                            @Override
                            public void onDateTimeSet(Date date)
                            {
                                Date today = new Date();
                                if (today.before(date))
                                {
                                    // 不能大于当前时间
                                    mInfoView.setText(R.string.greater_today);
                                    return;
                                }
                                if (mStopTime != 0)
                                {
                                    // 不能大于结束时间
                                    Date stopDate = new Date(mStopTime);
                                    if (stopDate.before(date))
                                    {
                                        mInfoView.setText(R.string.starttime_greater_stoptime);
                                        return;
                                    }
                                    mInfoView.setText(DateUtils.formatDistanceTime(date.getTime(), mStopTime));
                                }
                                mStartTime = date.getTime();
                                mStartTimeView.setText(DateUtils.formatDateAndTime(mStartTime));
                            }
                        })
                        .setInitialDate(date)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });

        mSelectStopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                date.setTime(date.getTime());
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener()
                        {
                            @Override
                            public void onDateTimeSet(Date date)
                            {
                                Date today = new Date();
                                if (today.before(date))
                                {
                                    mInfoView.setText(R.string.greater_today);
                                    return;
                                }
                                if (mStartTime != 0)
                                {
                                    Date startDate = new Date(mStartTime);
                                    if (date.before(startDate))
                                    {
                                        mInfoView.setText(R.string.stoptime_less_starttime);
                                        return;
                                    }
                                    mInfoView.setText(DateUtils.formatDistanceTime(mStartTime, date.getTime()));
                                }
                                mStopTime = date.getTime();
                                mStopTimeView.setText(DateUtils.formatDateAndTime(mStopTime));
                            }
                        })
                        .setInitialDate(date)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATELOG_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            mCatelog = data.getParcelableExtra(CatelogPickerActivity.EXTRA_CATELOG);

            if (mCatelog != null) {
                mCatelogNameView.setText(mCatelog.getName());
                mCatelogColorView.setBackgroundColor(mCatelog.getColor());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            if (mCatelog == null || mStartTime == 0 || mStopTime == 0)
            {
                Toast.makeText(this, R.string.info_less, Toast.LENGTH_SHORT).show();
                return true;
            }

            final Event event = new Event();
            event.setCatelogId(mCatelog.getCatelogId());
            event.setStopTime(mStopTime);
            event.setStartTime(mStartTime);
            event.setEventId(System.currentTimeMillis());

            new Thread()
            {
                @Override
                public void run() {
                    EventUtils.addEvent(AddEventActivity.this.getApplicationContext(), event);
                }
            }.start();

            finish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        EventBus.getDefault().post(new AddEventEvent());
    }
}
