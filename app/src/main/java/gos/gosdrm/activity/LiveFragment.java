package gos.gosdrm.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import gos.gosdrm.R;

public class LiveFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("live的消息", "进入onCreate()方法");

        //设置时间和日期
        setTime(inflater, container);
        //获取节目列表
        //获取节目epg
        //设置焦点监听
        //播放指定节目
        //检测wlan的状态
        //搜索框的实现

        return inflater.inflate(R.layout.live_main, container, false);
    }

    //设置时间日期
    public void setTime(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.live_main,container, false);
        TextClock time = (TextClock)view.findViewById(R.id.live_time);
        TextClock date = (TextClock)view.findViewById(R.id.live_date);
        TextClock week = (TextClock)view.findViewById(R.id.live_week);
        time.setFormat24Hour("hh:mm");//设置时间
        date.setFormat24Hour("yyyy-MM-dd");//设置日期
        date.setFormat24Hour("EEEE");//设置星期
        Log.e("设置时间日期消息", "设置完成");
    }
}