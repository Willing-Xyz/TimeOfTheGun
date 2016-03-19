package com.willing.android.timeofgun;

import android.app.Application;
import android.content.Context;

import com.willing.android.timeofgun.model.CatelogBmob;
import com.willing.android.timeofgun.model.EventBmob;
import com.willing.android.timeofgun.utils.CatelogUtils;
import com.willing.android.timeofgun.utils.DbHelper;
import com.willing.android.timeofgun.utils.EventUtils;
import com.willing.android.timeofgun.utils.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Willing on 2016/3/13.
 */
public class App extends Application{

    private List<EventBmob> mEventAdd;
    private List<EventBmob> mEventUpdate;
    private List<EventBmob> mEventDelete;

    private List<CatelogBmob> mCatelogAdd;
    private List<CatelogBmob> mCatelogUpdate;
    private List<CatelogBmob> mCatelogDelete;

    private AtomicInteger mCount;


    @Override
    public void onCreate() {
        super.onCreate();

        Bmob.initialize(this, "06d8fd4a25e1636b602397f71d281f57");

        BmobUser user = BmobUser.getCurrentUser(this);
        if (user != null && Utils.isNetworkConnected(this)) {

            mEventAdd = Collections.synchronizedList(new ArrayList<EventBmob>());
            mEventDelete = Collections.synchronizedList(new ArrayList<EventBmob>());
            mEventUpdate = Collections.synchronizedList(new ArrayList<EventBmob>());

            mCatelogAdd = Collections.synchronizedList(new ArrayList<CatelogBmob>());
            mCatelogUpdate = Collections.synchronizedList(new ArrayList<CatelogBmob>());
            mCatelogDelete = Collections.synchronizedList(new ArrayList<CatelogBmob>());

            mCount = new AtomicInteger(0);

            new Thread() {
                @Override
                public void run() {
                    handleFailureData();

                    try {

                        while (mCount.get() != 0)
                        {
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    saveFailureDataToFile();
                }
            }.start();
        }
    }

    private void saveFailureDataToFile() {

        saveFailureCatelogToFile(CatelogUtils.TYPE_ADD);
        saveFailureCatelogToFile(CatelogUtils.TYPE_DELETE);
        saveFailureCatelogToFile(CatelogUtils.TYPE_UPDATE);

        saveFailureEventToFile(EventUtils.TYPE_DELETE);
        saveFailureEventToFile(EventUtils.TYPE_UPDATE);
        saveFailureEventToFile(EventUtils.TYPE_ADD);
    }

    private void saveFailureCatelogToFile(int type)
    {
        DataOutputStream out = null;
        try {
            // 打开并清空文件内容
            out = new DataOutputStream(openFileOutput(BmobUser.getCurrentUser(this).getObjectId() + "catelog" + type, Context.MODE_PRIVATE));

            List<CatelogBmob> catelogs = null;
            switch (type)
            {
                case CatelogUtils.TYPE_DELETE:
                    catelogs = mCatelogDelete;
                    break;
                case CatelogUtils.TYPE_UPDATE:
                    catelogs = mCatelogUpdate;
                    break;
                case CatelogUtils.TYPE_ADD:
                    catelogs = mCatelogAdd;
                    break;
            }

            CatelogBmob catelog = null;
            for (int i = 0; i < catelogs.size(); ++i)
            {
                catelog = catelogs.get(i);
                out.writeUTF(catelog.getCatelogName());
                out.writeInt(catelog.getCatelogColor());
                out.writeLong(catelog.getCatelogId());
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(out);
        }
    }

    private void saveFailureEventToFile(int type)
    {
        DataOutputStream out = null;
        try {
            // 打开并清空文件内容
            out = new DataOutputStream(openFileOutput(BmobUser.getCurrentUser(this).getObjectId() + "event" + type, Context.MODE_PRIVATE));

            List<EventBmob> events = null;
            switch (type)
            {
                case EventUtils.TYPE_DELETE:
                    events = mEventDelete;
                    break;
                case EventUtils.TYPE_UPDATE:
                    events = mEventUpdate;
                    break;
                case EventUtils.TYPE_ADD:
                    events = mEventAdd;
                    break;
            }

            EventBmob event = null;
            for (int i = 0; i < events.size(); ++i)
            {
                event = events.get(i);
                out.writeLong(event.getStartTime());
                out.writeLong(event.getStopTime());
                out.writeLong(event.getCatelogId());
                out.writeLong(event.getEventId());
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(out);
        }
    }

    // 应用启动时，重新处理上传失败的数据
    private void handleFailureData() {

        handleFailureEvent(EventUtils.TYPE_ADD);
        handleFailureEvent(EventUtils.TYPE_DELETE);
        handleFailureEvent(EventUtils.TYPE_UPDATE);

        handleFailureCatelog(CatelogUtils.TYPE_UPDATE);
        handleFailureCatelog(CatelogUtils.TYPE_ADD);
        handleFailureCatelog(CatelogUtils.TYPE_DELETE);
    }

    private void handleFailureCatelog(int type) {

        DataInputStream in = null;
        try {
            in = new DataInputStream(openFileInput(BmobUser.getCurrentUser(this).getObjectId() + "catelog" + type));


            String name;
            int color;
            long catelogId;
            while ((name = in.readUTF()) != null)
            {
                color = in.readInt();
                catelogId = in.readLong();

                CatelogBmob catelogBmob = new CatelogBmob();
                catelogBmob.setUserId(BmobUser.getCurrentUser(this).getObjectId());
                catelogBmob.setCatelogColor(color);
                catelogBmob.setCatelogId(catelogId);
                catelogBmob.setCatelogName(name);

                mCount.incrementAndGet();
                switch (type)
                {
                    case CatelogUtils.TYPE_DELETE:
                        handleFailureCatelogDelete(catelogId);
                        break;
                    case CatelogUtils.TYPE_ADD:
                        handleFailureCatelogAdd(catelogBmob);
                        break;
                    case CatelogUtils.TYPE_UPDATE:
                        handleFailureCatelogUpdate(catelogBmob);
                        break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(in);
        }
    }

    private void handleFailureCatelogDelete(final long catelogId) {

                BmobQuery<CatelogBmob> query = new BmobQuery<>();
                query.addWhereEqualTo(DbHelper.CATELOG_ID, catelogId);
                query.findObjects(this, new FindListener<CatelogBmob>() {
                    @Override
                    public void onSuccess(List<CatelogBmob> list) {
                        mCount.decrementAndGet();
                        if (list == null || list.size() <= 0) {
                            return;
                        }
                        list.get(0).delete(App.this, list.get(0).getObjectId(), new DeleteListener() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                CatelogBmob catelog = new CatelogBmob();
                                catelog.setCatelogId(catelogId);
                                mCatelogDelete.add(catelog);
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        CatelogBmob catelog = new CatelogBmob();
                        catelog.setCatelogId(catelogId);
                        mCatelogDelete.add(catelog);
                        mCount.decrementAndGet();
                    }
                });

    }

    private void handleFailureCatelogUpdate(final CatelogBmob catelog) {
        BmobQuery<CatelogBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(DbHelper.CATELOG_ID, catelog.getCatelogId());
        query.findObjects(this, new FindListener<CatelogBmob>() {
            @Override
            public void onSuccess(List<CatelogBmob> list) {
                mCount.decrementAndGet();
                if (list == null || list.size() <= 0) {
                    return;
                }

                catelog.update(App.this, list.get(0).getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        mCatelogUpdate.add(catelog);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                mCount.decrementAndGet();
                mCatelogUpdate.add(catelog);
            }
        });
    }

    private void handleFailureCatelogAdd(final CatelogBmob catelog) {

        mCount.decrementAndGet();
        catelog.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {
                mCatelogAdd.add(catelog);
            }
        });

    }

    private void handleFailureEvent(int type) {
        DataInputStream in = null;
        try {
            in = new DataInputStream(openFileInput(BmobUser.getCurrentUser(this).getObjectId() + "event" + type));


            long startTime;
            long stopTime;
            long eventId;
            long catelogId;

            while ((startTime = in.readLong()) != -1L)
            {
                stopTime = in.readLong();
                catelogId = in.readLong();
                eventId = in.readLong();

                EventBmob eventBmob = new EventBmob();
                eventBmob.setCatelogId(catelogId);
                eventBmob.setEventId(eventId);
                eventBmob.setStartTime(startTime);
                eventBmob.setStopTime(stopTime);
                eventBmob.setUserId(BmobUser.getCurrentUser(this).getObjectId());

                mCount.incrementAndGet();
                switch (type)
                {
                    case EventUtils.TYPE_DELETE:
                        handleFailureEventDelete(eventBmob);
                        break;
                    case EventUtils.TYPE_ADD:
                        handleFailureEventAdd(eventBmob);
                        break;
                    case EventUtils.TYPE_UPDATE:
                        handleFailureEventUpdate(eventBmob);
                        break;
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(in);
        }
    }

    private void handleFailureEventAdd(final EventBmob eventBmob) {

        mCount.decrementAndGet();
        eventBmob.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onFailure(int i, String s) {

                mEventAdd.add(eventBmob);
            }
        });
    }

    private void handleFailureEventUpdate(final EventBmob eventBmob) {
        BmobQuery<EventBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(DbHelper.EVENT_ID,eventBmob.getEventId());
        query.findObjects(this, new FindListener<EventBmob>() {
            @Override
            public void onSuccess(List<EventBmob> list) {
                mCount.decrementAndGet();
                if (list == null || list.size() <= 0)
                {
                    return;
                }

                eventBmob.update(App.this, list.get(0).getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        mEventUpdate.add(eventBmob);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                mCount.decrementAndGet();
                mEventUpdate.add(eventBmob);
            }
        });
    }

    private void handleFailureEventDelete(final EventBmob eventBmob) {
        BmobQuery<EventBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(DbHelper.EVENT_ID,eventBmob.getEventId());
        query.findObjects(this, new FindListener<EventBmob>() {
            @Override
            public void onSuccess(List<EventBmob> list) {
                mCount.decrementAndGet();
                if (list == null || list.size() <= 0)
                {
                    return;
                }

                list.get(0).delete(App.this, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int i, String s)
                    {
                        mEventDelete.add(eventBmob);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                mCount.decrementAndGet();
                mEventDelete.add(eventBmob);
            }
        });
    }

}
