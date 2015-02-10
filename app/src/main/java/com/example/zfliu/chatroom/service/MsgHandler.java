package com.example.zfliu.chatroom.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.zfliu.chatroom.ChatActivity;
import com.example.zfliu.chatroom.HelpActivity;
import com.example.zfliu.chatroom.LoginActivity;
import com.example.zfliu.chatroom.R;
import com.example.zfliu.chatroom.database.DBManager;
import com.example.zfliu.chatroom.database.Msg;
import com.example.zfliu.chatroom.files.FListFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

/**
 * Created by zfliu on 2/8/2015.
 */
public class MsgHandler {
    private String type;
    private JSONArray jsonArray;
    private JSONTokener json;
    private Intent intent;
    private Context context;
    private AppUtil app;

    MsgHandler (String str,Context context) throws JSONException {
        this.context = context;
        app = (AppUtil)context.getApplicationContext();
        json =new JSONTokener(str);
        JSONObject info = (JSONObject) json.nextValue();
        type = info.getString("TYPE");
        jsonArray = info.getJSONArray("CONTENT");
    }

    public void judgeType() throws JSONException, IOException {
        intent = new Intent();
        switch (type){
            case "VERIFY":
                verify(jsonArray);
                break;
            case "FRIENDGROUP":
                friendGroupRcv(jsonArray);
                break;
            case "MSG":
                talkEach(jsonArray);
                break;
            case "ADDFRIEND":
                addFriend(jsonArray);
                break;
            case "DELFRIEND":
                delFriend(jsonArray);
                break;
            case "ADDGROUP":
                addGroup(jsonArray);
                break;
            case "DELGROUP":
                delGroup(jsonArray);
                break;
            case "MOVFRIEND":
                movFriend(jsonArray);
                break;
            default:
        }
    }

    public Intent getMyIntent() {
        return intent;
    }
    private void movFriend(JSONArray ja)throws JSONException{
        intent.setAction("MOVFRIEND");
        intent.putExtra("STATE",ja.get(0).toString());
        context.sendBroadcast(intent);
        Log.d("MsgHandler","移动好友");
    }

    private void delGroup(JSONArray ja) throws JSONException{
        intent.setAction("DELGROUP");
        intent.putExtra("STATE",ja.get(0).toString());
        context.sendBroadcast(intent);
        Log.d("MsgHandler","删除组");
    }

    private void addGroup(JSONArray ja) throws JSONException {
        intent.setAction("ADDGROUP");
        intent.putExtra("STATE",ja.get(0).toString());
        context.sendBroadcast(intent);
        Log.d("MsgHandler","添加组");
    }

    private void delFriend(JSONArray ja) throws JSONException {
        intent.setAction("DELFRIEND");
        intent.putExtra("STATE",ja.get(0).toString());
        context.sendBroadcast(intent);
        Log.d("MsgHandler","删除好友");
    }

    private void addFriend(JSONArray ja) throws JSONException {
        intent.setAction("ADDFRIEND");
        intent.putExtra("STATE",ja.get(0).toString());
        context.sendBroadcast(intent);
        Log.d("MsgHandler","添加好友");
    }
    private void verify(JSONArray ja) throws JSONException {
        intent.setAction("VERIFY");
        if (ja.get(0).equals("SUCCESS")){
            intent.putExtra("RESULT","SUCCESS");
        } else {
            intent.putExtra("RESULT","FAIL");
            intent.putExtra("WHY",ja.get(1).toString());
        }
        context.sendBroadcast(intent);
        Log.d("MsgHandler", "身份验证");
    }
    private void friendGroupRcv(JSONArray ja) throws JSONException, IOException {
        FListFile fListFile = new FListFile(context);
        fListFile.clean();
        fListFile.writeFListFile(ja);
    }
    private void talkEach(JSONArray ja) throws JSONException{
        Log.d("MsgHandler","当前Act"+app.getCurrentActivity());
        Log.d("MsgHandler","当前chat"+app.getChatWithWho());
        intent.putExtra("who", ja.get(1).toString());
        intent.putExtra("towho", ja.get(0).toString());
        intent.putExtra("time",ja.get(2).toString());
        intent.putExtra("what", ja.get(3).toString());
        if (app.getCurrentActivity().equals("chat")&&app.getChatWithWho().equals(ja.get(0).toString())) {
            intent.setAction("AMSGCOME");
            context.sendBroadcast(intent);
        } else if (!app.getUserName().equals(ja.get(0).toString())) {
            Log.d("MsgHandler","服务器发来一条消息");
            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext());

            intent.setClass(context.getApplicationContext(),ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentTitle(ja.get(0).toString()+" "+ja.get(2).toString())
                    .setContentText(ja.get(3).toString())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setTicker(ja.get(0).toString()+"："+ja.get(3).toString())
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent);

            mNotificationManager.notify(100, mBuilder.build());
        }
        DBManager dbManager = new DBManager(context.getApplicationContext());
        dbManager.addMsg(new Msg(ja.get(0).toString(),ja.get(1).toString(),ja.get(2).toString(),ja.get(3).toString()));
        dbManager.closeDB();
    }
}
