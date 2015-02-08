package com.example.zfliu.chatroom.files;

import android.content.Context;
import android.util.Log;

import com.example.zfliu.chatroom.chat.Bean;
import com.example.zfliu.chatroom.fragment.FListFragment;
import com.example.zfliu.chatroom.friendlist.Apple;
import com.example.zfliu.chatroom.service.AppUtil;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by zfliu on 2/8/2015.
 */
public class FListFile {

    private Context context;
    private String fileName;

    public FListFile(Context context){
        this.context=context;
        AppUtil app = (AppUtil)context.getApplicationContext();
        fileName = app.getUserName()+"'s_FriendList";
    }

    public void writeFListFile(JSONArray json) throws IOException {
        try {
            FileOutputStream fout = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter os = new OutputStreamWriter(fout);
            BufferedWriter br = new BufferedWriter(os);
            for (int i = 0; i < json.length(); i++) {
                if (json.get(i).toString().equals("")){
                    i++;
                    br.write("GroupName:" + json.get(i).toString());
                }else{
                    br.write(json.get(i).toString());
                }
                br.newLine();
            }
            br.flush();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public LinkedList<String> getGroup() throws IOException {
        LinkedList<String> group = new LinkedList<>();
        FileInputStream fin = context.openFileInput(fileName);
        InputStreamReader is = new InputStreamReader(fin);
        BufferedReader br = new BufferedReader(is);
        String temp = "";
        while ((temp=br.readLine())!=null){
            if (temp.indexOf("GroupName:")>-1){
                String[] arr = temp.split(":");
                group.add(arr[1]);
            }
        }
        Log.d("FListFile","读完");
        return group;
    }

    public LinkedList<LinkedList<Apple>> getChild() throws IOException {
        LinkedList<LinkedList<Apple>> child = new LinkedList<LinkedList<Apple>>();
        FileInputStream fin = context.openFileInput(fileName);
        InputStreamReader is = new InputStreamReader(fin);
        BufferedReader br = new BufferedReader(is);
        String str = "";
        LinkedList<Apple> temp = new LinkedList<Apple>();
        br.readLine();
        while ((str=br.readLine())!=null) {
            Log.d("FListFile", str);
            if (str.indexOf("GroupName:") > -1) {
                LinkedList<Apple> LL = new LinkedList<Apple>(temp);
                child.add(LL);
                temp.clear();
                Log.d("FListFile", "child\t"+child.toString());
            } else {
                temp.add(new Apple(str, 0));
                Log.d("FListFile","temp\t"+temp.toString());
            }
        }
        child.add(temp);
        Log.d("FListFile", child.toString());
        return child;
    }

    private LinkedList<Apple> fuck(LinkedList<Apple> temp){
        LinkedList<Apple> LL = new LinkedList<Apple>(temp);
        return LL;
    }

}
