package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.Fragment.MyCompeletdMatchesFragment;
import com.img.mybat11.Fragment.MyCricketMatchesFragment;
import com.img.mybat11.Fragment.MyLiveMatchesFragment;
import com.img.mybat11.Fragment.MyQuizFragment;
import com.img.mybat11.Fragment.MyUpcomingMatchesFragment;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyMatchesCricketActivity extends AppCompatActivity {


    UserSessionManager session;
    GlobalVariables gv;
    ConnectionDetector cd;
    MainActivity ma;
    ImageView back;
    ViewPager viewPager;
    TabLayout tabLayout;

    LinearLayout homeLL,contestLL,meLL,moreLL,feedLL,profileLL;
    ImageView contest;
    TextView contestText;

    CircleImageView userImage,profileImage;
    TextView teamName,btnAddCash;

    DrawerLayout drawer;
    LinearLayout myProfile,wallet,rewards,invite,settings,pointSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_matches_cricket);
        drawer=(DrawerLayout)findViewById(R.id.drawer);


        ma = new MainActivity();
        session = new UserSessionManager(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();

        cd = new ConnectionDetector(getApplicationContext());
        profileImage=(CircleImageView)findViewById(R.id.profileImage);
        viewPager=(ViewPager)findViewById(R.id.vp);
        tabLayout=(TabLayout)findViewById(R.id.tabLayout);

        Picasso.with(MyMatchesCricketActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(profileImage);
        userImage=(CircleImageView)findViewById(R.id.userImage);
        Picasso.with(MyMatchesCricketActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(userImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        profileLL= (LinearLayout)findViewById(R.id.profileLL);

        homeLL= (LinearLayout)findViewById(R.id.homeLL);
        contestLL= (LinearLayout)findViewById(R.id.contestLL);
        meLL= (LinearLayout)findViewById(R.id.meLL);
        moreLL= (LinearLayout)findViewById(R.id.moreLL);
        feedLL= (LinearLayout)findViewById(R.id.feedLL);

        profileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MyMatchesCricketActivity.this, MeActivity.class));
                finishAffinity();
            }
        });
        contest=(ImageView) findViewById(R.id.contest);
        contest.setImageResource(R.drawable.my_contest_selected);
        contestText = (TextView)findViewById(R.id.contestText);
        contestText.setTextColor(getResources().getColor(R.color.colorPrimary));

        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyMatchesCricketActivity.this, HomeActivity.class));
                finishAffinity();
            }
        });

        contestLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        moreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyMatchesCricketActivity.this, MoreActivity.class));
                finishAffinity();
            }
        });
        feedLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyMatchesCricketActivity.this,MeActivity.class));
                finishAffinity();
            }
        });

        userImage=(CircleImageView)findViewById(R.id.userImage);
        Picasso.with(MyMatchesCricketActivity.this).load(session.getImage()).into(userImage);

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
                startActivity(new Intent(MyMatchesCricketActivity.this,MeActivity.class));
                finishAffinity();
            }
        });


        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MyMatchesCricketActivity.this, BalanceActivity.class));
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
                startActivity(new Intent(MyMatchesCricketActivity.this, InviteFriendActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MyMatchesCricketActivity.this, PersonalDetailsActivity.class));
            }
        });

        pointSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MyMatchesCricketActivity.this, FantasyPointSystemActivity.class));
            }
        });


        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));


        if (gv.getPlateformmode().equals("cricket")){
            viewPager.setCurrentItem(0);
        }else{
            viewPager.setCurrentItem(1);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                if (i == 0){
                    gv.setPlateformmode("cricket");
                }else if (i == 1){
                    gv.setPlateformmode("quiz");
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MyCricketMatchesFragment();
                default:
                    return new MyQuizFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Cricket";
                default:
                    return "Quiz";
            }
        }

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START))
            drawer.closeDrawers();
        else{
            startActivity(new Intent(MyMatchesCricketActivity.this,HomeActivity.class));
            finishAffinity();
        }
    }

}
