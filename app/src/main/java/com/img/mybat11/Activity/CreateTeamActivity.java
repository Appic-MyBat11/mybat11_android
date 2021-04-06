package com.img.mybat11.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.Fragment.PlayersFragment;
import com.img.mybat11.GetSet.PlayerListGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTeamActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    ImageView team1Image,team2Image;
    TextView team1_name,team2_name,leftAmount;
    TabLayout playersTab;
    ViewPager vp;
    Button btnPreview,btnContinue;

    String TAG="Players";
    ArrayList<PlayerListGetSet> list,listWK,listBAT,listAR,listBOWL;
    boolean edit = false;
    ArrayList<captainListGetSet> Clist;
    int teamNumber,challengeId,entryFee;
    public static Activity fa;

    String type="";
    TextView maxPlayers,totalPlayers;
    String captain="",vc="",chooseType="",joinid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        fa = this;

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();

        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().hasExtra("teamNumber"))
            teamNumber = getIntent().getExtras().getInt("teamNumber");
        else
            teamNumber = 1;

        if(getIntent().hasExtra("chooseType")) {
            chooseType = getIntent().getExtras().getString("chooseType");
            joinid = getIntent().getExtras().getString("joinid");
        }
        else
            chooseType = "";


        if(teamNumber > gv.getMax_teams()){
            ma.showToast(CreateTeamActivity.this,"Can't create more teams");
            finish();
        }

        type = getIntent().getExtras().getString("type");

        maxPlayers=(TextView)findViewById(R.id.maxPlayers);
        totalPlayers=(TextView)findViewById(R.id.totalPlayers);
        leftAmount=(TextView)findViewById(R.id.leftAmount);
        if(type.equals("classic")) {
            maxPlayers.setText("Max 7 Players from a team");
            totalPlayers.setText("/11");
            leftAmount.setText("100");
        }else{
            maxPlayers.setText("Max 3 Players from a team");
            totalPlayers.setText("/5");
            leftAmount.setText("45");
        }
        if(getIntent().hasExtra("challengeId")){
            challengeId=getIntent().getExtras().getInt("challengeId");
            entryFee=getIntent().getExtras().getInt("entryFee");
        }
        else {
            challengeId = 0;
            entryFee = 0;
        }

        if(getIntent().hasExtra("edit")) {
            edit = getIntent().getExtras().getBoolean("edit");

            Clist = gv.getCaptainList();
            for(captainListGetSet zz:Clist){
                if(zz.getCaptain().equals("1"))
                    captain = zz.getId();
                if(zz.getVc().equals("1"))
                    vc = zz.getId();
            }
        }

        final TextView timeLeft=(TextView)findViewById(R.id.timeLeft);

        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate = gv.getMatchTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long diffInMs = endDate.getTime() - startDate.getTime();

        final long hours1 = 1*60*60*1000;
        final long hours4 = 4*60*60*1000;
        final long hours48 = 48*60*60*1000;

        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {
                if(diffInMs < hours1){
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) + "m : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))  + "s left"));
                }else if(diffInMs <  hours4){
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m left"));
                }else if(diffInMs <  hours48){
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h left"));

                }else{
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toDays(millisUntilFinished)) + " days left"));
                }            }

            public void onFinish() {
                finish();
            }
        };
        cT.start();

        team1_name =(TextView)findViewById(R.id.team1_name);
        team2_name =(TextView)findViewById(R.id.team2_name);

        team1_name.setText(gv.getTeam1());
        team2_name.setText(gv.getTeam2());

        team1Image=(ImageView)findViewById(R.id.team1Image);
        team2Image=(ImageView)findViewById(R.id.team2Image);

        Picasso.with(CreateTeamActivity.this).load(gv.getTeam1Image()).into(team1Image);
        Picasso.with(CreateTeamActivity.this).load(gv.getTeam2image()).into(team2Image);

        playersTab=(TabLayout)findViewById(R.id.playersTab);
        vp = (ViewPager)findViewById(R.id.vp);

        btnContinue=(Button)findViewById(R.id.btnContinue);
        btnPreview=(Button)findViewById(R.id.btnPreview);

        if(cd.isConnectingToInternet())
            PlayersList();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clist= new ArrayList<>();
                for(PlayerListGetSet zz:list){
                    if(zz.isSelected()){
                        captainListGetSet ob= new captainListGetSet();

                        ob.setCaptain("N");
                        ob.setName(zz.getName());
                        ob.setId(zz.getId());
                        if(zz.getRole().equals("keeper")) {
                            ob.setRole("Wk");
                        }if(zz.getRole().equals("batsman")) {
                            ob.setRole("Bat");
                        }if(zz.getRole().equals("bowler")) {
                            ob.setRole("Bow");
                        }if(zz.getRole().equals("allrounder")) {
                            ob.setRole("AR");
                        }
                        ob.setVc("N");
                        ob.setTeamcolor(zz.getTeamcolor());
                        ob.setType(zz.getRole());
                        ob.setTeamname(zz.getTeamname());
                        ob.setTeam(zz.getTeam());
                        ob.setPoints(zz.getTotalpoints());
                        ob.setCredit(zz.getCredit());
                        ob.setImage(zz.getImage());
                        ob.setPlayingstatus(zz.getPlayingstatus());

                        Log.i("team",zz.getTeam());

                        if(edit){
                            if(zz.getId().equals(captain))
                                ob.setCaptain("Y");
                            if(zz.getId().equals(vc))
                                ob.setVc("Y");
                        }

                        Clist.add(ob);
                    }
                }
                if(type.equals("classic") && Clist.size() != 11){
                    ma.showToast(CreateTeamActivity.this,"Complete your team first");
                }else if(!type.equals("classic") && Clist.size() != 5){
                    ma.showToast(CreateTeamActivity.this,"Complete your team first");
                }else{
                    String option;
                    if(edit)
                        option="Edit";
                    else
                        option="Create";

                    gv.setCaptainList(Clist);

                    Intent ii= new Intent(CreateTeamActivity.this,CaptainViceCaptainActivity.class);
                    ii.putExtra("teamNumber",teamNumber);
                    ii.putExtra("challengeId",challengeId);
                    ii.putExtra("entryFee",entryFee);
                    ii.putExtra("option",option);
                    ii.putExtra("type",type);
                    ii.putExtra("chooseType",chooseType);
                    ii.putExtra("joinid",joinid);
                    startActivity(ii);
                }
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clist= new ArrayList<>();
                for(PlayerListGetSet zz:list){
                    if(zz.isSelected()){
                        captainListGetSet ob= new captainListGetSet();

                        ob.setCaptain("N");
                        ob.setName(zz.getName());
                        ob.setId(zz.getId());
                        if(zz.getRole().equals("keeper")) {
                            ob.setRole("Wk");
                        }if(zz.getRole().equals("batsman")) {
                            ob.setRole("Bat");
                        }if(zz.getRole().equals("bowler")) {
                            ob.setRole("Bow");
                        }if(zz.getRole().equals("allrounder")) {
                            ob.setRole("AR");
                        }
                        ob.setVc("N");
                        ob.setTeamcolor(zz.getTeamcolor());
                        ob.setType(zz.getRole());
                        ob.setTeamname(zz.getTeamname());
                        ob.setTeam(zz.getTeam());
                        ob.setImage(zz.getImage());
                        ob.setPoints(zz.getTotalpoints());
                        ob.setCredit(zz.getCredit());
                        ob.setPlayingstatus(zz.getPlayingstatus());

                        Clist.add(ob);
                    }
                }

                gv.setCaptainList(Clist);

                Intent ii= new Intent(CreateTeamActivity.this, TeamPreviewActivity.class);
                ii.putExtra("team_name","Team "+teamNumber);
                ii.putExtra("TeamID",0);
                startActivity(ii);
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
                    return new PlayersFragment(list,listWK,"Pick 1-4 Wicket-Keepers",edit,type);
                case 1:
                    return new PlayersFragment(list,listBAT,"Pick 3-6 Batsmen",edit,type);
                case 2:
                    return new PlayersFragment(list,listAR,"Pick 1-4 All-Rounders",edit,type);
                default:
                    return new PlayersFragment(list,listBOWL,"Pick 3-6 Bowlers",edit,type);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "WK (0)";
                case 1:
                    return "BAT (0)";
                case 2:
                    return "AR (0)";
                default:
                    return "BOWL (0)";
            }
        }

    }

    public class SectionPagerAdapter_batsman extends FragmentStatePagerAdapter {

        public SectionPagerAdapter_batsman(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PlayersFragment(list,listWK,"Pick 1-5 Wicket-Keepers",edit,type);
                case 1:
                    return new PlayersFragment(list,listBAT,"Pick 1-5 Batsmen",edit,type);
                default:
                    return new PlayersFragment(list,listAR,"Pick 1-5 All-Rounders",edit,type);
//                default:
//                    return new PlayersFragment(list,listBOWL,"Pick 1-5 Bowlers",edit,type);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "WK (0)";
                case 1:
                    return "BAT (0)";
                default:
                    return "AR (0)";
//                default:
//                    return "BOWL (0)";
            }
        }

    }

    public class SectionPagerAdapter_bowler extends FragmentStatePagerAdapter {

        public SectionPagerAdapter_bowler(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
//                case 0:
//                    return new PlayersFragment(list,listWK,"Pick 1-5 Wicket-Keepers",edit,type);
//                case 1:
//                    return new PlayersFragment(list,listBAT,"Pick 1-5 Batsmen",edit,type);
                case 0:
                    return new PlayersFragment(list,listAR,"Pick 1-5 All-Rounders",edit,type);
                default:
                    return new PlayersFragment(list,listBOWL,"Pick 1-5 Bowlers",edit,type);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
//                case 0:
//                    return "WK (0)";
//                case 1:
//                    return "BAT (0)";
                case 0:
                    return "AR (0)";
                default:
                    return "BOWL (0)";
            }
        }

    }


    public void PlayersList(){
        ma.showProgressDialog(CreateTeamActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<PlayerListGetSet>> call = apiSeitemViewice.PlayersList(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<PlayerListGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<PlayerListGetSet>> call, Response<ArrayList<PlayerListGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    listWK = new ArrayList<>();
                    listBAT = new ArrayList<>();
                    listAR = new ArrayList<>();
                    listBOWL = new ArrayList<>();

                    for (PlayerListGetSet zz : list) {
                        if (zz.getRole().equals("keeper"))
                            listWK.add(zz);
                        if (zz.getRole().equals("batsman"))
                            listBAT.add(zz);
                        if (zz.getRole().equals("allrounder"))
                            listAR.add(zz);
                        if (zz.getRole().equals("bowler"))
                            listBOWL.add(zz);

                        if (zz.getTeamname() == null) {
                            ma.showToast(CreateTeamActivity.this, "Some player info missing, Please wait a moment! ");
                            new Handler().postDelayed(new Runnable() {
                                // Using handler with postDelayed called runnable run method
                                @Override
                                public void run() {
                                    try {
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                            }, 1000);
                            return;
                        }
                    }

                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            return (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? -1: (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? 1:0 ;
                        }
                    });

                    playersTab.setupWithViewPager(vp);
                    if(type.equals("classic"))
                        vp.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
                    else if(type.equals("batting"))
                        vp.setAdapter(new SectionPagerAdapter_batsman(getSupportFragmentManager()));
                    else if(type.equals("bowling"))
                        vp.setAdapter(new SectionPagerAdapter_bowler(getSupportFragmentManager()));

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(CreateTeamActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlayersList();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<PlayerListGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }


}
