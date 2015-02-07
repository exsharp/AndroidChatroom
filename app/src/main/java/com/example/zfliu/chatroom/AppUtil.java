package com.example.zfliu.chatroom;

import android.app.Application;
import android.util.Log;

import com.example.zfliu.chatroom.friendlist.Apple;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by zfliu on 2/6/2015.
 */
public class AppUtil extends Application {
    private String userName = null;
    private String passWord = null;
    private Socket socket = null;
    private LinkedList<String> group = new LinkedList<String>();
    private LinkedList<LinkedList<Apple>> child = new LinkedList<LinkedList<Apple>>();

    public String getUserName(){
        return userName;
    }
    public void setUser(String name,String word){
        userName = name;
        passWord = word;
    }
    public Socket getSocket(){
        return socket;
    }
    public void setSocket(Socket sck){
        socket = sck;
    }
    public LinkedList<String> getGroup(){
        return group;
    }
    public LinkedList<LinkedList<Apple>> getChild(){
        return child;
    }
    public void setFriendList(JSONArray json) throws JSONException {
        group.add(json.get(0).toString());
        LinkedList<Apple> temp = new LinkedList<Apple>();
        Apple apple;
        for (int i = 1 ;i<json.length();i++){
            apple= new Apple(json.get(i).toString(),0);
            temp.add(apple);
        }
        child.add(temp);
        //Log.d("AppUtil","组名"+group.toString());
        //Log.d("AppUtil",json.get(0).toString()+"有成员"+temp.toString());
    }
}
