package com.ahjsfy.www.e_line;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String line_id = intent.getStringExtra("line_id");
        WebView webView = (WebView)findViewById(R.id.webview);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setUseWideViewPort(true);
        String ServerIP = getResources().getString(R.string.ServerIP);
        String targetUrl = ServerIP + "/graph/?line_id=" + line_id + "&edit_right=0";
        webView.loadUrl(targetUrl);
    }
}
