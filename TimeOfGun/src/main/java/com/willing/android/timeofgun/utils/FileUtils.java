package com.willing.android.timeofgun.utils;

import android.content.Context;

import com.willing.android.timeofgun.model.Catelog;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Willing on 2016/3/15.
 */
public class FileUtils
{
    private static final String CURRENT_CATELOG_FILENAME = "cur_catelog";
    private static final String START_STATE_FILENAME = "start_state";
    private static final String START_TIME_FILENAME = "start_time";

    // 从文件中恢复当前Catelog
    public static Catelog restoreCurrentCatelog(Context context) {
        DataInputStream dataIn = null;
        Catelog catelog = null;
        int id = 0;
        int color = 0;
        String name = null;
        long catelogId = 0;
        try
        {
            InputStream in = context.openFileInput(CURRENT_CATELOG_FILENAME);
            dataIn = new DataInputStream(in);


            id = dataIn.readInt();
            color = dataIn.readInt();
            name = dataIn.readUTF();
            catelogId = dataIn.readLong();

            if (catelog == null)
            {
                catelog = new Catelog(id, name,color, catelogId);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataIn);
        }
        return catelog;
    }

    // 保存当前Catelog到文件中
    public static void saveCurrentCatelog(Context context, Catelog catelog) {
        DataOutputStream dataOut = null;
        try
        {
            OutputStream out = context.openFileOutput(CURRENT_CATELOG_FILENAME, Context.MODE_PRIVATE);
            dataOut = new DataOutputStream(out);

            dataOut.writeInt(catelog.getId());
            dataOut.writeInt(catelog.getColor());
            dataOut.writeUTF(catelog.getName());
            dataOut.writeLong(catelog.getCatelogId());

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataOut);
        }
    }

    // 存储start状态到文件中。
    public static void saveStartState(Context context, boolean started)
    {
        DataOutputStream dataOut = null;
        try
        {
            dataOut = new DataOutputStream(context.openFileOutput(START_STATE_FILENAME, Context.MODE_PRIVATE));
            dataOut.writeBoolean(started);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(dataOut);
        }
    }



    // 从文件中恢复start的状态
    public static boolean restoreStartState(Context context) {
        DataInputStream dataIn = null;
        boolean started = false;
        try
        {
            InputStream in = context.openFileInput(START_STATE_FILENAME);
            dataIn = new DataInputStream(in);

            started = dataIn.readBoolean();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataIn);
        }
        return started;
    }

    // 存储计时开始时间
    public static void saveStartTime(Context context, long startTime)
    {
        DataOutputStream dataOut = null;
        try
        {
            dataOut = new DataOutputStream(context.openFileOutput(START_TIME_FILENAME, Context.MODE_PRIVATE));
            dataOut.writeLong(startTime);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            Utils.closeIO(dataOut);
        }
    }

    // 恢复计时开始时间
    public static long restoreStartTime(Context context) {
        long startTime = 0;
        DataInputStream dataIn = null;
        try
        {
            InputStream in = context.openFileInput(START_TIME_FILENAME);
            dataIn = new DataInputStream(in);

            startTime = dataIn.readLong();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            Utils.closeIO(dataIn);
        }
        return startTime;
    }
}
