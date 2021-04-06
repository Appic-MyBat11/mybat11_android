package com.img.mybat11.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Adapter.referListAdater;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.ReferuserGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

public class InvitedFriendsActivity extends AppCompatActivity {

    ArrayList<ReferuserGetSet> list;
    GlobalVariables gv;
    TextView totalJoined;
    ListView referList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_friends);

        gv=(GlobalVariables)getApplicationContext();
        list = gv.getReferList();

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("My Referral");

        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        totalJoined=(TextView)findViewById(R.id.totalJoined);
        totalJoined.setText(list.size()+" friends Joined");

        referList=(ListView)findViewById(R.id.referList);
        referList.setAdapter(new referListAdater(this,list));

    }
}
