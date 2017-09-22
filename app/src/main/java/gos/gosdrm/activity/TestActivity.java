package gos.gosdrm.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import gos.gosdrm.R;
import gos.gosdrm.data.JsonParse;
import gos.gosdrm.data.ProgramRet;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    OkHttpClient httpClient;
    String url = "http://192.168.1.142:8080/3a_cs/encryptorContentAction/encryptor-api-content-list?pageSize=25&pageIndex=1&streamSystemType=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitytest);
        getPrograms();
    }

    private void getPrograms(){
        httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                ProgramRet programRet = JsonParse.getProgramRet(json);
                Log.i(TAG, json);
                Log.i(TAG, "getTotal:" + programRet.getTotal() + "");
            }
        });
    }
}
