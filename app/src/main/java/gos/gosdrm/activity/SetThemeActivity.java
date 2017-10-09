package gos.gosdrm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import gos.gosdrm.R;
import gos.gosdrm.data.SetTheme;
import gos.gosdrm.db.SharedDb;

/**lyx：
 * 切换主题背景的设置项ACT
 */
public class SetThemeActivity extends AppCompatActivity {
    private RadioGroup changeBg;
    private View mainLayout;
    private SharedDb sharedDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_theme);

        initView();//检查上次的默认背景选择

        initListener();//监听单选项事件
    }

    private void initView() {
        sharedDb = SharedDb.getInstance(this);

        changeBg = (RadioGroup)findViewById(R.id.setting_changeBg);
        switch (sharedDb.getSetTheme().getTheme()){
            case SetTheme.GOSPELL:
                changeBg.check(R.id.setting_autoBg);
                break;
            case SetTheme.BLACk:
                changeBg.check(R.id.setting_blackBg);
                break;
            default:
                Toast.makeText(this, "设置背景为错误", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void initListener() {
        mainLayout = MainActivity.mainLayout;

        //更改主背景
        changeBg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                SetTheme setTheme = new SetTheme();
                if (checkId == R.id.setting_autoBg) {
                    Toast.makeText(SetThemeActivity.this, "设置背景为 高斯贝尔", Toast.LENGTH_SHORT).show();
                    mainLayout.setBackgroundResource(R.drawable.global_bg);
                    setTheme.setTheme(SetTheme.GOSPELL);

                } else if (checkId == R.id.setting_blackBg) {
                    Toast.makeText(SetThemeActivity.this, "设置背景为 酷黑", Toast.LENGTH_SHORT).show();
                    mainLayout.setBackgroundResource(R.drawable.global_bg_black);
                    setTheme.setTheme(SetTheme.BLACk);
                }

                sharedDb.setSetTheme(setTheme);
            }
        });
    }
}
