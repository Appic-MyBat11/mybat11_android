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
import com.img.mybat11.Adapter.ChallengeListAdapter;
import com.img.mybat11.Adapter.FilterGridAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightGridView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.GetSet.SelectedTeamsGetSet;
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

public class AllChallengesActivity extends AppCompatActivity {

    TextView title;
    ImageView back;
    TextView myTeam, totalJoined;
    public static Activity fa;

    ListView challengeList;
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
    String type ="",catid="";
    String winning_sort = "asc", teams_sort = "asc", winners_sort = "asc", entry_sort = "asc";
    ExpandableHeightGridView entryGrid,numberOfTeamsGrid,prizePoolGrid,contestTypeGrid;
    TextView closeFilter,resetFilter;
    Button btnApply;
    ArrayList<filterGetSet> entryList,numofTeamsList,prizePoolList,contestTypeList;

    String entryAr[] = {"₹1 to ₹50","₹51 to ₹100","₹101 to 1000","₹1,001 & above"};
    String prizePoolAr[] = {"₹1 - ₹10,000","₹10,001 - ₹1 Lakh","₹1 Lakh - ₹10 Lakh","₹10 Lakh - ₹25 Lakh","₹25 Lakh & above"};
    String conTypeAr[] = {"Single Entry","Multi Entry","Single Winner","Multi Winner","Confirmed"};
    String numofTeamsAr [] = {"2","3 - 10","11 - 100","101 - 1,000","1,001 & above"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_challenges);

        fa = this;
        gv = (GlobalVariables) getApplicationContext();
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(AllChallengesActivity.this);

        totalContests = findViewById(R.id.totalContests);

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
                startActivity(new Intent(AllChallengesActivity.this, CreateTeamActivity.class));
            }
        });

        btnCreateTeam = (Button) findViewById(R.id.btnCreateTeam);
        btnCreateTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllChallengesActivity.this, CreateTeamActivity.class));
            }
        });

        title = (TextView) findViewById(R.id.title);
        title.setText("Contests");

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
                startActivity(new Intent(AllChallengesActivity.this, MyTeamActivity.class));
            }
        });

        joinedll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllChallengesActivity.this, JoinedChallengesActivity.class));
            }
        });

        match_name.setText(gv.getTeam1().toUpperCase() + " vs " + gv.getTeam2().toUpperCase());

        type = getIntent().getExtras().getString("type");
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
                timeRemaining.setText("⏰ "+String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
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

        challengeList = (ListView) findViewById(R.id.challengeList);

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
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
                } else {
                    winning.setText("Winning ⌄");
                    winning_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getWin_amount() - lhs.getWin_amount();
                        }
                    });
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
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
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
                } else {
                    winners.setText("Winners ⌄");
                    winners_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getTotalwinners() - lhs.getTotalwinners();
                        }
                    });
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
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
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
                } else {
                    teams.setText("Teams ⌄");
                    teams_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getMaximum_user() - lhs.getMaximum_user();
                        }
                    });
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
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
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
                } else {
                    entry.setText("Entry ⌄");
                    entry_sort = "asc";
                    Collections.sort(list1, new Comparator<challengesGetSet>() {
                        @Override
                        public int compare(challengesGetSet lhs, challengesGetSet rhs) {
                            return rhs.getEntryfee() - lhs.getEntryfee();
                        }
                    });
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
                }
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ma.showProgressDialog(AllChallengesActivity.this);
                if (catid.equals("")){
                    Challenges();
                }else {
                    remainchalenges();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        challengeList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else
                    swipeRefreshLayout.setEnabled(false);
            }
        });


        // filter

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        entryGrid=(ExpandableHeightGridView)findViewById(R.id.entryGrid);
        numberOfTeamsGrid=(ExpandableHeightGridView)findViewById(R.id.numberOfTeamsGrid);
        prizePoolGrid=(ExpandableHeightGridView)findViewById(R.id.prizePoolGrid);
        contestTypeGrid=(ExpandableHeightGridView)findViewById(R.id.contestTypeGrid);

        entryGrid.setExpanded(true);
        numberOfTeamsGrid.setExpanded(true);
        contestTypeGrid.setExpanded(true);
        prizePoolGrid.setExpanded(true);

        entryList = new ArrayList<>();
        numofTeamsList =new ArrayList<>();
        contestTypeList =new ArrayList<>();
        prizePoolList = new ArrayList<>();

        for(int i =0; i< entryAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(entryAr[i]);

            entryList.add(ob);
        }

        for(int i =0; i< numofTeamsAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(numofTeamsAr[i]);

            numofTeamsList.add(ob);
        }

        for(int i =0; i< conTypeAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(conTypeAr[i]);

            contestTypeList.add(ob);
        }

        for(int i =0; i< prizePoolAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(prizePoolAr[i]);

            prizePoolList.add(ob);
        }

        btnApply=(Button)findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyFilter();
            }
        });

        closeFilter=(TextView)findViewById(R.id.closeFilter);
        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        resetFilter = (TextView)findViewById(R.id.resetFilter);
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =0; i< entryList.size(); i++){
                    entryList.get(i).setSelected(false);
                }

                for(int i =0; i< numofTeamsList.size(); i++){
                    numofTeamsList.get(i).setSelected(false);
                }

                for(int i =0; i< contestTypeList.size(); i++){
                    contestTypeList.get(i).setSelected(false);
                }

                for(int i =0; i< prizePoolList.size(); i++){
                    prizePoolList.get(i).setSelected(false);
                }

                entryGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,entryList));
                numberOfTeamsGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,numofTeamsList));
                contestTypeGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,contestTypeList));
                prizePoolGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,prizePoolList));

                ApplyFilter();
            }
        });

        findViewById(R.id.filter1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entryGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,entryList));
                numberOfTeamsGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,numofTeamsList));
                contestTypeGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,contestTypeList));
                prizePoolGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,prizePoolList));

                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        if(getIntent().hasExtra("filter")) {
            entryGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,entryList));
            numberOfTeamsGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,numofTeamsList));
            contestTypeGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,contestTypeList));
            prizePoolGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,prizePoolList));

            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View view, int i) {
                if(mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onSlide(View view, float v) {

//                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (cd.isConnectingToInternet()) {
            ma.showProgressDialog(AllChallengesActivity.this);
            if (catid.equals("")){
                Challenges();
            }else {
                remainchalenges();
            }
        }

    }

    public void remainchalenges() {
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<challengesGetSet>> call = apiSeitemViewice.remain(session.getUserId(),gv.getMatchKey(),catid,type);
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

                    totalContests.setText(list1.size()+" Contests Available");
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
                    ma.dismissProgressDialog();
//                    Count();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(AllChallengesActivity.this);
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

        Call<ArrayList<challengesGetSet>> call = apiSeitemViewice.AllChallenges(session.getUserId(),gv.getMatchKey(),type);
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

                    totalContests.setText(list1.size()+" Contests Available");
                    challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list1,type));
                    ma.dismissProgressDialog();
//                    Count();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(AllChallengesActivity.this);
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

    public void Count(){
        try {

            String url = getResources().getString(R.string.app_url)+"myteam_joinedcontest?matchkey="+gv.getMatchKey();
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                int joined = jsonObject.getInt("mycontests");
                                int teams = jsonObject.getInt("myteams");

                                if (joined>0)
                                    totalJoined.setText("" + joined);
                                else
                                    totalJoined.setText("0");

                                if (teams > 0) {
                                    createTeamLL.setVisibility(View.GONE);
                                    myteamll.setVisibility(View.VISIBLE);
                                }

                                myTeam.setText("" + teams);

                                ma.dismissProgressDialog();
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }

                            ma.dismissProgressDialog();
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
                                ma.showToast(AllChallengesActivity.this,"Session Timeout");

                                startActivity(new Intent(AllChallengesActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(AllChallengesActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Count();
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
            strRequest.setShouldCache(false);
            strRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    public void  ApplyFilter(){
        ArrayList<challengesGetSet> list_1,list_2,list_3,list_4;

        list_1 = new ArrayList<>();
        list_2 = new ArrayList<>();
        list_3 = new ArrayList<>();
        list_4 = new ArrayList<>();

        list2 = new ArrayList<>();

        // entry fee
        if(entryList.get(0).isSelected()) {
            for (challengesGetSet zz : list1) {
                if(zz.getEntryfee() <= 50)
                    list_1.add(zz);
            }
        }
        if(entryList.get(1).isSelected()) {
            for (challengesGetSet zz : list1) {
                if(zz.getEntryfee() >= 51 && zz.getEntryfee() <= 100)
                    list_1.add(zz);
            }
        }
        if(entryList.get(2).isSelected()) {
            for (challengesGetSet zz : list1) {
                if(zz.getEntryfee() >= 1001 && zz.getEntryfee() <= 1000)
                    list_1.add(zz);
            }
        }
        if(entryList.get(3).isSelected()) {
            for (challengesGetSet zz : list1) {
                if(zz.getEntryfee() >1000)
                    list_1.add(zz);
            }
        }
        if(entryList.get(0).isSelected() == false && entryList.get(1).isSelected() == false && entryList.get(2).isSelected() == false && entryList.get(3).isSelected() == false)
            list_1 = list1;


        //num of teams
        if(numofTeamsList.get(0).isSelected()){
            for (challengesGetSet zz : list_1) {
                if(zz.getMaximum_user() ==2)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(1).isSelected()){
            for (challengesGetSet zz : list_1) {
                if(zz.getMaximum_user() >= 3 && zz.getMaximum_user() <= 10)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(2).isSelected()){
            for (challengesGetSet zz : list_1) {
                if(zz.getMaximum_user() >= 11 && zz.getMaximum_user() <= 100)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(3).isSelected()){
            for (challengesGetSet zz : list_1) {
                if(zz.getMaximum_user() >= 101 && zz.getMaximum_user() <= 1000)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(4).isSelected()){
            for (challengesGetSet zz : list_1) {
                if(zz.getMaximum_user() > 1000)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(0).isSelected() == false
                && numofTeamsList.get(1).isSelected() == false
                && numofTeamsList.get(2).isSelected() == false
                && numofTeamsList.get(3).isSelected() == false
                && numofTeamsList.get(4).isSelected() == false)
            list_2 = list_1;


        //Prize Pool
        if(prizePoolList.get(0).isSelected()){
            for (challengesGetSet zz : list_2) {
                if(zz.getWin_amount() >=1 && zz.getWin_amount() <= 10000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(1).isSelected()){
            for (challengesGetSet zz : list_2) {
                if(zz.getWin_amount() >= 10001 && zz.getWin_amount() <= 100000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(2).isSelected()){
            for (challengesGetSet zz : list_2) {
                if(zz.getWin_amount() >= 100001 && zz.getWin_amount() <= 1000000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(3).isSelected()){
            for (challengesGetSet zz : list_2) {
                if(zz.getWin_amount() >= 1000001 && zz.getWin_amount() <= 2500000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(4).isSelected()){
            for (challengesGetSet zz : list_2) {
                if(zz.getWin_amount() > 2500000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(0).isSelected() == false
                && prizePoolList.get(1).isSelected() == false
                && prizePoolList.get(2).isSelected() == false
                && prizePoolList.get(3).isSelected() == false
                && prizePoolList.get(4).isSelected() == false)
            list_3 = list_2;


        //Contest Type
        if(prizePoolList.get(0).isSelected()){
            for (challengesGetSet zz : list_3) {
                if(zz.getMulti_entry() == 0)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(1).isSelected()){
            for (challengesGetSet zz : list_3) {
                if(zz.getMulti_entry() == 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(2).isSelected()){
            for (challengesGetSet zz : list_3) {
                if(zz.getTotalwinners() == 0 || zz.getTotalwinners() == 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(3).isSelected()){
            for (challengesGetSet zz : list_3) {
                if(zz.getTotalwinners() > 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(4).isSelected()){
            for (challengesGetSet zz : list_3) {
                if(zz.getConfirmed_challenge() == 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(0).isSelected() == false
                && prizePoolList.get(1).isSelected() == false
                && prizePoolList.get(2).isSelected() == false
                && prizePoolList.get(3).isSelected() == false
                && prizePoolList.get(4).isSelected() == false)
            list_4 = list_3;

        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        list2 = list_4;
        totalContests.setText(list2.size()+" Contests Available");
        challengeList.setAdapter(new ChallengeListAdapter(AllChallengesActivity.this, list2,type));
    }

    @Override
    public void onBackPressed() {
        if( mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            finish();
    }
}
