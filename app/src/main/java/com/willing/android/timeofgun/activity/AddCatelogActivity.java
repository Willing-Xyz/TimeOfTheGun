package com.willing.android.timeofgun.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.CatelogBmob;
import com.willing.android.timeofgun.model.User;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.utils.Utils;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by Willing on 2015/11/17 0017.
 */
public class AddCatelogActivity extends AppCompatActivity
{
    private static final String STATE_COLOR = "stateColor";
    private static final String CATELOG_FOR_SERVER = "catelog_for_server";


    private int mCatelogColor = -1;

    private EditText mCatelogNameEditText;
    private ColorPicker mColorPicker;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_catelog);

        getSupportActionBar().setTitle(R.string.add_catelog);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.acion_ok);

        initView();
        setupListener();
    }

    private void initView()
    {
        mColorPicker = (ColorPicker) findViewById(R.id.colorPicker);
        mColorPicker.setShowOldCenterColor(false);

        mCatelogNameEditText = (EditText) findViewById(R.id.et_catelogName);
    }

    private void setupListener()
    {
        mColorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener()
        {
            @Override
            public void onColorSelected(int color)
            {
                mCatelogColor = color;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            final String name = mCatelogNameEditText.getText().toString().trim();
            if (name.length() == 0) {
                // 提示不能为空
                Toast.makeText(AddCatelogActivity.this, R.string.catelogName_cannot_null, Toast.LENGTH_LONG).show();
                return true;
            } else {
                // 判断是否名字是否重复
                if (DbHelper.isCatelogNameExisted(AddCatelogActivity.this, name)) {
                    Toast.makeText(AddCatelogActivity.this, R.string.catelogName_isExisted, Toast.LENGTH_LONG).show();
                    return true;
                }
            }

            if (mCatelogColor == -1) {
                Toast.makeText(AddCatelogActivity.this, R.string.must_select_color, Toast.LENGTH_LONG).show();
                return true;
            }

            final long catelogId = System.currentTimeMillis();
            final Catelog catelog = new Catelog(name, mCatelogColor, catelogId);
            // 保存到本地
            addCatelogToLocal(catelog);
            // 保存到服务器
            User user = BmobUser.getCurrentUser(this, User.class);
            if (user != null) {
                CatelogBmob catelogBmob = new CatelogBmob();
                catelogBmob.setCatelogColor(mCatelogColor);
                catelogBmob.setCatelogName(name);
                catelogBmob.setUserId(user.getObjectId());
                catelogBmob.setCatelogId(catelogId);

                catelogBmob.save(this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                        addCatelogForServer(catelog);
                    }
                });
            }
            else
            {
                addCatelogForServer(catelog);
            }

            finish();
        }
        return false;
    }

    // 添加Catelog到本地系统
    private void addCatelogToLocal(final Catelog catelog) {
        new Thread(){
            @Override
            public void run() {
                DbHelper.addCatelog(AddCatelogActivity.this, catelog);
            }
        }.start();
    }

    /**
     * 当没有网络或没有登录时，不能传递数据到服务器。
     * 因此，先保存到文件中，之后在传递到服务器。
     */
    private void addCatelogForServer(final Catelog catelog) {

        new Thread(){
            @Override
            public void run()
            {
                DataOutputStream out = null;
                try {
                    out = new DataOutputStream(AddCatelogActivity.this.openFileOutput(CATELOG_FOR_SERVER, Context.MODE_APPEND));

                    out.writeUTF(catelog.getName());
                    out.writeInt(catelog.getColor());
                    out.writeLong(catelog.getCatelogId());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    Utils.closeIO(out);
                }
            }
        }.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_COLOR, mCatelogColor);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            mCatelogColor = savedInstanceState.getInt(STATE_COLOR);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}
