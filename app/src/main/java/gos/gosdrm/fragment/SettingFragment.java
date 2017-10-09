package gos.gosdrm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import gos.gosdrm.R;
import gos.gosdrm.activity.SetSourceActivity;
import gos.gosdrm.activity.SetThemeActivity;
import gos.gosdrm.adapter.ReuseAdapter;
import gos.gosdrm.data.SettingItem;

/**
 * 这里只需要完成两个目的
 * 1、初始化设置项列表
 * 2、启动对应设置项的ACT
 */
public class SettingFragment extends Fragment {
    private View view;
    private ListView settingListView;
    private Handler handler;//自动辅助线程：自动模拟用户操作，查看现象

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            Log.e("SettingFragment消息", "view复用");
            return view;
        }
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        init_view();//初始化设置项列表

        return view;
    }

    private void init_view() {
        ArrayList<SettingItem> settingItem = new ArrayList<>();
        settingItem.add(new SettingItem(R.string.setting_item_autoSource,R.drawable.setting_channel_resource,SetSourceActivity.class ));
        settingItem.add(new SettingItem(R.string.setting_item_theme,R.drawable.setting_changebg, SetThemeActivity.class));
        settingItem.add(new SettingItem(R.string.setting_item_changeUser,R.drawable.setting_changeuser,null));
        settingItem.add(new SettingItem(R.string.setting_item_authorizationTimes,R.drawable.setting_drmcounter,null));

        settingListView =view.findViewById(R.id.setting_listView);

        /**初始化适配器
         * 这里可能没考虑item数据被复用的情况？
         */
        ReuseAdapter<SettingItem> settingAdapter = new ReuseAdapter<SettingItem>(getActivity(), settingItem, R.layout.item_setting) {
            @Override
            public void bindView(Holder holder, SettingItem obj) {
                //设置item的填充数据
                holder.setText(R.id.setting_itemName, obj.getNameId());
                holder.setImageResource(R.id.setting_itemLogo, obj.getLogoImgId());

                //监听设置项的选择
                holder.setItemOnClickListener(settingListView, new AdapterView.OnItemClickListener() {
                    boolean foldIt;
                    @Override
                    public void onItemClick(AdapterView adapterView, View view, int pos, long l) {
                        Class startActivity =  getItem(pos).getActivity();
                        if(null != startActivity){
                            startActivity(new Intent(getContext(),startActivity));
                        }

                        if (pos == 2) {
                            changeUser();
                        } else if (pos == 3) {
                            drmCounter();
                        }
                    }
                });
            }
        };

        settingListView.setAdapter(settingAdapter);
        settingListView.requestFocus();//使得切换到设置页面时焦点来到设置项上
        Log.e("消息", "设置项列表初始化完成");
    }

    //切换用户
    private void changeUser() {
        Log.e("消息", "切换用户");
        Toast.makeText(getActivity(), "重新登陆", Toast.LENGTH_SHORT).show();//吐司提示
        getActivity().finish();//结束ACT回到登陆界面
    }

    //授权设置
    private void drmCounter() {
        Log.e("消息", "授权设置");
        Toast.makeText(getActivity(), "未完成授权记录功能", Toast.LENGTH_SHORT).show();//吐司提示
    }
}