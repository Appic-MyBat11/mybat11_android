package com.img.mybat11.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Activity.HomeActivity;
import com.img.mybat11.Activity.MyMatchesCricketActivity;
import com.img.mybat11.Activity.NotificationActivity;
import com.img.mybat11.Adapter.contestListAdapter;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyUpcomingMatchesFragment extends Fragment {

    ArrayList<ContestMatchListGetSet> matchAR;
    ListView matchList;
    Context context;
    TextView noMatch;
    GlobalVariables gv;
    TextView nomatchbtn;
    LinearLayout nomatchlay;

    public MyUpcomingMatchesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MyUpcomingMatchesFragment(ArrayList<ContestMatchListGetSet> matchAR){
        this.matchAR = matchAR;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_upcoming_match, container, false);
        context= getActivity();

        gv= (GlobalVariables)context.getApplicationContext();
        nomatchbtn=(TextView)v.findViewById(R.id.nomatchbtn);
        nomatchlay=(LinearLayout) v.findViewById(R.id.nomatchlay);

        nomatchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HomeActivity.class));
                ((MyMatchesCricketActivity)context).finish();
            }
        });
        matchList= (ListView) v.findViewById(R.id.matchList);
        if(matchAR.size()>0) {

            Collections.sort(matchAR, new Comparator<ContestMatchListGetSet>() {
                @Override
                public int compare(ContestMatchListGetSet lhs, ContestMatchListGetSet rhs) {
                    return lhs.getStart_date().compareTo(rhs.getStart_date());
                }
            });
            matchList.setAdapter(new contestListAdapter(context, "0", matchAR));
        }else{
            matchList.setVisibility(View.GONE);
            nomatchlay.setVisibility(View.VISIBLE);
        }
        return v;
    }
}
