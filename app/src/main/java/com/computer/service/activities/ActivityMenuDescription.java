package com.computer.service.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.computer.service.R;

public class ActivityMenuDescription extends AppCompatActivity {
    Intent i;
    String desc;
    WebView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_description);
        i = getIntent();
        desc = i.getStringExtra("desc");
        txtDescription = (WebView) findViewById(R.id.txtDescription);
        txtDescription.loadDataWithBaseURL("", desc, "text/html", "UTF-8", "");
        txtDescription.setBackgroundColor(Color.parseColor("#ffffff"));

        txtDescription.getSettings().setDefaultTextEncodingName("UTF-8");
        WebSettings webSettings = txtDescription.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

    }
}
