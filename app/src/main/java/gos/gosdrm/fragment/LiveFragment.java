package gos.gosdrm.fragment;
import gos.gosdrm.R;
import gos.gosdrm.adapter.ReuseAdapter;
import gos.gosdrm.data.Channel;
import gos.gosdrm.data.CustomVideoView;
import gos.gosdrm.data.PageInfo;
import gos.gosdrm.data.Return;
import gos.gosdrm.tool.HttpUtils;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import okhttp3.Request;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextClock;
import android.text.format.DateFormat;
import android.content.BroadcastReceiver;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import static  gos.gosdrm.define.PlayerUrl.*;

public class LiveFragment extends Fragment{
    private final String TAG = getClass().getSimpleName();

    private WlanReceiver wlanReceiver;//监听WLAN状态
    private View view;
    private ImageView importFile;

    HttpUtils httpUtils = HttpUtils.getInstance();  //获取http实例

    private ReuseAdapter<Channel> channelAdapter;
    private ListView channelListView;   //频道listView
    private int channelCounter = 0;   //获取频道列表失败计数器

    public CustomVideoView mVideoView;
    private MediaController mediaController;

    private boolean isMediaPause = false;//播放器是否被暂停
    private boolean isFileImport = false;//是否为文件导入

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "进入onCreateView()方法");
        if(null != view){
            Log.e(TAG, "view复用");
            return view;
        }
        view = inflater.inflate(R.layout.fragment_live,container, false);

        initView();
        initData();

        return view;
    }

    /**
     * 初始化wifi
     */
    private void initWifi(){
        wlanReceiver = new WlanReceiver();
        IntentFilter itf = new IntentFilter();
        itf.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//无线状态
        getActivity().registerReceiver(wlanReceiver, itf);//注册广播
    }

    /**
     * 初始化文件导入
     */
    private void initImportFile(){
        ImageView importFile = view.findViewById(R.id.importFile);
        importFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"文件导入");
                getAllChannelByFile();
                isFileImport = true;
            }
        });
    }

    /**
     * 初始化网络导入
     */
    private void initImportNet(){
        ImageView importNet = view.findViewById(R.id.importNet);
        importNet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG,"网络导入");
                getAllChannel();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = getActivity().managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            File file = new File(img_path);
            Toast.makeText(getContext(), file.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 当fragment可见状态改变时回调
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){ //隐藏
            Log.e(TAG,"onPause");
            if(mVideoView.isPlaying()){
                mVideoView.pause();
                isMediaPause = true;
                Log.e(TAG,"onPause --暂停播放器");
            }
        }else { //显示

            Log.e(TAG,"onResume");
            if(isMediaPause){
                Log.e(TAG,"onResume --开始播放器");
                mVideoView.start();
            }
        }
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

    public void getAllChannelByFile() {
        channelAdapter.resetAll(importXlsFile());
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
        initImportFile();
        initImportNet();
    }

    private void initData() {
        initWifi();
        setTime(view);//初始化时间

        getAllChannelByFile();
        //getAllChannel();
    }

    /**
     * 初始化播放器
     */
    private void initVideoView() {
        mVideoView =  view.findViewById(R.id.live_videoView);
        mVideoView.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        mVideoView.clearFocus();
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(getContext(), "播放错误，错误码:"+i, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //播放器控制
      /*  mediaController = new MediaController(getActivity());
        mVideoView.setMediaController(mediaController);
        mediaController.setEnabled(false);*/

        /*mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setOnInfoListener(this);*/
    }

    /**
     * 初始化频道列表
     */
    private void initChannelView() {
        channelListView = view.findViewById(R.id.live_channelList);
        channelAdapter = new ReuseAdapter<Channel>(getActivity(), R.layout.item_channel) {
            @Override
            public void bindView(Holder holder, Channel obj) {
                holder.setText(R.id.live_channelName, obj.getChannelName());
            }
        };
        channelListView.setAdapter(channelAdapter);
        //选择 预览播放
        channelListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Channel channel = channelAdapter.getItem(i);

                if(isFileImport){
                    startPreviewPlay(channel.getLiveUrl());
                }else {
                    startPreviewPlay(formatUrl(channel));
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //点击 全屏播放
        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Channel channel = channelAdapter.getItem(i);
                if(isFileImport){
                    startFullPlay(channel.getLiveUrl());
                }else {
                    startFullPlay(formatUrl(channel));
                }

            }
        });
    }
    //url格式转换
    private String formatUrl(Channel channel){
        String urlFormat = "http://192.168.1.84:1935/live/_definst_/stream/%s.stream/playlist.m3u8";
        String url = (String.format(urlFormat,channel.getChannelName())).replaceAll(" ","");
        Log.e(TAG,"url:"+url);
        return url;
    }

    /**
     * 全屏播放
     * @param url
     */
    private void startFullPlay(String url){
        Log.e(TAG,"播放地址:"+url);
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
        Log.e(TAG,"播放地址:"+url);
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
                isFileImport = false;
                ArrayList<Channel> channels = ret.getBody().getItems();
                resetChannelListView(channels);
            }
            @Override
            public void failed(Request request, IOException e) {
                Log.e(TAG, "获取频道列表失败,错误信息："+e.getMessage());
                if(++channelCounter <3){
                    Log.e(TAG, "重新获取频道");
                    getAllChannel();
                }
                //e.printStackTrace();
            }
        });
    }

    /**
     * 重置频道列表数据
     * @param channels
     */
    private void resetChannelListView(ArrayList<Channel> channels){
        Log.e(TAG, "频道列表的长度：" + channels.size());
        channelAdapter.resetAll(channels);
        //channelListView.requestFocus();
    }

    /**
     * 导入文件中的节目
     * @return
     */
    private ArrayList<Channel> importXlsFile(){
        isFileImport = true;
        ArrayList<Channel> channels =null;
        try {
            InputStream is = new FileInputStream("/sdcard/ec.xls");
            try {
                Workbook workbook = Workbook.getWorkbook(is);
                int num = workbook.getNumberOfSheets();
                Log.e(TAG,"num:"+num);

                Sheet sheet = workbook.getSheet(0);
                int rows = sheet.getRows();
                int cols = sheet.getColumns();
                Log.e(TAG,"rows:"+rows);
                Log.e(TAG,"cols:"+cols);

                channels = new ArrayList<>();
                for(int r=1;r<rows;r++){//跳过标题
                    Channel channel = new Channel();
                    channel.setChannelName(sheet.getCell(0,r).getContents());
                    channel.setLiveUrl(sheet.getCell(2,r).getContents());
                    Log.e(TAG,"getChannelName:"+channel.getChannelName());
                    Log.e(TAG,"getLiveUrl:"+channel.getLiveUrl());
                    channels.add(channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BiffException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(), "不存在/sdcard/ec.xls文件，请传入！ ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {
            return channels;
        }

    }
}