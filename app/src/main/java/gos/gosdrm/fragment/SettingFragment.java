package gos.gosdrm.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import gos.gosdrm.R;
import gos.gosdrm.tool.SharedHelper;

public class SettingFragment extends Fragment {
    private View view;
    private SharedHelper sharedHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            Log.e("SettingFragment消息", "view复用");
            return view;
        }
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        init();//初始化

        return view;
    }

    public void init() {
        sharedHelper = new SharedHelper(getActivity());

        //检查上次的默认源类型
        RadioGroup autoResource = (RadioGroup)view.findViewById(R.id.setting_radioGroup);
        if (sharedHelper.get("autoResource").equals("0")) {
            autoResource.check(R.id.setting_netSource);
        } else {
            autoResource.check(R.id.setting_localSource);
        }

        //点击切换用户
        TextView changeUser = (TextView)view.findViewById(R.id.setting_chagneUser);
        changeUser.setOnClickListener(new View.OnClickListener() {
            boolean foldIt;
            @Override
            public void onClick(View view) {
                Log.e("消息", "切换用户");
                sharedHelper.del();//删除
                Toast.makeText(getActivity(), "重新登陆", Toast.LENGTH_SHORT).show();//吐司提示
                getActivity().finish();//结束ACT回到登陆界面
            }
        });

        //切换默认源
        autoResource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            boolean foldIt;
            //应该读取share中的保存类型，默认恢复选择
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId == R.id.setting_netSource) {
                    Log.e("消息", "设置网络为默认频道源");
                    Toast.makeText(getActivity(), "设置默认频道源为网络", Toast.LENGTH_SHORT).show();

                    sharedHelper.change("autoResource", "0");

                } else {
                    Log.e("消息", "设置本地为默认频道源");
                    Toast.makeText(getActivity(), "设置默认频道源为本地", Toast.LENGTH_SHORT).show();

                    sharedHelper.change("autoResource", "1");
                }
            }
        });
    }
}
