package com.willing.android.timeofgun.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.utils.CatelogUtils;
import com.willing.android.timeofgun.utils.DbHelper;


/**
 * Created by Willing on 2015/11/17 0017.
 */
public class AddCatelogActivity extends AppCompatActivity
{
    private static final String STATE_COLOR = "stateColor";


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
        mColorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
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

            new Thread(){
                @Override
                public void run() {
                    CatelogUtils.addCatelog(AddCatelogActivity.this, catelog);
                }
            }.start();


            finish();
        }
        return false;
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
