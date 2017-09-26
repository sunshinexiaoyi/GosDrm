package gos.gosdrm.activity;
import gos.gosdrm.R;
import gos.gosdrm.fragment.LiveFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fm;
    private Fragment live_fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        live_fm = new LiveFragment();
        fm = getSupportFragmentManager();//得到碎片对象
        fm.beginTransaction().add(R.id.live_fragment, live_fm).commit();//启动了碎片
        Log.e("消息", "启动碎片成功");
    }
}
