package com.img.mybat11.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.img.mybat11.Activity.CreateTeamActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Adapter.TeamListAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTeamsFragment extends Fragment {

    ListView teamList;
    TextView createTeam;
    LinearLayout noTeamLL;
    ArrayList<MyTeamsGetSet> list;
    ArrayList<MyTeamsGetSet> list1;
    String TAG="MyTeams";

    GlobalVariables gv;
    UserSessionManager session;
    ConnectionDetector cd;
    MainActivity ma;
    LinearLayout classic,batting,bowling;
    TextView classicSelected,battingSelected,bowlingSelected;
    TextView bowlingText,battinngText,classicText;
    String type="classic";
    ImageView coming_soon;
    Context context;
    TabLayout tabLayout;

    public MyTeamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_my_teams, container, false);
        context = getActivity();

        session= new UserSessionManager(context) ;
        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        ma = new MainActivity();

        list = new ArrayList<>();




        coming_soon =(ImageView) v.findViewById(R.id.coming_soon);

        classic =(LinearLayout)v.findViewById(R.id.classic);
        batting =(LinearLayout)v.findViewById(R.id.batting);
        bowling =(LinearLayout)v.findViewById(R.id.bowling);

        classicText=(TextView)v.findViewById(R.id.classicText);
        battinngText=(TextView)v.findViewById(R.id.battinngText);
        bowlingText=(TextView)v.findViewById(R.id.bowlingText);

        classicSelected=(TextView)v.findViewById(R.id.classicSelected);
        battingSelected=(TextView)v.findViewById(R.id.battingSelected);
        bowlingSelected=(TextView)v.findViewById(R.id.bowlingSelected);

        teamList= (ListView)v.findViewById(R.id.teamList);
        noTeamLL=(LinearLayout)v.findViewById(R.id.noTeamLL);

        classic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="classic";

                classicSelected.setVisibility(View.VISIBLE);
                battingSelected.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.GONE);

                coming_soon.setVisibility(View.GONE);
                teamList.setVisibility(View.VISIBLE);
                MyTeam("classic");
            }
        });

        batting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="batting";

                classicSelected.setVisibility(View.GONE);
                battingSelected.setVisibility(View.VISIBLE);
                bowlingSelected.setVisibility(View.GONE);

                coming_soon.setVisibility(View.VISIBLE);
                createTeam.setVisibility(View.GONE);
                teamList.setVisibility(View.GONE);
//                MyTeam("batting");

            }
        });
        bowling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="bowling";

                classicSelected.setVisibility(View.GONE);
                battingSelected.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.VISIBLE);

//                MyTeam("bowling");
                coming_soon.setVisibility(View.VISIBLE);
                createTeam.setVisibility(View.GONE);
                teamList.setVisibility(View.GONE);

            }
        });



        createTeam=(TextView)v.findViewById(R.id.createTeam);
        createTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.size() != gv.getMax_teams()) {
                    Intent ii = new Intent(context, CreateTeamActivity.class);
                    ii.putExtra("teamNumber",list.size()+1);
                    ii.putExtra("type",type);
                    startActivity(ii);
                }else
                {
                    ma.showToast(context,"Cannot create more team.");
                }
            }
        });

        tabLayout = (TabLayout)((Activity)context).findViewById(R.id.tabLayout);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()){
            MyTeam(type);
        }
    }

    public void MyTeam(final String type){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.getMyTeams1(session.getUserId(),gv.getMatchKey(),type);
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
                        if(zz.getPlayer_type().equals(type)) {
                            list1.add(zz);
                        }
                    }


                    if (list1.size() > 0) {
                        teamList.setVisibility(View.VISIBLE);
                        noTeamLL.setVisibility(View.GONE);
                        teamList.setAdapter(new TeamListAdapter(context, list1));
                    } else {
                        teamList.setVisibility(View.GONE);
                        noTeamLL.setVisibility(View.VISIBLE);
                    }

                    tabLayout.getTabAt(2).setText("My Teams("+list1.size()+")");

                    if (list1.size() == gv.getMax_teams()) {
                        createTeam.setVisibility(View.GONE);
                    }else {
                        createTeam.setVisibility(View.VISIBLE);
                    }
                    createTeam.setText("Create Team " + (list1.size() + 1));
                }else {
                    Log.i(TAG, "Responce code " + response.code());

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
