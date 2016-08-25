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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ahjsfy.www.e_line.MainActivity;
import com.ahjsfy.www.e_line.R;
import com.ahjsfy.www.e_line.util.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineListActivity extends AppCompatActivity {
    private ListView lineList;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_list);
        initListener();
    }
    public void initListener(){
        lineList = (ListView)findViewById(R.id.lineList);
        bulidListView();
    }
    public void bulidListView(){
        dialog = new ProgressDialog(LineListActivity.this);
        dialog.setTitle("提示信息");
        dialog.setMessage("获取数据......");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);

        Intent intent = getIntent();
        String area_id = intent.getStringExtra("area_id");
        String ServerIP = getResources().getString(R.string.ServerIP);
        String targetUrl = ServerIP + "/user/linelist";
        Map<String, String> mp = new HashMap<>();
        mp.put("url", targetUrl);
        mp.put("area_id", area_id);
        Log.i("zhaolongLineMp", String.valueOf(mp));
        new GetLineListAsyncTask().execute(mp);
    }
    public class GetLineListAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute(){
            dialog.show();
        }
        @Override
        protected Map<String, String> doInBackground(Map<String,String>... params) {
            String url = params[0].get("url");
            String parameter = params[0].get("area_id");
            url += "?area_id=" + parameter;
            Log.i("zhaolongLineList", String.valueOf(url));
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
                Toast.makeText(LineListActivity.this, "请检查数据连接", Toast.LENGTH_SHORT).show();
            }else{
                if(result.get("code").equals("200")){
                    String valText = result.get("text");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(valText);
                        String query_result = jsonObject.getString("result");
                        Log.i("zhaolong___1", query_result);
                        if(query_result.equals("success")){
                            JSONArray query_data = jsonObject.getJSONArray("lineData");
                            Log.i("zhaolong___", String.valueOf(query_data));
                            int len = query_data.length();
                            List<Map<String,String>> listItems = new ArrayList<>();
                            for(int i = 0; i < len; ++i){
                                Map<String, String> map = new HashMap<>();
                                JSONObject tmp = query_data.getJSONObject(i);
                                map.put("title", tmp.getString("LineName"));
                                map.put("line_id",tmp.getString("LineID"));
                                listItems.add(map);
                            }
                            SimpleAdapter adapter = new SimpleAdapter(LineListActivity.this, listItems,
                                    R.layout.area_list_item, new String[]{"title",
                                    "line_id"}, new int[]{R.id.title, R.id.line_id});
                            lineList.setAdapter(adapter);
                            lineList.setOnItemClickListener(new LinesItemClickListener());
                        }else{
                            Toast.makeText(LineListActivity.this, "查询失败11", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LineListActivity.this, "查询失败22", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LineListActivity.this, "查询失败33", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class LinesItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, String> currentItem = (HashMap<String, String>) lineList.getItemAtPosition(position);
            String line_id = currentItem.get("line_id");
//            Toast.makeText(LineListActivity.this, line_id, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
//            Intent传递参数
            intent.putExtra("line_id", line_id);
            intent.setClass(LineListActivity.this, MainActivity.class);
            LineListActivity.this.startActivity(intent);
        }
    }

}
