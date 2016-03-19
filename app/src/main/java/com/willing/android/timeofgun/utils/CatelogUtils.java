package com.willing.android.timeofgun.utils;

import android.content.Context;

import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.CatelogBmob;
import com.willing.android.timeofgun.model.User;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Willing on 2016/3/18.
 */
public class CatelogUtils
{

    public static final int TYPE_UPDATE = 1;
    public static final int TYPE_ADD = 2;
    public static final int TYPE_DELETE = 3;

    public static void addCatelog(final Context context, final Catelog catelog)
    {
        // 保存到本地
        addCatelogToLocal(context, catelog);
        // 保存到服务器
        addToServer(context, catelog);
    }

    private static void addToServer(final Context context, final Catelog catelog) {
        // 保存到服务器
        User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null) {
            CatelogBmob catelogBmob = new CatelogBmob();
            catelogBmob.setCatelogColor(catelog.getColor());
            catelogBmob.setCatelogName(catelog.getName());
            catelogBmob.setUserId(user.getObjectId());
            catelogBmob.setCatelogId(catelog.getCatelogId());

            catelogBmob.save(context, new SaveListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int i, String s) {

                    CatelogUtils.changeCatelogForServer(context, catelog, CatelogUtils.TYPE_ADD);
                }
            });
        }
    }

    // 添加Catelog到本地系统
    public static void addCatelogToLocal(final Context context, final Catelog catelog) {

        DbHelper.addCatelog(context, catelog);
    }

    /**
     * 当没有网络时，不能传递数据到服务器。
     * 因此，先保存到文件中，之后在传递到服务器。
     */
    public static void changeCatelogForServer(final Context context, final Catelog catelog, final int type) {


                DataOutputStream out = null;
                try {
                    out = new DataOutputStream(context.openFileOutput(BmobUser.getCurrentUser(context).getObjectId() + "catelog" + type
                            , Context.MODE_APPEND));

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

    public static void updateCatelog(Context context, Catelog catelog)
    {
        // 保存到本地
        updateCatelogToLocal(context, catelog);
        // 保存到服务器
        updateCatelogToServer(context, catelog);

    }

    // 更新Catelog
    public static void updateCatelogToLocal(final Context context, final Catelog catelog) {

                DbHelper.updateCatelog(context, catelog);
    }

    public static void updateCatelogToServer(final Context context, final Catelog catelog)
    {
        final User user = BmobUser.getCurrentUser(context, User.class);
        if (user != null) {

            BmobQuery<CatelogBmob> query = new BmobQuery<>();
            query.addWhereEqualTo(DbHelper.CATELOG_ID, catelog.getCatelogId());
            query.findObjects(context, new FindListener<CatelogBmob>() {
                @Override
                public void onSuccess(List<CatelogBmob> list) {
                    if (list == null || list.isEmpty())
                    {
                        return;
                    }
                    CatelogBmob catelogBmob = list.get(0);
                    catelogBmob.setCatelogColor(catelog.getColor());
                    catelogBmob.setCatelogName(catelog.getName());
                    catelogBmob.setCatelogId(catelog.getCatelogId());
                    catelogBmob.setUserId(user.getObjectId());
                    catelogBmob.update(context, catelogBmob.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int i, String s) {
                            changeCatelogForServer(context, catelog, TYPE_UPDATE);
                        }
                    });
                }

                @Override
                public void onError(int i, String s) {
                    changeCatelogForServer(context, catelog, TYPE_UPDATE);
                }
            });
        }
    }

    public static void deleteCatelogs(Context context, ArrayList<Long> catelogs) {

        deleteCatelogsToLocal(context, catelogs);
        deleteCatelogsToServer(context, catelogs);
    }


    public static void deleteCatelogsToLocal(Context context, ArrayList<Long> catelogs)
    {
        DbHelper.deleteCatelogs(context, catelogs);
    }

    public static void deleteCatelogsToServer(final Context context, final ArrayList<Long> catelogs) {

        for (int i = 0; i < catelogs.size(); ++i)
        {
            deleteCatelogToServer(context, catelogs.get(i));
        }
    }

    public static void deleteCatelogToServer(final Context context, final long catelogId)
    {
        BmobQuery<CatelogBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(DbHelper.CATELOG_ID, catelogId);
        query.findObjects(context, new FindListener<CatelogBmob>() {
            @Override
            public void onSuccess(List<CatelogBmob> list) {
                if (list == null || list.isEmpty())
                {
                    return;
                }
                final CatelogBmob catelogBmob = list.get(0);

                catelogBmob.delete(context, catelogBmob.getObjectId(), new DeleteListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Catelog catelog = new Catelog(catelogBmob.getCatelogName(), catelogBmob.getCatelogColor(), catelogBmob.getCatelogId());
                        changeCatelogForServer(context, catelog, TYPE_DELETE);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                Catelog catelog = new Catelog("", 0,catelogId);
                changeCatelogForServer(context, catelog, TYPE_DELETE);
            }
        });
    }
}
