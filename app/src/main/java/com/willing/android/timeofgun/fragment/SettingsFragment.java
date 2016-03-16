package com.willing.android.timeofgun.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.willing.android.timeofgun.R;

/**
 * Created by Willing on 2016/3/13.
 */
public class SettingsFragment extends BaseFragment
{
    private View mRootView;

    public static SettingsFragment getInstance()
    {
        SettingsFragment fragment = new SettingsFragment();
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
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            mRootView = view;
        }

        return view;
    }


}
