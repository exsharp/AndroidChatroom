package com.example.zfliu.chatroom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Space;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by zfliu on 1/27/2015.
 */
public class SplashActivity extends Activity {

    private Intent intent;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //测试用
        intent = new Intent(SplashActivity.this,LoginActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();

        /*正式用
        handler=new ShowStateHandler();
        if (checkNetworkState()){

            Thread checkServerState = new CheckServerState();
            checkServerState.start();
        } else {
            Toast.makeText(SplashActivity.this,"没有网络连接,将在3秒后退出程序",Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SplashActivity.this.finish();
                }
            },3000);
        }
        */
    }

    private boolean checkNetworkState() {
        boolean flag = false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        return flag;
    }

    class ShowStateHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            System.out.println("Handler:"+Thread.currentThread().getName());
            String error = (String)msg.obj;
            boolean flag = ("success"==error);
            if (flag){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        intent = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                },1000);
            } else {
                Toast.makeText(SplashActivity.this,error,Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SplashActivity.this.finish();
                    }
                },3000);
            }
        }
    }

    class CheckServerState extends Thread {
        @Override
        public void run() {
            System.out.println("runnable:"+Thread.currentThread().getName());
            try{
                String url = getString(R.string.server_home_url).toString()+"/home";
                System.out.println(url);

                BasicHttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5*1000);
                HttpConnectionParams.setSoTimeout(httpParams, 5*1000);

                HttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpGet);
                if (200==response.getStatusLine().getStatusCode()) {
                    String content = EntityUtils.toString(response.getEntity(), "utf-8");
                    System.out.println(content);
                    Message msg = handler.obtainMessage();
                    msg.obj = "success";
                    handler.sendMessage(msg);
                } else {
                    System.out.println("无法连接到服务器");
                    Message msg = handler.obtainMessage();
                    msg.obj = "fail";
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = handler.obtainMessage();
                msg.obj = e.getMessage();
                handler.sendMessage(msg);
                e.printStackTrace();
            }
        }
    }
}
