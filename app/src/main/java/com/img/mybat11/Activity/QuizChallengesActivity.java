package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.ChallengeCategoryListAdapter;
import com.img.mybat11.Adapter.QuizChallengeCategoryListAdapter;
import com.img.mybat11.Adapter.QuizJoinedChallengeListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.Fragment.JoinedContestFragment;
import com.img.mybat11.Fragment.MyContestsFragment;
import com.img.mybat11.Fragment.MyTeamsFragment;
import com.img.mybat11.GetSet.CategoriesGetSet;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
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

public class QuizChallengesActivity extends AppCompatActivity {



    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;
    TextView match_name,timeRemaining;
    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate=null,endDate = null;
    TextView joinContest,allContests;
    RecyclerView ll;
    ArrayList<CategoriesGetSet> list,list1;
    ArrayList<JoinedChallengesGetSet> jlist;
    Button joinContestcontest;
    SwipeRefreshLayout swipeRefreshLayout;
    String TAG = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_challenges);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();


        joinContest=(TextView)findViewById(R.id.joinContest);
        allContests=(TextView)findViewById(R.id.allContests);
        ll=(RecyclerView) findViewById(R.id.ll);
        ll.setLayoutManager(new LinearLayoutManager(QuizChallengesActivity.this));
        joinContestcontest=(Button) findViewById(R.id.joinContestcontest);
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        match_name=(TextView)findViewById(R.id.match_name);
        timeRemaining=(TextView)findViewById(R.id.timeRemaining);

        joinContest.setTypeface(Typeface.createFromAsset(QuizChallengesActivity.this.getAssets(), "fontawesome-webfont.ttf"));
        allContests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(QuizChallengesActivity.this, QuizAllChallengesActivity.class);
                ii.putExtra("catid","");
                startActivity(ii);
            }
        });


        joinContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizChallengesActivity.this, QuizJoinByCodeActivity.class));
            }
        });
        joinContestcontest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizChallengesActivity.this, QuizJoinedUpcomingChallengesActivity.class));
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Challenges();
                swipeRefreshLayout.setRefreshing(false);
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
                    match_name.setText("⏰ "+String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
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

    }


    @Override
    public void onResume() {
        super.onResume();
        ma.showProgressDialog(QuizChallengesActivity.this);
        if(cd.isConnectingToInternet()) {
            Challenges();
            Challengesjoin();
        }
    }

    public void Challengesjoin(){
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


                    jlist = response.body();


                    if (jlist.size() == 0) {
                        joinContestcontest.setVisibility(View.GONE);
                    } else {
                        joinContestcontest.setVisibility(View.VISIBLE);
                        joinContestcontest.setText("Joined Contest(" + jlist.size() +")");
                    }

                }else {
                    Log.i(TAG, "Responce code " + response.code());


                    AlertDialog.Builder d = new AlertDialog.Builder(QuizChallengesActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Challengesjoin();
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
            @Override
            public void onFailure(Call<ArrayList<JoinedChallengesGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }


    public void Challenges(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<CategoriesGetSet>> call = apiSeitemViewice.getquizContests(session.getUserId(),String.valueOf(gv.getQuizid()));
        call.enqueue(new Callback<ArrayList<CategoriesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<CategoriesGetSet>> call, Response<ArrayList<CategoriesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = new ArrayList<>();
                    list = response.body();
                    list1 = new ArrayList<>();
                    if (list.size() > 0) {

                        for (CategoriesGetSet zz : list) {
                            if (zz.getContest().size() > 0) {
                                list1.add(zz);
                            }
                        }
                        ll.setAdapter(new QuizChallengeCategoryListAdapter(QuizChallengesActivity.this,list1));
                    }


                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizChallengesActivity.this);
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

                        }
                    });
                    ma.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<CategoriesGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

}
