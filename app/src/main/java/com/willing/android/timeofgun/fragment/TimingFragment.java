package com.willing.android.timeofgun.fragment;

import android.app.Activity;
import android.content.Context;
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
import com.willing.android.timeofgun.utils.DateUtils;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.utils.Utils;
import com.willing.android.timeofgun.view.StartStopButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private static final String CURRENT_CATELOG_FILENAME = "cur_catelog";
    private static final String START_STATE_FILENAME = "start_state";
    private static final String START_TIME_FILENAME = "start_time";

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

            mCatelogNameTextView.setText(mCatelog.getName());
            mCatelogColorView.setBackgroundColor(mCatelog.getColor());

            // 当 当前Catelog改变时，存储到文件中
            saveCurrentCatelog();

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
        saveStartState();
        if (started)
        {
            if (mCatelog == null)
            {
                Toast.makeText(getActivity(), R.string.please_new_catelog, Toast.LENGTH_LONG).show();
                return;
            }
            mTimeShowTextView.setText(R.string.init_time);

            mStartTime = Calendar.getInstance();
            saveStartTime();


            mHandler.postDelayed(mUpdateTimeRunnable, 1000);
        }
        else
        {
            mHandler.removeCallbacks(mUpdateTimeRunnable);
            // 保存事件到数据库
        }

    }

    private void restoreState(Bundle savedInstanceState) {

        if (savedInstanceState == null)
        {
            // 首先从文件中获取当前Catelog
            // 如果不存在，则从数据库中随便查找一个
            mCatelog = restoreCurrentCatelog();
            if (mCatelog == null)
            {
                mCatelog = DbHelper.findAnyCatelogFromDb(getActivity());
                if (mCatelog == null)
                {
                    mCatelogColorView.setVisibility(View.GONE);
                    mCatelogNameTextView.setText(R.string.click_for_new_catelog);
                    return;
                }
                else
                {
                    saveCurrentCatelog();
                }
            }

            mStarted = restoreStartState();
            if (mStarted)
            {
                if (mStartTime == null)
                {
                    mStartTime = Calendar.getInstance();
                }
                mStartTime.setTimeInMillis(restoreStartTime());
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

    // 存储计时开始时间
    private void saveStartTime()
    {
        DataOutputStream dataOut = null;
        try
        {
            dataOut = new DataOutputStream(getActivity().openFileOutput(START_TIME_FILENAME, Context.MODE_PRIVATE));
            dataOut.writeLong(mStartTime.getTimeInMillis());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(dataOut);
        }
    }

    // 保存当前Catelog到文件中
    private void saveCurrentCatelog() {
        DataOutputStream dataOut = null;
        try
        {
            OutputStream out = getActivity().openFileOutput(CURRENT_CATELOG_FILENAME, Context.MODE_PRIVATE);
            dataOut = new DataOutputStream(out);

            dataOut.writeInt(mCatelog.getId());
            dataOut.writeInt(mCatelog.getColor());
            dataOut.writeUTF(mCatelog.getName());
            dataOut.writeLong(mCatelog.getCatelogId());

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataOut);
        }
    }

    // 从文件中恢复当前Catelog
    private Catelog restoreCurrentCatelog() {
        DataInputStream dataIn = null;
        try
        {
            InputStream in = getActivity().openFileInput(CURRENT_CATELOG_FILENAME);
            dataIn = new DataInputStream(in);

            if (mCatelog == null)
            {
                mCatelog = new Catelog();
            }
            mCatelog.setId(dataIn.readInt());
            mCatelog.setColor(dataIn.readInt());
            mCatelog.setName(dataIn.readUTF());
            mCatelog.setCatelogId(dataIn.readLong());

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataIn);
        }
        return mCatelog;
    }

    // 存储start状态到文件中。
    private void saveStartState()
    {
        DataOutputStream dataOut = null;
        try
        {
            dataOut = new DataOutputStream(getActivity().openFileOutput(START_STATE_FILENAME, Context.MODE_PRIVATE));
            dataOut.writeBoolean(mStarted);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(dataOut);
        }
    }



    // 从文件中恢复start的状态
    private boolean restoreStartState() {
        DataInputStream dataIn = null;
        try
        {
            InputStream in = getActivity().openFileInput(START_STATE_FILENAME);
            dataIn = new DataInputStream(in);

            mStarted = dataIn.readBoolean();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataIn);
        }
        return mStarted;
    }

    // 恢复计时开始时间
    private long restoreStartTime() {
        long startTime = 0;
        DataInputStream dataIn = null;
        try
        {
            InputStream in = getActivity().openFileInput(START_TIME_FILENAME);
            dataIn = new DataInputStream(in);

            startTime = dataIn.readLong();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataIn);
        }
        return startTime;
    }

    private class UpdateTimeRunnable implements Runnable {
        @Override
        public void run() {

            mTimeShowTextView.setText(DateUtils.formatDistanceTime(mStartTime.getTimeInMillis()));
            mHandler.postDelayed(mUpdateTimeRunnable, 1000);
        }
    }
}
