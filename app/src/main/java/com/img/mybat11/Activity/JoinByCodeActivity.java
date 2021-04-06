package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
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

public class JoinByCodeActivity extends AppCompatActivity {

    TextInputLayout promoCode;
    TextView btnJoin;
    RequestQueue requestQueue;
    String TAG="Join League";
    MainActivity ma;
    UserSessionManager session;
    ConnectionDetector cd;
    GlobalVariables gv;

    String joinnigB="";
    int challengeId;
    String type="";
    ArrayList<MyTeamsGetSet> selectedteamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_by_code);



        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(JoinByCodeActivity.this);

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("Invite Code");

        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        session= new UserSessionManager(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        cd = new ConnectionDetector(getApplicationContext());

        promoCode=(TextInputLayout)findViewById(R.id.promoCode);
        btnJoin=(TextView)findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(promoCode.getEditText().getText().toString().equals(""))
                    promoCode.setError("Please enter challenge code");
                else
                {
                    JoinByCode(promoCode.getEditText().getText().toString());
                }
            }
        });

    }

    public void JoinByCode(final String code){
        ma.showProgressDialog(JoinByCodeActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"joinbycode";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                ma.dismissProgressDialog();
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                ma.showToast(JoinByCodeActivity.this,jsonObject.getString("message"));
                                if(jsonObject.getBoolean("success")){
                                    challengeId = jsonObject.getInt("challengeid");
                                    joinnigB = jsonObject.getString("entryfee");
                                    gv.setMulti_entry(String.valueOf(jsonObject.getInt("multi_entry")));
                                    type = "classic";
                                    MyTeams(0);
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
                                ma.showToast(JoinByCodeActivity.this,"Session Timeout");

                                startActivity(new Intent(JoinByCodeActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        JoinByCode(code);
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        JoinByCode(code);
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
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("matchkey",gv.getMatchKey());
                    params.put("getcode",code);
                    Log.i("Header",params.toString());

                    return params;
                }

            };
            strRequest.setShouldCache(false);
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    public void MyTeams(final int pos){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.getMyTeams(session.getUserId(),gv.getMatchKey(),challengeId,type);
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    selectedteamList = response.body();

                    int total = selectedteamList.size();
                    int count =0;
                    int teamid =0;
/*
                    for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isSelected())
                            count++;
                        else
                            teamid = zz.getTeamid();
                    }
*/
                    int result = total-count;

                    if(result == 0){
                        Intent ii= new Intent(JoinByCodeActivity.this,CreateTeamActivity.class);
                        ii.putExtra("teamNumber",selectedteamList.size()+1);
                        ii.putExtra("challengeId",challengeId);
                        ii.putExtra("type",type);
                        ii.putExtra("entryFee",(int) Double.parseDouble(joinnigB));
                        startActivity(ii);
                        finish();
                    }/*else if (result == 1) {
                        Intent intent = new Intent(JoinByCodeActivity.this, JoinContestActivity.class);
                        intent.putExtra("challenge_id",challengeId);
                        intent.putExtra("team",String.valueOf(teamid));
                        intent.putExtra("entryFee",joinnigB);
                        startActivity(intent);
                    }*/ else {
                        gv.setSelectedTeamList(selectedteamList);
                        Intent intent = new Intent(JoinByCodeActivity.this, ChooseTeamActivity.class);
                        intent.putExtra("type","join");
                        intent.putExtra("type1",type);
                        intent.putExtra("challengeId",challengeId);
                        intent.putExtra("entryFee",joinnigB);
                        startActivity(intent);
                        finish();
                    }

                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeams(pos);
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
            public void onFailure(Call<ArrayList<MyTeamsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }
}
