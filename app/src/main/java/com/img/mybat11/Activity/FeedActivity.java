package com.img.mybat11.Activity;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Adapter.SidelistAdapter_more;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedActivity extends AppCompatActivity {

    TextView title;
    UserSessionManager session;
    GlobalVariables gv;
    String TAG="team name";
    MainActivity ma;
    ImageView back;

    LinearLayout homeLL,contestLL,meLL,moreLL,feedLL;
    ImageView feed;
    TextView feedText;

    CircleImageView userImage,profileImage;
    TextView teamName,btnAddCash;

    DrawerLayout drawer;
    LinearLayout myProfile,wallet,rewards,invite,settings,pointSystem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        ma= new MainActivity();
        gv= (GlobalVariables)getApplicationContext();
        session= new UserSessionManager(getApplicationContext());


        drawer=(DrawerLayout)findViewById(R.id.drawer);



        homeLL= (LinearLayout)findViewById(R.id.homeLL);
        contestLL= (LinearLayout)findViewById(R.id.contestLL);
        meLL= (LinearLayout)findViewById(R.id.meLL);
        moreLL= (LinearLayout)findViewById(R.id.moreLL);
        feedLL= (LinearLayout)findViewById(R.id.feedLL);

        feed=(ImageView) findViewById(R.id.feed);
        feedText=(TextView) findViewById(R.id.feedText);

        feedText.setTextColor(getResources().getColor(R.color.colorPrimary));
        feed.setImageResource(R.drawable.feed_deselected);

        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedActivity.this,HomeActivity.class));
                finish();
            }
        });

        contestLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedActivity.this,MyMatchesCricketActivity.class));
                finish();
            }
        });

        moreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FeedActivity.this,MoreActivity.class));
                finish();

            }
        });

        profileImage=(CircleImageView)findViewById(R.id.profileImage);

        Picasso.with(FeedActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(profileImage);
        userImage=(CircleImageView)findViewById(R.id.userImage);
        Picasso.with(FeedActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(userImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });




        teamName=(TextView) findViewById(R.id.teamName);
        teamName.setText(session.getTeamName());

        btnAddCash=(TextView)findViewById(R.id.btnAddCash);
        btnAddCash.setText("₹"+session.getWallet_amount());

        myProfile= (LinearLayout)findViewById(R.id.myProfile);
        wallet= (LinearLayout)findViewById(R.id.wallet);
        rewards= (LinearLayout)findViewById(R.id.rewards);
        invite= (LinearLayout)findViewById(R.id.invite);
        settings= (LinearLayout)findViewById(R.id.settings);
        pointSystem= (LinearLayout)findViewById(R.id.pointSystem);

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(FeedActivity.this,MeActivity.class));
                finishAffinity();
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(FeedActivity.this,BalanceActivity.class));
            }
        });

        rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();

            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(FeedActivity.this,InviteFriendActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(FeedActivity.this,PersonalDetailsActivity.class));
            }
        });

        pointSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(FeedActivity.this,FantasyPointSystemActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START))
            drawer.closeDrawers();
        else {
            startActivity(new Intent(FeedActivity.this, HomeActivity.class));
            finishAffinity();
        }
    }

}
