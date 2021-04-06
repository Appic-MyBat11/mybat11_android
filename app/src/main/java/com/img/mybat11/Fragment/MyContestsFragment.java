package com.img.mybat11.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.AllChallengesActivity;
import com.img.mybat11.Activity.CreateTeamActivity;
import com.img.mybat11.Activity.JoinByCodeActivity;
import com.img.mybat11.Activity.LoginActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.MakeChallengeActivity;
import com.img.mybat11.Adapter.ChallengeCategoryListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.CategoriesGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyContestsFragment extends Fragment {

    Context context;

    SwipeRefreshLayout swipeRefreshLayout;
    TextView joinContest,addContest,allContests;
    RecyclerView ll;


    String TAG="Challenges";
    ArrayList<CategoriesGetSet> list,list1;

    ImageView coming_soon;
    RelativeLayout currentrun;
    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;
    LinearLayout crcontest;
    LinearLayout classic,batting,bowling;
    TextView classicSelected,battingSelected,bowlingSelected;
    TextView bowlingText,battinngText,classicText;
    String type="classic";

    TextView createTeam;
    ArrayList<MyTeamsGetSet> TeamList;
    ArrayList<MyTeamsGetSet> TeamList1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_my_contests, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        ma = new MainActivity();


        crcontest =(LinearLayout)v.findViewById(R.id.crcontest);


        coming_soon=(ImageView) v.findViewById(R.id.coming_soon);
        currentrun=(RelativeLayout)v.findViewById(R.id.currentrun);
        classic =(LinearLayout)v.findViewById(R.id.classic);
        batting =(LinearLayout)v.findViewById(R.id.batting);
        bowling =(LinearLayout)v.findViewById(R.id.bowling);

        classicText=(TextView)v.findViewById(R.id.classicText);
        battinngText=(TextView)v.findViewById(R.id.battinngText);
        bowlingText=(TextView)v.findViewById(R.id.bowlingText);

        classicSelected=(TextView)v.findViewById(R.id.classicSelected);
        battingSelected=(TextView)v.findViewById(R.id.battingSelected);
        bowlingSelected=(TextView)v.findViewById(R.id.bowlingSelected);

        classic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="classic";

                crcontest.setVisibility(View.VISIBLE);
                classicSelected.setVisibility(View.VISIBLE);
                battingSelected.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.GONE);

                Challenges("classic");
                MyTeam("classic");

                coming_soon.setVisibility(View.GONE);
                currentrun.setVisibility(View.VISIBLE);
            }
        });

        batting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="batting";

                classicSelected.setVisibility(View.GONE);
                battingSelected.setVisibility(View.VISIBLE);
                bowlingSelected.setVisibility(View.GONE);
                crcontest.setVisibility(View.GONE);
//                Challenges("batting");
//                MyTeam("batting");

                coming_soon.setVisibility(View.VISIBLE);
                createTeam.setVisibility(View.GONE);
                currentrun.setVisibility(View.GONE);

            }
        });
        bowling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="bowling";

                classicSelected.setVisibility(View.GONE);
                battingSelected.setVisibility(View.GONE);
                crcontest.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.VISIBLE);
//                Challenges("bowling");
//                MyTeam("bowling");

                coming_soon.setVisibility(View.VISIBLE);
                createTeam.setVisibility(View.GONE);
                currentrun.setVisibility(View.GONE);

            }
        });

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        joinContest = v.findViewById(R.id.joinContest);
        addContest = v.findViewById(R.id.addContest);
        allContests = v.findViewById(R.id.allContests);

        ll = v.findViewById(R.id.ll);
        ll.setLayoutManager(new LinearLayoutManager(context));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Challenges(type);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        addContest.setTypeface(Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf"));
        joinContest.setTypeface(Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf"));

        addContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MakeChallengeActivity.class));
            }
        });

        joinContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, JoinByCodeActivity.class));
            }
        });

        allContests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(context, AllChallengesActivity.class);
                ii.putExtra("catid","");
                ii.putExtra("type",type);
                context.startActivity(ii);
            }
        });

        v.findViewById(R.id.entryFee).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(context,AllChallengesActivity.class);
                ii.putExtra("type",type);
                ii.putExtra("catid","");
                ii.putExtra("sortBy","EntryFee");
                context.startActivity(ii);
            }
        });

        v.findViewById(R.id.contestSize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(context,AllChallengesActivity.class);
                ii.putExtra("type",type);
                ii.putExtra("catid","");
                ii.putExtra("sortBy","contestSize");
                context.startActivity(ii);
            }
        });

        createTeam=(TextView)v.findViewById(R.id.createTeam);


        ma.showProgressDialog(context);

        // to view filter
        v.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(context,AllChallengesActivity.class);
                ii.putExtra("type",type);
                ii.putExtra("catid","");
                ii.putExtra("filter",true);
                context.startActivity(ii);
            }
        });

        return  v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            Challenges(type);
            MyTeam(type);
        }
    }

    public void Challenges(final String type){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<CategoriesGetSet>> call = apiSeitemViewice.getContests(session.getUserId(),gv.getMatchKey(),type);
        call.enqueue(new Callback<ArrayList<CategoriesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<CategoriesGetSet>> call, Response<ArrayList<CategoriesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = new ArrayList<>();
                    list = response.body();
                    list1 = new ArrayList<>();
                    if (list.size() > 0) {

                        for (CategoriesGetSet zz : list) {
                            if (zz.getContest().size() > 0) {
                                list1.add(zz);
                            }
                        }
                        ll.setAdapter(new ChallengeCategoryListAdapter(context,list1,type));
                    }


                    ma.dismissProgressDialog();
                }else if(response.code() == 401){
                    ma.showToast(context,"Session Timeout");
                    startActivity(new Intent(context, LoginActivity.class));
                    session.logoutUser();
                    ((Activity)context).finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Challenges(type);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    ma.dismissProgressDialog();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<CategoriesGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

    public void MyTeam(final String type){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.getMyTeams1(session.getUserId(),gv.getMatchKey(),type);
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + session.getUserId());
                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received123: " + String.valueOf(response.body().size()));
                    TeamList = new ArrayList<>();
                    TeamList = response.body();

//                    for(MyTeamsGetSet zz:TeamList){
//                        if(zz.getPlayer_type().equals(type)) {
//                            TeamList1.add(zz);
//                        }
//                    }


                    createTeam.setVisibility(View.VISIBLE);

                    if (TeamList.size() > 0) {
                        createTeam.setText("Create Team "+ String.valueOf(TeamList.size()+1));
                    }

                    if (TeamList.size() == gv.getMax_teams()) {
                        createTeam.setVisibility(View.GONE);
                    }

                    createTeam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        if(TeamList.size() <= gv.getMax_teams()) {
                            Intent ii = new Intent(context, CreateTeamActivity.class);
                            ii.putExtra("teamNumber",TeamList.size()+1);
                            ii.putExtra("type",type);
                            startActivity(ii);
                        }else
                        {
                            ma.showToast(context,"Can't create more team.");
                        }
                        }
                    });

                }else {
                    Log.i(TAG, "Responce code " + response.code());
                    ma.dismissProgressDialog();

                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeam(type);
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
