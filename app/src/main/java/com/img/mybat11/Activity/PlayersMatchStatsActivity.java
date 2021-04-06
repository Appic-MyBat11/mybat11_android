package com.img.mybat11.Activity;

import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.Adapter.PlayerInfoViewPageAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.playerMatchStatsGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayersMatchStatsActivity extends AppCompatActivity {

    String team_id="";
    int player_id =0;
    ViewPager playerInfoVP;
    ArrayList<playerMatchStatsGetSet> list;
    ConnectionDetector cd;
    UserSessionManager session;
    GlobalVariables gv;
    String TAG= "Stats";
    MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_match_stats);


        cd = new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        ma = new MainActivity();
        session = new UserSessionManager(getApplicationContext());

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Players Info");

        team_id= getIntent().getExtras().getString("team_id");
        player_id= getIntent().getExtras().getInt("player_id");

        playerInfoVP=(ViewPager)findViewById(R.id.playerInfoVP);

        if(cd.isConnectingToInternet())
            Stats(player_id);
    }

    public void Stats(final int player_id){
        ma.showProgressDialog(PlayersMatchStatsActivity.this);

        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<playerMatchStatsGetSet>> call = apiSeitemViewice.getjointeamplayers(session.getUserId(),gv.getMatchKey(),team_id);
        call.enqueue(new Callback<ArrayList<playerMatchStatsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<playerMatchStatsGetSet>> call, Response<ArrayList<playerMatchStatsGetSet>> response) {
                Log.i(TAG, "Number of movies received: " + response.toString());
                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();
                    playerInfoVP.setAdapter(new PlayerInfoViewPageAdapter(PlayersMatchStatsActivity.this,list));

                    int pos =0;
                    for(int i = 0; i <list.size(); i++){
                        if(list.get(i).getPlayerid() == player_id)
                            pos = i;
                    }
                    playerInfoVP.setCurrentItem(pos);

                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(PlayersMatchStatsActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Stats(player_id);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                        }
                    });
                }
                ma.dismissProgressDialog();
            }
            @Override
            public void onFailure(Call<ArrayList<playerMatchStatsGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

}
