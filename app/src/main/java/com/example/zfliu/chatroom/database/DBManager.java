package com.example.zfliu.chatroom.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zfliu on 2/7/2015.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
        Log.d("DBMANAGER", "构造DBMGR");
    }

    public void addMsg(Msg msg) {
        db.execSQL("INSERT INTO chatlog VALUES(?, ?, ?, ?)", new Object[]{msg.user, msg.towho, msg.time, msg.msg});
        Log.d("DBMANAGER", "DBMGR插入数据");
    }

    public void delMsg(Msg msg){
        db.execSQL("DELETE FROM chatlog VALUES(?, ?, ?, ?)", new Object[]{msg.user, msg.towho, msg.time, msg.msg});
        Log.d("DBMANAGER", "DBMGR删除数据");
    }

    public List<Msg> Query(String who,String withwho){
        ArrayList<Msg> msgs = new ArrayList<>();
        Cursor c = queryTheCursor(who,withwho);
        while(c.moveToNext()){
            Log.d("DBMANAGER", "DBMGR提取数据数据");
            Msg msg = new Msg();
            msg.user = c.getString(c.getColumnIndex("user"));
            msg.towho = c.getString(c.getColumnIndex("towho"));
            msg.time = c.getString(c.getColumnIndex("time"));
            msg.msg = c.getString(c.getColumnIndex("msg"));
            msgs.add(msg);
        }
        c.close();
        return msgs;
    }

    public Cursor queryTheCursor(String who,String withwho){
        Cursor c = db.rawQuery("SELECT * FROM chatlog where user=? and towho=?", new String[]{who,withwho});
        return c;
    }

    public void closeDB(){
        db.close();
        Log.d("DBMANAGER", "关闭DB");
    }
}
