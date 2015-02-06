package com.example.zfliu.chatroom.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.zfliu.chatroom.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by sharp on 2/4/2015.
 */
public class SocketNet extends Service {

    private Socket client;
    private BufferedReader reader;
    private BufferedWriter writer;

    private String host = "192.168.191.1";  //要连接的服务端IP地址
    private int port = 3333;   //要连接的服务端对应的监听端口
//    private String host = getString(R.string.server_home_url).toString();
//    private int port = getString(R.string.server_home_port).toString().t;

    private void StartConnect () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new Socket(host,port);
                    writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    writer.write("HelloWroldeof");
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (;;){
                        StringBuffer sb = new StringBuffer();
                        String temp;
                        while ((temp=reader.readLine())!=null){
                            Log.d("SOCKET",temp);
                            MyHandlerMsg handler = new MyHandlerMsg(temp);
                            Intent intent= handler.handler();
                            sendBroadcast(intent);
                        }
                        System.out.println("一次循环结束");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCreate() {
        this.StartConnect();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.loop();
        this.iniBroadcastReceiver();
        Log.d("SOCKET","到了这里onCreate函数");
    }

    public void iniBroadcastReceiver(){
        SocketBroadcastReceiver socketBroadcastReceiver = new SocketBroadcastReceiver(writer);
        IntentFilter filter = new IntentFilter();
        filter.addAction("SOCKET");
        filter.addAction("LOGIN");
        registerReceiver(socketBroadcastReceiver,filter);
        Log.d("SOCKET","注册广播接收器");
    };

    @Override
    public void onDestroy() {
        try {
            reader.close();
            writer.close();
            client.close();
            Log.d("SOCKET","关闭各种流");
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
