package com.example.zfliu.chatroom;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zfliu.chatroom.service.AppUtil;
import com.example.zfliu.chatroom.service.SendMsg;
import com.example.zfliu.chatroom.service.SocketNet;

public class LoginActivity extends ActionBarActivity {

    private Button loginButton;
    private Button registerButton;
    private Button helpButton;
    private String username;
    private String password;
    private Intent mIntent;
    private LoginReceive loginReceive;
    private ProgressDialog progressDialog = null;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginReceive);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent serviceIntent = new Intent(LoginActivity.this,SocketNet.class);
        startService(serviceIntent);

        loginReceive = new LoginReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("VERIFY");
        registerReceiver(loginReceive,filter);

        loginButton = (Button)findViewById(R.id.loginBT);
        registerButton = (Button)findViewById(R.id.registerBT);
        helpButton = (Button) findViewById(R.id.helpBT);

        loginButton.setOnClickListener(new ButtonListener());
        registerButton.setOnClickListener(new ButtonListener());
        helpButton.setOnClickListener(new ButtonListener());
    }
    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.loginBT:
                    progressDialog = ProgressDialog.show(LoginActivity.this, "登陆中", "正在登陆,请稍候！");
                    EditText usernameET = (EditText)findViewById(R.id.usernameET);
                    EditText passwordET = (EditText)findViewById(R.id.passwordET);
                    username = usernameET.getText().toString();
                    password = passwordET.getText().toString();
                    this.login();
                    break;
                case R.id.registerBT:
                    Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    break;
                case R.id.helpBT:
                    Intent tintent = new Intent(LoginActivity.this,HelpActivity.class);
                    startActivity(tintent);
                    break;
                default:
                    System.out.println("未知错误");
                    break;
            }
        }
        private void login(){
            AppUtil app = (AppUtil)getApplication();
            SendMsg sendMsg = new SendMsg("LOGIN",app.getWriter());
            app.setUser(username,password);
            //String str[] = new String[]{username, password};
            String str[] = new String[]{"aaa","111"};
            app.setUser("aaa","111");
            sendMsg.setJSON(str);
        }
    }

    private class LoginReceive extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String getResult;
            String why;
            getResult = intent.getStringExtra("RESULT");
            progressDialog.dismiss();
            if (getResult.equals("SUCCESS")){
                mIntent = new Intent(LoginActivity.this,FragmentActivity.class);
                mIntent.putExtra("WHO",username);
                startActivity(mIntent);
                LoginActivity.this.finish();
            }else {
                why = intent.getStringExtra("WHY");
                switch (why){
                    case "NOFOUND":
                        Toast.makeText(LoginActivity.this,"没有此用户",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
