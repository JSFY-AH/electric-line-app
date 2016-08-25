package com.ahjsfy.www.e_line.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ahjsfy.www.e_line.MainActivity;
import com.ahjsfy.www.e_line.R;
import com.ahjsfy.www.e_line.util.HttpRequest;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    private TextView username;
    private TextView password;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initEvent();
    }
    public void initEvent(){
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new LoginOnClickListener());
    }
    public class LoginOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String usernameVal = username.getText().toString();
            String passwordVal = password.getText().toString();
//            Toast.makeText(LoginActivity.this, usernameVal, Toast.LENGTH_SHORT).show();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("提示信息");
            dialog.setMessage("正在登录......");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            String ServerIP = getResources().getString(R.string.ServerIP);
            String targetUrl = ServerIP + "/user/login";
            Map<String, String> mp = new HashMap<>();
            mp.put("url", targetUrl);
            mp.put("username", usernameVal);
            mp.put("password", passwordVal);

            new LoginAsyncTask().execute(mp);
        }
    }
    public class LoginAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected Map<String, String> doInBackground(Map<String,String>... params) {
            String url = params[0].get("url");
            String parameter = params[0].get("username");
            try {
                parameter = URLEncoder.encode(parameter, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            parameter += "&password=" + params[0].get("password");
            url += "?username=" + parameter;
            HttpRequest httpRequest = new HttpRequest(url);

            try {
                httpRequest.get_connect();
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("zhaolong_2", String.valueOf(e));
                result = null;
            }
            return result;
        }
        protected void onPostExecute(Map<String,String> result){
            dialog.dismiss();
            if(result == null){
                Toast.makeText(LoginActivity.this, "请检查数据连接", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    String valText = result.get("text");
                    try {
                        JSONObject jsonObject = new JSONObject(valText);
                        String user_id = jsonObject.getString("user_id");
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences;
                        SharedPreferences.Editor editor;
                        sharedPreferences = getSharedPreferences("elineSharedPreferences", MODE_WORLD_READABLE);
                        editor = sharedPreferences.edit();
                        editor.putString("user_id", user_id);
                        Intent intent = new Intent(LoginActivity.this, AreaListActivity.class);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);
                        editor.apply();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                        Log.i("el_Login_Exc", String.valueOf(e));
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
