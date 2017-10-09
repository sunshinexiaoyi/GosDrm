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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import gos.gosdrm.R;
import gos.gosdrm.activity.Setting_autoResActivity;
import gos.gosdrm.activity.Setting_changeBgActivity;
import gos.gosdrm.adapter.ReuseAdapter;
import gos.gosdrm.data.SettingItem;
import gos.gosdrm.tool.SharedHelper;

/**
 * 这里只需要完成两个目的
 * 1、初始化设置项列表
 * 2、启动对应设置项的ACT
 */
public class SettingFragment extends Fragment {
    private View view;
    private ListView settingListView;
    private SharedHelper sharedHelper;
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
        settingItem.add(new SettingItem(R.drawable.setting_channel_resource, "默认频道源"));
        settingItem.add(new SettingItem(R.drawable.setting_changebg, "背景主题"));
        settingItem.add(new SettingItem(R.drawable.setting_changeuser, "切换账户"));
        settingItem.add(new SettingItem(R.drawable.setting_drmcounter, "授权次数"));

       settingListView = (ListView)view.findViewById(R.id.setting_listView);

        /**初始化适配器
         * 这里可能没考虑item数据被复用的情况？
         */
        ReuseAdapter<SettingItem> settingAdapter = new ReuseAdapter<SettingItem>(getActivity(), settingItem, R.layout.item_setting) {
            @Override
            public void bindView(Holder holder, SettingItem obj) {
                //设置item的填充数据
                holder.setText(R.id.setting_itemName, obj.getItemName());
                holder.setImageResource(R.id.setting_itemLogo, obj.getLogoImgId());

                //监听设置项的选择
                holder.setItemSelectListener(settingListView, new AdapterView.OnItemSelectedListener() {
                    int autoSelected = 0;
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                        Log.e("消息", "启动第" + pos + "个设置项ACT");
                        switch (pos) {
                            case 0: {
                                if (autoSelected == 0) {
                                    autoSelected++;
                                    break;
                                }
                                startActivity(new Intent(getActivity(), Setting_autoResActivity.class));
                                break;
                            }
                            case 1: {
                                startActivity(new Intent(getActivity(), Setting_changeBgActivity.class));
                                break;
                            }
                            case 2: {
                                //startActivity(new Intent(getActivity(), Setting_changeUserActivity.class));
                                changeUser();//切换用户（回到登陆界面）
                                break;
                            }
                            case 3: {
                                //startActivity(new Intent(getActivity(), Setting_drmCounterActivity.class));
                                Toast.makeText(getActivity(), "暂未加入drm授权次数记录", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                Log.e("消息", "发现异常选择");
                                break;
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });

//                holder.setItemOnClickListener(settingListView, new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView adapterView, View view, int pos, long l) {
//                        Log.e("消息", "启动第" + pos + "个设置项ACT");
//                        switch (pos) {
//                            case 0: {
//                                startActivity(new Intent(getActivity(), Setting_autoResActivity.class));
//                                break;
//                            }
//                            case 1: {
//                                startActivity(new Intent(getActivity(), Setting_changeBgActivity.class));
//                                break;
//                            }
//                            case 2: {
//                                //startActivity(new Intent(getActivity(), Setting_changeUserActivity.class));
//                                changeUser();//切换用户（回到登陆界面）
//                                break;
//                            }
//                            case 3: {
//                                //startActivity(new Intent(getActivity(), Setting_drmCounterActivity.class));
//                                Toast.makeText(getActivity(), "暂未加入drm授权次数记录", Toast.LENGTH_SHORT).show();
//                                break;
//                            }
//                            default: {
//                                Log.e("消息", "发现异常选择");
//                                break;
//                            }
//                        }
//                    }
//                });
            }
        };

        settingListView.setAdapter(settingAdapter);
        settingListView.requestFocus();
        Log.e("消息", "设置项列表初始化完成");
    }

    //切换用户
    private void changeUser() {
        sharedHelper = new SharedHelper(getActivity());
        Log.e("消息", "切换用户");
        sharedHelper.del();//删除
        Toast.makeText(getActivity(), "重新登陆", Toast.LENGTH_SHORT).show();//吐司提示
        getActivity().finish();//结束ACT回到登陆界面
    }
}