package gos.gosdrm.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import gos.gosdrm.R;
import gos.gosdrm.data.SetTheme;
import gos.gosdrm.db.SharedDb;
import gos.gosdrm.define.SystemFile;

/**lyx：
 * 切换主题背景的设置项ACT
 */
public class SetImportActivity extends AppCompatActivity {
    private View.OnClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_import);

        initView();//检查上次的默认背景选择

        initListener();//监听单选项事件
    }

    private void initView() {

    }

    private void initListener() {

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file = "";
                int id = v.getId();
                Log.e("导入","id:"+id);
                if (id == R.id.setting_live) {
                    file = SystemFile.liveFile;
                } else if (id == R.id.setting_vod) {
                    file = SystemFile.vodFile;
                }else if(id == R.id.setting_da){
                    file = SystemFile.settingFile;
                }else{
                    return;
                }

                if(SystemFile.copyFile(SetImportActivity.this,SystemFile.getUDiskPath(file),SystemFile.getLocalPath(file))){
                    Toast.makeText(SetImportActivity.this, "导入 成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SetImportActivity.this, "导入 失败", Toast.LENGTH_SHORT).show();
                }
            }
        };

        findViewById(R.id.setting_live).setOnClickListener(clickListener);
        findViewById(R.id.setting_vod).setOnClickListener(clickListener);
        findViewById(R.id.setting_da).setOnClickListener(clickListener);

    }
}
