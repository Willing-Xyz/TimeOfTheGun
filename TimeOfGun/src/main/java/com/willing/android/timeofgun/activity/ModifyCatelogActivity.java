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
 * Created by Willing on 2016/3/18.
 */
public class ModifyCatelogActivity extends AppCompatActivity
{
    private static final String STATE_CATELOG = "state_catelog";

    private EditText mCatelogNameEditText;
    private ColorPicker mColorPicker;
    private Catelog mCatelog;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_catelog);

        getSupportActionBar().setTitle(R.string.modify_catelog);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.acion_ok);

        initView();
        setupListener();
    }

    private void initView()
    {
        mColorPicker = (ColorPicker) findViewById(R.id.colorPicker);
        mColorPicker.setShowOldCenterColor(true);

        mCatelogNameEditText = (EditText) findViewById(R.id.et_catelogName);

        mCatelog = getIntent().getParcelableExtra(ManageCatelogActivity.EXTRA_CATELOG);
        mCatelogNameEditText.setText(mCatelog.getName());
        mColorPicker.setColor(mCatelog.getColor());

    }

    private void setupListener()
    {
        mColorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mCatelog.setColor(color);
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
                Toast.makeText(ModifyCatelogActivity.this, R.string.catelogName_cannot_null, Toast.LENGTH_LONG).show();
                return true;
            } else {
                // 判断是否名字是否重复
                if (!name.equals(mCatelog.getName())) {
                    if (DbHelper.isCatelogNameExisted(ModifyCatelogActivity.this, name)) {
                        Toast.makeText(ModifyCatelogActivity.this, R.string.catelogName_isExisted, Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
            }

            mCatelog.setName(name);

            new Thread(){
                @Override
                public void run() {

                    CatelogUtils.updateCatelog(ModifyCatelogActivity.this, mCatelog);
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

        outState.putParcelable(STATE_CATELOG, mCatelog);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            mCatelog = savedInstanceState.getParcelable(STATE_CATELOG);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}
