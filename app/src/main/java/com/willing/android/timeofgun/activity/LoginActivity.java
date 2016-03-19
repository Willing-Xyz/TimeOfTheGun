package com.willing.android.timeofgun.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.model.User;
import com.willing.android.timeofgun.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Willing on 2016/3/13.
 */
public class LoginActivity extends AppCompatActivity{


    private TextInputLayout mUserName;
    private TextInputLayout mPassword;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        initView();
        setupListener();
    }

    private void initView() {
        mUserName = (TextInputLayout) findViewById(R.id.userName);
        mPassword = (TextInputLayout) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.login);
    }

    private void setupListener() {

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userName = mUserName.getEditText().getEditableText().toString();
                String password = mPassword.getEditText().getEditableText().toString();

                if (userName == null || userName.isEmpty()) {
                    mUserName.setError(getResources().getString(R.string.error_username_not_null));
                }
                if (password == null || password.length() < 6) {
                    mPassword.setError(getResources().getString(R.string.error_password_too_short));
                }

                User user = new User();
                user.setUsername(userName);
                MessageDigest digest = null;
                try {
                    digest = MessageDigest.getInstance("SHA1");
                    digest.update(password.getBytes("UTF-8"));
                    password = Utils.byteArrayToHex(digest.digest());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                user.setPassword(password);
                user.login(LoginActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();

                        // TODO: 2016/3/15 处理未上传和未下载的数据

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        String reson = getResources().getString(R.string.login_fail) + ": " + s;
                        Toast.makeText(LoginActivity.this, reson, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
