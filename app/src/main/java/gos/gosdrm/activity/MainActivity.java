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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    private ReuseAdapter<HomeMenuItem> homeMenuAdapter;
    private Fragment curFragment; //当前Fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();//得到碎片对象
        initView();
    }

    /**重写返回键事件
     * getRepeatCount：重复次数，防止多次后退
     * 1、短按回到导航
     * 2、长按退出应用
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            Log.e("消息", "按下了返回键，焦点回到导航栏");
            GridView homeMenuView = (GridView)findViewById(R.id.homeMenu);
            homeMenuView.requestFocus();
            return false;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() != 0)) {
            Log.e("消息", "重复按下返回键，退出程序");
            finish();
            return false;
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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
                    fragmentManager.beginTransaction().show(curFragment)
                            .commit();//启动了碎片
                   return;
                }
                fragmentManager.beginTransaction().add(R.id.homeContent, curFragment)
                        .commit();//启动了碎片
                Log.e("消息", "启动碎片成功");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        Log.e("消息", "setAdapter");
        homeMenuView.setAdapter(homeMenuAdapter);
    }


}
