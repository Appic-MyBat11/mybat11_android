package com.img.mybat11.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.Activity.BalanceActivity;
import com.img.mybat11.Activity.FantasyPointSystemActivity;
import com.img.mybat11.Activity.HomeActivity;
import com.img.mybat11.Activity.InviteFriendActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.MeActivity;
import com.img.mybat11.Activity.MoreActivity;
import com.img.mybat11.Activity.MyMatchesCricketActivity;
import com.img.mybat11.Activity.PersonalDetailsActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyCricketMatchesFragment extends Fragment {
    View v;
    UserSessionManager session;
    GlobalVariables gv;
    ArrayList<ContestMatchListGetSet> matchList,matchListLive,matchListUpcoming,matchListEnded;
    String TAG="main page";
    TabLayout tabLayout;
    ConnectionDetector cd;
    MainActivity ma;
    ViewPager viewPager;
    ImageView back;


    Context context;
    public MyCricketMatchesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_my_cricket_matches, container, false);


        context = getActivity();
        session= new UserSessionManager(context);
        cd= new ConnectionDetector(context);

        ma = new MainActivity();

        viewPager=(ViewPager)v.findViewById(R.id.vp);
        tabLayout=(TabLayout)v.findViewById(R.id.tabLayout);

        matchListUpcoming = new ArrayList<>();
        matchListLive = new ArrayList<>();
        matchListEnded = new ArrayList<>();




        matchListUpcoming = new ArrayList<>();
        matchListLive = new ArrayList<>();
        matchListEnded = new ArrayList<>();


        if(cd.isConnectingToInternet()) {
            MatchList();
        }


        return v;
    }
    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MyUpcomingMatchesFragment(matchListUpcoming);
                case 1:
                    return new MyLiveMatchesFragment(matchListLive);
                default:
                    return new MyCompeletdMatchesFragment(matchListEnded);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Upcoming";
                case 1:
                    return "Live";
                default:
                    return "Completed";
            }
        }

    }

    public void MatchList(){
        ma.showProgressDialog(context);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<ContestMatchListGetSet>> call = apiSeitemViewice.ContestsmatchList(session.getUserId());
        call.enqueue(new Callback<ArrayList<ContestMatchListGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<ContestMatchListGetSet>> call, Response<ArrayList<ContestMatchListGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    matchList = response.body();

                    if (matchList.size() != 0) {
                        for (ContestMatchListGetSet zz : matchList) {
                            if (zz.getStatus().equals("opened"))
                                matchListUpcoming.add(zz);
                            else if (zz.getStatus().equals("closed") && (zz.getFinal_status().equals("pending") || zz.getFinal_status().equals("IsReviewed")))
                                matchListLive.add(zz);
                            else if (zz.getStatus().equals("closed") && (zz.getFinal_status().equals("winnerdeclared") || zz.getFinal_status().equals("IsAbandoned") || zz.getFinal_status().equals("IsCanceled")))
                                matchListEnded.add(zz);
                        }
                    } else {

                    }
                    tabLayout.setupWithViewPager(viewPager);
                    viewPager.setAdapter(new SectionPagerAdapter(getChildFragmentManager()));

                    ma.dismissProgressDialog();
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
            public void onFailure(Call<ArrayList<ContestMatchListGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }
}
