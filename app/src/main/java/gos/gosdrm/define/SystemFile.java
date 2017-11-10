package gos.gosdrm.define;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by wuxy on 2017/10/20.m
 */

public class SystemFile {
     public final static String vodFile = "vodList.xls";    //点播文件名
     public final static String liveFile = "liveList.xls";  //直播文件名
     public final static String settingFile = "DrmServiceCfg.xml";  //DA设置文件名

     final static String local = "/mnt/sdcard/";        //本地存储路径
     final static String uDisk = "/storage/sda1/import/";      //U盘存储路径

    /**
     * 获取对应文件的 本地路径
     * @param file  对应文件
     * @return
     */
    public static String getLocalPath(String file){
        return local+file;
    }

    /**
     *  获取对应文件的 U盘路径
     * @param file  对应文件
     * @return
     */
    public static String getUDiskPath(String file){
        return uDisk+file;
    }

    /**
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(Context context,String oldPath, String newPath) {
        String msg = "";
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                Log.e(TAG,oldPath+" 文件存在");
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                //RandomAccessFile raf = new RandomAccessFile(oldfile, "rw");
                File newFile = new File(newPath);
                if(newFile.exists()){
                    newFile.delete();
                }
                FileOutputStream fs = new FileOutputStream(newPath);

                byte[] buffer = new byte[1444];
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();

                msg = "将 "+oldPath+" 拷贝到 "+ newPath+" 成功!";
                Log.e(TAG,msg);
                if(null != context){
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

                return true;
            }else {
                msg = oldPath+"  文件不存在";
                Log.e(TAG,msg);
                if(null != context){
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            msg = oldPath+"  复制单个文件操作出错";
            Log.e(TAG,msg);
            if(null != context){
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }

        }

        return false;
    }

    public static boolean copyFile(String oldPath, String newPath) {
        return copyFile(null,oldPath,newPath);
    }

}
