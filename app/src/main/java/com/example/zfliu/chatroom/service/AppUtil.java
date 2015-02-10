package com.example.zfliu.chatroom.service;

import android.app.Application;
import android.util.Log;

import com.example.zfliu.chatroom.friendlist.Apple;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by zfliu on 2/6/2015.
 */
public class AppUtil extends Application {
    private String userName = null;
    private String passWord = null;
    private String currentActivity = "";
    private String chatWithWho = "";
    private BufferedWriter writer;

    public String getUserName(){
        return userName;
    }
    public void setUser(String name,String word){
        userName = name;
        passWord = word;
    }

    public void setCurrentActivity(String activity){
        currentActivity = activity;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setChatWithWho(String who) {
        chatWithWho = who;
    }

    public String getChatWithWho() {
        return chatWithWho;
    }

    public void setWriter(BufferedWriter writer){
        this.writer = writer;
    }

    public BufferedWriter getWriter(){
        return writer;
    }
}
