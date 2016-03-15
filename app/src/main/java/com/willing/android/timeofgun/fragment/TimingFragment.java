package com.willing.android.timeofgun.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.activity.CatelogPickerActivity;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.view.StartStopButton;

/**
 * Created by Willing on 2016/3/13.
 */
public class TimingFragment extends BaseFragment
{

    private static final int CATELOG_PICKER_REQUEST_CODE = 1;

    private View mRootView;

    private TextView mTimeShowTextView;
    private View mCatelogSelectorView;
    private View mCatelogColorView;
    private TextView mCatelogNameTextView;
    private StartStopButton mStartStopButton;


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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CATELOG_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            Catelog catelog = data.getParcelableExtra(CatelogPickerActivity.EXTRA_CATELOG);

            mCatelogNameTextView.setText(catelog.getName());
            mCatelogColorView.setBackgroundColor(catelog.getColor());

//            saveRecentCatelog();

            mStartStopButton.setStarted(false);
            mTimeShowTextView.setText(R.string.init_time);
        }
    }
}
