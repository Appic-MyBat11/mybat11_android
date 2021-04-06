package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.LeaderBoardAdapter2;
import com.img.mybat11.Adapter.PriceCardAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.Fragment.LeaderboardLiveFramgent;
import com.img.mybat11.Fragment.PriceCardFragment;
import com.img.mybat11.Fragment.PriceCardPercentageFragment;
import com.img.mybat11.Fragment.QuizLeaderboardLiveFramgent;
import com.img.mybat11.GetSet.JoinTeamGetSet;
import com.img.mybat11.GetSet.LeagueDetailsGetSet;
import com.img.mybat11.GetSet.LiveChallengesGetSet;
import com.img.mybat11.GetSet.SelectedPlayersGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.GetSet.priceCardGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizDetailsDoneActivity extends AppCompatActivity {
    MainActivity ma;
    private RecyclerView.LayoutManager mLayoutManager;
    CircleImageView fab;
    TextView title,numWinners,prizeMoney,entryFee,lb,totalTeamslb,timeRemaining,match_name,totalTeamsJoined;
    ImageView back;
    LinearLayout winnerLL,cll,mll;
    TextView score;
    GlobalVariables gv;
    ConnectionDetector cd;
    ExpandableHeightListView priceCard;
    private BottomSheetBehavior mBottomSheetBehavior1;
    UserSessionManager session;
    ProgressBar teamEnteredPB;
    RequestQueue requestQueue;
    ImageView download;
    TextView teamsLeft,totalTeams;
    String Pdflink="";
    static Activity fa;

    String TAG="Challenge list";
    Dialog d;
    ArrayList<LeagueDetailsGetSet> list;
    ArrayList<LiveChallengesGetSet> liveList;
    ArrayList<priceCardGetSet> priceCardlist;
    ArrayList<JoinTeamGetSet> teams,teams1;
    LeaderBoardAdapter2 adapter;
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details_done);




        ma = new MainActivity();

        fa= this;

        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(QuizDetailsDoneActivity.this);
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        timeRemaining=(TextView)findViewById(R.id.timeRemaining);
        match_name=(TextView)findViewById(R.id.match_name);


        lb=(TextView)findViewById(R.id.lb);
        totalTeamslb=(TextView)findViewById(R.id.totalteamsLB);
        cll=(LinearLayout)findViewById(R.id.cll);
        mll=(LinearLayout)findViewById(R.id.mll);

        totalTeamsJoined =(TextView)findViewById(R.id.totalTeamsJoined);


        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        title=(TextView)findViewById(R.id.title);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(gv.getMatchTime()));
            title.setText(newDate + " Quiz");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeRemaining.setText(gv.getQuizquestions()+" Questions");
        match_name.setText("Finished");

        winnerLL=(LinearLayout)findViewById(R.id.winnerLL);
        teamEnteredPB=(ProgressBar)findViewById(R.id.teamEnteredPB);

        teamsLeft=(TextView)findViewById(R.id.teamsLeft);
        totalTeams=(TextView)findViewById(R.id.totalTeams);
        numWinners=(TextView)findViewById(R.id.numWinners);
        entryFee=(TextView)findViewById(R.id.entryFee);
        prizeMoney=(TextView)findViewById(R.id.prizeMoney);


        fab=(CircleImageView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutManager.scrollToPosition(0);
            }
        });


        priceCard=(ExpandableHeightListView)findViewById(R.id.priceCard);
        priceCard.setExpanded(true);

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        TextView close = (TextView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        winnerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceCardlist= list.get(0).getPrice_card();
                if(priceCardlist != null && list.get(0).getContest_type().equals("Amount")){
                    priceCard.setAdapter(new PriceCardAdapter(QuizDetailsDoneActivity.this,priceCardlist));
                    if(mBottomSheetBehavior1.getState() != BottomSheetBehavior.STATE_EXPANDED)
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    else
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        teams1 = new ArrayList<>();
        if(cd.isConnectingToInternet()) {
            Details();
            LeaderBoard();
        }




    }



    public void Details(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Log.i("matchkey",gv.getMatchKey());
        Log.i("challengeid",String.valueOf(gv.getChallengeId()));

        Call<ArrayList<LeagueDetailsGetSet>> call = apiSeitemViewice.QuizContestDetails(String.valueOf(gv.getChallengeId()),session.getUserId());
        call.enqueue(new Callback<ArrayList<LeagueDetailsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LeagueDetailsGetSet>> call, Response<ArrayList<LeagueDetailsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());
                if(response.code() == 200 && response.body() != null) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();
                    final NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
                    ((DecimalFormat)nf2).applyPattern("##,##,###.##");

                    cll.setVisibility(View.GONE);

                    mll.setVisibility(View.GONE);

                    priceCardlist = list.get(0).getPrice_card();


                    if(list.get(0).getPrice_card() == null && list.get(0).getContest_type().equals("Amount")){
                        ArrayList<priceCardGetSet> price_card = new ArrayList<>();

                        priceCardGetSet ob = new priceCardGetSet();
                        ob.setStart_position("1");
                        ob.setTotal(String.valueOf((int) Math.round(list.get(0).getWin_amount())));
                        ob.setPrice(String.valueOf((int) Math.round(list.get(0).getWin_amount())));
                        ob.setPrice_percent("100%");
                        ob.setWinners("1");
                        ob.setId("0");
                        price_card.add(ob);

                        gv.setPriceCard(price_card);
                    }else
                        gv.setPriceCard(list.get(0).getPrice_card());

                    entryFee.setText("₹ " + (int) list.get(0).getEntryfee());
                    prizeMoney.setText("₹ " + nf2.format(list.get(0).getWin_amount()));

                    if (list.get(0).getWin_amount() == 0) {
                        winnerLL.setVisibility(View.GONE);
                        prizeMoney.setText("Net Practice");
                    }

                    if(getIntent().hasExtra("tabPos"))
                        viewPager.setCurrentItem(getIntent().getExtras().getInt("tabPos"));
                    else
                        viewPager.setCurrentItem(1);
                    if (list.get(0).getContest_type().equals("Amount")) {
                        if (list.get(0).getTotalwinners() == 1)
                            numWinners.setText("1");
                        else
                            numWinners.setText(""+nf2.format(list.get(0).getTotalwinners()) + " ▼");

                        int left = (list.get(0).getMaximum_user()) - (list.get(0).getJoinedusers());
                        if (left != 0) {
                            if (left == 1)
                                teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " team left");
                            else
                                teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " teams left");
                        } else
                            teamsLeft.setText("Contest Completed");
                        totalTeams.setText(nf2.format(list.get(0).getMaximum_user()) + " Teams");

                        teamEnteredPB.setMax(list.get(0).getMaximum_user());
                        teamEnteredPB.setProgress(list.get(0).getJoinedusers());

                    } else {
                        numWinners.setText(list.get(0).getWinning_percentage() + " %");
                        if (list.get(0).getJoinedusers() == 1)
                            teamsLeft.setText(nf2.format(list.get(0).getJoinedusers())+" team joined");
                        else
                            teamsLeft.setText(nf2.format(list.get(0).getJoinedusers())+" teams joined");
                        totalTeams.setText("∞ teams");

                        teamEnteredPB.setMax(list.get(0).getJoinedusers());
                        teamEnteredPB.setProgress(list.get(0).getJoinedusers());
                    }
                }
            }
            @Override
            public void onFailure(Call<ArrayList<LeagueDetailsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }

    public void LeaderBoard(){
        ma.showProgressDialog(QuizDetailsDoneActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<LiveChallengesGetSet>> call = apiSeitemViewice.qliveScores(session.getUserId(),String.valueOf(gv.getChallengeId()), String.valueOf(gv.getQuizid()));
        call.enqueue(new Callback<ArrayList<LiveChallengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LiveChallengesGetSet>> call, Response<ArrayList<LiveChallengesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());
                ma.dismissProgressDialog();
                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    liveList = response.body();

                    teams = liveList.get(0).getJointeams();
                    if (teams != null) {
                        viewPager.setAdapter(new QuizDetailsDoneActivity.SectionPagerAdapter(getSupportFragmentManager()));
                        tabLayout.setupWithViewPager(viewPager);
                        viewPager.setCurrentItem(1);
                    }
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(QuizDetailsDoneActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LeaderBoard();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<LiveChallengesGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }
    class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    if(list.get(0).getPricecard_type().equals("Amount"))
                        return new PriceCardFragment();
                    else
                        return new PriceCardPercentageFragment();
                default: return new QuizLeaderboardLiveFramgent();
//                default: return new PlayerStatsFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            switch (i){
                case 0: return "Prize Breakup";
                default: return "Leaderboard";
//                default: return "Player Stats";
            }
        }
    }


}
