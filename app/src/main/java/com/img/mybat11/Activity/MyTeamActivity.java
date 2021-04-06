package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Adapter.TeamListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTeamActivity extends AppCompatActivity {

    ListView teamList;
    TextView title,createTeam;
    ImageView back;
    LinearLayout noTeamLL;
    ArrayList<MyTeamsGetSet> list,list1;
    String TAG="MyTeams";
    public static Activity fa;
    LinearLayout ctll;

    TextView match_name,timeRemaining;
    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate=null,endDate = null;

    GlobalVariables gv;
    UserSessionManager session;
    ConnectionDetector cd;
    MainActivity ma;

    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_team);


        fa=this;

        session= new UserSessionManager(getApplicationContext()) ;
        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        ma = new MainActivity();

        title= (TextView)findViewById(R.id.title);
        title.setText("My Teams");

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii= new Intent(MyTeamActivity.this,ChallengesActivity.class);
                ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ii);
                finish();
            }
        });

        match_name=(TextView)findViewById(R.id.match_name);
        timeRemaining=(TextView)findViewById(R.id.timeRemaining);

        match_name.setText(gv.getTeam1()+" vs "+gv.getTeam2());

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
                timeRemaining.setText("⏰ "+String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));
            }

            public void onFinish() {
                finish();
            }
        };
        cT.start();

        type = getIntent().getExtras().getString("type");

        ctll=(LinearLayout)findViewById(R.id.ctll);

        teamList= (ListView)findViewById(R.id.teamList);
        noTeamLL=(LinearLayout)findViewById(R.id.noTeamLL);

        createTeam=(TextView)findViewById(R.id.createTeam);
        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list1.size()!=6) {
                    Intent ii = new Intent(MyTeamActivity.this, CreateTeamActivity.class);
                    ii.putExtra("teamNumber",list1.size()+1);
                    ii.putExtra("type",type);
                    startActivity(ii);
                }else
                {
                    ma.showToast(MyTeamActivity.this,"Cannot create more team.");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()){
            MyTeam();
        }
    }

    public void MyTeam(){
        ma.showProgressDialog(MyTeamActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.myTeams(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();
                    list1 = new ArrayList<>();

                    for(MyTeamsGetSet zz:list){
                        if(zz.getPlayer_type().equals(type))
                            list1.add(zz);
                    }

                    if (list1.size() > 0) {
                        teamList.setVisibility(View.VISIBLE);
                        noTeamLL.setVisibility(View.GONE);
                        teamList.setAdapter(new TeamListAdapter(MyTeamActivity.this, list1));
                    } else {
                        teamList.setVisibility(View.GONE);
                        noTeamLL.setVisibility(View.VISIBLE);
                    }

                    if (list1.size() == 6) {
                        ctll.setVisibility(View.GONE);
                    }
                    createTeam.setText("Create Team " + (list1.size() + 1));
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());
                    ma.dismissProgressDialog();

                    AlertDialog.Builder d = new AlertDialog.Builder(MyTeamActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeam();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d.show();
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
