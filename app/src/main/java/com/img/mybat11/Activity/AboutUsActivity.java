package com.img.mybat11.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;

public class AboutUsActivity extends AppCompatActivity {

    MainActivity ma;
    UserSessionManager session;
    ConnectionDetector cd;
    GlobalVariables gv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        ma = new MainActivity();
        cd = new ConnectionDetector(getApplicationContext());
        gv=(GlobalVariables)getApplicationContext();
        session = new UserSessionManager(getApplicationContext());

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("About Us");


        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        WebView web= (WebView)findViewById(R.id.web);
        web.loadUrl("file:///android_asset/about_us.html");
    }
}
