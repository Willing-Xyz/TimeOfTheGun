package com.willing.android.timeofgun.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Willing on 2016/3/15.
 */
public class Utils
{
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
}
