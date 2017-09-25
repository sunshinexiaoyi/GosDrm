package gos.gosdrm.activity;
import gos.gosdrm.R;
import gos.gosdrm.adapter.MyAdapter;
import gos.gosdrm.data.Channel;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.text.format.DateFormat;
import android.content.BroadcastReceiver;
import android.widget.Toast;

import java.util.ArrayList;

public class LiveFragment extends Fragment implements View.OnFocusChangeListener{
    private WlanReceiver wlanReceiver;//监听WLAN状态
    private View view;

    private MyAdapter<Channel> myAdapter;
    private ArrayList<Channel> channelData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("live的消息", "进入onCreate()方法");
        view = inflater.inflate(R.layout.live_main,container, false);

        wlanReceiver = new WlanReceiver();
        IntentFilter itf = new IntentFilter();
        itf.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//无线状态
        getActivity().registerReceiver(wlanReceiver, itf);//注册广播

        setTime(view, inflater, container);//初始化时间

        initList();//初始化适配器
        //设置焦点监听
        //获取节目列表
        //获取节目epg
        //播放指定节目
        //搜索框的改良
        return view;
    }

    //监听焦点改变检测
    @Override
    public void onFocusChange(View view, boolean bl) {}
    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(wlanReceiver);//取消WLAN广播捕获
    }

    //设置时间日期
    public void setTime(View view, LayoutInflater inflater, ViewGroup container) {
        boolean is24Hours = DateFormat.is24HourFormat(getActivity());
        if (!is24Hours) {
            TextClock time = (TextClock) view.findViewById(R.id.live_time);
            TextClock date = (TextClock) view.findViewById(R.id.live_date);
            TextClock week = (TextClock) view.findViewById(R.id.live_week);
            time.setFormat12Hour("hh:mm");//设置时间
            date.setFormat12Hour("yyyy-MM-dd");//设置日期
            week.setFormat12Hour("EEEE");//设置星期
            Log.e("调整时间日期消息", "已经修正时间格式为12小时制");
        }
    }
    //设置无线检测
    public class WlanReceiver extends BroadcastReceiver {
        private NetworkInfo networkInfo;
        private ImageView imageView;
        private WifiManager wifiManager;
        private WifiInfo wifiInfo;
        private boolean isConnected = false;//防止状态改变时过早检测断开与连接
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);//获取wlan状态
                if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    if (isConnected == true) {
                        Toast.makeText(getActivity(), "WLAN网络被断开", Toast.LENGTH_SHORT).show();//吐司wlan断开
                        imageView = (ImageView) view.findViewById(R.id.live_wlan);
                        imageView.setImageResource(R.drawable.live_wlan_disconnect);//改变图标状态
                    }
                    isConnected = false;

                } else if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    if (isConnected == false) {
                        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiInfo = wifiManager.getConnectionInfo();
                        Toast.makeText(getActivity(), "WLAN网络已连接：" + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();//吐司wlan断开
                        imageView = (ImageView) view.findViewById(R.id.live_wlan);
                        imageView.setImageResource(R.drawable.live_wlan_connected);//改变图标状态
                    }
                    isConnected = true;
                }
            }
        }
    }
    //初始化适配器
    public void initList() {
        ListView progList = (ListView)view.findViewById(R.id.live_list_prog);//频道列表item
        myAdapter = new MyAdapter<Channel>(getActivity(), R.layout.live_proglist_item) {
            @Override
            public void bindView(Holder holder, Channel obj) {}
        };
        progList.setAdapter(myAdapter);
    }
}