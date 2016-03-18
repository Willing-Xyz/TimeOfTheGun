package com.willing.android.timeofgun.utils;

import android.content.Context;

import com.willing.android.timeofgun.model.Event;
import com.willing.android.timeofgun.model.EventBmob;
import com.willing.android.timeofgun.model.User;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Willing on 2016/3/16.
 */
public class EventUtils
{
    private static final String EVENT_FOR_SERVER = "event_for_server";

    public static void addEvent(final Context context, final Event event) {

        // 保存到本地
        DbHelper.addEvent(context, event);
        addToServer(context, event);

    }

    private static void addToServer(final Context context, final Event event) {
        // 保存到服务器
        User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null) {
            EventBmob eventBmob = new EventBmob();
            eventBmob.setCatelogId(event.getCatelogId());
            eventBmob.setStartTime(event.getStartTime());
            eventBmob.setStopTime(event.getStopTime());
            eventBmob.save(context, new SaveListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i, String s) {
                    // 保存到待处理列表
                    addEventForServer(context, event);
                }
            });
        }
        else
        {
            addEventForServer(context, event);
        }
    }

    // 上传服务器失败时，保存到待处理列表
    private static void addEventForServer(final Context context, final Event event) {

        new Thread(){
            @Override
            public void run()
            {
                DataOutputStream out = null;
                try {
                    out = new DataOutputStream(context.openFileOutput(EVENT_FOR_SERVER, Context.MODE_APPEND));

                    out.writeLong(event.getStartTime());
                    out.writeLong(event.getStopTime());
                    out.writeLong(event.getCatelogId());

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

    public static void updateEvent(Context context, Event event) {

        DbHelper.updateEvent(context, event);

        addToServer(context, event);

    }
}
