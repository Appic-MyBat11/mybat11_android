package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.LeaderboardByUserAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.LeaderboardByUserGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardByUserActivity extends AppCompatActivity {

    int userid,seriesid;
    ArrayList<LeaderboardByUserGetSet> list;
    ListView matchList;
    String TAG="LeaderboardByUserGetSet";

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_by_user);

        userid = getIntent().getExtras().getInt("user_id");
        seriesid = getIntent().getExtras().getInt("Series_id");

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Leaderboard Stats");

        matchList=(ListView)findViewById(R.id.matchList);

        Leaderboard();

    }

    public void Leaderboard(){
        ma.showProgressDialog(LeaderboardByUserActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<LeaderboardByUserGetSet>> call = apiSeitemViewice.getleaderboardbyuser(session.getUserId(),seriesid,userid);
        call.enqueue(new Callback<ArrayList<LeaderboardByUserGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LeaderboardByUserGetSet>> call, Response<ArrayList<LeaderboardByUserGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();
                    matchList.setAdapter(new LeaderboardByUserAdapter(LeaderboardByUserActivity.this,list));
                    ma.dismissProgressDialog();

                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(LeaderboardByUserActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Leaderboard();
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
            public void onFailure(Call<ArrayList<LeaderboardByUserGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

}
