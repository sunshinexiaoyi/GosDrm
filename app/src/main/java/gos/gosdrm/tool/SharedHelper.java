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
}
