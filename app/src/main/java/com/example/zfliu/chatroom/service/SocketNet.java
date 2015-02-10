package com.example.zfliu.chatroom.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.example.zfliu.chatroom.database.DBManager;
import com.example.zfliu.chatroom.database.Msg;
import com.example.zfliu.chatroom.files.FListFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

    private String host = "192.168.1.110";  //要连接的服务端IP地址
    private int port = 3333;   //要连接的服务端对应的监听端口

    private void StartConnect () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new Socket(host,port);
                    writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    AppUtil app = (AppUtil)getApplication();
                    app.setWriter(writer);
                } catch (Exception e) {
                    e.printStackTrace();
                    stopSelf();
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
                        String temp;
                        while ((temp=reader.readLine())!=null){
                            Log.d("SOCKET", "服务器发来" + temp);
                            passStr(temp);
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
        Log.d("SOCKET","到了这里onCreate函数");
    }

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

    private void passStr(String str) throws JSONException, IOException {
        MsgHandler msgHandler = new MsgHandler(str,this);
        msgHandler.judgeType();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
