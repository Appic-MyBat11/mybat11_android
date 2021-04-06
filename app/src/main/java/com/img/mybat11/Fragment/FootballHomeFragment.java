package com.img.mybat11.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.img.mybat11.Adapter.matchListAdapter;
import com.img.mybat11.Api.ExpandableHeightExpendableListView;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.GetSet.MatchListGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class FootballHomeFragment extends Fragment {

    View v;
    ExpandableHeightListView matchList;

    @SuppressLint("ValidFragment")
    public FootballHomeFragment() {
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
        v = inflater.inflate(R.layout.fragment_football_home, container, false);
        matchList = (ExpandableHeightListView) v.findViewById(R.id.matchList);
        matchList.setExpanded(true);

//        matchList.setAdapter(new matchListAdapter(getContext(), "cricket", list,"0"));


        return v;
    }
}
