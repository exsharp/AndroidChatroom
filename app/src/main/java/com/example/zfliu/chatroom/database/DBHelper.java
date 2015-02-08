package com.example.zfliu.chatroom.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zfliu.chatroom.service.AppUtil;

/**
 * Created by zfliu on 2/7/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chatmsg.db";
    private static final int DATABASE_VERSION = 1;
    private String tableName="";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //第一次建立数据库的时候用到
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS chatlog" +
                "(user VARCHAR, towho VARCHAR, time VARCHAR, msg TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE chatlog ADD COLUMN other STRING");
    }
}
