package gos.gosdrm.fragment;

import gos.gosdrm.R;
import gos.gosdrm.activity.MainActivity;
import gos.gosdrm.adapter.ReuseAdapter;
import gos.gosdrm.data.Channel;
import gos.gosdrm.data.CustomVideoView;
import gos.gosdrm.data.ReturnChannel;
import gos.gosdrm.data.SetSource;
import gos.gosdrm.db.SharedDb;
import gos.gosdrm.define.SystemFile;
import gos.gosdrm.tool.HttpUtils;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextClock;
import android.text.format.DateFormat;
import android.widget.TextView;
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

    //private WlanReceiver wlanReceiver;//监听WLAN状态
    private Context context;
    private View view;

    private HttpUtils httpUtils = HttpUtils.getInstance();  //获取http实例

    private ReuseAdapter<Channel> channelAdapter;
    private ListView channelListView;   //频道listView
    private int channelCounter = 0;   //获取频道列表失败计数器

    private CustomVideoView mVideoView;//自定义VideoView，目前仅实现测量视频画面尺寸方法

    private boolean isMediaPause = false;   //播放器是否被暂停
    private Channel selectedChannel;        //选择的频道

    private View layout;//频道导入类型变化强迫背景更换

    private boolean fragmentHidden;//fragment是否隐藏

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "进入onCreateView()方法");
        if(null != view){
            return view;
        }

        view = inflater.inflate(R.layout.fragment_live,container, false);
        context = getContext();
        initView();//View部分初始化
        initData();//数据部分初始化
        return view;
    }


    //初始化文件导入
    private void initImportFile(){
        layout = (View)view.findViewById(R.id.live_CHANNELLIST);
        TextView importFile = view.findViewById(R.id.live_importLocal);
        importFile.setOnClickListener(new View.OnClickListener() {
            boolean foldIt;
            @Override
            public void onClick(View view) {
                Log.e(TAG,"本地源导入");
                layout.setBackgroundResource(R.drawable.live_channel_bg_importlocal);//更改频道列表背景图
                getVodAllChannel();
            }
        });
    }

    //初始化网络导入
    private void initImportNet(){
        layout = (View)view.findViewById(R.id.live_CHANNELLIST);
        TextView importNet = view.findViewById(R.id.live_importNet);
        importNet.setOnClickListener(new View.OnClickListener() {
            boolean foldIt;
            @Override
            public void onClick(View view) {
                Log.e(TAG,"网络源导入");
                layout.setBackgroundResource(R.drawable.live_channel_bg_importnet);//更改频道列表背景图
                getLiveAllChannel();
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
        fragmentHidden = hidden;
        if(hidden){ //隐藏
            pauseMedia();
        }else { //显示
            resumeMedia();
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG,"** onPause **");
        pauseMedia();
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.e(TAG,"** onResume **");
        if(!fragmentHidden){
            resumeMedia();
        }
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //getActivity().unregisterReceiver(wlanReceiver);//取消WLAN广播捕获
    }

    //设置时间日期
    public void setTime(View view) {
        boolean is24Hours = DateFormat.is24HourFormat(getActivity());
        if (!is24Hours) {
            TextClock time =  view.findViewById(R.id.live_time);
            TextClock date =  view.findViewById(R.id.live_date);
            TextClock week =  view.findViewById(R.id.live_week);
            if(null != time){
                time.setFormat12Hour("hh:mm");//设置时间
                date.setFormat12Hour("yyyy-MM-dd");//设置日期
                week.setFormat12Hour("EEEE");//设置星期
                Log.e("调整时间日期消息", "已经修正时间格式为12小时制");
            }else {
                Log.e(TAG, "null == time");

            }
        }
    }


    public void getVodAllChannel(){
        resetChannelListView(new ArrayList<Channel>());//重置
        channelAdapter.resetAll(importXlsFile(SystemFile.getLocalPath(SystemFile.vodFile)));
        MainActivity.isChannelEmpty = channelAdapter.isEmpty() ? true : false;//保存列表内容状态
    }

    public void getLiveAllChannel(){
        resetChannelListView(new ArrayList<Channel>());//重置
        channelAdapter.resetAll(importXlsFile(SystemFile.getLocalPath(SystemFile.liveFile)));
        MainActivity.isChannelEmpty = channelAdapter.isEmpty() ? true : false;//保存列表内容状态
    }

    private void initView(){
        initChannelView();
        initVideoView();
        initImportFile();
        initImportNet();
    }

    //初始化集合
    private void initData() {
        //initWifi();
        setTime(view);//初始化时间

        /**lyx：
         * 默认频道源自动切换
         * 同时强迫列表背景跟随
         */
        View channelListBg = view.findViewById(R.id.live_CHANNELLIST);//得到频道列表view
        SharedDb sharedDb = SharedDb.getInstance(getActivity());
        switch (sharedDb.getSetSource().getSource()){
            case SetSource.NETWORK:
                channelListBg.setBackgroundResource(R.drawable.live_channel_bg_importnet);//强迫背景跟随
                getLiveAllChannel();
                break;
            case SetSource.LOCAL:
                channelListBg.setBackgroundResource(R.drawable.live_channel_bg_importlocal);//强迫背景跟随
                getVodAllChannel();
                break;
            default:
                Toast.makeText(getContext(), "设置默认频道源错误", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //初始化播放器
    private void initVideoView() {
        mVideoView =  view.findViewById(R.id.live_videoView);
        mVideoView.clearFocus();
        //重写播放器的错误监听以消除播放失败弹出对话框
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                String errorInfo = "";
                switch (i){
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                        errorInfo="发生未知错误";
                        Log.e("text", "发生未知错误");
                        break;
                    case MediaPlayer.MEDIA_ERROR_IO:
                        errorInfo="文件或网络相关的IO操作错误";

                        Log.e("text", "文件或网络相关的IO操作错误");
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        errorInfo="比特流编码标准或文件不符合相关规范";

                        Log.e("text", "比特流编码标准或文件不符合相关规范");
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        errorInfo="操作超时";

                        Log.e("text", "操作超时");
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        errorInfo="比特流编码标准或文件符合相关规范,但媒体框架不支持该功能";

                        Log.e("text", "比特流编码标准或文件符合相关规范,但媒体框架不支持该功能");
                        break;
                    default:
                        errorInfo="不知道发生了什么";
                        Log.e("text", "不知道发生了什么" );
                        break;
                }
                Toast.makeText(getContext(), errorInfo, Toast.LENGTH_SHORT).show();
                return true;//经常会碰到视频编码格式不支持的情况，若不想弹出提示框就返回true
            }
        });
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null == selectedChannel){
                    Toast.makeText(getContext(), "未选择节目", Toast.LENGTH_SHORT).show();
                    return;
                }
                startFullPlay(selectedChannel.getLiveUrl());
            }
        });
    }

    //初始化频道列表
    private void initChannelView() {
        channelListView = view.findViewById(R.id.live_channelList);
        channelListView.setVerticalScrollBarEnabled(false);//隐藏滚动条，还有布局内另一个属性需要配合
        channelAdapter = new ReuseAdapter<Channel>(getActivity(), R.layout.item_channel) {
            @Override
            public void bindView(Holder holder, Channel obj) {
                holder.setText(R.id.live_channelName, obj.getChannelName());
            }
        };
        channelListView.setAdapter(channelAdapter);
        //选择 预览播放
        channelListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            TextView tempView = null;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //先还原上一个item的文字颜色
                if (tempView != null) {
                    tempView.setTextColor(Color.parseColor("#ffffff"));
                }
                //文字高亮
                View v = channelListView.getSelectedView();
                TextView t = (TextView) v.findViewById(R.id.live_channelName);
                t.setTextColor(Color.parseColor("#18ad00"));
                tempView = t;//保存这个"上一个文字对象"

                /**
                 * 到达频道列表末端处理：回到顶端
                 * 缺陷未处理：但长按的话会导致来不及处理而直接调到导航栏
                 */
                if (i == (adapterView.getCount())) {
                    Log.e("消息", "已经到达列表末端，返回最上端");
                    channelListView.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //点击 播放
        channelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedChannel = channelAdapter.getItem(i);
                startPreviewPlay(selectedChannel.getLiveUrl());
            }
        });
    }

    //url格式转换
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

    void getAllChannelTest(){
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(channelRequestUrl).build();
        Log.i(TAG,channelRequestUrl);
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                Log.i(TAG,json);

            }
        });
    }

    //获取所有频道
    private void getAllChannelByNet(){
        getAllChannelTest();
        resetChannelListView(new ArrayList<Channel>());//重置

        httpUtils.get(channelRequestUrl,new HttpUtils.Back<ReturnChannel>() {
            ArrayList<Channel> channels;
            @Override
            public void success( ReturnChannel ret) {
                channels = ret.getBody().getItems();
                resetChannelListView(channels);
            }
            @Override
            public void failed(Request request, IOException e) {
                Log.e(TAG, "获取频道列表失败,错误信息："+e.getMessage());
                Toast.makeText(context, "获取频道列表失败,错误信息："+e.getMessage(), Toast.LENGTH_SHORT).show();
                /*if(++channelCounter < 3){
                    Log.e(TAG, "重新获取频道");

                }*/
            }
        });
        MainActivity.isChannelEmpty = channelAdapter.isEmpty();//保存列表内容状态
    }

    /**
     * 重置频道列表数据
     * @param channels
     */
    private void resetChannelListView(ArrayList<Channel> channels){
        Log.e(TAG, "频道列表的长度：" + channels.size());
        channelAdapter.resetAll(channels);
    }

    /**
     * 导入文件中的节目
     * @return
     */
    private ArrayList<Channel> importXlsFile(String path){
        ArrayList<Channel> channels =null;
        try {
            InputStream is = new FileInputStream(path);
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
            Toast.makeText(getContext(), "不存在"+path+"文件，请传入！ ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }finally {
            return channels;
        }

    }


    /*   播放器操作   */

    private void pauseMedia(){
        if(mVideoView.isPlaying()){
            mVideoView.pause();
            isMediaPause = true;
            Log.e(TAG,"onPause --暂停播放器");
        }
    }

    private void resumeMedia(){
        if(isMediaPause){
            Log.e(TAG,"onResume --开始播放器");
            mVideoView.start();
        }
    }
}