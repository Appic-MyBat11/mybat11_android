package com.img.mybat11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.img.mybat11.GetSet.MatchStatsGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;


public class PlayerStatsAdapter extends BaseAdapter {

    Context context;
    ArrayList<MatchStatsGetSet> matches;

    public PlayerStatsAdapter(Context context, ArrayList<MatchStatsGetSet> matches){
        this.context=context;
        this.matches= matches;
    }

    @Override
    public int getCount() {
        return matches.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v;
        TextView matchName,date,point,selectedBy;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.player_stats_list,null);

        matchName=(TextView)v.findViewById(R.id.match_name);
        date=(TextView)v.findViewById(R.id.match_date);
        point=(TextView)v.findViewById(R.id.points);
        selectedBy=(TextView)v.findViewById(R.id.selectedBy);

        matchName.setText(matches.get(i).getShortname());
        date.setText(matches.get(i).getMatchdate());
        point.setText(String.valueOf(matches.get(i).getTotal_points()));
        selectedBy.setText(matches.get(i).getSelectper());

        return v;
    }
}
