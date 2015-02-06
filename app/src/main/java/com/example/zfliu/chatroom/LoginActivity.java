package com.example.zfliu.chatroom;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zfliu.chatroom.service.SocketNet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ActionBarActivity {

    private Button loginButton;
    private Button registerButton;
    private String username;
    private String password;
    private Handler handler;
    private TextView textView;
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

        Intent loginIntent = new Intent(LoginActivity.this,SocketNet.class);
        startService(loginIntent);

        loginReceive = new LoginReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("VERIFY");
        registerReceiver(loginReceive,filter);

        loginButton = (Button)findViewById(R.id.loginBT);
        registerButton = (Button)findViewById(R.id.registerBT);

        loginButton.setOnClickListener(new ButtonListener());
        registerButton.setOnClickListener(new ButtonListener());
    }
    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            textView = (TextView)findViewById(R.id.showTV);
            switch (v.getId()){
                case R.id.loginBT:
                    EditText usernameET = (EditText)findViewById(R.id.usernameET);
                    EditText passwordET = (EditText)findViewById(R.id.passwordET);
                    username = usernameET.getText().toString();
                    password = passwordET.getText().toString();
                    progressDialog = ProgressDialog.show(LoginActivity.this, "登陆中", "正在登陆,请稍候！");
                    this.login();
                    break;
                case R.id.registerBT:
                    Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    break;
                default:
                    System.out.println("未知错误");
                    break;
            }
        }
        private void login(){
            Intent loginIntent = new Intent();
            loginIntent.setAction("LOGIN");
            loginIntent.putExtra("username",username);
            loginIntent.putExtra("password",password);
            sendBroadcast(loginIntent);
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
                mIntent = new Intent(LoginActivity.this,FriendListActivity.class);
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
                        Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
