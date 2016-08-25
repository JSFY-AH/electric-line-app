package com.ahjsfy.www.e_line.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ahjsfy.www.e_line.R;
import com.ahjsfy.www.e_line.util.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaListActivity extends AppCompatActivity {
    private ListView areaList;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_list);
        initListener();
    }
    public void initListener(){
        areaList = (ListView)findViewById(R.id.areaList);
        bulidListView();
    }
    public void bulidListView(){
        dialog = new ProgressDialog(AreaListActivity.this);
        dialog.setTitle("提示信息");
        dialog.setMessage("获取数据......");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);

        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("elineSharedPreferences", MODE_WORLD_READABLE);
        String user_id = sharedPreferences.getString("user_id", null);
        String ServerIP = getResources().getString(R.string.ServerIP);
        String targetUrl = ServerIP + "/user/arealist";
        Map<String, String> mp = new HashMap<>();
        mp.put("url", targetUrl);
        mp.put("user_id", user_id);
        Log.i("zhaolong", String.valueOf(mp));
        new GetAreaListAsyncTask().execute(mp);
    }
    public class GetAreaListAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected Map<String, String> doInBackground(Map<String,String>... params) {
            String url = params[0].get("url");
            String parameter = params[0].get("user_id");
            url += "?user_id=" + parameter;
            HttpRequest httpRequest = new HttpRequest(url);
            try {
                httpRequest.get_connect();
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(Map<String,String> result){
            dialog.dismiss();
            if(result == null){
                Toast.makeText(AreaListActivity.this, "请检查数据连接", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    String valText = result.get("text");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(valText);
                        String query_result = jsonObject.getString("result");
                        Log.i("zhaolong___1", query_result);
                        if(query_result.equals("success")){
                            JSONArray query_data = jsonObject.getJSONArray("areaData");
                            Log.i("zhaolong___", String.valueOf(query_data));
                            int len = query_data.length();
                            List<Map<String,String>> listItems = new ArrayList<>();
                            for(int i = 0; i < len; ++i){
                                Map<String, String> map = new HashMap<>();
                                JSONObject tmp = query_data.getJSONObject(i);
                                map.put("title", tmp.getString("Name"));
//                                map.put("phone",tmp.getString("Phone"));
//                                map.put("time",tmp.getString("Ver"));
                                map.put("area_id",tmp.getString("AreaID"));
                                listItems.add(map);
                            }
                            SimpleAdapter adapter = new SimpleAdapter(AreaListActivity.this, listItems,
                                    R.layout.area_list_item, new String[]{"title",
                                    "area_id"}, new int[]{R.id.title, R.id.area_id});
                            areaList.setAdapter(adapter);
                            areaList.setOnItemClickListener(new AreasItemClickListener());
                        }else{
                            Toast.makeText(AreaListActivity.this, "查询失败1", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AreaListActivity.this, "查询失败2", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AreaListActivity.this, "查询失败3", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class AreasItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> currentItem = (HashMap<String, String>) areaList.getItemAtPosition(position);
            String area_id = currentItem.get("area_id");
//            Toast.makeText(AreaListActivity.this, area_id, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
//            Intent传递参数
            intent.putExtra("area_id", area_id);
            intent.setClass(AreaListActivity.this, LineListActivity.class);
            AreaListActivity.this.startActivity(intent);
        }
    }
}
