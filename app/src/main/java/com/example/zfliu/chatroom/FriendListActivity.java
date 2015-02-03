package com.example.zfliu.chatroom;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;

import com.example.zfliu.chatroom.friendlist.Apple;
import com.example.zfliu.chatroom.friendlist.ExpandableAdapter;


public class FriendListActivity extends ActionBarActivity {

    private ExpandableListView expandableListView;
    private LinkedList<String> groups = new LinkedList<String>();
    private LinkedList<LinkedList<Apple>> children = new LinkedList<>();
    private ExpandableAdapter adapter;
    private String userName="";
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent=getIntent();
        userName = intent.getStringExtra("username");

        setContentView(R.layout.activity_friend_list);

        LinkedList<Apple> l1= new LinkedList<Apple>();
        l1.add(new Apple("aaa",1));
        l1.add(new Apple("bbb",2));


        LinkedList<Apple> l2=new LinkedList<Apple>();
        l2.add(new Apple("ccc",3));
        l2.add(new Apple("ddd",4));

        children.add(l1);
        children.add(l2);

        groups.add("第一组");
        groups.add("第二组");

        adapter = new ExpandableAdapter(this,groups,children);
        expandableListView = (ExpandableListView) findViewById(R.id.list);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
                //Toast.makeText(getApplicationContext(), "你点击了" + adapter.getChild(groupPosition, childPosition), Toast.LENGTH_SHORT).show();
                intent = new Intent(FriendListActivity.this,ChatActivity.class);
                intent.putExtra("TOWHO",adapter.getChild(groupPosition,childPosition).toString());
                intent.putExtra("WHO",userName);
                startActivity(intent);
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_list, menu);
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
