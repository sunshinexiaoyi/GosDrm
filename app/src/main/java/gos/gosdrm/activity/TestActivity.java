package gos.gosdrm.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import gos.gosdrm.R;
import gos.gosdrm.adapter.ReuseAdapter;
import gos.gosdrm.data.Channel;
import gos.gosdrm.data.PageInfo;
import gos.gosdrm.data.Return;
import gos.gosdrm.tool.HttpUtils;
import okhttp3.Request;

public class TestActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    String url = "http://192.168.1.84:8090/ottserver/IPLiveInfo/getAllChannel?clientId=boss00042&pageSize=20&pageNumber=1";
    HttpUtils httpUtils = HttpUtils.getInstance();

    ListView channelListView;   //频道listView
    ReuseAdapter<Channel> channelAdapter; //频道适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytest);
        initView();
        initData();
    }


    private void initView(){
        initChannelView();
    }

    private void initData() {
        getAllChannel();
    }


    private void initChannelView() {
        channelListView = (ListView) findViewById(R.id.listViewChannel);
        channelAdapter = new ReuseAdapter<Channel>(R.layout.item_channel) {
            @Override
            public void bindView(ViewHolder holder, Channel obj) {
                TextView channelName = holder.getView(R.id.textView);
                channelName.setText(obj.getChannelName());
            }
        };
        channelListView.setAdapter(channelAdapter);
        channelListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Channel channel = channelAdapter.getItem(i);
                Log.i(TAG,"position:"+i);

                String urlFormat = "http://192.168.1.84:1935/live/_definst_/stream/%s.stream/playlist.m3u8";
                String url = (String.format(urlFormat,channel.getChannelName())).replaceAll(" ","");
                Log.i(TAG,"url:"+url);

                startPlay(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void startPlay(String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String type = "video/*";
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri, type);
        startActivity(intent);
    }

    private void getAllChannel(){
        httpUtils.get(url,new HttpUtils.Back<Return<PageInfo<Channel>>>(){
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "keyCode："+keyCode);
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 重置频道列表数据
     * @param channels
     */
    private void resetChannelListView(ArrayList<Channel> channels){
        channelAdapter.reset(channels);
    }
}
