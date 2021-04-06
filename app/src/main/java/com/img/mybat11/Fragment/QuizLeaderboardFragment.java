package com.img.mybat11.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Adapter.LeaderBoardAdapter;
import com.img.mybat11.Adapter.QuizLeaderBoardAdapter;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.teamsGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizLeaderboardFragment extends Fragment {

    Context context;

    TextView totalTeamslb;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView leardboard;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<teamsGetSet> teams,teams1;
    QuizLeaderBoardAdapter adapter;

    GlobalVariables gv;
    MainActivity ma;
    RequestQueue requestQueue;
    ConnectionDetector cd;
    UserSessionManager session;


    TabLayout tabLayout;



    @SuppressLint("ValidFragment")
    public QuizLeaderboardFragment(ArrayList<teamsGetSet>teams) {
        // Required empty public constructor
        this.teams = teams;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_quiz_leaderboard, container, false);
        context = getActivity();

        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(context);
        gv= (GlobalVariables)context.getApplicationContext();
        session = new UserSessionManager(context);
        cd= new ConnectionDetector(context);

        totalTeamslb=(TextView)v.findViewById(R.id.totalteamsLB);
        totalTeamslb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.showToast(context,"Hang on!You'll be able to download teams only after deadline");
            }
        });

        leardboard= (RecyclerView) v.findViewById(R.id.leardboard);
        leardboard.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setAutoMeasureEnabled(false);
        leardboard.setLayoutManager(mLayoutManager);

        if (teams != null) {
            if (teams.size() > 50)
                teams1 = new ArrayList<>(teams.subList(0, 50));
            else
                teams1 = teams;

            adapter = new QuizLeaderBoardAdapter(context, teams1);
            leardboard.setAdapter(adapter);

            leardboard.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(1)) {
                        if (teams.size() > teams1.size()) {
                            int x, y;
                            if ((teams.size() - teams1.size()) >= 50) {
                                x = teams1.size();
                                y = x + 50;
                            } else {
                                x = teams1.size();
                                y = x + teams.size() - teams1.size();
                            }
                            for (int i = x; i < y; i++) {
                                teams1.add(teams.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            });
        }
        return v;
    }

}
