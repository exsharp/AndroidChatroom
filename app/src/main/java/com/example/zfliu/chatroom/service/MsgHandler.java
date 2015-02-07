package com.example.zfliu.chatroom.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.zfliu.chatroom.database.DBManager;
import com.example.zfliu.chatroom.database.Msg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by zfliu on 2/8/2015.
 */
public class MsgHandler {
    private String type;
    private JSONArray jsonArray;
    private JSONTokener json;
    private Intent rtIntent;
    private Context context;

    MsgHandler (String str,Context context) throws JSONException {
        this.context = context;
        json =new JSONTokener(str);
        JSONObject info = (JSONObject) json.nextValue();
        type = info.getString("TYPE");
        jsonArray = info.getJSONArray("CONTENT");
    }

    public void judgeType() throws JSONException {
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
    private void friendGroupRcv(JSONArray ja) throws JSONException{
        AppUtil app = (AppUtil)context.getApplicationContext();
        app.setFriendList(ja);
    }
    private void talkEach(JSONArray ja) throws JSONException {
        rtIntent = new Intent();
        rtIntent.setAction("AMSGCOME");
        rtIntent.putExtra("who", ja.get(0).toString());
        rtIntent.putExtra("towho", ja.get(1).toString());
        rtIntent.putExtra("time",ja.get(2).toString());
        rtIntent.putExtra("what", ja.get(3).toString());
        context.sendBroadcast(rtIntent);
        DBManager dbManager = new DBManager(context.getApplicationContext());
        dbManager.addMsg(new Msg(ja.get(0).toString(),ja.get(1).toString(),ja.get(2).toString(),ja.get(3).toString()));
        dbManager.closeDB();
        Log.d("MsgHandler","服务器发来一条消息");
    }
}
