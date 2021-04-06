package com.img.mybat11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.GetSet.fantasyScorecardGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class fantasyScorecardAdapter extends BaseAdapter{

    Context context;
    ArrayList<fantasyScorecardGetSet> list;

    public  fantasyScorecardAdapter(Context context, ArrayList<fantasyScorecardGetSet> list){
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

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.scorecard_list,null);

        TextView playerName,playerPoints,selectper;
        ImageView playerImage;

        playerImage=(ImageView)v.findViewById(R.id.playerImage);
        playerName=(TextView)v.findViewById(R.id.playerName);
        playerPoints=(TextView)v.findViewById(R.id.playerPoints);
        selectper=(TextView)v.findViewById(R.id.selectper);

        playerName.setText(list.get(i).getPlayer_name());
        playerPoints.setText(list.get(i).getTotal());
        selectper.setText(list.get(i).getSelectper());
        Picasso.with(context).load(list.get(i).getPlayerimage()).placeholder(R.drawable.avtar).into(playerImage);

        return v;
    }
}
