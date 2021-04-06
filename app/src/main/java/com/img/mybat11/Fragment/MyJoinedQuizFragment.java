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
import com.img.mybat11.Adapter.MyJoinedQuizListAdapter;
import com.img.mybat11.Adapter.contestListAdapter;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.MyQuizListGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyJoinedQuizFragment extends Fragment {

    ArrayList<MyQuizListGetSet> list;
    ListView matchList;
    Context context;
    TextView noMatch;
    GlobalVariables gv;
    TextView nomatchbtn;
    LinearLayout nomatchlay;
    String type;
    public MyJoinedQuizFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ValidFragment")
    public MyJoinedQuizFragment(ArrayList<MyQuizListGetSet> list, String type){
        this.list = list;
        this.type = type;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_my_joined_quiz, container, false);


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
        if(list.size()>0) {

            if (type.equals("0")) {

                Collections.sort(list, new Comparator<MyQuizListGetSet>() {
                    @Override
                    public int compare(MyQuizListGetSet lhs, MyQuizListGetSet rhs) {
                        return lhs.getStart_date().compareTo(rhs.getStart_date());
                    }
                });
            }else {
                Collections.sort(list, new Comparator<MyQuizListGetSet>() {
                    @Override
                    public int compare(MyQuizListGetSet lhs, MyQuizListGetSet rhs) {
                        return rhs.getStart_date().compareTo(lhs.getStart_date());
                    }
                });
            }
            matchList.setAdapter(new MyJoinedQuizListAdapter(context, type, list));
        }else{
            matchList.setVisibility(View.GONE);
            nomatchlay.setVisibility(View.VISIBLE);
        }
        return v;
    }
}

