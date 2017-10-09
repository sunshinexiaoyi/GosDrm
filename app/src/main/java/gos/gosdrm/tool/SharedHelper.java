package gos.gosdrm.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import gos.gosdrm.R;

//SP数据保存
public class SharedHelper {
    private Context mContext;
    private SharedPreferences sp;

    public SharedHelper(Context mContext) {
        sp = mContext.getSharedPreferences(mContext.getResources().getString(R.string.file_name), Context.MODE_PRIVATE);
        this.mContext = mContext;
        SharedPreferences.Editor editor = sp.edit();//添加频道源类型
        editor.putString("autoResource", "0");//加入默认视频源记录   0：网络源   1：本地源
        editor.putString("backgroundRes", "0");//加入背景选择记录   0：高斯贝尔  1：酷黑
    }

    //定义一个保存数据的方法
    public void save(String username, String passwd) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("passwd", passwd);
        editor.apply();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> read() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("username", sp.getString("username", ""));
        data.put("passwd", sp.getString("passwd", ""));
        return data;
    }

    //删除数据
    public void del() {
        String editname;
        String editpasswd;
        SharedPreferences.Editor editor = sp.edit();
        SharedHelper sh = new SharedHelper(mContext);
        Map<String,String> data = sh.read();
        editname = data.get("username");
        editpasswd = data.get("passwd");
        Log.e("记住的用户名为：","name:"+editname);

        if (editname.equals("") && editpasswd.equals("")) {
            Log.e("用户名和密码都为空", "不做处理");
        } else {
            editor.putString("username", "");
            editor.putString("passwd", "");
            editor.apply();

            Log.e("清空后的用户名为","name:"+editname);
        }
    }

    //添加一个数据对
    public void add(String key, String data) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, data);
        editor.apply();
    }

    //读取一个数据对
    public String get(String key) {
        return sp.getString(key, "");
    }

    public void change(String key, String data) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, data);
        editor.apply();
    }
}
