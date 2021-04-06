package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.QuizAllChallengeListAdapter;
import com.img.mybat11.Adapter.QuizChallengeListAdapter;
import com.img.mybat11.Adapter.QuizChallengeListAdapter;
import com.img.mybat11.Adapter.FilterGridAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightGridView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.GetSet.filterGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizAllChallengesActivity extends AppCompatActivity {

    TextView title;
    ImageView back;
    TextView myTeam, totalJoined;
    public static Activity fa;

    RecyclerView challengeList;
    TextView totalContests;
    ArrayList<challengesGetSet> list, list1, list2;
    String TAG = "Challenge list";

    GlobalVariables gv;
    ConnectionDetector cd;
    MainActivity ma;
    UserSessionManager session;
    RequestQueue requestQueue;

    LinearLayout myteamll, joinedll, createTeamLL;
    TextView winning, teams, winners, entry;
    Button btnCreateTeam;

    TextView match_name, timeRemaining;
    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate = null, endDate = null;


    private BottomSheetBehavior mBottomSheetBehavior1;
    String catid="";
    String winning_sort = "asc", teams_sort = "asc", winners_sort = "asc", entry_sort = "asc"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_all_challenges);


        fa = this;
        gv = (GlobalVariables) getApplicationContext();
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(QuizAllChallengesActivity.this);


        winners = (TextView) findViewById(R.id.winners);
        teams = (TextView) findViewById(R.id.teams);
        winning = (TextView) findViewById(R.id.winning);
        entry = (TextView) findViewById(R.id.entry);

        myteamll = (LinearLayout) findViewById(R.id.myteamll);
        joinedll = (LinearLayout) findViewById(R.id.joinedll);
        createTeamLL = (LinearLayout) findViewById(R.id.createTeamLL);

        createTeamLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizAllChallengesActivity.this, CreateTeamActivity.class));
            }
        });

        btnCreateTeam = (Button) findViewById(R.id.btnCreateTeam);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizAllChallengesActivity.this, CreateTeamActivity.class));
            }
        });

        title = (TextView) findViewById(R.id.title);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        totalJoined = (TextView) findViewById(R.id.totalJoined);
        myTeam = (TextView) findViewById(R.id.myTeam);
        match_name = (TextView) findViewById(R.id.match_name);
        timeRemaining = (TextView) findViewById(R.id.timeRemaining);

        myteamll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizAllChallengesActivity.this, MyTeamActivity.class));
            }
        });

        joinedll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizAllChallengesActivity.this, JoinedChallengesActivity.class));
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
        timeRemaining.setText("10 Questions");
 
        catid = getIntent().getExtras().getString("catid");


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
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        cd = new ConnectionDetector(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());
        gv = (GlobalVariables) getApplicationContext();

        challengeList = (RecyclerView) findViewById(R.id.challengeList);
        challengeList.setLayoutManager(new LinearLayoutManager(QuizAllChallengesActivity.this));

        winning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                winning.setTextColor(getResources().getColor(R.color.selected));
                winners.setTextColor(getResources().getColor(R.color.unselected));
                teams.setTextColor(getResources().getColor(R.color.unselected));
                entry.setTextColor(getResources().getColor(R.color.unselected));

                if (winning_sort.equals("asc")) {
                    winning_sort = "desc";
                    winning.setText("Winning ⌃");
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return lhs.getWin_amount() - rhs.getWin_amount();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                } else {
                    winning.setText("Winning ⌄");
                    winning_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getWin_amount() - lhs.getWin_amount();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                }
            }
        });

        winners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                winning.setTextColor(getResources().getColor(R.color.unselected));
                winners.setTextColor(getResources().getColor(R.color.selected));
                teams.setTextColor(getResources().getColor(R.color.unselected));
                entry.setTextColor(getResources().getColor(R.color.unselected));

                if (winners_sort.equals("asc")) {
                    winners.setText("Winners ⌃");
                    winners_sort = "desc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return lhs.getTotalwinners() - rhs.getTotalwinners();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                } else {
                    winners.setText("Winners ⌄");
                    winners_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getTotalwinners() - lhs.getTotalwinners();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                }
            }
        });

        teams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                winning.setTextColor(getResources().getColor(R.color.unselected));
                winners.setTextColor(getResources().getColor(R.color.unselected));
                teams.setTextColor(getResources().getColor(R.color.selected));
                entry.setTextColor(getResources().getColor(R.color.unselected));

                if (teams_sort.equals("asc")) {
                    teams_sort = "desc";
                    teams.setText("Teams ⌃");
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return lhs.getMaximum_user() - rhs.getMaximum_user();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                } else {
                    teams.setText("Teams ⌄");
                    teams_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getMaximum_user() - lhs.getMaximum_user();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                }
            }
        });

        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                winning.setTextColor(getResources().getColor(R.color.unselected));
                winners.setTextColor(getResources().getColor(R.color.unselected));
                teams.setTextColor(getResources().getColor(R.color.unselected));
                entry.setTextColor(getResources().getColor(R.color.selected));

                if (entry_sort.equals("asc")) {
                    entry_sort = "desc";
                    entry.setText("Entry ⌃");
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return lhs.getEntryfee() - rhs.getEntryfee();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                } else {
                    entry.setText("Entry ⌄");
                    entry_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getEntryfee() - lhs.getEntryfee();
                        }
                    });
                    challengeList.setAdapter(new QuizChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                }
            }
        });

//        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                ma.showProgressDialog(QuizAllChallengesActivity.this);
//                if (catid.equals("")){
//                    Challenges();
//                }else {
//                    remainchalenges();
//                }
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });

//        challengeList.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    swipeRefreshLayout.setEnabled(true);
//                } else
//                    swipeRefreshLayout.setEnabled(false);
//            }
//        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (cd.isConnectingToInternet()) {
            ma.showProgressDialog(QuizAllChallengesActivity.this);
            if (catid.equals("")){
                Challenges();
            }else {
                remainchalenges();
            }
        }

    }

    public void remainchalenges() {
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<challengesGetSet>> call = apiSeitemViewice.quizremain(session.getUserId(), String.valueOf(gv.getQuizid()),catid);
        call.enqueue(new Callback<ArrayList<challengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<challengesGetSet>> call, Response<ArrayList<challengesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if (response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = new ArrayList<>();
                    list1 = new ArrayList<>();
                    list = response.body();
                    list1 = response.body();

                    if(getIntent().hasExtra("sortBy")){
                        if(getIntent().getExtras().getString("sortBy").equals("EntryFee")){
                            entry_sort = "desc";
                            entry.setText("Entry ⌃");
                            Collections.sort(list1, new Comparator<challengesGetSet>() {
                                @Override
                                public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                                    return lhs.getEntryfee() - rhs.getEntryfee();
                                }
                            });
                        } else if(getIntent().getExtras().getString("sortBy").equals("contestSize")){
                            teams_sort = "desc";
                            teams.setText("Teams ⌃");
                            Collections.sort(list1, new Comparator<challengesGetSet>() {
                                @Override
                                public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                                    return lhs.getMaximum_user() - rhs.getMaximum_user();
                                }
                            });
                        }
                    }

//                    totalContests.setText(list1.size()+" Contests Available");
                    challengeList.setAdapter(new QuizAllChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                    ma.dismissProgressDialog();
//                    Count();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizAllChallengesActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            remainchalenges();
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
            public void onFailure(Call<ArrayList<challengesGetSet>> call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }
    public void Challenges() {
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<challengesGetSet>> call = apiSeitemViewice.quizAllChallenges(session.getUserId(), String.valueOf(gv.getQuizid()));
        call.enqueue(new Callback<ArrayList<challengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<challengesGetSet>> call, Response<ArrayList<challengesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if (response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = new ArrayList<>();
                    list1 = new ArrayList<>();
                    list = response.body();
                    list1 = response.body();

                    if(getIntent().hasExtra("sortBy")){
                        if(getIntent().getExtras().getString("sortBy").equals("EntryFee")){
                            entry_sort = "desc";
                            entry.setText("Entry ⌃");
                            Collections.sort(list1, new Comparator<challengesGetSet>() {
                                @Override
                                public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                                    return lhs.getEntryfee() - rhs.getEntryfee();
                                }
                            });
                        } else if(getIntent().getExtras().getString("sortBy").equals("contestSize")){
                            teams_sort = "desc";
                            teams.setText("Teams ⌃");
                            Collections.sort(list1, new Comparator<challengesGetSet>() {
                                @Override
                                public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                                    return lhs.getMaximum_user() - rhs.getMaximum_user();
                                }
                            });
                        }
                    }

                    challengeList.setAdapter(new QuizAllChallengeListAdapter(QuizAllChallengesActivity.this, list1));
                    ma.dismissProgressDialog();
//                    Count();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizAllChallengesActivity.this);
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
                }
            }

            @Override
            public void onFailure(Call<ArrayList<challengesGetSet>> call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }



}
