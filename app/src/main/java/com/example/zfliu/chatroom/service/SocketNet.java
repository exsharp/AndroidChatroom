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

import com.example.zfliu.chatroom.AppUtil;
import com.example.zfliu.chatroom.R;

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

    private String host = "192.168.1.127";  //要连接的服务端IP地址
    private int port = 3333;   //要连接的服务端对应的监听端口

    private void StartConnect () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client = new Socket(host,port);
                    writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    writer.write("helloworld");
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
                        String temp;
                        while ((temp=reader.readLine())!=null){
                            Log.d("SOCKET",temp);
                            HandlerMsg handler = new HandlerMsg(temp);
                            handler.handler();
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
        filter.addAction("LOGIN");
        filter.addAction("SendMsg");
        registerReceiver(socketBroadcastReceiver, filter);
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

    //这里处理从服务器接收回来的东西
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class HandlerMsg {
        private String type;
        private JSONArray jsonArray;
        private JSONTokener json;
        private Intent rtIntent;

        HandlerMsg (String str) throws JSONException {
            json =new JSONTokener(str);
            JSONObject info = (JSONObject) json.nextValue();
            type = info.getString("TYPE");
            jsonArray = info.getJSONArray("CONTENT");
        }
        public void handler() throws JSONException {
            switch (type){
                case "VERIFY":
                    verify(jsonArray);
                    break;
                case "FRIENDGROUP":
                    friendGroupRcv(jsonArray);
                    break;
                case "Msg":
                    talkEach(jsonArray);
                    break;
                default:
            }
        }
        private void verify(JSONArray ja) throws JSONException {
            Intent intent = new Intent();
            intent.setAction("VERIFY");
            if (ja.get(0).equals("SUCCESS")){
                intent.putExtra("RESULT","SUCCESS");
            } else {
                intent.putExtra("RESULT","FAIL");
                intent.putExtra("WHY",ja.get(1).toString());
            }
            sendBroadcast(intent);
            Log.d("SOCKET","Socket发出广播");
        }
        private void friendGroupRcv(JSONArray ja) throws JSONException{
            AppUtil app = (AppUtil)getApplication();
            app.setFriendList(ja);
        }
        private void talkEach(JSONArray ja) throws JSONException {
            Intent intent = new Intent();
            intent.setAction("MsgBack");
            intent.putExtra("who",ja.get(0).toString());
            intent.putExtra("what", ja.get(1).toString());
            sendBroadcast(intent);
            Log.d("SOCKET","Socket发出广播");
        }
    }
}
