package com.img.mybat11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.img.mybat11.GetSet.LeaderboardByUserGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

public class LeaderboardByUserAdapter extends BaseAdapter{

    ArrayList<LeaderboardByUserGetSet> list;
    Context context;

    public LeaderboardByUserAdapter(Context context, ArrayList<LeaderboardByUserGetSet> list){
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v;

        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.leaderboard_by_user,null);

        TextView matchName,points;
        matchName =(TextView)v.findViewById(R.id.matchName);
        points =(TextView)v.findViewById(R.id.points);

        matchName.setText(list.get(i).getShort_name());
        points.setText(list.get(i).getPoints());

        return v;
    }
}
