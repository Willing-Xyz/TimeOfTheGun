package com.willing.android.timeofgun.utils;

import android.content.Context;

import com.willing.android.timeofgun.model.Catelog;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Willing on 2016/3/18.
 */
public class CatelogUtils
{
    private static final String CATELOG_FOR_SERVER = "catelog_for_server";

    public static final int TYPE_UPDATE = 1;
    public static final int TYPE_ADD = 2;

    // 添加Catelog到本地系统
    public static void addCatelogToLocal(final Context context, final Catelog catelog) {
        new Thread(){
            @Override
            public void run() {
                DbHelper.addCatelog(context, catelog);
            }
        }.start();
    }

    /**
     * 当没有网络或没有登录时，不能传递数据到服务器。
     * 因此，先保存到文件中，之后在传递到服务器。
     */
    public static void addCatelogForServer(final Context context, final Catelog catelog, final int type) {

        new Thread(){
            @Override
            public void run()
            {
                DataOutputStream out = null;
                try {
                    out = new DataOutputStream(context.openFileOutput(CATELOG_FOR_SERVER, Context.MODE_APPEND));

                    out.writeInt(type);
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

    // 更新Catelog
    public static void updateCatelogToLocal(final Context context, final Catelog catelog) {

        new Thread(){
            @Override
            public void run() {
                DbHelper.updateCatelog(context, catelog);
            }
        }.start();
    }
}
