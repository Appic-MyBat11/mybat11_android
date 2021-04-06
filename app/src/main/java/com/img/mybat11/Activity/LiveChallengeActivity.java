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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveChallengeActivity extends AppCompatActivity {

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
    TextView noChallenge;
    MainActivity ma;

    TextView match_name,timeRemaining,score;
    TextView team1,team1Score,team1Overs,team2,team2Score,team2Overs,finalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_challenge);



        ma = new MainActivity();
        fa = this;
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(LiveChallengeActivity.this);
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        title =(TextView)findViewById(R.id.title);
        title.setText("Joined Contests");

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        match_name=(TextView)findViewById(R.id.match_name);
        timeRemaining=(TextView)findViewById(R.id.timeRemaining);
        score=(TextView)findViewById(R.id.playerStats);
        noChallenge=(TextView)findViewById(R.id.noChallenge);

        match_name.setText(gv.getTeam1()+" vs "+gv.getTeam2());

        LinearLayout scorell = (LinearLayout)findViewById(R.id.scorell);
        scorell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LiveChallengeActivity.this,FantasyScorecardActivity.class));
            }
        });

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

        team1=(TextView)findViewById(R.id.team1);
        team1Score=(TextView)findViewById(R.id.team1Score);
        team1Overs=(TextView)findViewById(R.id.team1Overs);
        team2=(TextView)findViewById(R.id.team2);
        team2Score=(TextView)findViewById(R.id.team2Score);
        team2Overs=(TextView)findViewById(R.id.team2Overs);
        finalResult=(TextView)findViewById(R.id.finalResult);

        team1.setText(gv.getTeam1());
        team2.setText(gv.getTeam2());

        timeRemaining.setText(gv.getStatus());

        challengeList= (ListView) findViewById(R.id.challengeList);

        if(cd.isConnectingToInternet()){
            ma.showProgressDialog(LiveChallengeActivity.this);
            LiveChallenges();
            LiveScores();
        }

        final SwipeRefreshLayout swipeRefreshLayout= (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ma.showProgressDialog(LiveChallengeActivity.this);
                LiveChallenges();
                LiveScores();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public void LiveChallenges(){
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

                    list = response.body();
                    if (list.size() >0) {
                        noChallenge.setVisibility(View.GONE);
                        challengeList.setAdapter(new LiveChallengeListAdapter(LiveChallengeActivity.this, list));
                    }else {
                        challengeList.setVisibility(View.GONE);
                        noChallenge.setVisibility(View.VISIBLE);
                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(LiveChallengeActivity.this);
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
                                ma.showToast(LiveChallengeActivity.this,"Session Timeout");

                                startActivity(new Intent(LiveChallengeActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(LiveChallengeActivity.this);
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
