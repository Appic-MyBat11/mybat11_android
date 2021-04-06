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
import com.img.mybat11.Adapter.QuizJoinedChallengeListAdapter;
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

public class QuizJoinedUpcomingChallengesActivity extends AppCompatActivity {
    ListView challengeList;
    ArrayList<JoinedChallengesGetSet> list,list1;
    String TAG="Challenge list";
    TextView noChallenge;
    ConnectionDetector cd;
    UserSessionManager session;
    GlobalVariables gv;
    MainActivity ma;
    ArrayList<SelectedTeamsGetSet> TeamList;
    ImageView back;
    TextView match_name,timeRemaining;
    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate=null,endDate = null;
    SwipeRefreshLayout swipeRefreshLayout;   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_joined_upcoming_challenges);


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


        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(gv.getMatchTime()));
            title.setText(newDate + " Quiz");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeRemaining.setText(gv.getQuizquestions()+" Questions");

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
                    match_name.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
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
            Challenges();
        }

        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Challenges();
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



 

    }

    public void Challenges(){
        ma.showProgressDialog(QuizJoinedUpcomingChallengesActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<JoinedChallengesGetSet>> call = apiSeitemViewice.FindJoinedQuizChallenges(String.valueOf(gv.getQuizid()),session.getUserId());
        call.enqueue(new Callback<ArrayList<JoinedChallengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<JoinedChallengesGetSet>> call, Response<ArrayList<JoinedChallengesGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list1 = new ArrayList<>();

                    list = response.body();


                    Log.i("Size", list.size() + "");
                    if (list.size() == 0) {
                        challengeList.setVisibility(View.GONE);
                        noChallenge.setVisibility(View.VISIBLE);
                    } else {
                        challengeList.setVisibility(View.VISIBLE);
                        noChallenge.setVisibility(View.GONE);
                        challengeList.setAdapter(new QuizJoinedChallengeListAdapter(QuizJoinedUpcomingChallengesActivity.this, list));
                    }

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    ma.dismissProgressDialog();

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizJoinedUpcomingChallengesActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Challenges();
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
