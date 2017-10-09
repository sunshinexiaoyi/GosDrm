package gos.gosdrm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import gos.gosdrm.R;
import gos.gosdrm.tool.SharedHelper;

/**lyx：
 * 切换主题背景的设置项ACT
 */
public class Setting_changeBgActivity extends AppCompatActivity {
    private SharedHelper sharedHelper;
    private RadioGroup changeBg;
    private View mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_change_bg);

        init_view();//检查上次的默认背景选择

        init_listener();//监听单选项事件
    }

    private void init_view() {
        sharedHelper = new SharedHelper(this);

        changeBg = (RadioGroup)findViewById(R.id.setting_changeBg);
        if (sharedHelper.get("backgroundRes").equals("0")) {
            changeBg.check(R.id.setting_autoBg);
        } else if (sharedHelper.get("backgroundRes").equals("1")){
            changeBg.check(R.id.setting_blackBg);
        }
    }

    private void init_listener() {
        mainLayout = findViewById(R.id.main_MAIN);

        //更改主背景
        changeBg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            boolean foldIt;
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                if (checkId == R.id.setting_autoBg) {
                    Toast.makeText(Setting_changeBgActivity.this, "设置背景为 高斯贝尔", Toast.LENGTH_SHORT).show();
                    mainLayout.setBackgroundResource(R.drawable.global_bg);
                    sharedHelper.change("backgroundRes", "0");

                } else if (checkId == R.id.setting_blackBg) {
                    Toast.makeText(Setting_changeBgActivity.this, "设置背景为 酷黑", Toast.LENGTH_SHORT).show();
                    mainLayout.setBackgroundResource(R.color.black);
                    sharedHelper.change("backgroundRes", "1");
                }
            }
        });
    }
}
