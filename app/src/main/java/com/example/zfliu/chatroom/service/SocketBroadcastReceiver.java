package com.example.zfliu.chatroom.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by zfliu on 2/5/2015.
 */
class SocketBroadcastReceiver extends BroadcastReceiver {

    private BufferedWriter Writer;

    SocketBroadcastReceiver(BufferedWriter out){
        Writer=out;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String jsonString = "";
        final String TYPE = "TYPE";
        final String CONTENT = "CONTENT";

        switch (action){
            case "LOGIN":
                String username = intent.getStringExtra("username");
                String password = intent.getStringExtra("password");
                jsonArray.put(username);
                jsonArray.put(password);
                try {
                    jsonObject.put(TYPE, "LOGIN");
                    jsonObject.put(CONTENT, jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonString = jsonObject.toString();
                break;
            default:
        }
        Log.d("SBR", "Socket广播");
        this.sentMsg(jsonString);
    }

    public void sentMsg(final String str){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Writer.write(str);
                    Writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}