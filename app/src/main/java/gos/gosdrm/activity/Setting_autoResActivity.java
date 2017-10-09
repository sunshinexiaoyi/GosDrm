package gos.gosdrm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import gos.gosdrm.R;
import gos.gosdrm.tool.SharedHelper;


/**lyx：
 * 切换默认频道源的设置项ACT
 */
public class Setting_autoResActivity extends AppCompatActivity {
    private SharedHelper sharedHelper;
    RadioGroup autoResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_auto_res);

        init_view();//初始化单选组

        init_listener();//切换默认源
    }

    private void init_view() {
        sharedHelper = new SharedHelper(this);

        //检查上次的默认源类型
        autoResource = (RadioGroup)findViewById(R.id.setting_radioGroup);
        if (sharedHelper.get("autoResource").equals("0")) {
            autoResource.check(R.id.setting_netSource);
        } else if (sharedHelper.get("autoResource").equals("1")){
            autoResource.check(R.id.setting_localSource);
        }
    }

    private void init_listener() {
        autoResource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            boolean foldIt;
            //应该读取share中的保存类型，默认恢复选择
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId == R.id.setting_netSource) {
                    Log.e("消息", "设置网络为默认频道源");
                    Toast.makeText(Setting_autoResActivity.this, "设置默认频道源为网络", Toast.LENGTH_SHORT).show();
                    sharedHelper.change("autoResource", "0");
                    finish();

                } else if (checkId == R.id.setting_localSource) {
                    Log.e("消息", "设置本地为默认频道源");
                    Toast.makeText(Setting_autoResActivity.this, "设置默认频道源为本地", Toast.LENGTH_SHORT).show();
                    sharedHelper.change("autoResource", "1");
                    finish();
                }
            }
        });
    }
}
