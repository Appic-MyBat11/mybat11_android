package com.img.mybat11.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.img.mybat11.Activity.AllChallengesActivity;
import com.img.mybat11.Activity.FeedActivity;
import com.img.mybat11.Activity.HomeActivity;
import com.img.mybat11.Activity.JoinedMatchesViewPageAdapter;
import com.img.mybat11.Activity.MeActivity;
import com.img.mybat11.Activity.MoreActivity;
import com.img.mybat11.Activity.MyMatchesCricketActivity;
import com.img.mybat11.Adapter.ChallengeListAdapter;
import com.img.mybat11.Adapter.SlideBannerAdapter;
import com.img.mybat11.Adapter.matchListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightExpendableListView;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.GetSet.MatchListGetSet;
import com.img.mybat11.GetSet.bannersGetSet;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@SuppressLint("ValidFragment")
public class CricketHomeFragment extends Fragment {
    View v;
    RecyclerView matchList;
    ArrayList<MatchListGetSet> list;
    ArrayList<bannersGetSet> imageList;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;

    RelativeLayout joinedRL;
    ArrayList<ContestMatchListGetSet> joinedList;
    ViewPager joinedVp,vpAdvertisments;
    TabLayout tabLayout;
    UserSessionManager session;
    ConnectionDetector cd;
    TextView viewall;
    @SuppressLint("ValidFragment")
    public CricketHomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_cricket_home, container, false);
        LinearLayout homeLL,contestLL,moreLL,feedLL;
        ImageView home;
        TextView homeText;

        context = getActivity();
        session= new UserSessionManager(context);
        cd= new ConnectionDetector(context);
        homeLL= (LinearLayout)v.findViewById(R.id.homeLL);
        contestLL= (LinearLayout)v.findViewById(R.id.contestLL);
        moreLL= (LinearLayout)v.findViewById(R.id.moreLL);
        feedLL= (LinearLayout)v.findViewById(R.id.feedLL);

        home=(ImageView) v.findViewById(R.id.home);
        homeText=(TextView) v.findViewById(R.id.homeText);
        viewall=(TextView) v.findViewById(R.id.viewall);
        homeText.setTextColor(getResources().getColor(R.color.colorPrimary));
        home.setImageResource(R.drawable.home_selected);

        viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,MyMatchesCricketActivity.class));
            }
        });
        if(cd.isConnectingToInternet()) {
            MyMatches();
            MatchList();
            Banners();
        }

        joinedRL=(RelativeLayout)v.findViewById(R.id.joinedRL);
        joinedVp =(ViewPager)v.findViewById(R.id.joinedVp);
        vpAdvertisments =(ViewPager)v.findViewById(R.id.vpAdvertisments);
        tabLayout = (TabLayout)v.findViewById(R.id.tab_layout);

        joinedVp.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float v, int i1 ) {
            }

            @Override
            public void onPageSelected( int position ) {
            }

            @Override
            public void onPageScrollStateChanged( int state ) {
                enableDisableSwipeRefresh( state == ViewPager.SCROLL_STATE_IDLE );
            }
        } );

        swipeRefreshLayout=(SwipeRefreshLayout)v.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cd.isConnectingToInternet()) {
                    MatchList();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        contestLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MyMatchesCricketActivity.class));
                getActivity().finish();
            }
        });

        moreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MoreActivity.class));
                getActivity().finish();
            }
        });
        feedLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, MeActivity.class));
                getActivity().finish();
            }
        });

        matchList=(RecyclerView) v.findViewById(R.id.matchList);
        matchList.setLayoutManager(new LinearLayoutManager(context));
//        matchList.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//
//                int topRowVerticalPosition =
//                        (matchList == null || matchList.getChildCount() == 0) ? 0 : matchList.getChildAt(0).getTop();
//                swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);
//            }
//        });
        return v;


    }

    private void enableDisableSwipeRefresh(boolean enable) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setEnabled(enable);
        }
    }

    public void Banners(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Log.i("Auth key",session.getUserId());

        Call<ArrayList<bannersGetSet>> call = apiSeitemViewice.getmainbanner(session.getUserId());
        call.enqueue(new Callback<ArrayList<bannersGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<bannersGetSet>> call, Response<ArrayList<bannersGetSet>> response) {


                if(response.code() == 200) {



                    imageList = response.body();



                    vpAdvertisments.setAdapter(new SlideBannerAdapter(context,imageList));
                    final int[] currentPage = {0};
                    Timer timer;
                    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
                    final long PERIOD_MS = 10000; // time in milliseconds between successive task executions.

                    final Handler handler = new Handler();
                    final Runnable Update = new Runnable() {
                        public void run() {
                            if (currentPage[0] == imageList.size()) {
                                currentPage[0] = 0;
                            }
                            vpAdvertisments.setCurrentItem(currentPage[0]++, true);
                        }
                    };

                    timer = new Timer(); // This will create a new Thread
                    timer .schedule(new TimerTask() { // task to be scheduled

                        @Override
                        public void run() {
                            handler.post(Update);
                        }
                    }, DELAY_MS, PERIOD_MS);
                }else {

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Banners();
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
            public void onFailure(Call<ArrayList<bannersGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    public void MatchList(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Log.i("Authkey",session.getUserId());

        Call<ArrayList<MatchListGetSet>> call = apiSeitemViewice.matchList(session.getUserId());
        call.enqueue(new Callback<ArrayList<MatchListGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MatchListGetSet>> call, Response<ArrayList<MatchListGetSet>> response) {


                if(response.code() == 200) {


                    list = response.body();

                    if(list.size()>0){
                        Collections.sort(list, new Comparator<MatchListGetSet>() {
                            @Override
                            public int compare(MatchListGetSet lhs, MatchListGetSet rhs) {
                                return lhs.getTime_start().compareTo(rhs.getTime_start());
                            }
                        });



                        Collections.sort(list, new Comparator<MatchListGetSet>() {
                            @Override
                            public int compare(MatchListGetSet lhs, MatchListGetSet rhs) {
                                return rhs.getOrder_status() - lhs.getOrder_status();
                            }
                        });


                        matchList.setAdapter(new matchListAdapter(context, "cricket", list,"0"));
                    }

                }else {

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MatchList();
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
            public void onFailure(Call<ArrayList<MatchListGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }


    public void MyMatches(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<ContestMatchListGetSet>> call = apiSeitemViewice.ContestsmatchList(session.getUserId());
        call.enqueue(new Callback<ArrayList<ContestMatchListGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<ContestMatchListGetSet>> call, Response<ArrayList<ContestMatchListGetSet>> response) {


                if(response.code() == 200) {

                    joinedList = response.body();
                    if(joinedList.size() > 0){
                        joinedRL.setVisibility(View.VISIBLE);
                        joinedVp.setAdapter(new JoinedMatchesViewPageAdapter(context,joinedList));
                        tabLayout.setupWithViewPager(joinedVp, true);
                    }


                }else {
                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyMatches();
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
            public void onFailure(Call<ArrayList<ContestMatchListGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }


}
