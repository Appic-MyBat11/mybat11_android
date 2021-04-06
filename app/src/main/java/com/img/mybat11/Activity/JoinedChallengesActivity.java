package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Adapter.JoinedChallengeListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.GetSet.SelectedTeamsGetSet;
import com.img.mybat11.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinedChallengesActivity extends AppCompatActivity {

    ListView challengeList;
    ArrayList<JoinedChallengesGetSet> list,list1;
    String TAG="Challenge list";
    GlobalVariables gv;
    TextView noChallenge;
    ConnectionDetector cd;
    UserSessionManager session;
    ArrayList<SelectedTeamsGetSet> TeamList;
    MainActivity ma;
    ImageView back;
    TextView match_name,timeRemaining;
    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate=null,endDate = null;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout classic,batting,bowling;
    TextView classicSelected,battingSelected,bowlingSelected;
    TextView bowlingText,battinngText,classicText;
    String type="classic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_challenges);



        ma = new MainActivity();
        cd= new ConnectionDetector(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Join Contests");

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        final BottomSheetBehavior mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        TextView close =(TextView)findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        match_name=(TextView)findViewById(R.id.match_name);
        timeRemaining=(TextView)findViewById(R.id.timeRemaining);

        match_name.setText(gv.getTeam1()+" vs "+gv.getTeam2());

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
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);

            long diffInMs = endDate.getTime() - startDate.getTime();

            CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

                public void onTick(long millisUntilFinished) {
                    timeRemaining.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));
                }

                public void onFinish() {
                    finish();
                }
            };
            cT.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        noChallenge=(TextView)findViewById(R.id.noChallenge);
        challengeList= (ListView) findViewById(R.id.challengeList);

        if(cd.isConnectingToInternet()){
            Challenges(type);
        }

        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Challenges(type);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        challengeList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem == 0) {
                    swipeRefreshLayout.setEnabled(true);
                }else
                    swipeRefreshLayout.setEnabled(false);
            }
        });




        classic =(LinearLayout)findViewById(R.id.classic);
        batting =(LinearLayout)findViewById(R.id.batting);
        bowling =(LinearLayout)findViewById(R.id.bowling);

        classicText=(TextView)findViewById(R.id.classicText);
        battinngText=(TextView)findViewById(R.id.battinngText);
        bowlingText=(TextView)findViewById(R.id.bowlingText);

        classicSelected=(TextView)findViewById(R.id.classicSelected);
        battingSelected=(TextView)findViewById(R.id.battingSelected);
        bowlingSelected=(TextView)findViewById(R.id.bowlingSelected);

        classic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="classic";

                classicSelected.setVisibility(View.VISIBLE);
                battingSelected.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.GONE);

                Challenges("classic");
            }
        });

        batting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="batting";

                classicSelected.setVisibility(View.GONE);
                battingSelected.setVisibility(View.VISIBLE);
                bowlingSelected.setVisibility(View.GONE);
                Challenges("batting");

            }
        });
        bowling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="bowling";

                classicSelected.setVisibility(View.GONE);
                battingSelected.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.VISIBLE);
                Challenges("bowling");

            }
        });

    }

    public void Challenges(final String type){
        ma.showProgressDialog(JoinedChallengesActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<JoinedChallengesGetSet>> call = apiSeitemViewice.FindJoinedChallenges(gv.getMatchKey(),session.getUserId());
        call.enqueue(new Callback<ArrayList<JoinedChallengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<JoinedChallengesGetSet>> call, Response<ArrayList<JoinedChallengesGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list1 = new ArrayList<>();

                    list1 = response.body();

                    list = new ArrayList<>();
                    if (list1.size() != 0) {
                        for (JoinedChallengesGetSet zz : list1) {
                            if (zz.getType().equals(type))
                                list.add(zz);
                        }
                    }

                    Log.i("Size", list.size() + "");
                    if (list.size() == 0) {
                        challengeList.setVisibility(View.GONE);
                        noChallenge.setVisibility(View.VISIBLE);
                    } else {
                        challengeList.setVisibility(View.VISIBLE);
                        noChallenge.setVisibility(View.GONE);
                        challengeList.setAdapter(new JoinedChallengeListAdapter(JoinedChallengesActivity.this, list));
                    }

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    ma.dismissProgressDialog();

                    AlertDialog.Builder d = new AlertDialog.Builder(JoinedChallengesActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Challenges(type);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                            finish();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<JoinedChallengesGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }
}
