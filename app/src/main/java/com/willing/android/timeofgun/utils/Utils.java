package com.willing.android.timeofgun.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.Closeable;
import java.io.IOException;

import cn.bmob.v3.BmobUser;

/**
 * Created by Willing on 2016/3/15.
 */
public class Utils
{
    public static final String NOUSER = "nouser";

    public static void closeIO(Closeable io)
    {
        try {
            if (io != null) {
                io.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符

        char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };

        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray =new char[byteArray.length * 2];


        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;

        for (byte b : byteArray) {

            resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];

            resultCharArray[index++] = hexDigits[b& 0xf];

        }

        // 字符数组组合成字符串返回

        return new String(resultCharArray);
    }

    public static String getDbName(Context context)
    {
        BmobUser user = BmobUser.getCurrentUser(context);
        if (user == null)
        {
            return NOUSER;
        }
        else
        {
            return user.getObjectId();
        }
    }
}
