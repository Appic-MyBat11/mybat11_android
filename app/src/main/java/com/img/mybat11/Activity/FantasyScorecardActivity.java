package com.img.mybat11.Activity;

import android.content.DialogInterface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Adapter.fantasyScorecardAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.fantasyScorecardGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FantasyScorecardActivity extends AppCompatActivity {

    ListView scoreCard;
    TextView title;
    ImageView back;
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;
    String TAG="Challenge list";
    MainActivity ma;
    ArrayList<fantasyScorecardGetSet> list;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView player,selection,points;

    TextView noScorecard;
    LinearLayout scorecard;
    private BottomSheetBehavior mBottomSheetBehavior1;
    TextView close;
    ImageView playerImage;
    TextView hitterPoints,throwerPoints,playerName,selectper, startingPoint, runPoints, fourPoints, sixPoints, strikeRatePoints, points50, duckPoints, wicketspoints, maidenPoints, economypoints, bonusPoints, catchPoints, stumpingPoints, playerPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasy_scorecard);

        gv= (GlobalVariables)getApplicationContext();
        ma = new MainActivity();
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        title =(TextView)findViewById(R.id.title);
        title.setText("Fantasy Scorecard");

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View bottomSheet = findViewById(R.id.scoreSheet);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        scoreCard=(ListView)findViewById(R.id.scoreCard);

        player=(TextView)findViewById(R.id.player);
        selection=(TextView)findViewById(R.id.selection);
        points=(TextView)findViewById(R.id.points);

        playerImage=(ImageView)findViewById(R.id.playerImage);
        playerName = (TextView) findViewById(R.id.playerName);
        throwerPoints = (TextView) findViewById(R.id.throwerPoints);
        selectper = (TextView) findViewById(R.id.selectper);
        playerPoints = (TextView) findViewById(R.id.playerPoints);
        startingPoint = (TextView) findViewById(R.id.startingPoint);
        runPoints = (TextView) findViewById(R.id.runPoints);
        fourPoints = (TextView) findViewById(R.id.fourPoints);
        sixPoints = (TextView) findViewById(R.id.sixPoints);
        strikeRatePoints = (TextView) findViewById(R.id.strikeRatePoints);
        points50 = (TextView) findViewById(R.id.points50);
        duckPoints = (TextView) findViewById(R.id.duckPoints);
        wicketspoints = (TextView) findViewById(R.id.wicketspoints);
        maidenPoints = (TextView) findViewById(R.id.maidenPoints);
        economypoints = (TextView) findViewById(R.id.economypoints);
        bonusPoints = (TextView) findViewById(R.id.bonusPoints);
        catchPoints = (TextView) findViewById(R.id.catchPoints);
        stumpingPoints = (TextView) findViewById(R.id.stumpingPoints);
        hitterPoints = (TextView) findViewById(R.id.hitterPoints);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cd.isConnectingToInternet()){
                    ma.showProgressDialog(FantasyScorecardActivity.this);
                    LiveChallenges();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        scoreCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                playerName.setText(list.get(i).getPlayer_name());
                startingPoint.setText(String.valueOf(list.get(i).getStartingpoints()));
                runPoints.setText(String.valueOf(list.get(i).getRuns()));
                fourPoints.setText(String.valueOf(list.get(i).getFours()));
                sixPoints.setText(String.valueOf(list.get(i).getSixs()));
                strikeRatePoints.setText(String.valueOf(list.get(i).getStrike_rate()));
                points50.setText(String.valueOf(list.get(i).getCentury()));
                duckPoints.setText(String.valueOf(list.get(i).getNegative()));
                wicketspoints.setText(String.valueOf(list.get(i).getWickets()));
                maidenPoints.setText(String.valueOf(list.get(i).getMaidens()));
                economypoints.setText(String.valueOf(list.get(i).getEconomy_rate()));
                bonusPoints.setText(String.valueOf(list.get(i).getBonus()));
                catchPoints.setText(String.valueOf(list.get(i).getCatch_points()));
                stumpingPoints.setText(String.valueOf(list.get(i).getStumping()));
                throwerPoints.setText(String.valueOf(list.get(i).getThrower()));
                hitterPoints.setText(String.valueOf(list.get(i).getHitter()));
                playerPoints.setText("Total Points : "+String.valueOf(list.get(i).getTotal()));
                selectper.setText("Selected by : "+String.valueOf(list.get(i).getSelectper()));

                Picasso.with(FantasyScorecardActivity.this).load(list.get(i).getPlayerimage()).placeholder(R.drawable.avtar).into(playerImage);

                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        close =(TextView)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(list, new Comparator<fantasyScorecardGetSet>() {
                    @Override
                    public int compare(fantasyScorecardGetSet ob1, fantasyScorecardGetSet ob2) {
                        return (Double.parseDouble(ob1.getTotal()) > Double.parseDouble(ob2.getTotal())) ? -1: (Double.parseDouble(ob1.getTotal()) > Double.parseDouble(ob2.getTotal())) ? 1:0 ;
                    }
                });
                scoreCard.setAdapter(new fantasyScorecardAdapter(FantasyScorecardActivity.this,list));
            }
        });

        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(list, new Comparator<fantasyScorecardGetSet>() {
                    @Override
                    public int compare(fantasyScorecardGetSet ob1, fantasyScorecardGetSet ob2) {
                        return (Double.parseDouble(ob1.getSelectper().replace("%","")) > Double.parseDouble(ob2.getSelectper().replace("%",""))) ? -1: (Double.parseDouble(ob1.getSelectper().replace("%","")) > Double.parseDouble(ob2.getSelectper().replace("%",""))) ? 1:0 ;
                    }
                });
                scoreCard.setAdapter(new fantasyScorecardAdapter(FantasyScorecardActivity.this,list));
            }
        });

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(list, new Comparator<fantasyScorecardGetSet>() {
                    @Override
                    public int compare(fantasyScorecardGetSet ob1, fantasyScorecardGetSet ob2) {
                        char l = Character.toUpperCase(ob1.getPlayer_name().charAt(0));
                        if (l < 'A' || l > 'Z')
                            l += 'Z';
                        char r = Character.toUpperCase(ob2.getPlayer_name().charAt(0));
                        if (r < 'A' || r > 'Z')
                            r += 'Z';
                        String s1 = l + ob1.getPlayer_name().substring(1);
                        String s2 = r + ob2.getPlayer_name().substring(1);
                        return s1.compareTo(s2);
                    }
                });
                scoreCard.setAdapter(new fantasyScorecardAdapter(FantasyScorecardActivity.this,list));
            }
        });

        noScorecard=(TextView) findViewById(R.id.noScorecard);
        scorecard=(LinearLayout) findViewById(R.id.scorecard);

        if(cd.isConnectingToInternet()) {
            ma.showProgressDialog(FantasyScorecardActivity.this);
            LiveChallenges();
        }
    }

    public void LiveChallenges(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<fantasyScorecardGetSet>> call = apiSeitemViewice.fantasyscorecards(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<fantasyScorecardGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<fantasyScorecardGetSet>> call, Response<ArrayList<fantasyScorecardGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();
                    if(list.size() >0) {
                        noScorecard.setVisibility(View.GONE);
                        scorecard.setVisibility(View.VISIBLE);
                        scoreCard.setAdapter(new fantasyScorecardAdapter(FantasyScorecardActivity.this, list));
                    }else{
                        noScorecard.setVisibility(View.VISIBLE);
                        scorecard.setVisibility(View.GONE);
                    }
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(FantasyScorecardActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LiveChallenges();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                        }
                    });
                }
                ma.dismissProgressDialog();
            }
            @Override
            public void onFailure(Call<ArrayList<fantasyScorecardGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            super.onBackPressed();
    }
}
