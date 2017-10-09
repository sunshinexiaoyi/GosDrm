package gos.gosdrm.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Map;

import gos.gosdrm.R;
import gos.gosdrm.tool.SharedHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText editname;
    private EditText editpasswd;
    private Button login;
    private String strname;
    private String strpasswd;
    private SharedHelper sh;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = getApplicationContext();
        sh = new SharedHelper(mContext);
        bindViews();
        initState();
    }

    private void bindViews() {
        editname = (EditText)findViewById(R.id.login_username);
        editpasswd = (EditText)findViewById(R.id.login_password);
        login = (Button)findViewById(R.id.login_btn);

        //获取按钮焦点
        login.setOnFocusChangeListener(new Button.OnFocusChangeListener(){
            boolean foldIt;
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if(hasFocus) {
                    login.setBackgroundResource(R.drawable.login_chosen_pressed);

                }else{
                    login.setBackgroundResource(R.drawable.global_item_null);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            boolean foldIt;
            @Override
            public void onClick(View view) {
                strname = editname.getText().toString();
                strpasswd = editpasswd.getText().toString();
                sh.save(strname,strpasswd);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String,String> data = sh.read();
        editname.setText(data.get("username"));
        editpasswd.setText(data.get("passwd"));
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
    }
}
