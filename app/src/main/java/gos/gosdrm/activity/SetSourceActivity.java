package gos.gosdrm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import gos.gosdrm.R;
import gos.gosdrm.data.SetSource;
import gos.gosdrm.db.SharedDb;


/**lyx：
 * 切换默认频道源的设置项ACT
 */
public class SetSourceActivity extends AppCompatActivity {
    private RadioGroup autoResource;
    private SharedDb sharedDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_source);

        init_view();//初始化单选组

        init_listener();//切换默认源
    }

    private void init_view() {
        sharedDb = SharedDb.getInstance(this);

        //检查上次的默认源类型
        autoResource = (RadioGroup)findViewById(R.id.setting_radioGroup);
        switch (sharedDb.getSetSource().getSource()){
            case SetSource.NETWORK:
                autoResource.check(R.id.setting_netSource);
                break;
            case SetSource.LOCAL:
                autoResource.check(R.id.setting_localSource);
                break;
            default:
                Toast.makeText(this, "设置默认频道源错误", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void init_listener() {
        autoResource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            boolean foldIt;
            //应该读取share中的保存类型，默认恢复选择
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                SetSource setSource = new SetSource();
                if (checkId == R.id.setting_netSource) {
                    Log.e("消息", "设置网络为默认频道源");
                    Toast.makeText(SetSourceActivity.this, "设置默认频道源为网络", Toast.LENGTH_SHORT).show();
                    setSource.setSource(SetSource.NETWORK);
                    finish();

                } else if (checkId == R.id.setting_localSource) {
                    Log.e("消息", "设置本地为默认频道源");
                    Toast.makeText(SetSourceActivity.this, "设置默认频道源为本地", Toast.LENGTH_SHORT).show();
                    setSource.setSource(SetSource.LOCAL);
                    finish();
                }

                sharedDb.setSetSource(setSource);
            }
        });
    }
}
