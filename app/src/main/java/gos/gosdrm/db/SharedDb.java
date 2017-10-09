package gos.gosdrm.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

import gos.gosdrm.data.JsonParse;
import gos.gosdrm.data.SetSource;
import gos.gosdrm.data.SetTheme;
import gos.gosdrm.data.User;


/**
 * 共享数据库
 * Created by wuxy on 2017/8/11.
 */

public class SharedDb {
    private static SharedDb sharedDb;

    private final String DB = "drm_db";   //用户数据库文件

    private final String KEY_USER = "user";     //用户
    private final String KEY_SOURCE = "source"; //节目数据源
    private final String KEY_THEME = "theme";   //应用主题


    private Context context;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;


    private User user;
    private SetSource setSource;
    private SetTheme setTheme;

    private SharedDb(Context context) {
        this.context = context;

        sp = context.getSharedPreferences(DB, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public static SharedDb getInstance(Context context){
        if(null == sharedDb){
            sharedDb = new SharedDb(context);
        }

        return sharedDb;
    }

    public User getUser(){
        if(null != user){
            return user;
        }

        String jsonStr = sp.getString(KEY_USER,null);

        if(null != jsonStr){
            return JSON.parseObject(jsonStr,User.class);
        }

        return new User();
    }

    public void setUser(User user){
        String jsonStr = JSON.toJSONString(user);

        editor.putString(KEY_USER,jsonStr);
        editor.apply();

        this.user = user;
    }


    public SetSource getSetSource() {
        if(null != setSource){  //返回缓存
            return  setSource;
        }

        String jsonStr = sp.getString(KEY_SOURCE,null);
        if(null != jsonStr){    //返回保存
            return JSON.parseObject(jsonStr,SetSource.class);
        }

        return new SetSource();//返回默认
    }

    public void setSetSource(SetSource setSource) {
        this.setSource = setSource;
        editor.putString(KEY_SOURCE,JSON.toJSONString(setSource));
        editor.apply();

    }

    public SetTheme getSetTheme() {
        if(null != setTheme){
            return setTheme;
        }

        String jsonStr = sp.getString(KEY_THEME,null);
        if(null != jsonStr){
            return JSON.parseObject(jsonStr,SetTheme.class);
        }

        return new SetTheme();
    }

    public void setSetTheme(SetTheme setTheme) {
        this.setTheme = setTheme;

        editor.putString(KEY_THEME,JSON.toJSONString(setTheme));
        editor.apply();
    }

}
