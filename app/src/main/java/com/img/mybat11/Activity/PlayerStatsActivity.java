package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.Adapter.PlayerStatsAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.MatchStatsGetSet;
import com.img.mybat11.GetSet.PlayerStatsGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerStatsActivity extends AppCompatActivity {

    ExpandableHeightListView statsList;
    ImageView back;
    TextView bats,bowls,nationality,birthday;
    CircleImageView img;
    String key;
    TextView title,credit,points;
    ArrayList<PlayerStatsGetSet> list;
    String TAG="Stats";
    MainActivity ma;
    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        ma = new MainActivity();
        session = new UserSessionManager(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        cd = new ConnectionDetector(getApplicationContext());

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        key= getIntent().getExtras().getString("key");

        title=(TextView)findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("PlayerName"));

        statsList=(ExpandableHeightListView)findViewById(R.id.statsList);
        statsList.setExpanded(true);

        img=(CircleImageView)findViewById(R.id.img);
        credit=(TextView)findViewById(R.id.credits);
        points=(TextView)findViewById(R.id.points);
        birthday=(TextView)findViewById(R.id.birthday);
        nationality=(TextView)findViewById(R.id.nationality);
        bowls=(TextView)findViewById(R.id.bowls);
        bats=(TextView)findViewById(R.id.bats);
//        squad=(TextView)findViewById(R.id.squad);
//        role=(TextView)findViewById(R.id.role);

        if(cd.isConnectingToInternet()) {
            PlayerStats();
        }
    }


    public void PlayerStats(){
        ma.showProgressDialog(PlayerStatsActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);


        Call<ArrayList<PlayerStatsGetSet>> call = apiSeitemViewice.PlayerStats(session.getUserId(),key,gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<PlayerStatsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<PlayerStatsGetSet>> call, Response<ArrayList<PlayerStatsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    ArrayList<MatchStatsGetSet> matches;
                    matches = new ArrayList<>();

                    if (list.get(0).getMatches() != null)
                        matches = list.get(0).getMatches();

                    statsList.setAdapter(new PlayerStatsAdapter(PlayerStatsActivity.this, matches));

                    credit.setText(list.get(0).getPlayercredit());
                    title.setText(list.get(0).getPlayername());
                    Picasso.with(PlayerStatsActivity.this).load(list.get(0).getPlayerimage()).into(img);
                    points.setText(String.valueOf(list.get(0).getPlayerpoints()));
                    nationality.setText(list.get(0).getPlayercountry());
                    bats.setText(list.get(0).getBattingstat());
                    bowls.setText(list.get(0).getBowlerstat());

                    if(!list.get(0).getPlayerdob().equals("")) {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd,yyyy");

                            birthday.setText(outputFormat.format(inputFormat.parse(list.get(0).getPlayerdob())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

//                    squad.setText(list.get(0).getTeams());
//                    role.setText(list.get(0).getPlayerrole());

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(PlayerStatsActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlayerStats();
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
            public void onFailure(Call<ArrayList<PlayerStatsGetSet>> call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }
}
