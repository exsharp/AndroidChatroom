package com.example.zfliu.chatroom.service;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by zfliu on 2/5/2015.
 */
public class MyHandlerMsg {
    private String type;
    private JSONArray jsonArray;
    private JSONTokener json;
    private Intent rtIntent;

    MyHandlerMsg (String str) throws JSONException {
        json =new JSONTokener(str);
        JSONObject info = (JSONObject) json.nextValue();
        type = info.getString("TYPE");
        jsonArray = info.getJSONArray("CONTENT");
        //System.out.println(type+jsonArray.get());
    }
    public Intent handler() throws JSONException {
        switch (type){
            case "VERIFY":
                rtIntent = verify(jsonArray);
                break;
            default:
        }
        return rtIntent;
    }
    private Intent verify(JSONArray ja) throws JSONException {
        Intent intent = new Intent();
        intent.setAction("VERIFY");
        if (ja.get(0).equals("SUCCESS")){
            intent.putExtra("RESULT","SUCCESS");
        } else {
            intent.putExtra("RESULT","FAIL");
            intent.putExtra("WHY",ja.get(1).toString());
        }
        return intent;
    }
}