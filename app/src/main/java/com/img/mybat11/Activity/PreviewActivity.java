package com.img.mybat11.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    ArrayList<captainListGetSet> list,listWK,listBAT,listAR,listBALL;
    LinearLayout wklayout,batlayout,allayout,ballayout;

    int TeamID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);


        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();

        list = gv.getCaptainList();
        TeamID=getIntent().getExtras().getInt("TeamID");

        gv.setCaptainList(new ArrayList<captainListGetSet>());

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title=(TextView)findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("team_name"));

        wklayout=(LinearLayout)findViewById(R.id.wklayout);
        batlayout=(LinearLayout)findViewById(R.id.batlayout);
        allayout=(LinearLayout)findViewById(R.id.allayout);
        ballayout=(LinearLayout)findViewById(R.id.ballayout);

        listWK = new ArrayList<>();
        listBAT = new ArrayList<>();
        listAR = new ArrayList<>();
        listBALL = new ArrayList<>();

        for(captainListGetSet zz:list){
            Log.i("Role",zz.getRole());
            if(zz.getRole().equals("Wk"))
                listWK.add(zz);
            else if(zz.getRole().equals("Bat"))
                listBAT.add(zz);
            else if(zz.getRole().equals("AR"))
                listAR.add(zz);
            else if(zz.getRole().equals("Bow"))
                listBALL.add(zz);
        }

        Player(wklayout,listWK);
        Player(batlayout,listBAT);
        Player(allayout,listAR);
        Player(ballayout,listBALL);

    }

    public void Player(LinearLayout ll, final ArrayList<captainListGetSet> list){
        ll.removeAllViews();
        for (int f = 0 ;f < list.size();f++){

            View v;
            LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v= inflater.inflate(R.layout.preview_layout,null);

            ImageView image;
            TextView name,credit,cap,vc;

            image = v.findViewById(R.id.image);

            name = v.findViewById(R.id.name);
            credit = v.findViewById(R.id.credit);
            cap = v.findViewById(R.id.cap);
            vc = v.findViewById(R.id.vc);

            if (list.get(f).getCaptain().equals("1")){
                cap.setVisibility(View.VISIBLE);
                list.get(f).setPoints(String.valueOf(Double.parseDouble(list.get(f).getPoints())*2.0));
            }else
                cap.setVisibility(View.GONE);

            if (list.get(f).getVc().equals("1")) {
                vc.setVisibility(View.VISIBLE);
                list.get(f).setPoints(String.valueOf(Double.parseDouble(list.get(f).getPoints())*1.5));
            }else
                vc.setVisibility(View.GONE);

            Picasso.with(PreviewActivity.this).load(list.get(f).getImage()).placeholder(R.drawable.avtar).into(image);
            name.setText(list.get(f).getName());
            credit.setText(list.get(f).getPoints()+" PTS");

            if(list.get(f).getTeam().equals("team1"))
                name.setBackground(getResources().getDrawable(R.drawable.blue_name_bg));
            else
                name.setBackground(getResources().getDrawable(R.drawable.red_name_bg));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            params.gravity = Gravity.CENTER;
            v.setLayoutParams(params);

            final int finalF = f;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TeamID != 0) {
                        Intent ii = new Intent(PreviewActivity.this, PlayersMatchStatsActivity.class);
                        ii.putExtra("team_id", String.valueOf(TeamID));
                        ii.putExtra("player_id", Integer.parseInt(list.get(finalF).getId()));
                        startActivity(ii);
                    }
                }
            });

            ll.addView(v);
        }
    }

}
