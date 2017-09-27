package gos.gosdrm.fragment;
import gos.gosdrm.R;
import gos.gosdrm.adapter.ReuseAdapter;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
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
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import static  gos.gosdrm.define.PlayerUrl.*;

public class LiveFragment extends Fragment implements View.OnFocusChangeListener{
    private WlanReceiver wlanReceiver;//监听WLAN状态
    private View view;

    private final String TAG = getClass().getSimpleName();

    HttpUtils httpUtils = HttpUtils.getInstance();

    ListView channelListView;   //频道listView
    ReuseAdapter<Channel> channelAdapter; //频道适配器

    public VideoView mVideoView;

    private boolean mediaIsPause = false;//播放器是否被暂停

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("live的消息", "进入onCreate()方法");
        view = inflater.inflate(R.layout.fragment_live,container, false);

        wlanReceiver = new WlanReceiver();
        IntentFilter itf = new IntentFilter();
        itf.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//无线状态
        getActivity().registerReceiver(wlanReceiver, itf);//注册广播

        setTime(view, inflater, container);//初始化时间


        initView();
        initData();

        //设置焦点监听
        //获取节目列表
        //获取节目epg
        //播放指定节目
        //搜索框的改良
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mediaIsPause){
            Log.i(TAG,"onResume --开始播放器");
            mVideoView.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mVideoView.isPlaying()){
            mVideoView.pause();
            mediaIsPause = true;
            Log.i(TAG,"onPause --暂停播放器");
        }
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

    private void initView(){
        initChannelView();
        initVideoView();
    }

    private void initData() {
        getAllChannel();
    }

    /**
     * 初始化播放器
     */
    private void initVideoView() {
        mVideoView =  view.findViewById(R.id.videoView);
        /*mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnInfoListener(this);*/
    }

    /**
     * 初始化频道列表
     */
    private void initChannelView() {
        channelListView = view.findViewById(R.id.listViewChannel);
        channelAdapter = new ReuseAdapter<Channel>(R.layout.item_channel) {
            @Override
            public void bindView(ViewHolder holder, Channel obj) {
                TextView channelName = holder.getView(R.id.channelName);
                channelName.setText(obj.getChannelName());
            }
        };
        channelListView.setAdapter(channelAdapter);
        //选择 预览播放
        channelListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Channel channel = channelAdapter.getItem(i);
                if(mpdUrl.length>i){
                    startPreviewPlay(mpdUrl[i]);
                }else {
                    startPreviewPlay(formatUrl(channel));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //点击 全屏播放
        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Channel channel = channelAdapter.getItem(i);
                if(mpdUrl.length>i){
                    startFullPlay(mpdUrl[i]);
                }else {
                    startFullPlay(formatUrl(channel));
                }
                //startFullPlay(formatUrl(channel));
            }
        });

    }

    private String formatUrl(Channel channel){
        String urlFormat = "http://192.168.1.84:1935/live/_definst_/stream/%s.stream/playlist.m3u8";
        String url = (String.format(urlFormat,channel.getChannelName())).replaceAll(" ","");
        return url;
    }

    /**
     * 全屏播放
     * @param url
     */
    private void startFullPlay(String url){
        Log.i(TAG,"播放地址:"+url);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String type = "video/*";
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri, type);
        startActivity(intent);
    }

    /**
     * 小窗口预览播放
     * @param url
     */
    private void startPreviewPlay(String url){
        if(mVideoView.isPlaying()){
            mVideoView.stopPlayback();
        }
        Log.i(TAG,"播放地址:"+url);
        mVideoView.setVideoPath(url);
        mVideoView.start();
    }

    /**
     * 获取所有频道
     */
    private void getAllChannel(){
        httpUtils.get(channelRequestUrl,new HttpUtils.Back<Return<PageInfo<Channel>>>(){
            @Override
            public void success( Return<PageInfo<Channel>> ret) {
                ArrayList<Channel> channels = ret.getBody().getItems();
                resetChannelListView(channels);
            }
            @Override
            public void failed(Request request, IOException e) {
                e.printStackTrace();
                Log.i(TAG, "IOException：");
            }
        });
    }

    /**
     * 重置频道列表数据
     * @param channels
     */
    private void resetChannelListView(ArrayList<Channel> channels){
        channelAdapter.reset(channels);
        channelListView.requestFocus();
    }
}