package gos.gosdrm.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import gos.gosdrm.R;
import gos.gosdrm.adapter.TabMenuAdapter;
import gos.gosdrm.fragment.AboutFragment;
import gos.gosdrm.fragment.HomeFragment;
import gos.gosdrm.fragment.LiveFragment;
import gos.gosdrm.fragment.SettingFragment;
import gos.gosdrm.tool.TabMenuManager;

public class HomeActivity extends AppCompatActivity{
    ArrayList<Button> radioButton;
    private final String TAG = this.getClass().getSimpleName();
    private TabMenuAdapter tabMenuAdapter = null;
    private ViewPager viewPager = null;

    TabMenuManager tabMenuManager = new TabMenuManager();
    RadioGroup menuBar;
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();//初始化界面
        initState();//沉浸标题栏
        initViewPager();//翻页
    }


    void initView() {
        initTabMenu();

    }

    void initTabMenu()  {

        tabMenuManager.addTabMenuItem(HomeFragment.class);
        tabMenuManager.addTabMenuItem(LiveFragment.class);
        tabMenuManager.addTabMenuItem(SettingFragment.class);
        tabMenuManager.addTabMenuItem(AboutFragment.class);

        if (radioButton == null) {
            radioButton = new ArrayList<>();
        }

        menuBar = (RadioGroup) findViewById(R.id.tab_menu);
       radioButton.add((RadioButton)findViewById(R.id.tab_home));
       radioButton.add((RadioButton)findViewById(R.id.tab_live));
       radioButton.add((RadioButton)findViewById(R.id.tab_setting));
       radioButton.add((RadioButton)findViewById(R.id.tab_about));

        radioButton.get(0).performClick();

        radioButton.get(0).setOnFocusChangeListener(new FocusChanged());
        radioButton.get(1).setOnFocusChangeListener(new FocusChanged());
        radioButton.get(2).setOnFocusChangeListener(new FocusChanged());
        radioButton.get(3).setOnFocusChangeListener(new FocusChanged());

    }

    void initViewPager() {

        ArrayList<Fragment> list = new ArrayList<>();

        Fragment homeFragment = new HomeFragment();
        LiveFragment liveFragment = new LiveFragment();
        SettingFragment settingFragment = new SettingFragment();
        AboutFragment aboutFragment = new AboutFragment();

        list.add(homeFragment);
        list.add(liveFragment);
        list.add(settingFragment);
        list.add(aboutFragment);
        tabMenuAdapter = new TabMenuAdapter(getSupportFragmentManager());
        tabMenuAdapter.setFragmentList(list);
        viewPager = (ViewPager) findViewById(R.id.vpager);
        viewPager.setAdapter(tabMenuAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(null != menuBar){
                    RadioButton curButton =    (RadioButton) menuBar.getChildAt(position);
                    curButton.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
      jumpToFragment(0);

    }

    void jumpToFragment(int index) {
        viewPager.setCurrentItem(index);
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

    public class FocusChanged implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean bl) {
            v = view;
            int position = -1;
            Log.e("焦点被改变的id", radioButton.indexOf(view) + "");
            int pos = radioButton.indexOf(v);
            Log.e("消息", "POS的值" + pos);
            if (pos != radioButton.size()) {
                radioButton.get(pos ).performClick();
            }

            switch (view.getId()) {
                case R.id.tab_home:
                    position = 0;
                    break;
                case R.id.tab_live:

                    position = 1;
                    break;
                case R.id.tab_setting:

                    position = 2;
                    break;
                case R.id.tab_about:
                    position = 3;
                    break;
                default:
                    break;
            }
            if (-1 != position) {
                viewPager.setCurrentItem(position);

                    View views = findViewById(R.id.parent);
                if (0 == position) {
                    views.setBackgroundResource(R.drawable.drm);
                } else {
                    views.setBackgroundResource(R.drawable.bg);
                }
            }

        }
    }
}
