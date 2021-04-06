package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinContestActivity extends AppCompatActivity {

    TextView availableBalance,entryFee,bonus,toPay,btnJoinContest;
    ImageView back;
    UserSessionManager session;
    MainActivity ma;
    ConnectionDetector cd;
    GlobalVariables gv;
    RequestQueue requestQueue;

    int challenge_id,count;
    String teamid ="";
    double usableB,joiningB;

    String TAG="Balance";
    ArrayList<JoinChallengeBalanceGetSet> balancelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_contest);



        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        gv=(GlobalVariables)getApplicationContext();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        availableBalance=(TextView)findViewById(R.id.availableBalance);
        entryFee=(TextView)findViewById(R.id.entryFee);
        bonus=(TextView)findViewById(R.id.bonus);
        toPay=(TextView)findViewById(R.id.toPay);
        btnJoinContest=(TextView)findViewById(R.id.btnJoinContest);

        challenge_id = getIntent().getExtras().getInt("challenge_id");
        teamid=getIntent().getExtras().getString("team");

        count=getIntent().getExtras().getInt("count");
        if (count == 0){
            count = 1;
        }


        btnJoinContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet())
                    joinChallenge(teamid);
            }
        });

        Log.i("countattime", String.valueOf(count));
        Log.i("countattimeteam", String.valueOf(teamid));
        Log.i("countattimechallenge_id", String.valueOf(challenge_id));

        if(cd.isConnectingToInternet()) {
            CheckBalance();
            Details();
        }

    }
    public void Details(){

        try {

            String url = getResources().getString(R.string.app_url)+"userfulldetails";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    startActivity(new Intent(JoinContestActivity.this,LoginActivity.class));
                                    finishAffinity();
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
                                ma.showToast(JoinContestActivity.this,"Session Timeout");

                                startActivity(new Intent(JoinContestActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(JoinContestActivity.this);
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

    public void CheckBalance(){
        ma.showProgressDialog(JoinContestActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<JoinChallengeBalanceGetSet>> call = apiSeitemViewice.checkBalance(session.getUserId(),String.valueOf(challenge_id));
        call.enqueue(new Callback<ArrayList<JoinChallengeBalanceGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<JoinChallengeBalanceGetSet>> call, Response<ArrayList<JoinChallengeBalanceGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    balancelist = response.body();
                    if (balancelist.get(0).isSuccess()) {
                        availableBalance.setText("Unutilized Balance + Winnings = ₹" + String.format("%.1f", balancelist.get(0).getUsablebalance()));
                        bonus.setText("-₹" + String.format("%.1f", balancelist.get(0).getBonusused()*count));
                        entryFee.setText("₹" + String.format("%.1f", balancelist.get(0).getEntryfee()*count));
                        double pay = balancelist.get(0).getEntryfee()*count - balancelist.get(0).getBonusused()*count;
                        toPay.setText("₹" + String.format("%.1f", pay));

                        usableB = balancelist.get(0).getUsablebalance();
                        joiningB = balancelist.get(0).getEntryfee()*count;



                        if (pay > usableB) {
                            ma.showToast(JoinContestActivity.this, "Insufficient Balance");
                            Intent ii = new Intent(new Intent(JoinContestActivity.this, AddBalanceActivity.class));
                            startActivity(ii);
                            gv.setPaytype("contest");
                            finish();
                        }
                    }else {
                        ma.showToast(JoinContestActivity.this, balancelist.get(0).getMsg());
                        finish();
                    }
                    ma.dismissProgressDialog();

                }else {
                    Log.i(TAG, "Responce code " + response.code());
                    AlertDialog.Builder d = new AlertDialog.Builder(JoinContestActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CheckBalance();
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
            public void onFailure(Call<ArrayList<JoinChallengeBalanceGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }

    public void joinChallenge(final String id){
        ma.showProgressDialog(JoinContestActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"joinleauge";
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
                                Log.i("message",jsonObject.getString("message"));
                                if(jsonObject.getBoolean("success")){
                                    session.setWallet_amount(jsonObject.getString("totalbalance"));
                                    ma.successDialog(JoinContestActivity.this, jsonObject.getString("message"), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                if(gv.isIsprivate()){
                                                    gv.setIsprivate(false);


                                                    String shareBody ="You’ve been challenged! \n" +
                                                            "\n" +
                                                            "Think you can beat me? Join the contest on "+getResources().getString(R.string.app_name)+" for the "+gv.getTeam1()+" vs "+gv.getTeam2()+" match and prove it!\n" +
                                                            "\n" +
                                                            "Use Contest Code "+jsonObject.getString("refercode")+" & join the action NOW!"+
                                                            "\nDownload Application from "+ getResources().getString(R.string.apk_url);
                                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                                    sharingIntent.setType("text/plain");

                                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                                    startActivity(Intent.createChooser(sharingIntent, "Share via"));

//                                                    String shareBody =jsonObject.getString("refercode");
//
//                                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                                                    sharingIntent.setType("text/plain");
//
//                                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
//                                                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ma.dismissDialog();
                                            finish();
                                        }
                                    });
                                }else {
                                    ma.errorDialog(JoinContestActivity.this, jsonObject.getString("message"), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ma.dismissDialog();
                                            finish();
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
                                ma.showToast(JoinContestActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinContestActivity.this);
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
                    params.put("challengeid",String.valueOf(challenge_id));
                    params.put("teamid",id);
                    Log.i("Header",params.toString());

                    return params;
                }

            };
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

}
