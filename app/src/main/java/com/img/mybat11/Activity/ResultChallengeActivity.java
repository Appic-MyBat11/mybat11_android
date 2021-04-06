package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.img.mybat11.Adapter.ResultChallengeListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.GetSet.SelectedPlayersGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
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

public class ResultChallengeActivity extends AppCompatActivity {

    TextView title;
    ImageView notification,back;
    public static Activity fa;
    Button btnDreamTeam;
    RequestQueue requestQueue;

    ListView challengeList;
    ArrayList<JoinedChallengesGetSet> list;
    String TAG="Challenge list";
    GlobalVariables gv;
    MainActivity ma;
    ConnectionDetector cd;
    UserSessionManager session;
    TextView noChallenge;

    TextView match_name,timeRemaining,score;
    TextView team1,team1Score,team1Overs,team2,team2Score,team2Overs,finalResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_challenge);


        ma= new MainActivity();
        fa = this;
        gv= (GlobalVariables)getApplicationContext();
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        requestQueue = Volley.newRequestQueue(ResultChallengeActivity.this);

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
        score=(TextView)findViewById(R.id.playerStats);
        timeRemaining=(TextView)findViewById(R.id.timeRemaining);
        noChallenge=(TextView)findViewById(R.id.noChallenge);
        btnDreamTeam=(Button)findViewById(R.id.btnDreamTeam);

        match_name.setText(gv.getTeam1().toUpperCase()+" vs "+gv.getTeam2().toUpperCase());

        timeRemaining.setText("Challenge Closed");
        challengeList= (ListView) findViewById(R.id.challengeList);

        LinearLayout scorell = (LinearLayout)findViewById(R.id.scorell);
        scorell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultChallengeActivity.this,FantasyScorecardActivity.class));
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

        if(cd.isConnectingToInternet()){
            ma.showProgressDialog(ResultChallengeActivity.this);
            LiveChallenges();
            LiveScores();
        }

        btnDreamTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // teamPreviewActivity
                ViewTeam();
            }
        });
    }

    public void ViewTeam(){
        ma.showProgressDialog(ResultChallengeActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<SelectedPlayersGetSet>> call = apiSeitemViewice.dreamteam(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<SelectedPlayersGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<SelectedPlayersGetSet>> call, Response<ArrayList<SelectedPlayersGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    ArrayList<SelectedPlayersGetSet>playerList = response.body();
                    ma.dismissProgressDialog();

                    ArrayList<captainListGetSet>captainList= new ArrayList<>();
                    for(SelectedPlayersGetSet zz:playerList){

                        Log.i("Selected team ",zz.getPlayer_name());
                        Log.i("captain",zz.getCaptain());
                        Log.i("Vice captain",zz.getVicecaptain());

                        captainListGetSet ob = new captainListGetSet();
                        ob.setTeamcolor(zz.getTeamcolor());
                        ob.setTeam(zz.getTeam());
                        ob.setName(zz.getPlayer_name());
                        ob.setCredit(zz.getCredit());
                        ob.setImage(zz.getImage());
                        ob.setPoints(zz.getPoints());
                        if(zz.getRole().equals("keeper")) {
                            ob.setRole("Wk");
                        }if(zz.getRole().equals("batsman")) {
                            ob.setRole("Bat");
                        }if(zz.getRole().equals("bowler")) {
                            ob.setRole("Bow");
                        }if(zz.getRole().equals("allrounder")) {
                            ob.setRole("AR");
                        }
                        ob.setId(zz.getId());
                        ob.setCaptain(String.valueOf(zz.getCaptain()));
                        ob.setVc(String.valueOf(zz.getVicecaptain()));

                        captainList.add(ob);
                    }

                    Intent ii= new Intent(ResultChallengeActivity.this, PreviewActivity.class);
                    ii.putExtra("team_name","Dream Team");
                    ii.putExtra("TeamID",0);
                    gv.setCaptainList(captainList);
                    startActivity(ii);

                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(ResultChallengeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewTeam();
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
            public void onFailure(Call<ArrayList<SelectedPlayersGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
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
                    if (list.size() >0)
                        challengeList.setAdapter(new ResultChallengeListAdapter(ResultChallengeActivity.this, list));
                    else {
                        challengeList.setVisibility(View.GONE);
                        noChallenge.setVisibility(View.VISIBLE);
                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(ResultChallengeActivity.this);
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
                                ma.showToast(ResultChallengeActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(ResultChallengeActivity.this);
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
