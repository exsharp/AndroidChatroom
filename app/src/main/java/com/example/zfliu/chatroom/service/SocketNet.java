package com.example.zfliu.chatroom.service;

/**
 * Created by sharp on 2/4/2015.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import org.json.JSONObject;

public class SocketNet extends Service {

    private MyBinder mBinder = new MyBinder();
    private Socket client;
    private Writer writer;
    private Reader reader;
    private String host = "172.22.71.216";  //要连接的服务端IP地址
    private int port = 3333;   //要连接的服务端对应的监听端口

    @Override
    public void onCreate() {
        super.onCreate();
        this.askForConnet();
        //this.recFromServerLoop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //this.askForConnet();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //this.recFromServerLoop();
        return mBinder;
    }

    public class MyBinder extends Binder {
        public void startDownload() {
            Log.d("TAG", "startDownload() executed");
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void askForConnet(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //与服务端建立连接
                    client = new Socket(host, port);
                    writer = new OutputStreamWriter(client.getOutputStream());
                    reader = new InputStreamReader(client.getInputStream());
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("name","11");
//                    map.put("img","22");
//                    map.put("op","33");
//                    //将json转化为String类型
//                    JSONObject json = new JSONObject(map);
//                    String jsonString = "";
//                    jsonString = json.toString();
//                    writer.write(jsonString);
                    writer.write("CONNETEDeof");
                    writer.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void recFromServerLoop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 开始执行后台任务
                try {
                    for (;;){
                        Log.d("TAG","asdf");
//                        client = new Socket(host, port);
//                        writer = new OutputStreamWriter(client.getOutputStream());
                        reader = new InputStreamReader(client.getInputStream());
                        char chars[] = new char[1024];
                        int len;
                        StringBuffer sb = new StringBuffer();
                        String temp;
                        int index;

                        while ((len=reader.read(chars)) != -1) {
                            temp = new String(chars, 0, len);
                            if ((index = temp.indexOf("eof")) != -1) {
                                sb.append(temp.substring(0, index));
                                break;
                            }
                            sb.append(new String(chars, 0, len));
                        }
                        Log.d("TAG","from server: " + sb);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendToServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writer = new OutputStreamWriter(client.getOutputStream());
                    writer.write("CONNETEDeof");
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}