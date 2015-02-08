package com.example.zfliu.chatroom.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by zfliu on 2/7/2015.
 */
public class SendMsg {
    private BufferedWriter writer;
    private String type;
    private final String TYPE = "TYPE";
    private final String CONTENT = "CONTENT";
    private JSONObject jsonObject = new JSONObject();
    private JSONArray jsonArray = new JSONArray();
    private String jsonString = "";

    public SendMsg(String type,Context context){
        this.type = type;
        AppUtil app = (AppUtil)context.getApplicationContext();
        this.writer = app.getWriter();
    }

    public SendMsg(String type,BufferedWriter writer){
        this.type = type;
        this.writer = writer;
    }

    public void setJSON(String arr[]){
        for (int i = 0; i < arr.length; i++) {
            jsonArray.put(arr[i]);
        }
        this.putJSONarray();
        this.sentMsg(jsonString);
    }

    public void setJSON(JSONArray jsonArray){
        this.jsonArray = jsonArray;
        this.putJSONarray();
        this.sentMsg(jsonString);
    }

    private void putJSONarray(){
        try {
            jsonObject.put(TYPE, type);
            jsonObject.put(CONTENT, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonString = jsonObject.toString();
    }

    private void sentMsg(final String str){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writer.write(str);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void reLogin(){

    }
}
