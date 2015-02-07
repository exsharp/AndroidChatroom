package com.example.zfliu.chatroom.database;

/**
 * Created by zfliu on 2/7/2015.
 */
public class Msg {
    public String user;
    public String towho;
    public String msg;
    public String time;

    public Msg(){

    }
    public Msg(String user,String towho,String time,String msg){
        this.user = user;
        this.towho = towho;
        this.time = time;
        this.msg = msg;
    }
}
