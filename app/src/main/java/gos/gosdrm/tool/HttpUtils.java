package gos.gosdrm.tool;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HTTP工具类
 * Created by wuxy on 2017/9/22.
 */

public class HttpUtils {
    private final String TAG = getClass().getSimpleName();
    private OkHttpClient httpClient;
    private Handler handler;    //更新到ui线程

    private HttpUtils(){
        httpClient = new OkHttpClient();
        handler = new Handler(Looper.getMainLooper());
    }


    private static class init{
        private static HttpUtils httpUtils = new HttpUtils();
    }

    public static HttpUtils getInstance(){
        return init.httpUtils;
    }

    public <T> void get(String url, final Back<T> back){
        Request request = new Request.Builder().url(url).build();
        Log.i(TAG,url);
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call,final IOException e) {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        back.failed(call.request(),e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String json = response.body().string();
                Log.i(TAG,json);
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        back.success( (T)JSONObject.parseObject(json,back.getType()));
                    }
                });
            }
        });
    }


    public static abstract class Back<T>  {
        private final Type type;//泛型 T 类型
        public Back() {
            //参数化类型
            Type superClass = this.getClass().getGenericSuperclass();
            //返回表示此类型实际类型参数的 Type 对象的数组
            this.type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }

        public Type getType() {
            return this.type;
        }



        public abstract void success(T t);
        public abstract void failed(Request request, IOException e);

    }

}
