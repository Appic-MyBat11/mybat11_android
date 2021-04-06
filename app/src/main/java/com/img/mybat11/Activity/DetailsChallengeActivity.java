package com.img.mybat11.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.LeaderBoardAdapter1;
import com.img.mybat11.Adapter.PriceCardAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.Fragment.LeaderboardLiveFramgent;
import com.img.mybat11.Fragment.PlayerStatsFragment;
import com.img.mybat11.Fragment.PriceCardFragment;
import com.img.mybat11.Fragment.PriceCardPercentageFragment;
import com.img.mybat11.GetSet.JoinTeamGetSet;
import com.img.mybat11.GetSet.LeagueDetailsGetSet;
import com.img.mybat11.GetSet.LiveChallengesGetSet;
import com.img.mybat11.GetSet.priceCardGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsChallengeActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager mLayoutManager;
    CircleImageView fab;
    TextView title,numWinners,prizeMoney,entryFee,lb,totalTeamslb,timeRemaining,match_name,totalTeamsJoined;
    ImageView back;
    LinearLayout winnerLL,cll,mll;
    GlobalVariables gv;
    MainActivity ma;
    ConnectionDetector cd;
    RequestQueue requestQueue;
    ExpandableHeightListView priceCard;
    private BottomSheetBehavior mBottomSheetBehavior1;
    UserSessionManager session;
    ProgressBar teamEnteredPB;
    ImageView download;
    String Pdflink="";
    TextView teamsLeft,totalTeams;
    static Activity fa;
    String maxusers="";

    String TAG="Challenge list";
    Dialog d;
    ArrayList<LeagueDetailsGetSet> list;
    ArrayList<priceCardGetSet> priceCardlist;
    ArrayList<LiveChallengesGetSet>liveList;
    ArrayList<JoinTeamGetSet> teams,teams1;
    TextView score;
    LeaderBoardAdapter1 adapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView team1,team1Score,team1Overs,team2,team2Score,team2Overs,finalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_challenge);



        ma = new MainActivity();
        fa= this;
        requestQueue = Volley.newRequestQueue(DetailsChallengeActivity.this);
        gv= (GlobalVariables)getApplicationContext();
        cd = new ConnectionDetector(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());

        timeRemaining=(TextView)findViewById(R.id.timeRemaining);
        match_name=(TextView)findViewById(R.id.match_name);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        match_name.setText(gv.getTeam1().toUpperCase()+" vs "+gv.getTeam2().toUpperCase());

        lb=(TextView)findViewById(R.id.lb);

        totalTeamsJoined=(TextView)findViewById(R.id.totalTeamsJoined);

        cll=(LinearLayout)findViewById(R.id.cll);
        mll=(LinearLayout)findViewById(R.id.mll);


        score=(TextView)findViewById(R.id.playerStats);
        score.setVisibility(View.VISIBLE);
        LinearLayout scorell = (LinearLayout)findViewById(R.id.scorell);
        scorell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailsChallengeActivity.this,FantasyScorecardActivity.class));
            }
        });

        title=(TextView)findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("title"));

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        winnerLL=(LinearLayout)findViewById(R.id.winnerLL);
        teamEnteredPB=(ProgressBar)findViewById(R.id.teamEnteredPB);

        teamsLeft=(TextView)findViewById(R.id.teamsLeft);
        totalTeams=(TextView)findViewById(R.id.totalTeams);
        numWinners=(TextView)findViewById(R.id.numWinners);
        entryFee=(TextView)findViewById(R.id.entryFee);
        prizeMoney=(TextView)findViewById(R.id.prizeMoney);

        timeRemaining.setText(gv.getStatus());


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
                if(list.get(0).getPrice_card() != null && list.get(0).getContest_type().equals("Amount")){
                    priceCard.setAdapter(new PriceCardAdapter(DetailsChallengeActivity.this,list.get(0).getPrice_card()));
                    if(mBottomSheetBehavior1.getState() != BottomSheetBehavior.STATE_EXPANDED)
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    else
                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        team1=(TextView)findViewById(R.id.team1);
        team1Score=(TextView)findViewById(R.id.team1Score);
        team1Overs=(TextView)findViewById(R.id.team1Overs);
        team2=(TextView)findViewById(R.id.team2);
        team2Score=(TextView)findViewById(R.id.team2Score);
        team2Overs=(TextView)findViewById(R.id.team2Overs);
        finalResult=(TextView)findViewById(R.id.finalResult);

        team1.setText(gv.getTeam1());
        team2.setText(gv.getTeam2());

        if(cd.isConnectingToInternet()) {
            ma.showProgressDialog(DetailsChallengeActivity.this);
            Details();
            LiveScores();
        }


    }

    public void Details(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<LeagueDetailsGetSet>> call = apiSeitemViewice.leagueDetails(String.valueOf(gv.getChallengeId()),session.getUserId());
        call.enqueue(new Callback<ArrayList<LeagueDetailsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LeagueDetailsGetSet>> call, Response<ArrayList<LeagueDetailsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    cll.setVisibility(View.GONE);
                    mll.setVisibility(View.GONE);
                    final NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
                    ((DecimalFormat)nf2).applyPattern("##,##,###.##");

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


                    int left = (list.get(0).getMaximum_user()) - (list.get(0).getJoinedusers());

                    if(getIntent().hasExtra("tabPos"))
                        viewPager.setCurrentItem(getIntent().getExtras().getInt("tabPos"));
                    else
                        viewPager.setCurrentItem(1);

                    if (list.get(0).getContest_type().equals("Amount")) {
                        if (list.get(0).getTotalwinners() == 1)
                            numWinners.setText("1");
                        else
                            numWinners.setText(""+nf2.format(list.get(0).getTotalwinners()) + " ▼");

                        if(left!=0) {
                            if(left == 1)
                                teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " team left");
                            else
                                teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " teams left");
                        }else
                            teamsLeft.setText("Contest Completed");
                        totalTeams.setText(nf2.format(list.get(0).getMaximum_user()) + " Teams");

                        teamEnteredPB.setMax(list.get(0).getMaximum_user());
                        teamEnteredPB.setProgress(list.get(0).getJoinedusers());
                        maxusers = ""+list.get(0).getMaximum_user();

                    } else {
                        numWinners.setText(list.get(0).getWinning_percentage() + " %");
                        if (list.get(0).getJoinedusers() == 1)
                            teamsLeft.setText(nf2.format(list.get(0).getJoinedusers())+" team joined");
                        else
                            teamsLeft.setText(nf2.format(list.get(0).getJoinedusers())+" teams joined");
                        totalTeams.setText("∞ teams");

                        teamEnteredPB.setMax(list.get(0).getJoinedusers());
                        teamEnteredPB.setProgress(list.get(0).getJoinedusers());
                        maxusers = ""+list.get(0).getJoinedusers();
                    }

                    priceCardlist = list.get(0).getPrice_card();

                    entryFee.setText("₹ " + (int) list.get(0).getEntryfee());
                    prizeMoney.setText("₹ " + nf2.format(list.get(0).getWin_amount()));

                    if (list.get(0).getWin_amount() == 0) {
                        winnerLL.setVisibility(View.GONE);
                        prizeMoney.setText("Net Practice");
                    }


                    LeaderBoard();
                }
                ma.dismissProgressDialog();
            }
            @Override
            public void onFailure(Call<ArrayList<LeagueDetailsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }

    public void LeaderBoard(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<LiveChallengesGetSet>> call = apiSeitemViewice.liveScores(session.getUserId(),gv.getMatchKey(),gv.getChallengeId());
        call.enqueue(new Callback<ArrayList<LiveChallengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LiveChallengesGetSet>> call, Response<ArrayList<LiveChallengesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    liveList = response.body();

                    teams = liveList.get(0).getJointeams();
                    if (teams != null) {

                        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
                        tabLayout.setupWithViewPager(viewPager);

                        viewPager.setCurrentItem(1);
                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(DetailsChallengeActivity.this);
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
                default: return new LeaderboardLiveFramgent(maxusers);
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

    public void LiveScores(){
        try {
            String url = getResources().getString(R.string.app_url)+"getlivescores?matchkey="+gv.getMatchKey();
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                team1Score.setText(jsonObject.getString("Team1_Totalruns1")+"/"+jsonObject.getString("Team1_Totalwickets1"));
                                team2Score.setText(jsonObject.getString("Team2_Totalruns1")+"/"+jsonObject.getString("Team2_Totalwickets1"));
                                team1Overs.setText("("+jsonObject.getString("Team1_Totalovers1")+")");
                                team2Overs.setText("("+jsonObject.getString("Team2_Totalovers1")+")");

                                if(!jsonObject.getString("Team1_Totalovers2").equals("0") || !jsonObject.getString("Team2_Totalovers2").equals("0")){
                                    team1Overs.setText("");
                                    team2Overs.setText("");

                                    if(!jsonObject.getString("Team1_Totalovers2").equals("0"))
                                        team1Score.append("  &  "+jsonObject.getString("Team1_Totalruns2")+"/"+jsonObject.getString("Team1_Totalwickets2"));
                                    if(!jsonObject.getString("Team2_Totalovers2").equals("0"))
                                        team2Score.append("  &  "+jsonObject.getString("Team2_Totalruns2")+"/"+jsonObject.getString("Team2_Totalwickets2"));
                                }

                                if(jsonObject.has("Winning_Status"))
                                    finalResult.setText(jsonObject.getString("Winning_Status"));

                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(DetailsChallengeActivity.this,"Session Timeout");

                                startActivity(new Intent(DetailsChallengeActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(DetailsChallengeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LiveScores();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                            }
                        }
                    })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }

            };
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

}