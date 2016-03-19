package com.willing.android.timeofgun.utils;

import android.content.Context;

import com.willing.android.timeofgun.model.Event;
import com.willing.android.timeofgun.model.EventAndCatelog;
import com.willing.android.timeofgun.model.EventBmob;
import com.willing.android.timeofgun.model.User;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Willing on 2016/3/16.
 */
public class EventUtils
{

    public static final int TYPE_ADD = 1;
    public static final int TYPE_UPDATE = 2;
    public static final int TYPE_DELETE = 3;

    public static void addEvent(final Context context, final Event event) {

        // 保存到本地
        DbHelper.addEvent(context, event);
        addEventToServer(context, event);

    }

    private static void addEventToServer(final Context context, final Event event) {
        // 保存到服务器
        User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null) {
            EventBmob eventBmob = new EventBmob();
            eventBmob.setCatelogId(event.getCatelogId());
            eventBmob.setStartTime(event.getStartTime());
            eventBmob.setStopTime(event.getStopTime());
            eventBmob.setEventId(event.getEventId());
            eventBmob.save(context, new SaveListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i, String s) {
                    // 保存到待处理列表
                    changeEventForServer(context, event, TYPE_ADD);
                }
            });
        }
    }

    // 上传服务器失败时，保存到待处理列表
    private static void changeEventForServer(final Context context, final Event event, final int type) {


                DataOutputStream out = null;
                try {

                    out = new DataOutputStream(context.openFileOutput(BmobUser.getCurrentUser(context).getObjectId() + "event" + type
                            , Context.MODE_APPEND));

                    out.writeLong(event.getStartTime());
                    out.writeLong(event.getStopTime());
                    out.writeLong(event.getCatelogId());
                    out.writeLong(event.getEventId());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    Utils.closeIO(out);
                }

    }

    public static void updateEvent(Context context, Event event) {

        DbHelper.updateEvent(context, event);

        updateEventToServer(context, event);

    }

    private static void updateEventToServer(final Context context, final Event event) {

        BmobQuery<EventBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(DbHelper.EVENT_ID, event.getCatelogId());
        query.findObjects(context, new FindListener<EventBmob>() {
            @Override
            public void onSuccess(List<EventBmob> list) {
                if (list == null || list.isEmpty())
                {
                    return;
                }
                EventBmob eventBmob = list.get(0);
                eventBmob.setStartTime(event.getStartTime());
                eventBmob.setStopTime(event.getStopTime());
                eventBmob.setCatelogId(event.getCatelogId());
                eventBmob.setEventId(event.getEventId());
                eventBmob.setUserId(BmobUser.getCurrentUser(context).getObjectId());
                eventBmob.update(context, eventBmob.getObjectId(), new UpdateListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        changeEventForServer(context, event, TYPE_UPDATE);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                changeEventForServer(context, event, TYPE_UPDATE);
            }
        });
    }

    public static void deleteEventToServer(final Context context, final Event event) {

        BmobQuery<EventBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(DbHelper.EVENT_ID, event.getStartTime());
        query.findObjects(context, new FindListener<EventBmob>() {
            @Override
            public void onSuccess(List<EventBmob> list) {
                if (list == null || list.isEmpty())
                {
                    return;
                }
                final EventBmob eventBmob = list.get(0);
                if (eventBmob == null)
                {
                    return;
                }
                eventBmob.delete(context, eventBmob.getObjectId(), new DeleteListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {

                        changeEventForServer(context, event, TYPE_DELETE);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                changeEventForServer(context, event, TYPE_DELETE);
            }
        });
    }

    public static void deleteEvent(Context context, EventAndCatelog event) {

        DbHelper.deleteEvent(context, event.getId());

        Event e = new Event();
        e.setEventId(event.getEventId());
        e.setStartTime(event.getStartTime());
        e.setStopTime(event.getStopTime());
        e.setCatelogId(event.getCatelog().getCatelogId());
        EventUtils.deleteEventToServer(context, e);
    }
}
