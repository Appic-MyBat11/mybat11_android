package com.img.mybat11.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.GetSet.playerMatchStatsGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerInfoViewPageAdapter extends PagerAdapter {

    Context context;
    ArrayList <playerMatchStatsGetSet> list;

    public PlayerInfoViewPageAdapter(Context context, ArrayList <playerMatchStatsGetSet> list)
    {
        this.context=context;
        this.list= list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {

        View v;
        ImageView playerImage;
        TextView playerName,selectper, startingPoint, runPoints,hitterPoints, fourPoints, sixPoints, strikeRatePoints, points50, duckPoints, wicketspoints, maidenPoints, economypoints, throwerPoints, catchPoints, stumpingPoints, playerPoints;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.single_player_info,null);

        playerImage=(ImageView)v.findViewById(R.id.playerImage);
        playerName = (TextView)v.findViewById(R.id.playerName);
        selectper = (TextView) v.findViewById(R.id.selectper);
        playerPoints = (TextView) v.findViewById(R.id.playerPoints);
        startingPoint = (TextView) v.findViewById(R.id.startingPoint);
        runPoints = (TextView) v.findViewById(R.id.runPoints);
        fourPoints = (TextView) v.findViewById(R.id.fourPoints);
        sixPoints = (TextView) v.findViewById(R.id.sixPoints);
        strikeRatePoints = (TextView) v.findViewById(R.id.strikeRatePoints);
        points50 = (TextView) v.findViewById(R.id.points50);
        duckPoints = (TextView) v.findViewById(R.id.duckPoints);
        wicketspoints = (TextView) v.findViewById(R.id.wicketspoints);
        maidenPoints = (TextView) v.findViewById(R.id.maidenPoints);
        economypoints = (TextView) v.findViewById(R.id.economypoints);
        throwerPoints = (TextView) v.findViewById(R.id.throwerPoints);
        catchPoints = (TextView) v.findViewById(R.id.catchPoints);
        stumpingPoints = (TextView) v.findViewById(R.id.stumpingPoints);
        hitterPoints = (TextView) v.findViewById(R.id.hitterPoints);

        playerName.setText(list.get(i).getPlayer_name());
        startingPoint.setText(String.valueOf(list.get(i).getStartingpoints()));
        runPoints.setText(String.valueOf(list.get(i).getRuns()));
        fourPoints.setText(String.valueOf(list.get(i).getFours()));
        sixPoints.setText(String.valueOf(list.get(i).getSixs()));
        strikeRatePoints.setText(String.valueOf(list.get(i).getStrike_rate()));
        if(list.get(i).getHalfcentury().equals("0"))
            points50.setText(String.valueOf(list.get(i).getCentury()));
        else
            points50.setText(String.valueOf(list.get(i).getHalfcentury()));
        duckPoints.setText(String.valueOf(list.get(i).getNegative()));
        wicketspoints.setText(String.valueOf(list.get(i).getWickets()));
        maidenPoints.setText(String.valueOf(list.get(i).getMaidens()));
        economypoints.setText(String.valueOf(list.get(i).getEconomy_rate()));
        throwerPoints.setText(String.valueOf(list.get(i).getThrower()));
        catchPoints.setText(String.valueOf(list.get(i).getCatch_points()));
        stumpingPoints.setText(String.valueOf(list.get(i).getStumping()));
        hitterPoints.setText(String.valueOf(list.get(i).getHitter()));
        playerPoints.setText("Total Points : "+String.valueOf(list.get(i).getTotal()));
        selectper.setText("Selected by : "+String.valueOf(list.get(i).getSelectper()));

        Picasso.with(context).load(list.get(i).getPlayerimage()).placeholder(R.drawable.avtar).into(playerImage);
        container.addView(v);
        return v;

     }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);
    }
}
