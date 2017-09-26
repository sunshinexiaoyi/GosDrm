package gos.gosdrm.fragment;
import gos.gosdrm.R;
import gos.gosdrm.adapter.ChannelAdapter;
import gos.gosdrm.data.Channel;
import gos.gosdrm.data.PageInfo;
import gos.gosdrm.data.Return;
import gos.gosdrm.tool.HttpUtils;
import okhttp3.Request;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextClock;
import android.text.format.DateFormat;
import android.content.BroadcastReceiver;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class LiveFragment extends Fragment{
    private WlanReceiver wlanReceiver;//监听WLAN状态
    private View view;

    private String TAG = getClass().getSimpleName();
    private String url = "http://192.168.1.84:8090/ottserver/IPLiveInfo/getAllChannel?clientId=boss00042&pageSize=20&pageNumber=1";
    private HttpUtils httpUtils = HttpUtils.getInstance();

    private ChannelAdapter<Channel> channelAdapter;
    private ListView channelListView;   //频道listView

    private ArrayList<Channel> channels;//频道列表
    private boolean isGetChannel = false;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("live的消息", "进入onCreateView()方法");
        view = inflater.inflate(R.layout.fragment_live,container, false);

        wlanReceiver = new WlanReceiver();
        IntentFilter itf = new IntentFilter();
        itf.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//无线状态
        getActivity().registerReceiver(wlanReceiver, itf);//注册广播
        handler = new Handler();

        setTime(view);//初始化时间
        initChannelView();//初始化频道列表
        new Thread() {
            public void run() {
                while (!getAllChannel()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException sleepError) {}
                }
                Thread.interrupted();
                Log.e("消息", "获取频道列表的线程死亡");
            }
        }.start();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(wlanReceiver);//取消WLAN广播捕获
    }

    //设置时间日期
    public void setTime(View view) {
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

    //初始化频道列表
    private void initChannelView() {
        channelListView = view.findViewById(R.id.live_channelList);
        channelAdapter = new ChannelAdapter<Channel>(getActivity(), R.layout.item_channel) {
            @Override
            public void bindView(Holder holder, Channel obj) {
                holder.setText(R.id.live_channelName, obj.getChannelName());
            }
        };
        channelListView.setAdapter(channelAdapter);
        
        //频道列表被选中事件
        channelListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                //播放预览
                //Channel channel = channelAdapter.getItem(i);
                //startPlay(formatUrl(channel));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        //频道列表被点击事件
        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            boolean foldThis;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                startPlayer(formatUrl(channelAdapter.getItem(pos)));
            }
        });
    }
    //url格式转换
    private String formatUrl(Channel channel){
        String urlFormat = "http://192.168.1.84:1935/live/_definst_/stream/%s.stream/playlist.m3u8";
        String url = (String.format(urlFormat,channel.getChannelName())).replaceAll(" ","");
        Log.e(TAG,"得到URL:"+url);
        return url;
    }
    //启动播放器
    private void startPlayer(String url){
        Log.e("消息", "启动播放器");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String type = "video/*";
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri, type);
        startActivity(intent);
    }
    //获取所有频道
    private boolean getAllChannel(){
        httpUtils.get(url,new HttpUtils.Back<Return<PageInfo<Channel>>>(){
            @Override
            public void success( Return<PageInfo<Channel>> ret) {
                channels = ret.getBody().getItems();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetChannelListView(channels);
                    }
                });
                isGetChannel = true;
            }
            @Override
            public void failed(Request request, IOException e) {
                Log.e(TAG, "获取频道列表失败");
                e.printStackTrace();
                isGetChannel = false;
            }
        });
        return isGetChannel;
    }

    //重置频道列表数据
    private void resetChannelListView(ArrayList<Channel> channels){
        Log.e(TAG, "频道列表的长度：" + channels.size());
        channelAdapter.addAll(channels);
        channelListView.requestFocus();
    }
}