package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeChallengeActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    TextInputLayout name,amount,numWinners;
    Switch switchMultiple;
    TextView timeLeft,match_name;
    int multi_entry =0;
    TextView entryFee;
    Button btnCreate;
    static Activity fa;

    double availableB,usableB;
    String TAG="Challenge list",joinnigB="";
    int challengeId;
    Dialog d,teamd;
    ArrayList<JoinChallengeBalanceGetSet> balancelist;
    ArrayList<MyTeamsGetSet> selectedteamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_challenge);


        fa=this;

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Make Your Own Contest");

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();

        timeLeft=(TextView)findViewById(R.id.timeRemaining);
        match_name=(TextView)findViewById(R.id.match_name);

        Date startDate=null,endDate = null;
        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";

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
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMs = endDate.getTime() - startDate.getTime();

        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {
                timeLeft.setText("⏰ "+String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));
            }

            public void onFinish() {
                SweetAlertDialog d = new SweetAlertDialog(MakeChallengeActivity.this,SweetAlertDialog.WARNING_TYPE);
                d.setContentText("Match time is over")
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                finish();
                            }
                        });
                d.show();
            }
        };
        cT.start();

        match_name.setText(gv.getTeam1()+" vs "+gv.getTeam2());

        name=(TextInputLayout)findViewById(R.id.name);
        amount=(TextInputLayout)findViewById(R.id.amount);
        numWinners=(TextInputLayout)findViewById(R.id.numWinners);

        entryFee=(TextView)findViewById(R.id.entryFee);

        switchMultiple=(Switch)findViewById(R.id.switchMultiple);
        btnCreate=(Button)findViewById(R.id.btnCreate);

        amount.getEditText().addTextChangedListener(watch1);
        numWinners.getEditText().addTextChangedListener(watch2);

        switchMultiple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    multi_entry=1;
                else
                    multi_entry =0;
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    TextWatcher watch1= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!amount.getEditText().getText().toString().equals("") && !amount.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("")){
                Double total= (Double.parseDouble(amount.getEditText().getText().toString())*1.15) / Double.parseDouble(numWinners.getEditText().getText().toString()) ;
                entryFee.setText("₹ "+String.format("%.2f", total));
            } else {
                entryFee.setText("₹ 0.0");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher watch2= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!amount.getEditText().getText().toString().equals("") && !amount.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("")){
                Double total= (Double.parseDouble(amount.getEditText().getText().toString())*1.15) / Double.parseDouble(numWinners.getEditText().getText().toString()) ;
                entryFee.setText("₹ "+String.format("%.2f", total));
            } else {
                entryFee.setText("₹ 0.0");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void validate(){

        final String name1,amount1,numWinners1;

        name1=name.getEditText().getText().toString();
        amount1=amount.getEditText().getText().toString();
        numWinners1= numWinners.getEditText().getText().toString();

        if(name1.equals(""))
            name.setError("Please enter a name for your contest");
        else if(amount1.equals("") || Integer.parseInt(amount1)>10000)
            amount.setError("Please enter a valid amount");
        else if(numWinners1.equals("") || Integer.parseInt(numWinners1)>100 || Integer.parseInt(numWinners1) < 2 )
            numWinners.setError("Please enter valid contest size");
        else{
            joinnigB = entryFee.getText().toString().replace("₹ ","");
            if(cd.isConnectingToInternet()) {
                CreateChallenge();
            }
        }
    }

    public void CreateChallenge(){
        ma.showProgressDialog(MakeChallengeActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"create_eleven_challenge";
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

                                ma.showToast(MakeChallengeActivity.this,jsonObject.getString("msg"));
                                if(jsonObject.getBoolean("success")){
                                    gv.setIsprivate(true);
                                    gv.setMulti_entry(String.valueOf(multi_entry));
                                    challengeId = jsonObject.getInt("challengeid");
                                    MyTeams(0);
                                }

                                ma.dismissProgressDialog();
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
                                ma.showToast(MakeChallengeActivity.this,"Session Timeout");

                                startActivity(new Intent(MakeChallengeActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MakeChallengeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateChallenge();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(MakeChallengeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateChallenge();
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

                @Override
                public Map<String, String> getParams(){
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("matchkey",gv.getMatchKey());
                    params.put("maximum_user",numWinners.getEditText().getText().toString());
                    params.put("win_amount",amount.getEditText().getText().toString());
                    params.put("entryfee",entryFee.getText().toString().replaceAll("₹ ",""));
                    params.put("name",name.getEditText().getText().toString());
                    params.put("multi_entry",String.valueOf(multi_entry));
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
        ma.showProgressDialog(MakeChallengeActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.getMyTeams1(session.getUserId(),gv.getMatchKey(),"classic");
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    ma.dismissProgressDialog();
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    selectedteamList = response.body();

                    int total = selectedteamList.size();
                    int count =0;
                    int teamid =0;
                    /*for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isSelected())
                            count++;
                        else
                            teamid = zz.getTeamid();
                    }*/
                    int result = total-count;

                    if(result == 0){
                        Intent ii= new Intent(MakeChallengeActivity.this,CreateTeamActivity.class);
                        ii.putExtra("teamNumber",selectedteamList.size()+1);
                        ii.putExtra("challengeId",challengeId);
                        ii.putExtra("entryFee",(int) Double.parseDouble(joinnigB));
                        ii.putExtra("type","classic");
                        startActivity(ii);
                        finish();
                    }/*else if (result == 1) {
                        Intent intent = new Intent(MakeChallengeActivity.this, JoinContestActivity.class);
                        intent.putExtra("challenge_id",challengeId);
                        intent.putExtra("team",String.valueOf(teamid));
                        intent.putExtra("entryFee",joinnigB);
                        startActivity(intent);
                        finish();
                    } */else {
                        gv.setSelectedTeamList(selectedteamList);
                        Intent intent = new Intent(MakeChallengeActivity.this, ChooseTeamActivity.class);
                        intent.putExtra("type","join");
                        intent.putExtra("type1","classic");
                        intent.putExtra("challengeId",challengeId);
                        intent.putExtra("entryFee",joinnigB);
                        startActivity(intent);
                        finish();
                    }
                }else {
//                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(MakeChallengeActivity.this);
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
                            ma.dismissProgressDialog();
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
