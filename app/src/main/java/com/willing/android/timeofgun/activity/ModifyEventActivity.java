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

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.event.UpdateEventEvent;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.Event;
import com.willing.android.timeofgun.model.EventAndCatelog;
import com.willing.android.timeofgun.utils.DateUtils;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.utils.EventUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

/**
 * Created by Willing on 2016/3/18.
 */
public class ModifyEventActivity extends AppCompatActivity
{
    public static final String EXTRA_EVENT = "event";
    private static final int CATELOG_PICKER_REQUEST_CODE = 1;

    private View mSelectCatelog;
    private View mSelectStopTime;
    private View mSelectStartTime;

    private Button mCatelogNameView;
    private View mCatelogColorView;
    private Button mStartTimeView;
    private Button mStopTimeView;

    private TextView mInfoView;

    private Button mDeleteEvent;

    private EventAndCatelog mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_event);

        getSupportActionBar().setTitle(R.string.modify_event);
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
        mDeleteEvent = (Button) findViewById(R.id.delete_event);

        mEvent = getIntent().getParcelableExtra(EXTRA_EVENT);
        mCatelogNameView.setText(mEvent.getCatelog().getName());
        mCatelogColorView.setBackgroundColor(mEvent.getCatelog().getColor());
        mStartTimeView.setText(DateUtils.formatDateAndTime(mEvent.getStartTime()));
        mStopTimeView.setText(DateUtils.formatDateAndTime(mEvent.getStopTime()));

    }

    private void setupListener() {
        mSelectCatelog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModifyEventActivity.this, CatelogPickerActivity.class);
                startActivityForResult(intent, CATELOG_PICKER_REQUEST_CODE);
            }
        });

        mSelectStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                date.setTime(date.getTime());
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(new SlideDateTimeListener() {
                            @Override
                            public void onDateTimeSet(Date date) {
                                Date today = new Date();
                                if (today.before(date)) {
                                    // 不能大于当前时间
                                    mInfoView.setText(R.string.greater_today);
                                    return;
                                }

                                Date stopDate = new Date(mEvent.getStopTime());
                                if (stopDate.before(date)) {
                                    mInfoView.setText(R.string.starttime_greater_stoptime);
                                    return;
                                }
                                mEvent.setStartTime(date.getTime());
                                mInfoView.setText(DateUtils.formatDistanceTime(date.getTime(), mEvent.getStopTime()));
                                mStartTimeView.setText(DateUtils.formatDateAndTime(mEvent.getStartTime()));
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
                        .setListener(new SlideDateTimeListener() {
                            @Override
                            public void onDateTimeSet(Date date) {
                                Date today = new Date();
                                if (today.before(date)) {
                                    mInfoView.setText(R.string.greater_today);
                                    return;
                                }

                                Date startDate = new Date(mEvent.getStartTime());
                                if (date.before(startDate)) {
                                    mInfoView.setText(R.string.stoptime_less_starttime);
                                    return;
                                }
                                mEvent.setStopTime(date.getTime());
                                mInfoView.setText(DateUtils.formatDistanceTime(mEvent.getStartTime(), date.getTime()));

                                mStopTimeView.setText(DateUtils.formatDateAndTime(mEvent.getStopTime()));
                            }
                        })
                        .setInitialDate(date)
                        .setIs24HourTime(true)
                        .build()
                        .show();
            }
        });
        mDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DbHelper.deleteEvent(ModifyEventActivity.this, mEvent.getId());

                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATELOG_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Catelog catelog = data.getParcelableExtra(CatelogPickerActivity.EXTRA_CATELOG);

            if (catelog != null) {
                mCatelogNameView.setText(catelog.getName());
                mCatelogColorView.setBackgroundColor(catelog.getColor());
                mEvent.setCatelog(catelog);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {

            final Event event = new Event();
            event.setStartTime(mEvent.getStartTime());
            event.setStopTime(mEvent.getStopTime());
            event.setCatelogId(mEvent.getCatelog().getCatelogId());
            event.setId(mEvent.getId());


            new Thread()
            {
                @Override
                public void run() {
                    EventUtils.updateEvent(ModifyEventActivity.this.getApplicationContext(), event);
                }
            }.start();

            finish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // 模拟发送UpdateEventEvent。
        // 因为如果用户直接按下返回键返回，那么就不会发送事件，那么就不会解注册。之后也会重复注册
        EventBus.getDefault().post(new UpdateEventEvent());
    }
}
