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

import gos.gosdrm.R;
import gos.gosdrm.data.User;
import gos.gosdrm.db.SharedDb;

public class LoginActivity extends AppCompatActivity {

    private EditText editname;
    private EditText editpasswd;
    private Button login;
    private String strname;
    private String strpasswd;
    private SharedDb sharedDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedDb = SharedDb.getInstance(this);

        bindViews();
        initState();
        initData();
    }

    private void bindViews() {
        editname = (EditText)findViewById(R.id.login_username);
        editpasswd = (EditText)findViewById(R.id.login_password);
        login = (Button)findViewById(R.id.login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strname = editname.getText().toString();
                strpasswd = editpasswd.getText().toString();
                sharedDb.setUser(new User(strname,strpasswd));

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
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

    private void initData(){
        User user = sharedDb.getUser();
        editname.setText(user.getName());
        editpasswd.setText(user.getPassword());
    }
}

