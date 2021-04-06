package com.img.mybat11.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.img.mybat11.Adapter.fantasyScorecardAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.fantasyScorecardGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerStatsFragment extends Fragment {

    Context context;

    ListView scoreCard;
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;
    String TAG="Challenge list";
    ArrayList<fantasyScorecardGetSet> list;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView player,selection,points;

    TextView noScorecard;
    LinearLayout scorecard;

    public PlayerStatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_player_stats, container, false);
        context = getActivity();

        cd = new ConnectionDetector(context);
        gv = (GlobalVariables) context.getApplicationContext();
        session = new UserSessionManager(context);

        scoreCard=(ListView)v.findViewById(R.id.scoreCard);

        player=(TextView)v.findViewById(R.id.player);
        selection=(TextView)v.findViewById(R.id.selection);
        points=(TextView)v.findViewById(R.id.points);

        swipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cd.isConnectingToInternet()){
                    LiveChallenges();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(list, new Comparator<fantasyScorecardGetSet>() {
                    @Override
                    public int compare(fantasyScorecardGetSet ob1, fantasyScorecardGetSet ob2) {
                        return (Double.parseDouble(ob1.getTotal()) > Double.parseDouble(ob2.getTotal())) ? -1: (Double.parseDouble(ob1.getTotal()) > Double.parseDouble(ob2.getTotal())) ? 1:0 ;
                    }
                });
                scoreCard.setAdapter(new fantasyScorecardAdapter(context,list));
            }
        });

        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(list, new Comparator<fantasyScorecardGetSet>() {
                    @Override
                    public int compare(fantasyScorecardGetSet ob1, fantasyScorecardGetSet ob2) {
                        return (Double.parseDouble(ob1.getSelectper().replace("%","")) > Double.parseDouble(ob2.getSelectper().replace("%",""))) ? -1: (Double.parseDouble(ob1.getSelectper().replace("%","")) > Double.parseDouble(ob2.getSelectper().replace("%",""))) ? 1:0 ;
                    }
                });
                scoreCard.setAdapter(new fantasyScorecardAdapter(context,list));
            }
        });

        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(list, new Comparator<fantasyScorecardGetSet>() {
                    @Override
                    public int compare(fantasyScorecardGetSet ob1, fantasyScorecardGetSet ob2) {
                        char l = Character.toUpperCase(ob1.getPlayer_name().charAt(0));
                        if (l < 'A' || l > 'Z')
                            l += 'Z';
                        char r = Character.toUpperCase(ob2.getPlayer_name().charAt(0));
                        if (r < 'A' || r > 'Z')
                            r += 'Z';
                        String s1 = l + ob1.getPlayer_name().substring(1);
                        String s2 = r + ob2.getPlayer_name().substring(1);
                        return s1.compareTo(s2);
                    }
                });
                scoreCard.setAdapter(new fantasyScorecardAdapter(context,list));
            }
        });

        noScorecard=(TextView) v.findViewById(R.id.noScorecard);
        scorecard=(LinearLayout) v.findViewById(R.id.scorecard);

        if(cd.isConnectingToInternet()) {
            LiveChallenges();
        }

        return v;
    }

    public void LiveChallenges(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<fantasyScorecardGetSet>> call = apiSeitemViewice.fantasyscorecards(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<fantasyScorecardGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<fantasyScorecardGetSet>> call, Response<ArrayList<fantasyScorecardGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();
                    if(list.size() >0) {
                        noScorecard.setVisibility(View.GONE);
                        scorecard.setVisibility(View.VISIBLE);
                        scoreCard.setAdapter(new fantasyScorecardAdapter(context, list));
                    }else{
                        noScorecard.setVisibility(View.VISIBLE);
                        scorecard.setVisibility(View.GONE);
                    }
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LiveChallenges();
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
            public void onFailure(Call<ArrayList<fantasyScorecardGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }

}
