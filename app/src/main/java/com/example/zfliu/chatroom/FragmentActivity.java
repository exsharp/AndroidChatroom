package com.example.zfliu.chatroom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.zfliu.chatroom.fragment.FListFragment;
import com.example.zfliu.chatroom.fragment.InfoFragment;


public class FragmentActivity extends ActionBarActivity implements View.OnClickListener{

    private FListFragment fListFragment;
    private InfoFragment infoFragment;

    private Button fListBT;
    private Button infoBT;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        initViews();
        fragmentManager = getSupportFragmentManager();
        setTabSelection(1);
    }

    private void initViews(){
        fListBT = (Button)findViewById(R.id.fListBT);
        infoBT = (Button)findViewById(R.id.infoBT);
        fListBT.setOnClickListener(this);
        infoBT.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fListBT:
                Log.d("FRAGMENT","按了好友列表按钮");
                setTabSelection(1);
                break;
            case R.id.infoBT:
                Log.d("FRAGMENT","按了信息按钮");
                setTabSelection(0);
                break;
            default:
        }
    }

    private void setTabSelection(int index){
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index){
            case 0:
                if (infoFragment == null) {
                    infoFragment = new InfoFragment();
                    transaction.add(R.id.content, infoFragment);
                } else {
                    transaction.show(infoFragment);
                }
                break;
            case 1:
                if (fListFragment == null) {
                    fListFragment = new FListFragment();
                    transaction.add(R.id.content, fListFragment);
                } else {
                    transaction.show(fListFragment);
                }
                break;
            default:
        }
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fListFragment != null) {
            transaction.hide(fListFragment);
        }
        if (infoFragment != null) {
            transaction.hide(infoFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frag, menu);
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
