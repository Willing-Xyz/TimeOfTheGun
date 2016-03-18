package com.willing.android.timeofgun.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.willing.android.timeofgun.R;
import com.willing.android.timeofgun.adapter.CatelogAdapter;
import com.willing.android.timeofgun.event.DeleteCatelogEvent;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.utils.LoadCatelogTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by Willing on 2015/11/19 0019.
 */
public class ManageCatelogActivity extends AppCompatActivity
{

    private static final int ADD_CATELOG_REQUEST_CODE = 2;
    private static final int MODIFY_CATELOG_REQUEST_CODE = 1;

    public static final String EXTRA_CATELOG = "extra_catelog";

    private ListView mCatelogListView;
    private LoadCatelogTask mLoadCatelogTask;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_catelog);

        getSupportActionBar().setTitle(R.string.catelog_manage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        initView();
        setupListener();
    }

    private void initView()
    {
        mCatelogListView = (ListView) findViewById(R.id.lv_catelog);
        mCatelogListView.setMultiChoiceModeListener(new CatelogMultiChoiceModeListener());
        mCatelogListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    }

    private void setupListener()
    {
        mCatelogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ManageCatelogActivity.this, ModifyCatelogActivity.class);

                Cursor cursor = (Cursor) mCatelogListView.getItemAtPosition(position);

                Catelog catelog = new Catelog();
                catelog.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                catelog.setName(cursor.getString(cursor.getColumnIndex(DbHelper.CATELOG_NAME)));
                catelog.setColor(cursor.getInt(cursor.getColumnIndex(DbHelper.CATELOG_COLOR)));
                catelog.setCatelogId(cursor.getLong(cursor.getColumnIndex(DbHelper.CATELOG_ID)));

                intent.putExtra(EXTRA_CATELOG, catelog);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


        mLoadCatelogTask = new LoadCatelogTask(this, mCatelogListView);
        mLoadCatelogTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mLoadCatelogTask.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_catelog, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.add_catelog)
        {
            Intent intent = new Intent(ManageCatelogActivity.this, AddCatelogActivity.class);

            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home)
        {
            finish();
        }
        return false;
    }

    @Subscribe
    public void onDeleteCatelog(DeleteCatelogEvent event)
    {
        mLoadCatelogTask.cancel(true);
        new LoadCatelogTask(this, mCatelogListView).execute();
    }


    private class CatelogMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            View view = mCatelogListView.getChildAt(position - mCatelogListView.getFirstVisiblePosition());
            CheckBox checkbox = (CheckBox) view.findViewById(R.id.cb_checked);
            checkbox.setChecked(mCatelogListView.isItemChecked(position));
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mActionMode = mode;

            mode.getMenuInflater().inflate(R.menu.catelog_manage, menu);

            CatelogAdapter adapter = (CatelogAdapter) mCatelogListView.getAdapter();
            adapter.setActionModeStarted(true);
            adapter.notifyDataSetChanged();

            EventBus.getDefault().register(ManageCatelogActivity.this);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.delete_catelog:

                    AlertDialog.Builder builder = new AlertDialog.Builder(ManageCatelogActivity.this);
                    builder.setTitle(R.string.sure_delete_catelog);
                    builder.setMessage(R.string.sure_delete_catelog_msg);
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SparseBooleanArray checks = mCatelogListView.getCheckedItemPositions();
                            ArrayList<Long> catelogs = new ArrayList<>();
                            Cursor cursor = null;
                            for (int i = 0; i < checks.size(); ++i) {
                                    cursor = (Cursor) mCatelogListView.getAdapter().getItem(checks.keyAt(i));
                                    catelogs.add(cursor.getLong(cursor.getColumnIndex(DbHelper.CATELOG_ID)));
                            }

                            DbHelper.deleteCatelogs(ManageCatelogActivity.this, catelogs);

                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();

                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            CatelogAdapter adapter = (CatelogAdapter) mCatelogListView.getAdapter();
            adapter.setActionModeStarted(false);
            adapter.notifyDataSetChanged();
            mActionMode = null;

            EventBus.getDefault().unregister(ManageCatelogActivity.this);
        }
    }
}
