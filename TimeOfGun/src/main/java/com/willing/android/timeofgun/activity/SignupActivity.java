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
import com.willing.android.timeofgun.utils.BmobUtils;
import com.willing.android.timeofgun.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Willing on 2016/3/13.
 */
public class SignupActivity extends AppCompatActivity{

    private TextInputLayout mEmail;
    private TextInputLayout mUserName;
    private TextInputLayout mPassword;
    private Button mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle(R.string.signup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));


        initView();
        setupListener();
    }

    private void initView() {

        mUserName = (TextInputLayout) findViewById(R.id.userName);
        mEmail = (TextInputLayout) findViewById(R.id.email);
        mPassword = (TextInputLayout) findViewById(R.id.password);
        mSignup = (Button) findViewById(R.id.signup);
    }

    private void setupListener() {

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mUserName.getEditText().getEditableText().toString();
                String email = mEmail.getEditText().getEditableText().toString();
                String password = mPassword.getEditText().getEditableText().toString();

                if (userName == null || userName.isEmpty()) {
                    mUserName.setError(getResources().getString(R.string.error_username_not_null));
                }
                if (password == null || password.length() < 6) {
                    mPassword.setError(getResources().getString(R.string.error_password_too_short));
                }

                // TODO: 2016/3/13 验证邮箱
                if (email != null && true) {

                    User user = new User();
                    user.setUsername(userName);
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA1");
                        digest.update(password.getBytes("UTF-8"));
                        password = Utils.byteArrayToHex(digest.digest());
                    } catch (NoSuchAlgorithmException e) {
                        // nothing
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    user.setPassword(password);
                    user.setEmail(email);

                    user.signUp(SignupActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(SignupActivity.this, R.string.signup_success, Toast.LENGTH_SHORT).show();

                            // 注册成功后，会自动登录。因此传递NOUSER数据库的内容到服务器，并复制NOUSER数据库到USERID数据库，并清空NOUSER数据库.
                            new Thread(){
                                @Override
                                public void run() {
                                    BmobUtils.uploadDatas(SignupActivity.this);
                                    BmobUtils.moveDatas(SignupActivity.this);
                                }
                            }.start();

                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            String reson = getResources().getString(R.string.signup_fail) + ": " + s;
                            Toast.makeText(SignupActivity.this, reson, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mEmail.setError(getResources().getString(R.string.error_input_corrent_email));
                }

            }
        });
    }


}
