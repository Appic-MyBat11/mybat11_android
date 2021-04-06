package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.captainListAdapter;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.GetSet.SelectedTeamsGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
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

public class CaptainViceCaptainActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    String TAG = "Join Challenge";
    double availableB,usableB;
    ArrayList<SelectedTeamsGetSet> TeamList;
    ArrayList<JoinChallengeBalanceGetSet> balancelist;

    int teamNumber,challengeId=0;
    ListView playerList;
    Button btnPreview,btnContinue;
    ArrayList<captainListGetSet> list,list1;
    String selectedPlayers="",captain_id="",viceCaptain_id="";
    TextView playerType,points;
    String sortBy = "type",joinid = "",chooseType = "",type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_vice_captain);



        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();

        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list= new ArrayList<>();
        list= gv.getCaptainList();

        teamNumber = getIntent().getExtras().getInt("teamNumber");
        challengeId = getIntent().getExtras().getInt("challengeId");
        type = getIntent().getExtras().getString("type");

        Log.i("teamNumber",String.valueOf(teamNumber));
        Log.i("challengeId",String.valueOf(challengeId));
        Log.i("entryFee",String.valueOf(getIntent().getExtras().getInt("entryFee")));

        if(getIntent().hasExtra("chooseType")){
            chooseType = getIntent().getExtras().getString("chooseType");
            joinid = getIntent().getExtras().getString("joinid");
        }
        else
            chooseType = "";

        final TextView timeLeft=(TextView)findViewById(R.id.timeLeft);

        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;

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

        final long diffInMs = endDate.getTime() - startDate.getTime();

        final long hours1 = 1*60*60*1000;
        final long hours4 = 4*60*60*1000;
        final long hours48 = 48*60*60*1000;

        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {
                if(diffInMs < hours1){
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) + "m : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))  + "s left"));
                }else if(diffInMs <  hours4){
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m left"));
                }else if(diffInMs <  hours48){
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h left"));

                }else{
                    timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toDays(millisUntilFinished)) + " days left"));
                }            }

            public void onFinish() {
                finish();
            }
        };
        cT.start();

        btnContinue=(Button)findViewById(R.id.btnContinue);
        btnPreview=(Button)findViewById(R.id.btnPreview);

        playerList=(ListView)findViewById(R.id.playerList);
        final captainListGetSet ob = new captainListGetSet();
        ob.setPoints("");
        ob.setCredit("");
        ob.setTeam("");
        ob.setRole("");
        ob.setCaptain("N");
        ob.setVc("N");
        ob.setId("0");
        ob.setImage("");
        ob.setName("");

        list1= new ArrayList<>();
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("Wk"))
                list1.add(zz);
        }
        if(type.equals("classic"))
            list1.add(ob);
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("Bat"))
                list1.add(zz);
        }
        if(type.equals("classic"))
            list1.add(ob);
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("AR"))
                list1.add(zz);
        }
        if(type.equals("classic"))
            list1.add(ob);
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("Bow"))
                list1.add(zz);
        }
        playerList.setAdapter(new captainListAdapter(CaptainViceCaptainActivity.this,list1));


        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPlayers="";
                for(captainListGetSet zz:list){
                    if(selectedPlayers.equals(""))
                        selectedPlayers=zz.getId();
                    else
                        selectedPlayers= selectedPlayers+","+zz.getId();

                    Log.i("Selected players sent",zz.getId()+",");

                    if(zz.getCaptain().equals("Y"))
                        captain_id= zz.getId();
                    if(zz.getVc().equals("Y"))
                        viceCaptain_id= zz.getId();
                }

                Log.i("Selected players sent",selectedPlayers);

                if(viceCaptain_id.equals("")  || captain_id.equals("") || captain_id.equals(viceCaptain_id)) {
                    ma.showToast(CaptainViceCaptainActivity.this,"Please select captain & vice-captain");
                }else{
                    if(cd.isConnectingToInternet()){
                        CreateTeam();
                    }
                }
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii= new Intent(CaptainViceCaptainActivity.this, TeamPreviewActivity.class);
                ii.putExtra("team_name","Team "+teamNumber);
                ii.putExtra("TeamID",0);
                startActivity(ii);
            }
        });

        playerType=(TextView)findViewById(R.id.playerType);
        points=(TextView)findViewById(R.id.points);

        playerType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerType.setTextColor(getResources().getColor(R.color.text_color));
                points.setTextColor(getResources().getColor(R.color.font_color));

                playerType.setBackground(getResources().getDrawable(R.drawable.blue_border_white_bg));
                points.setBackground(getResources().getDrawable(R.drawable.btn_white));

                list1= new ArrayList<>();
                for(captainListGetSet zz:list){
                    if(zz.getRole().equals("Wk"))
                        list1.add(zz);
                }
                if(type.equals("classic"))
                    list1.add(ob);
                for(captainListGetSet zz:list){
                    if(zz.getRole().equals("Bat"))
                        list1.add(zz);
                }
                if(type.equals("classic"))
                    list1.add(ob);
                for(captainListGetSet zz:list){
                    if(zz.getRole().equals("AR"))
                        list1.add(zz);
                }
                if(type.equals("classic"))
                    list1.add(ob);
                for(captainListGetSet zz:list){
                    if(zz.getRole().equals("Bow"))
                        list1.add(zz);
                }
                playerList.setAdapter(new captainListAdapter(CaptainViceCaptainActivity.this,list1));
            }
        });

        points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                points.setTextColor(getResources().getColor(R.color.text_color));
                playerType.setTextColor(getResources().getColor(R.color.font_color));

                points.setBackground(getResources().getDrawable(R.drawable.blue_border_white_bg));
                playerType.setBackground(getResources().getDrawable(R.drawable.btn_white));

                Collections.sort(list, new Comparator<captainListGetSet>() {
                    @Override
                    public int compare(captainListGetSet ob2, captainListGetSet ob1) {
                        return (Double.parseDouble(ob1.getPoints()) < Double.parseDouble(ob2.getPoints())) ? -1: (Double.parseDouble(ob1.getPoints()) > Double.parseDouble(ob2.getPoints())) ? 1:0 ;
                    }
                });
                playerList.setAdapter(new captainListAdapter(CaptainViceCaptainActivity.this,list));

            }
        });
    }

    public void CreateTeam(){
        ma.showProgressDialog(CaptainViceCaptainActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"createmyteam";
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


                                if(jsonObject.getBoolean("success")){
                                    if (chooseType.equals("switch")) {
                                        SwitchTeam(jsonObject.getString("teamid"), joinid);
                                    }else if(challengeId !=0){
                                        ma.showToast(CaptainViceCaptainActivity.this,jsonObject.getString("msg"));
                                        Intent intent = new Intent(CaptainViceCaptainActivity.this, JoinContestActivity.class);
                                        intent.putExtra("challenge_id",challengeId);
                                        intent.putExtra("team",jsonObject.getString("teamid"));
                                        intent.putExtra("entryFee",String.valueOf(getIntent().getExtras().getInt("entryFee")));
                                        startActivity(intent);
                                    }else {
                                        ma.showToast(CaptainViceCaptainActivity.this,jsonObject.getString("msg"));
                                    }
                                    finish();
                                    CreateTeamActivity.fa.finish();
                                }else {
                                    ma.showToast(CaptainViceCaptainActivity.this,jsonObject.getString("msg"));
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
                                ma.showToast(CaptainViceCaptainActivity.this,"Session Timeout");

                                startActivity(new Intent(CaptainViceCaptainActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(CaptainViceCaptainActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateTeam();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(CaptainViceCaptainActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateTeam();
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
                public Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("matchkey", gv.getMatchKey());
                    map.put("teamnumber", String.valueOf(getIntent().getExtras().getInt("teamNumber")));
                    map.put("players", selectedPlayers);
                    map.put("captain",captain_id);
                    map.put("vicecaptain",viceCaptain_id);
                    map.put("player_type",getIntent().getExtras().getString("type"));

                    Log.i("map",map.toString());

                    return map;
                }

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
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    public void SwitchTeam(final String teamid, final String joinid){
        ma.showProgressDialog(CaptainViceCaptainActivity.this);
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
                                    ma.showToast(CaptainViceCaptainActivity.this,jsonObject.getString("msg"));
                                    Intent ii= new Intent(CaptainViceCaptainActivity.this, DetailsActivity.class);
                                    ii.putExtra("challenge_id",gv.getChallengeId());
                                    startActivity(ii);
                                }else {
                                    ma.errorDialog(CaptainViceCaptainActivity.this, jsonObject.getString("msg"), new View.OnClickListener() {
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
                                ma.showToast(CaptainViceCaptainActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(CaptainViceCaptainActivity.this);
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
                    params.put("challengeid", String.valueOf(gv.getChallengeId()));
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
