package com.willing.android.timeofgun.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.activity.ManageCatelogActivity;

import cn.bmob.v3.BmobUser;

/**
 * Created by Willing on 2016/3/13.
 */
public class SettingsFragment extends BaseFragment
{
    private View mRootView;

    private Button mAboutButton;
    private View mCatelogManageView;
    private Button mQuitButton;

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

        initView();
        setupListener();

        if (BmobUser.getCurrentUser(getActivity()) != null)
        {
            mQuitButton.setVisibility(View.VISIBLE);
        }
        else
        {
            mQuitButton.setVisibility(View.GONE);
        }

        return view;
    }

    private void initView()
    {
        mCatelogManageView =  mRootView.findViewById(R.id.catelog_manage);
        mAboutButton = (Button) mRootView.findViewById(R.id.bt_about);
        mQuitButton = (Button) mRootView.findViewById(R.id.bt_quit);
    }

    private void setupListener()
    {
        mCatelogManageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ManageCatelogActivity.class);

                startActivity(intent);
            }
        });


        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = "作者：Willing Xyz\n" +
                        "邮箱：sxswilling@163.com\n" +
                        "源码：https://github.com/Willing-Xyz/TimeOfTheGun";
                AlertDialog dialog = new AlertDialog.Builder(SettingsFragment.this.getActivity()).setMessage(msg).create();
                dialog.show();
            }
        });

        mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut(SettingsFragment.this.getActivity());
                mQuitButton.setVisibility(View.GONE);
            }
        });
    }
}
