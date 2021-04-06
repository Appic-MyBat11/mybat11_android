package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.LeaderBoardAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.Fragment.LeaderboardFragment;
import com.img.mybat11.Fragment.PriceCardFragment;
import com.img.mybat11.Fragment.PriceCardPercentageFragment;
import com.img.mybat11.Fragment.QuizLeaderboardFragment;
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.GetSet.LeagueDetailsGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.priceCardGetSet;
import com.img.mybat11.GetSet.teamsGetSet;
import com.img.mybat11.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizDetailsActivity extends AppCompatActivity {
    private RecyclerView.LayoutManager mLayoutManager;
    CircleImageView fab;
    TextView title,numWinners,prizeMoney,entryFee,lb,totalTeamslb,timeRemaining,match_name,t5,close,bonusText;
    ImageView back;
    LinearLayout winnerLL,myteamsLL,cll,mll,bll;
    GlobalVariables gv;
    MainActivity ma;
    RequestQueue requestQueue;
    ExpandableHeightListView priceCard;
    private BottomSheetBehavior mBottomSheetBehavior1;
    TextView totalPrizeAmount;
    UserSessionManager session;
    Button btnJoin,btnInvite,btnJoinAgain;
    ProgressBar teamEnteredPB;
    TextView teamsLeft,totalTeams;
    static Activity fa;
    TextView megaPriceCondition;
    String datacode = "";
    String jid = "";

    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate,endDate;

    double availableB,usableB;
    String TAG="Challenge list",joinnigB="";
    Dialog d,teamd;
    ArrayList<JoinChallengeBalanceGetSet> balancelist;
    ArrayList<LeagueDetailsGetSet> list;
    ArrayList<teamsGetSet> teams,teams1,myteams;
    ConnectionDetector cd;
    LeaderBoardAdapter adapter;
    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<MyTeamsGetSet> selectedteamList;
    boolean isSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);




        fa= this;

        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(QuizDetailsActivity.this);
        gv= (GlobalVariables)getApplicationContext();
        session = new UserSessionManager(getApplicationContext());
        cd= new ConnectionDetector(getApplicationContext());

        joinnigB = getIntent().getStringExtra("joinnigB");

        timeRemaining=(TextView)findViewById(R.id.timeRemaining);
        match_name=(TextView)findViewById(R.id.match_name);


        lb=(TextView)findViewById(R.id.lb);
        close=(TextView)findViewById(R.id.close);
        totalTeamslb=(TextView)findViewById(R.id.totalteamsLB);
        bonusText=(TextView)findViewById(R.id.bonusText);

        btnInvite=(Button)findViewById(R.id.btnInvite);
        btnJoinAgain=(Button)findViewById(R.id.btnJoinAgain);
        btnJoin=(Button)findViewById(R.id.btnJoin);



        cll=(LinearLayout)findViewById(R.id.cll);
        mll=(LinearLayout)findViewById(R.id.mll);
        bll=(LinearLayout)findViewById(R.id.bll);

        title=(TextView)findViewById(R.id.title);


        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(gv.getMatchTime()));
            title.setText(newDate + " Quiz");
            datacode = newDate + " Quiz";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeRemaining.setText(gv.getQuizquestions()+" Questions");

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareBody ="You’ve been challenged! \n" +
                        "\n" +
                        "Think you can beat me? Join the contest on "+getResources().getString(R.string.app_name)+" for the "+datacode+" and prove it!\n" +
                        "\n" +
                        "Use Contest Code "+list.get(0).getRefercode()+" & join the action NOW!"+
                        "\nDownload Application from "+ getResources().getString(R.string.apk_url);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(QuizDetailsActivity.this,ChallengesActivity.class));
                finish();
            }
        });



        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        winnerLL=(LinearLayout)findViewById(R.id.winnerLL);
        myteamsLL=(LinearLayout)findViewById(R.id.myteamsLL);
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

        entryFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSelected) {
                    Intent ii= new Intent(QuizDetailsActivity.this,QuizCheckBalanceActivity.class);
                    ii.putExtra("challengeId",list.get(0).getId());
                    startActivity(ii);
                }
            }
        });
        priceCard=(ExpandableHeightListView)findViewById(R.id.priceCard);
        priceCard.setExpanded(true);

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        bottomSheet.setVisibility(View.VISIBLE);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);


        totalPrizeAmount = findViewById(R.id.totalPrizeAmount);
        close = findViewById(R.id.close);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });



        TextView close = (TextView) findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        // code for counter
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate =gv.getMatchTime();

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

        teams1 = new ArrayList<>();

//        final SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Details();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            myteamsLL.setVisibility(View.VISIBLE);
            myteamsLL.removeAllViews();
            Details();
        }
    }


    public void Details(){
        ma.showProgressDialog(QuizDetailsActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Log.i("url is","");
        Call<ArrayList<LeagueDetailsGetSet>> call = apiSeitemViewice.QuizContestDetails(String.valueOf(getIntent().getExtras().getInt("challenge_id")),session.getUserId());
        call.enqueue(new Callback<ArrayList<LeagueDetailsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LeagueDetailsGetSet>> call, Response<ArrayList<LeagueDetailsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    jid = "";

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


                    final NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
                    ((DecimalFormat)nf2).applyPattern("##,##,###.##");

                    for(int j=0; j < list.get(0).getTotal_joined(); j++){
                        jid =  jid + ","+ list.get(0).getJointeams().get(j).getJid();
                    }

                    jid = jid.replaceFirst(",","");

                    joinnigB = String.valueOf(list.get(0).getEntryfee());

                    if (list.get(0).getConfirmed_challenge() == 1)
                        cll.setVisibility(View.VISIBLE);

                    if (list.get(0).getMulti_entry() == 1)
                        mll.setVisibility(View.VISIBLE);

                    if (list.get(0).isIsselected()) {
                        btnJoinAgain.setVisibility(View.GONE);
                        btnJoin.setVisibility(View.GONE);
                        btnInvite.setVisibility(View.VISIBLE);
                        isSelected = true;
                    }

                    int left = (list.get(0).getMaximum_user()) - (list.get(0).getJoinedusers());

                    if ((list.get(0).getJoinedusers() == list.get(0).getMaximum_user()) && (!list.get(0).getContest_type().equalsIgnoreCase("Percentage"))) {
                        btnInvite.setVisibility(View.GONE);
                        btnJoinAgain.setVisibility(View.GONE);
                        btnJoin.setVisibility(View.GONE);
                    }

                    if (list.get(0).getIs_bonus() == 1) {
                        bll.setVisibility(View.VISIBLE);
                        if (list.get(0).getBonus_type().equals("Amount")) {
                            bonusText.setText("₹ "+list.get(0).getBonus_percentage() + " Bonus");
                        }else {
                            bonusText.setText(list.get(0).getBonus_percentage() + "% Bonus");
                        }
                    }

                    if (list.get(0).getContest_type().equals("Amount")) {
                        if (list.get(0).getTotalwinners() == 1)
                            numWinners.setText("1");
                        else
                            numWinners.setText(nf2.format(list.get(0).getTotalwinners()) + " ▼");

                        if(left!=0) {
                                teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " Left");
                        }else
                            teamsLeft.setText("Contest Completed");
                        totalTeams.setText(nf2.format(list.get(0).getMaximum_user()) + " Entries");

                        teamEnteredPB.setMax(list.get(0).getMaximum_user());
                        teamEnteredPB.setProgress(list.get(0).getJoinedusers());

                    } else {
                        numWinners.setText(list.get(0).getWinning_percentage() + " %");
                        if(list.get(0).getJoinedusers() == 1)
                            teamsLeft.setText(nf2.format(list.get(0).getJoinedusers())+" Entries Joined");
                        else
                            teamsLeft.setText(nf2.format(list.get(0).getJoinedusers())+" Entries Joined");
                        totalTeams.setText("∞ Entries");

                        teamEnteredPB.setMax(list.get(0).getJoinedusers());
                        teamEnteredPB.setProgress(list.get(0).getJoinedusers());
                    }

                    entryFee.setText("₹ " + (int) list.get(0).getEntryfee());
                    prizeMoney.setText("₹ " + nf2.format(list.get(0).getWin_amount()));

                    if(list.get(0).getWin_amount() == 0){
                        winnerLL.setVisibility(View.GONE);
                        prizeMoney.setText("Net Practice");
                    }

                    teams = list.get(0).getJointeams();


                    viewPager.setAdapter(new QuizDetailsActivity.SectionPagerAdapter(getSupportFragmentManager()));
                    tabLayout.setupWithViewPager(viewPager);

                    btnJoinAgain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent ii= new Intent(QuizDetailsActivity.this,QuizCheckBalanceActivity.class);
                            ii.putExtra("challenge_id",list.get(0).getId());
                            startActivity(ii);
                        }
                    });

                    btnJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent ii= new Intent(QuizDetailsActivity.this,QuizCheckBalanceActivity.class);
                            ii.putExtra("challenge_id",list.get(0).getId());
                            startActivity(ii);
                        }
                    });

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizDetailsActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Details();
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
            public void onFailure(Call<ArrayList<LeagueDetailsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
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
                default: return new QuizLeaderboardFragment(teams);
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
            }

        }
    }


 }
