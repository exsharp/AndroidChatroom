package com.example.zfliu.chatroom;

import android.content.DialogInterface;
import android.content.Intent;
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
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        loginButton = (Button)findViewById(R.id.loginBT);
        registerButton = (Button)findViewById(R.id.registerBT);

        handler = new ShowRespHandler();

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

                    //正式用
                    //LoginNetWorkThread login= new LoginNetWorkThread();
                    //login.start();

                    //测试用
                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    intent = new Intent(LoginActivity.this,FriendListActivity.class);
                    intent.putExtra("WHO",username);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    //

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
    }

    class ShowRespHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String s = (String)msg.obj;
            if (s.equals("success")){
                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                intent = new Intent(LoginActivity.this,FriendListActivity.class);
                intent.putExtra("WHO",username);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
            }
        }
    }

    class LoginNetWorkThread extends Thread {
        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            String url = getString(R.string.server_home_url).toString()+"/login";
            try{
                HttpPost httpPost = new HttpPost(url);

                List params = new ArrayList();
                params.add(new BasicNameValuePair("username",username));
                params.add(new BasicNameValuePair("password",password));

                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse httpResponse = httpClient.execute(httpPost);

                if (200==httpResponse.getStatusLine().getStatusCode()){
                    String content = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
                    Message msg = handler.obtainMessage();
                    msg.obj=content;
                    handler.sendMessage(msg);
                }else {
                    Message msg = handler.obtainMessage();
                    msg.obj="网络错误";
                    handler.sendMessage(msg);
                }

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
