package com.img.mybat11.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.img.mybat11.Adapter.contestListAdapter;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyLiveMatchesFragment extends Fragment {

    ArrayList<ContestMatchListGetSet> matchAR;
    ListView matchList;
    Context context;
    TextView noMatch,nomatchupper;
    GlobalVariables gv;
    TextView nomatchbtn;
    LinearLayout nomatchlay;

    public MyLiveMatchesFragment() {
    }

    @SuppressLint("ValidFragment")
    public MyLiveMatchesFragment(ArrayList<ContestMatchListGetSet> matchAR){
        this.matchAR = matchAR;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_live_matches, container, false);
        context= getActivity();
        gv= (GlobalVariables)context.getApplicationContext();
        nomatchbtn=(TextView)v.findViewById(R.id.nomatchbtn);
        nomatchupper=(TextView)v.findViewById(R.id.nomatchupper);
        nomatchlay=(LinearLayout) v.findViewById(R.id.nomatchlay);
        nomatchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, HomeActivity.class));
                ((MyMatchesCricketActivity)context).finish();
            }
        });
        nomatchupper.setText("You haven't joined any contests that are live");
        matchList= (ListView) v.findViewById(R.id.matchList);
        if(matchAR.size()>0) {

            Collections.sort(matchAR, new Comparator<ContestMatchListGetSet>() {
                @Override
                public int compare(ContestMatchListGetSet lhs, ContestMatchListGetSet rhs) {
                    return rhs.getStart_date().compareTo(lhs.getStart_date());
                }
            });
            matchList.setAdapter(new contestListAdapter(context, "1", matchAR));
        }else{
            matchList.setVisibility(View.GONE);
            nomatchlay.setVisibility(View.VISIBLE);
        }
        return v;
    }

}
