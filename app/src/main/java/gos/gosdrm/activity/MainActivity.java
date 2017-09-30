package gos.gosdrm.activity;
import gos.gosdrm.R;
import gos.gosdrm.adapter.ReuseAdapter;
import gos.gosdrm.data.HomeMenuItem;
import gos.gosdrm.fragment.AboutFragment;
import gos.gosdrm.fragment.HomeFragment;
import gos.gosdrm.fragment.LiveFragment;
import gos.gosdrm.fragment.SettingFragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private ReuseAdapter<HomeMenuItem> homeMenuAdapter;
    private Fragment curFragment; //当前Fragment

    public static boolean isChannelEmpty = true;//观察频道列表是否为空
    private Handler handler;//在捕获物理按键时的线程数据提交，以启动线程产生时间差使方法提前返回。

    private View channelList;
    private View importLocal;
    private View importNet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();//得到碎片对象
        initView();
    }

    /**lyx：重写返回键事件
     * getRepeatCount：重复次数，防止多次后退
     * 1、短按回到导航
     * 2、长按退出应用
     * 3、向下可以回到导航栏
     * 4、频道列表中无频道时，焦点被允许来到源选项
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        GridView homeMenuView = (GridView)findViewById(R.id.homeMenu);
        //lyx：短按返回导航栏
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            Log.e("消息", "短按返回键，焦点需要无条件回到导航栏");
            homeMenuView.requestFocus();
            return false;
        }
        //lyx：长按退出应用
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() != 0)) {
            Log.e("消息", "长按返回键，需要退出程序");
            finish();
            return false;
        }
        //lyx：向下回到导航栏
        View videoView = findViewById(R.id.live_videoView);
        if ((videoView != null) && (videoView.isFocused()) && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
            Log.e("消息", "焦点需要从播放器回到导航栏");
            homeMenuView.requestFocus();
        }

        /**lyx：
         * 频道列表有内容时，立即禁止TextView允许获得焦点和点击，完成跳转到列表后恢复现场
         * 当频道列表为空时，不取消源选择的焦点和点击
         */
        channelList = findViewById(R.id.live_channelList);
        importLocal = findViewById(R.id.live_importLocal);
        importNet = findViewById(R.id.live_importNet);
        Log.e("消息", "channelList是否为空：" + (channelList == null));
        Log.e("消息", "importLocal是否为空：" + (importLocal == null));
        Log.e("消息", "importNet是否为空：" + (importNet == null));

        /**
         * fcx：当按键指令输入太快时，会由于View还未加载完成，但已经执行了按键指令，导致产生空对象textView，空对象调用方法从而报错
         * 解决方案：当textView为空对象时，放弃按键指令
         */
        if (importLocal != null) {
            if ((videoView != null) && (videoView.isFocused())
                    && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && (!isChannelEmpty)) {
                Log.e("消息", "频道列表中有内容，焦点需要从播放器回到频道列表，立即禁止源选项允许获得焦点");

                importLocal.setClickable(false);
                importNet.setClickable(false);
                importLocal.setFocusable(false);
                importNet.setFocusable(false);

                channelList.requestFocus();//频道列表请求夺取焦点

                /**lyx：
                 * 猜测原因：当方法返回后，系统才进行焦点改变处理，所以不能在此方法返回前就恢复现场
                 * 线程的启动和数据处理需要时间，所以方法内的返回语句要先于线程中的逻辑实现被执行，因而能达到目的
                 */
                handler = new Handler();
                new Thread() {
                    boolean foldIt;
                    public void run() {
                        if (channelList.isFocused()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    importLocal.setClickable(true);
                                    importNet.setClickable(true);
                                    importLocal.setFocusable(true);
                                    importNet.setFocusable(true);
                                }
                            });
                        }
                        Log.e("恢复现场线程消息", "线程已经死亡");
                    }
                }.start();

            } else {
                importLocal.setClickable(true);
                importNet.setClickable(true);
                importLocal.setFocusable(true);
                importNet.setFocusable(true);
            }
        } else {
            Log.e("消息", "View未加载，放弃本次指令执行");
        }
        return false;
    }

    private void initView(){
        initState();
        initHomeMenu();
    }

    //沉浸式标题栏
    private void initState() {
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initHomeMenu(){
        GridView homeMenuView = (GridView)findViewById(R.id.homeMenu);
        ArrayList<HomeMenuItem> homeMenuItems = new ArrayList<>();

        homeMenuItems.add(new HomeMenuItem(new HomeFragment(),R.string.home));
        homeMenuItems.add(new HomeMenuItem(new LiveFragment(),R.string.live));
        homeMenuItems.add(new HomeMenuItem(new SettingFragment(),R.string.setting));
        homeMenuItems.add(new HomeMenuItem(new AboutFragment(),R.string.about));

        homeMenuAdapter = new ReuseAdapter<HomeMenuItem>(this,homeMenuItems,R.layout.item_home_menu) {
            @Override
            public void bindView(Holder holder, HomeMenuItem obj) {
                holder.setText(R.id.name,obj.getName());
            }
        };

        homeMenuView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(null != curFragment){
                    fragmentManager.beginTransaction().hide(curFragment).commit();//启动了碎片
                }
                curFragment = homeMenuAdapter.getItem(position).getFragment();

                if(curFragment.isAdded()){
                    fragmentManager.beginTransaction().show(curFragment).commit();//启动了碎片
                   return;
                }
                fragmentManager.beginTransaction().add(R.id.homeContent, curFragment).commit();//启动了碎片
                Log.e("消息", "启动碎片成功");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        Log.e("消息", "setAdapter");
        homeMenuView.setAdapter(homeMenuAdapter);
    }
}
