package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.TeamListAdapter1;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
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

public class ChooseTeamActivity extends AppCompatActivity {

    UserSessionManager session;
    ConnectionDetector cd;
    MainActivity ma;
    GlobalVariables gv;
    String TAG="balanceshhet";
    RequestQueue requestQueue;

    ArrayList<JoinChallengeBalanceGetSet> balancelist;
    ArrayList<MyTeamsGetSet> selectedteamList;

    String type;
    String selectedteam;
    int count = 0;
    ListView teamList;
    TextView btnCreateTeam,join,text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);



        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title=(TextView)findViewById(R.id.title);
        title.setText("Choose Your Team");

        type = getIntent().getExtras().getString("type");


        session= new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        ma = new MainActivity();
        gv=(GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        selectedteamList = gv.getSelectedTeamList();



        teamList= (ListView)findViewById(R.id.teamList);
        teamList.setAdapter(new TeamListAdapter1(ChooseTeamActivity.this,selectedteamList,type));

        btnCreateTeam = (TextView)findViewById(R.id.btnCreateTeam);
        join = (TextView)findViewById(R.id.btnJoin);
        text = (TextView)findViewById(R.id.text);

        if(selectedteamList.size() >= gv.getMax_teams()){
            btnCreateTeam.setVisibility(View.GONE);
        }


        if(type.equals("join")){
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    count = 0;
                    selectedteam = "";
                    for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isPicked()) {
                            selectedteam += "," + String.valueOf(zz.getTeamid());
                            count++;
                        }
                    }


                    if (gv.getMulti_entry().equals("1")) {
                        if (count > 0) {
                            Log.i("selectedteam", selectedteam.substring(1));
                            Intent intent = new Intent(ChooseTeamActivity.this, JoinContestActivity.class);
                            intent.putExtra("challenge_id", getIntent().getExtras().getInt("challengeId"));
                            intent.putExtra("team", selectedteam.substring(1));
                            intent.putExtra("count", count);
                            gv.setMulti_entry("");
                            intent.putExtra("entryFee", String.valueOf(getIntent().getExtras().getInt("entryFee")));
                            startActivity(intent);
                            finish();
                        } else {
                            ma.showToast(ChooseTeamActivity.this, "Please select your team to join this contest");
                        }
                    }else {
                        if (count == 1) {
                            Intent intent = new Intent(ChooseTeamActivity.this, JoinContestActivity.class);
                            intent.putExtra("challenge_id", getIntent().getExtras().getInt("challengeId"));
                            intent.putExtra("team", selectedteam.substring(1));
                            intent.putExtra("count", count);
                            gv.setMulti_entry("");
                            intent.putExtra("entryFee", String.valueOf(getIntent().getExtras().getInt("entryFee")));
                            startActivity(intent);
                            finish();
                        } else {
                            ma.showToast(ChooseTeamActivity.this, "Please select only team to join this contest");
                        }
                    }
                }
            });

            btnCreateTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedteamList.size()!= gv.getMax_teams()) {
                        Intent ii =new Intent(new Intent(ChooseTeamActivity.this,CreateTeamActivity.class));
                        ii.putExtra("teamNumber",selectedteamList.size()+1);
                        ii.putExtra("challengeId", getIntent().getExtras().getInt("challengeId"));
                        ii.putExtra("entryFee",getIntent().getExtras().getInt("entryFee"));
                        ii.putExtra("type",selectedteamList.get(0).getPlayer_type());
                        startActivity(ii);
                        finish();
                    }else{
                        ma.showToast(ChooseTeamActivity.this,"You can only create maximum of "+gv.getMax_teams()+" teams");
                    }
                }
            });

        }else if(type.equals("switch")){
            join.setText("Rejoin");
            text.setText("Choose team to rejoin this contest with");
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedteam = "";
                    count = 0;
                    for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isPicked()) {
                            selectedteam += "," + String.valueOf(zz.getTeamid());
                            count++;
                        }
                    }
                    selectedteam = selectedteam.replaceFirst(",","");

                    if(count == 1){
                        SwitchTeam(selectedteam,getIntent().getExtras().getString("joinid"));
                    }else{
                        ma.showToast(ChooseTeamActivity.this,"Please select exact 1 teams");
                    }
                }
            });

            btnCreateTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedteamList.size()!= gv.getMax_teams()) {
                        Intent ii =new Intent(new Intent(ChooseTeamActivity.this,CreateTeamActivity.class));
                        ii.putExtra("teamNumber",selectedteamList.size()+1);
                        ii.putExtra("challengeId", getIntent().getExtras().getInt("challengeId"));
                        ii.putExtra("entryFee",getIntent().getExtras().getInt("entryFee"));
                        ii.putExtra("type",selectedteamList.get(0).getPlayer_type());
                        ii.putExtra("chooseType","switch");
                        ii.putExtra("joinid",getIntent().getExtras().getString("joinid"));
                        startActivity(ii);
                        finish();
                    }else{
                        ma.showToast(ChooseTeamActivity.this,"You can only create maximum of "+gv.getMax_teams()+" teams");
                    }
                }
            });

        }

    }

    public void SwitchTeam(final String teamid, final String joinid){
        ma.showProgressDialog(ChooseTeamActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"switchteams";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                final JSONObject jsonObject = new JSONObject(response.toString());

                                ma.dismissProgressDialog();
                                if(jsonObject.getBoolean("success")){
                                    ma.successDialog(ChooseTeamActivity.this, jsonObject.getString("msg"), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ma.dismissDialog();
                                            Intent ii= new Intent(ChooseTeamActivity.this, DetailsActivity.class);
                                            ii.putExtra("challenge_id",getIntent().getExtras().getInt("challengeId"));
                                            startActivity(ii);
                                            finish();
                                        }
                                    });
                                }else {
                                    ma.errorDialog(ChooseTeamActivity.this, jsonObject.getString("msg"), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ma.dismissDialog();
                                        }
                                    });
                                }
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
                                ma.showToast(ChooseTeamActivity.this,"Session Timeout");

                                startActivity(new Intent(ChooseTeamActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(ChooseTeamActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

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

                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("matchkey",gv.getMatchKey());
                    params.put("challengeid",String.valueOf(getIntent().getExtras().getInt("challengeId")));
                    params.put("teamid", teamid);
                    params.put("joinid",joinid);
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
