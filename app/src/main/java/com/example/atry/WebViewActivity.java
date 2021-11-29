package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;

public class WebViewActivity extends AppCompatActivity {
    private WebView mwbserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mwbserver=findViewById(R.id.servertips);
        mwbserver.loadUrl("file:///android_asset/云集读书软件许可及服务协议.html");
        WebSettings settings = mwbserver.getSettings();
        settings.setDomStorageEnabled(true);
    }
}
