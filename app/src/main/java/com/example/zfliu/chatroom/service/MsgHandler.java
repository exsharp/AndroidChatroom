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
    private Intent rtIntent;
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
            default:
        }
    }

    public Intent getMyIntent() {
        return rtIntent;
    }

    private void verify(JSONArray ja) throws JSONException {
        rtIntent = new Intent();
        rtIntent.setAction("VERIFY");
        if (ja.get(0).equals("SUCCESS")){
            rtIntent.putExtra("RESULT","SUCCESS");
        } else {
            rtIntent.putExtra("RESULT","FAIL");
            rtIntent.putExtra("WHY",ja.get(1).toString());
        }
        context.sendBroadcast(rtIntent);
        Log.d("MsgHandler", "身份验证");
    }
    private void friendGroupRcv(JSONArray ja) throws JSONException, IOException {
//        AppUtil app = (AppUtil)context.getApplicationContext();
//        //app.setFriendList(ja);
        FListFile fListFile = new FListFile(context);
        fListFile.writeFListFile(ja);
    }
    private void talkEach(JSONArray ja) throws JSONException{
        Log.d("MsgHandler","当前Act"+app.getCurrentActivity());
        Log.d("MsgHandler","当前chat"+app.getChatWithWho());
        rtIntent = new Intent();
        rtIntent.putExtra("who", ja.get(0).toString());
        rtIntent.putExtra("towho", ja.get(1).toString());
        rtIntent.putExtra("time",ja.get(2).toString());
        rtIntent.putExtra("what", ja.get(3).toString());
        if (app.getCurrentActivity().equals("chat")&&app.getChatWithWho().equals(ja.get(0).toString())){
            rtIntent.setAction("AMSGCOME");
            context.sendBroadcast(rtIntent);
        } else if (!app.getUserName().equals(ja.get(0).toString())) {
            Log.d("MsgHandler","服务器发来一条消息");
            NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext());

            rtIntent.setClass(context.getApplicationContext(),ChatActivity.class);
            rtIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0,rtIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentTitle(ja.get(0).toString())
                    .setContentText(ja.get(1).toString())
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .setTicker(ja.get(0).toString()+"："+ja.get(1).toString())
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
