package com.willing.android.timeofgun.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.activity.CatelogPickerActivity;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.Event;
import com.willing.android.timeofgun.utils.DateUtils;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.utils.EventUtils;
import com.willing.android.timeofgun.utils.FileUtils;
import com.willing.android.timeofgun.view.StartStopButton;

import java.util.Calendar;

/**
 *
 * 为了当程序销毁后仍然保存计时状态，需要把一些信息保存在文件中。
 需要保存的信息有：
 started：是否正在计时
 startTime：计时时开始的时间（这样可以根据当前时间减去开始时间求得差值）
 catelog：当前计时的类别

 保存的时机：

 catelog：
 1. 当切换catelog时保存。
 2. 当进入Activity时，如果没有保存的catelog，会自动从数据库中读取一个，如果读取成功，也会保存。
 started：
 1. 按下按钮时保存。
 startTime：
 1. 按下按钮，并且为启动状态时，保存当前时间
 2.
 * Created by Willing on 2016/3/13.
 */

public class TimingFragment extends BaseFragment implements StartStopButton.StateChangeListener
{

    private static final int CATELOG_PICKER_REQUEST_CODE = 1;

    private static final String STATE_STARTED = "started";
    private static final String STATE_CATELOG = "catelog";



    private View mRootView;

    private TextView mTimeShowTextView;
    private View mCatelogSelectorView;
    private View mCatelogColorView;
    private TextView mCatelogNameTextView;
    private StartStopButton mStartStopButton;

    private boolean mStarted;
    private Catelog mCatelog;
    private Calendar mStartTime;

    private Handler mHandler;
    private Runnable mUpdateTimeRunnable;


    public static TimingFragment getInstance()
    {
        TimingFragment fragment = new TimingFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = null;
        if (mRootView != null)
        {
            view = mRootView;
        }
        else
        {
            view = inflater.inflate(R.layout.fragment_timing, container, false);
            mRootView = view;
        }

        initView();
        setupListenter();

        mHandler = new Handler();
        mUpdateTimeRunnable = new UpdateTimeRunnable();
        mCatelog = new Catelog();
        mStartTime = Calendar.getInstance();

        restoreState(savedInstanceState);

        return view;
    }

    private void initView() {
        mTimeShowTextView = (TextView) mRootView.findViewById(R.id.tv_timeShow);
        mCatelogSelectorView = mRootView.findViewById(R.id.rl_event);
        mCatelogColorView = mCatelogSelectorView.findViewById(R.id.iv_eventColor);
        mCatelogNameTextView = (TextView) mCatelogSelectorView.findViewById(R.id.tv_eventName);
        mStartStopButton = (StartStopButton) mRootView.findViewById(R.id.start_stop_button);

    }

    private void setupListenter() {

        mCatelogSelectorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimingFragment.this.getActivity(), CatelogPickerActivity.class);
                startActivityForResult(intent, CATELOG_PICKER_REQUEST_CODE);
            }
        });

        mStartStopButton.registerStateChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mStarted) {
            mHandler.post(mUpdateTimeRunnable);
        }
        // 验证当前Catelog是否有效
        if (!DbHelper.isCatelogNameExisted(getActivity(), mCatelog.getName()))
        {
            Catelog catelog = DbHelper.findAnyCatelogFromDb(getActivity());
            if (catelog != null)
            {
                mCatelog = catelog;
                mCatelogColorView.setBackgroundColor(catelog.getColor());
                mCatelogNameTextView.setText(catelog.getName());
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        mHandler.removeCallbacks(mUpdateTimeRunnable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATELOG_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            mCatelog = data.getParcelableExtra(CatelogPickerActivity.EXTRA_CATELOG);

            if (mCatelogColorView.getVisibility() == View.GONE)
            {
                mCatelogColorView.setVisibility(View.VISIBLE);
            }
            mCatelogNameTextView.setText(mCatelog.getName());
            mCatelogColorView.setBackgroundColor(mCatelog.getColor());

            // 当 当前Catelog改变时，存储到文件中
            FileUtils.saveCurrentCatelog(getActivity(), mCatelog);

            mStartStopButton.setStarted(false);
            mStarted = false;
            mTimeShowTextView.setText(R.string.init_time);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBoolean(STATE_STARTED, mStarted);
        outState.putParcelable(STATE_CATELOG, mCatelog);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void stateChanged(boolean started) {
        mStarted = started;
        FileUtils.saveStartState(getActivity(), mStarted);
        if (started)
        {
            if (mCatelog.getName() == "")
            {
                Toast.makeText(getActivity(), R.string.please_new_catelog, Toast.LENGTH_LONG).show();
                mStartStopButton.setStartStateJust(false);
                mStarted = false;
                FileUtils.saveStartState(getActivity(), false);
                return;
            }
            mTimeShowTextView.setText(R.string.init_time);

            mStartTime = Calendar.getInstance();
            FileUtils.saveStartTime(getActivity(), mStartTime.getTimeInMillis());


            mHandler.postDelayed(mUpdateTimeRunnable, 1000);
        }
        else
        {
            mHandler.removeCallbacks(mUpdateTimeRunnable);

            if (mCatelog.getName() == "")
            {
                return;
            }

            new Thread()
            {
                @Override
                public void run() {
                    // TODO: 2016/3/16 划分Event
                    Event event = new Event();
                    event.setStartTime(mStartTime.getTimeInMillis());
                    event.setStopTime(Calendar.getInstance().getTimeInMillis());
                    event.setCatelogId(mCatelog.getCatelogId());
                    EventUtils.addEvent(getActivity(), event);
                }
            }.start();
        }

    }



    private void restoreState(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            // 首先从文件中获取当前Catelog
            // 如果不存在，则从数据库中随便查找一个
            Catelog catelog = null;
            catelog = FileUtils.restoreCurrentCatelog(getActivity());
            if (catelog == null) {
                catelog = DbHelper.findAnyCatelogFromDb(getActivity());
                if (catelog == null) {
                    mCatelogColorView.setVisibility(View.GONE);
                    mCatelogNameTextView.setText(R.string.click_for_new_catelog);
                    return;
                }
                else
                {
                    FileUtils.saveCurrentCatelog(getActivity(), catelog);
                }
            }
            else
            {
                // 验证当前Catelog是否有效
                if (!DbHelper.isCatelogNameExisted(getActivity(), catelog.getName()))
                {
                    catelog = DbHelper.findAnyCatelogFromDb(getActivity());
                    if (catelog != null)
                    {
                        mCatelogColorView.setBackgroundColor(catelog.getColor());
                        mCatelogNameTextView.setText(catelog.getName());
                    }
                }
            }
            if (catelog != null)
            {
                mCatelog = catelog;
            }

            mStarted = FileUtils.restoreStartState(getActivity());
            if (mStarted)
            {
                if (mStartTime == null)
                {
                    mStartTime = Calendar.getInstance();
                }
                mStartTime.setTimeInMillis(FileUtils.restoreStartTime(getActivity()));
                mHandler.post(mUpdateTimeRunnable);
            }

        }
        else
        {
            mStarted = savedInstanceState.getBoolean(STATE_STARTED);
            mCatelog = savedInstanceState.getParcelable(STATE_CATELOG);
        }

        if (mStarted) {
            mStartStopButton.setStartStateJust(mStarted);
        }
        mCatelogNameTextView.setText(mCatelog.getName());
        mCatelogColorView.setBackgroundColor(mCatelog.getColor());
    }


    private class UpdateTimeRunnable implements Runnable {
        @Override
        public void run() {

            mTimeShowTextView.setText(DateUtils.formatDistanceTime(mStartTime.getTimeInMillis()));
            mHandler.postDelayed(mUpdateTimeRunnable, 1000);
        }
    }
}
