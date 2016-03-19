package com.willing.android.timeofgun.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.willing.android.timeofgun.R;

import cn.bmob.v3.BmobUser;


public class UserActivity extends AppCompatActivity {

    private TextView mEmailTextView;
    private TextView mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportActionBar().setTitle(R.string.user_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        initView();
    }

    private void initView() {

        mEmailTextView = (TextView) findViewById(R.id.email);
        mUserName = (TextView) findViewById(R.id.userName);

        BmobUser user = BmobUser.getCurrentUser(this);
        mEmailTextView.setText(user.getEmail());
        mUserName.setText(user.getUsername());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home)
        {
            finish();
        }
        return false;
    }

}
