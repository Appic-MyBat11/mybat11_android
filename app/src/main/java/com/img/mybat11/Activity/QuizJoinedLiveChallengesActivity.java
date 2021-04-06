package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.LiveChallengeListAdapter;
import com.img.mybat11.Adapter.QuizJoinedLiveChallengeListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizJoinedLiveChallengesActivity extends AppCompatActivity {

    TextView title;
    ImageView back;
    public static Activity fa;
    RequestQueue requestQueue;

    ListView challengeList;
    ArrayList<JoinedChallengesGetSet> list;
    String TAG="Challenge list";
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;
    TextView noChallenge,nomatchbtn;
    LinearLayout sometime;
    MainActivity ma;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView match_name,timeRemaining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_joined_live_challenges);



        ma = new MainActivity();
        fa = this;
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(QuizJoinedLiveChallengesActivity.this);
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        title =(TextView)findViewById(R.id.title);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        match_name=(TextView)findViewById(R.id.match_name);
        timeRemaining=(TextView)findViewById(R.id.timeRemaining);
        sometime=(LinearLayout) findViewById(R.id.sometime);
        noChallenge=(TextView)findViewById(R.id.noChallenge);
        nomatchbtn=(TextView)findViewById(R.id.nomatchbtn);


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

        challengeList= (ListView) findViewById(R.id.challengeList);

        if(cd.isConnectingToInternet()){
            ma.showProgressDialog(QuizJoinedLiveChallengesActivity.this);
            LiveChallenges();
        }

        swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ma.showProgressDialog(QuizJoinedLiveChallengesActivity.this);
                LiveChallenges();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        nomatchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuizJoinedLiveChallengesActivity.this,HomeActivity.class));
                finish();
            }
        });

    }

    public void LiveChallenges(){
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

                    list = response.body();
                    if (list.size() >0) {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        sometime.setVisibility(View.GONE);
                        noChallenge.setVisibility(View.GONE);
                        if (list.get(0).getUserpoints() == null) {
                            swipeRefreshLayout.setVisibility(View.GONE);
                            sometime.setVisibility(View.VISIBLE);
                        }else {
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            sometime.setVisibility(View.GONE);
                            challengeList.setAdapter(new QuizJoinedLiveChallengeListAdapter(QuizJoinedLiveChallengesActivity.this, list));
                        }
                    }else {
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        sometime.setVisibility(View.GONE);
                        challengeList.setVisibility(View.GONE);
                        noChallenge.setVisibility(View.VISIBLE);
                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizJoinedLiveChallengesActivity.this);
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
