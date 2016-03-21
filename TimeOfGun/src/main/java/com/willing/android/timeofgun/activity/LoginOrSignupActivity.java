package com.willing.android.timeofgun.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.willing.android.timeofgun.R;

/**
 * Created by Willing on 2016/3/13.
 */
public class LoginOrSignupActivity extends AppCompatActivity
{
    private Button mLogin;
    private Button mSignup;
    private Button mTouristTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_or_signup);

        initView();
        setupListener();
    }

    private void initView() {
        mLogin = (Button) findViewById(R.id.login);
        mSignup = (Button) findViewById(R.id.signup);
        mTouristTry = (Button) findViewById(R.id.tourist_try);
    }

    private void setupListener() {

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrSignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrSignupActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        mTouristTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
