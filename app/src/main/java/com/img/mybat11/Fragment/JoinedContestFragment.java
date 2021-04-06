package com.img.mybat11.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Adapter.JoinedChallengeListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinedContestFragment extends Fragment {

    ListView challengeList;
    ArrayList<JoinedChallengesGetSet> list,list1;
    String TAG="Challenge list";
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;
    MainActivity ma;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout noContestLL;
    ImageView coming_soon;
    LinearLayout classic,batting,bowling;
    TextView classicSelected,battingSelected,bowlingSelected;
    TextView bowlingText,battinngText,classicText;
    String type="classic";

    Context context;
    TabLayout tabLayout;

    public JoinedContestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_joined_contest, container, false);
        context = getActivity();

        ma = new MainActivity();
        cd= new ConnectionDetector(context);
        session= new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();

        challengeList= (ListView) v.findViewById(R.id.challengeList);
        coming_soon= (ImageView) v.findViewById(R.id.coming_soon);

        tabLayout = (TabLayout)((Activity)context).findViewById(R.id.tabLayout);

        noContestLL = v.findViewById(R.id.noContestLL);

        if(cd.isConnectingToInternet()){
            Challenges(type);
        }

        swipeRefreshLayout= (SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Challenges(type);
                swipeRefreshLayout.setRefreshing(false);
            }
        });




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

                classicSelected.setVisibility(View.VISIBLE);
                battingSelected.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.GONE);

                coming_soon.setVisibility(View.GONE);
                noContestLL.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                Challenges("classic");
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
                noContestLL.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
//                Challenges("batting");

            }
        });
        bowling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="bowling";

                classicSelected.setVisibility(View.GONE);
                battingSelected.setVisibility(View.GONE);
                bowlingSelected.setVisibility(View.VISIBLE);
                noContestLL.setVisibility(View.GONE);
//                Challenges("bowling");
                coming_soon.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()){
            Challenges(type);
        }
    }
    public void Challenges(final String type){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<JoinedChallengesGetSet>> call = apiSeitemViewice.FindJoinedChallenges(gv.getMatchKey(),session.getUserId());
        call.enqueue(new Callback<ArrayList<JoinedChallengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<JoinedChallengesGetSet>> call, Response<ArrayList<JoinedChallengesGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    list1 = new ArrayList<>();

                    list1 = response.body();

                    list = new ArrayList<>();
                    if (list1.size() != 0) {
                        for (JoinedChallengesGetSet zz : list1) {
                            if (zz.getType().equals(type))
                                list.add(zz);
                        }
                    }

                    if(list.size() == 0) {
                        noContestLL.setVisibility(View.VISIBLE);
                        challengeList.setVisibility(View.GONE);
                    }
                    else {
                        noContestLL.setVisibility(View.GONE);
                        challengeList.setVisibility(View.VISIBLE);
                    }

                    Log.i("Size", list.size() + "");
                    challengeList.setAdapter(new JoinedChallengeListAdapter(context, list));


                    tabLayout.getTabAt(1).setText("My Contests("+list.size()+")");

                }else {
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
                            ma.dismissProgressDialog();
                            ((Activity)context).finish();
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

}
