package com.img.mybat11.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizCheckBalanceActivity extends AppCompatActivity {

    TextView availableBalance,entryFee,bonus,toPay,btnJoinContest;
    ImageView back;

    UserSessionManager session;
    MainActivity ma;
    ConnectionDetector cd;
    GlobalVariables gv;
    ArrayList<JoinedChallengesGetSet> jlist;
    RequestQueue requestQueue;

    String quizname="";
    int challenge_id;
    double usableB,joiningB;
    int remindtime = 1;

    String TAG="Balance";
    private String SD_YeaR,SD_MontH,SD_DaY,SD_HouR,SD_MinutE;
    ArrayList<JoinChallengeBalanceGetSet> balancelist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_check_balance);



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

        btnJoinContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet())
                    joinChallenge();
            }
        });

        Log.i("countattimechallenge_id", String.valueOf(challenge_id));

        if(cd.isConnectingToInternet()) {
            Details();
            CheckBalance();
            Challengesjoin();
        }

    }

    public void Challengesjoin(){
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


                    jlist = response.body();



                }else {
                    Log.i(TAG, "Responce code " + response.code());


                    AlertDialog.Builder d = new AlertDialog.Builder(QuizCheckBalanceActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Challengesjoin();
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
            @Override
            public void onFailure(Call<ArrayList<JoinedChallengesGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
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
                                    startActivity(new Intent(QuizCheckBalanceActivity.this,LoginActivity.class));
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
                                ma.showToast(QuizCheckBalanceActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(QuizCheckBalanceActivity.this);
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
        ma.showProgressDialog(QuizCheckBalanceActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<JoinChallengeBalanceGetSet>> call = apiSeitemViewice.quizcheckBalance(session.getUserId(),String.valueOf(challenge_id));
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
                        bonus.setText("-₹" + String.format("%.1f", balancelist.get(0).getBonusused()));
                        entryFee.setText("₹" + String.format("%.1f", balancelist.get(0).getEntryfee()));
                        double pay = balancelist.get(0).getEntryfee() - balancelist.get(0).getBonusused();
                        toPay.setText("₹" + String.format("%.1f", pay));

                        usableB = balancelist.get(0).getUsablebalance();
                        joiningB = balancelist.get(0).getEntryfee();

                        if (pay > usableB) {
                            ma.showToast(QuizCheckBalanceActivity.this, "Insufficient Balance");
                            Intent ii = new Intent(new Intent(QuizCheckBalanceActivity.this, AddBalanceActivity.class));
                            startActivity(ii);
                            gv.setPaytype("contest");
                            finish();
                        }
                    }else {
                        ma.showToast(QuizCheckBalanceActivity.this, balancelist.get(0).getMsg());
                        finish();
                    }
                    ma.dismissProgressDialog();

                }else {
                    Log.i(TAG, "Responce code " + response.code());
                    AlertDialog.Builder d = new AlertDialog.Builder(QuizCheckBalanceActivity.this);
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

    public void joinChallenge(){
        ma.showProgressDialog(QuizCheckBalanceActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"quiz_joinleauge";
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
//                                    ma.successDialog(QuizCheckBalanceActivity.this, jsonObject.getString("message"));

                                    if (jlist.size() == 0) {
                                        final Dialog d = new Dialog(QuizCheckBalanceActivity.this);
                                        d.setContentView(R.layout.remainder_layout);


                                        final TextView one, two, five, no, yes;

                                        one = (TextView) d.findViewById(R.id.one);
                                        two = (TextView) d.findViewById(R.id.two);
                                        five = (TextView) d.findViewById(R.id.five);
                                        no = (TextView) d.findViewById(R.id.no);
                                        yes = (TextView) d.findViewById(R.id.yes);

                                        one.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                remindtime = 1;
                                                one.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_primer));
                                                two.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_border));
                                                five.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_border));

                                                one.setTextColor(getResources().getColor(R.color.white));
                                                two.setTextColor(getResources().getColor(R.color.font_color));
                                                five.setTextColor(getResources().getColor(R.color.font_color));
                                            }
                                        });
                                        two.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                remindtime = 2;
                                                one.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_border));
                                                two.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_primer));
                                                five.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_border));
                                                one.setTextColor(getResources().getColor(R.color.font_color));
                                                two.setTextColor(getResources().getColor(R.color.white));
                                                five.setTextColor(getResources().getColor(R.color.font_color));
                                            }
                                        });
                                        five.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                remindtime = 5;
                                                one.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_border));
                                                two.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_gray_border));
                                                five.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_primer));
                                                one.setTextColor(getResources().getColor(R.color.font_color));
                                                two.setTextColor(getResources().getColor(R.color.font_color));
                                                five.setTextColor(getResources().getColor(R.color.white));
                                            }
                                        });
                                        no.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                d.dismiss();
                                                finish();
                                            }
                                        });
                                        yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

                                                try {
                                                    String newDate = d2.format(d1.parse(gv.getMatchTime()));
                                                    quizname = newDate + " Quiz";
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                String stDate = gv.getMatchTime();
                                                String enDate = gv.getMatchTime();

                                                GregorianCalendar calDate = new GregorianCalendar();

                                                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy,MM,dd,HH,mm");
                                                Date date, edate;
                                                try {
                                                    date = originalFormat.parse(gv.getMatchTime());
                                                    stDate = targetFormat.format(date);

                                                } catch (ParseException ex) {
                                                }

                                                long startMillis = 0;
                                                long endMillis = 0;
                                                String dates[] = stDate.split(",");

                                                SD_YeaR = dates[0];
                                                SD_MontH = dates[1];
                                                SD_DaY = dates[2];
                                                SD_HouR = dates[3];
                                                SD_MinutE = dates[4];

                                                calDate.set(Integer.parseInt(SD_YeaR), Integer.parseInt(SD_MontH) - 1, Integer.parseInt(SD_DaY), Integer.parseInt(SD_HouR), Integer.parseInt(SD_MinutE));
                                                startMillis = calDate.getTimeInMillis();

                                                try {
                                                    ContentResolver cr = getApplicationContext().getContentResolver();
                                                    ContentValues values = new ContentValues();
                                                    values.put(CalendarContract.Events.DTSTART, startMillis);
                                                    values.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis() + gv.getQuizquestions() * 15 * 1000);
                                                    values.put(CalendarContract.Events.TITLE, quizname);
                                                    values.put(CalendarContract.Events.DESCRIPTION, "MyBat11 Quiz");
                                                    values.put(CalendarContract.Events.HAS_ALARM, 1);
                                                    values.put(CalendarContract.Events.CALENDAR_ID, 1);
                                                    values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                                                            .getTimeZone().getID());
                                                    if (ActivityCompat.checkSelfPermission(QuizCheckBalanceActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                                                        return;
                                                    }
                                                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                                                    long eventId = Long.parseLong(uri.getLastPathSegment());
                                                    Log.d("Ketan_Event_Id", String.valueOf(eventId));


                                                    Calendar calendar = Calendar.getInstance();
                                                    ContentResolver cr1 = getApplication().getContentResolver();
                                                    ContentValues reminders = new ContentValues();
                                                    reminders.put(CalendarContract.Reminders.EVENT_ID, eventId);
                                                    reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                                                    reminders.put(CalendarContract.Reminders.MINUTES, remindtime);
                                                    Uri eventsUri = Uri.parse("content://com.android.calendar/reminders");
                                                    // event is added
                                                    cr1.insert(eventsUri, reminders);

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.d("Ketan_Event_Id", String.valueOf(e));
                                                }
                                                d.dismiss();
                                                finish();
                                            }
                                        });


                                        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                        d.setCancelable(false);
                                        d.show();
                                    }else {
                                        ma.successDialog(QuizCheckBalanceActivity.this, jsonObject.getString("message"), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ma.dismissDialog();
                                                finish();
                                            }
                                        });
                                    }

                                }else {
                                    ma.errorDialog(QuizCheckBalanceActivity.this, jsonObject.getString("message"), new View.OnClickListener() {
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
                                ma.showToast(QuizCheckBalanceActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(QuizCheckBalanceActivity.this);
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
                    params.put("quiz_id", String.valueOf(gv.getQuizid()));
                    params.put("challengeid",String.valueOf(challenge_id));
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
