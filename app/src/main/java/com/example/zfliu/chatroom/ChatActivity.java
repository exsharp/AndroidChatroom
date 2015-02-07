package com.example.zfliu.chatroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.example.zfliu.chatroom.chat.*;
import com.example.zfliu.chatroom.database.DBManager;
import com.example.zfliu.chatroom.database.Msg;
import com.example.zfliu.chatroom.service.AppUtil;
import com.example.zfliu.chatroom.service.SendMsg;

public class ChatActivity extends ActionBarActivity {

    private DBManager dbManager;

    private LinkedList<Bean> beans = null;
    /** 聊天message 格式 */
    private ListView listView;
    /** 信息编辑框 */
    private EditText edt;
    /** 信息发送按钮 */
    private Button btnEnter;
    private CustomAdapter adapter;
    private String toWho;
    private String who;
    private ChatReceiver chatReceiver = new ChatReceiver();

    @Override
    protected void onPause() {
        super.onPause();
        beans = new LinkedList<Bean>();
        this.fillMsgList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //获取数据库管理
        dbManager = new DBManager(this);
        //获得正在两天的两个人信息
        Intent getIntent = getIntent();
        who = getIntent.getStringExtra("WHO");
        toWho = getIntent.getStringExtra("ToWho");
        //注册接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("AMSGCOME");
        registerReceiver(chatReceiver,intentFilter);
        Log.d("CHAT","chat注册广播接收器");

        beans = new LinkedList<Bean>();
        this.fillMsgList();
        setContentView(R.layout.activity_chat);
        initViewsMethod();
        onHandleMethod();
    }

    //处理listView的item方法
    private void initViewsMethod(){
        listView = (ListView)findViewById(R.id.lvMessages);
        edt = (EditText)findViewById(R.id.edt);
        btnEnter = (Button)findViewById(R.id.enter);

        listView.setOnCreateContextMenuListener(
                new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle("提示：");
                        menu.setHeaderIcon(android.R.drawable.stat_notify_error);
                        menu.add(0,0,1,"删除");
                        menu.add(1,1,0,"取消");
                    }
                }
        );

    }

    //处理发送消息的方法
    public void onHandleMethod(){
        adapter = new CustomAdapter(this,beans);
        listView.setAdapter(adapter);
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = edt.getText().toString().trim();
                if (txt.equals("")){
                    Toast.makeText(getApplicationContext(), "发送内容不能为空 !", Toast.LENGTH_SHORT).show();
                }else{

                    String time = DateFomats.getCurrentTime(new Date().getTime());
                    AppUtil app = (AppUtil)getApplication();
                    SendMsg sendMsg = new SendMsg("MSG",app.getWriter());
                    String str[] = new String[]{who,toWho,time,txt};
                    sendMsg.setJSON(str);

                    adapter.addItemNotifiChange(new Bean(txt, R.drawable.me, DateFomats.getCurrentTime(new Date().getTime()), 0));
                    edt.setText("");
                    listView.setSelection(beans.size() - 1);

                    dbManager.addMsg(new Msg(who,toWho,DateFomats.getCurrentTime(new Date().getTime()),txt));
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0 :
                Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Bean bean = (Bean) adapter.getItem(info.position);
                beans.remove(bean);
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private class ChatReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("CHATRCV","chat广播接收器");
            intent.getStringExtra("who");
            String msg = intent.getStringExtra("what");
            String time = intent.getStringExtra("time");
            adapter.addItemNotifiChange(new Bean(msg, R.drawable.you, time, 1));
            listView.setSelection(beans.size() - 1);
        }
    }

    private void fillMsgList() {
        LinkedList<Msg> iSay = new LinkedList<>(dbManager.Query(who, toWho));
        LinkedList<Msg> tSay = new LinkedList<>(dbManager.Query(toWho, who));
        while(!iSay.isEmpty()&&!tSay.isEmpty()) {
            if (iSay.getFirst().time.compareTo(tSay.getFirst().time) <= 0) {
                String msg = iSay.getFirst().msg;
                String time = iSay.getFirst().time;
                Bean bean = new Bean(msg, R.drawable.me, time, 0);
                beans.add(bean);
                iSay.pop();
            } else {
                String msg = tSay.getFirst().msg;
                String time = tSay.getFirst().time;
                Bean bean = new Bean(msg, R.drawable.you, time, 1);
                beans.add(bean);
                tSay.pop();
            }
        }
        while (!iSay.isEmpty()){
            String msg = iSay.getFirst().msg;
            String time = iSay.getFirst().time;
            Bean bean = new Bean(msg, R.drawable.me, time, 0);
            beans.add(bean);
            iSay.pop();
        }
        while (!tSay.isEmpty()) {
            String msg = tSay.getFirst().msg;
            String time = tSay.getFirst().time;
            Bean bean = new Bean(msg, R.drawable.you, time, 1);
            beans.add(bean);
            tSay.pop();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(chatReceiver);
        super.onDestroy();
        dbManager.closeDB();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
