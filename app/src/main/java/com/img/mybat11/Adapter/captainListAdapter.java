package com.img.mybat11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.img.mybat11.Activity.CaptainViceCaptainActivity;
import com.img.mybat11.Activity.PlayerStatsActivity;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class captainListAdapter extends BaseAdapter{

    Context context;
    ArrayList<captainListGetSet> list;
    Button btnContinue;

    public captainListAdapter(Context context, ArrayList<captainListGetSet> list){
        this.context= context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int i, View view, final ViewGroup viewGroup) {

        View v;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.captain_list,null);

        final String[] captain = {""};
        final String vicecaptain[] = {""};
        final TextView c,vc,playerName,team,points,role;
        CircleImageView img;
        RelativeLayout rl;

        c= (TextView)v.findViewById(R.id.captain);
        vc= (TextView)v.findViewById(R.id.vicecaptain);
        img=(CircleImageView) v.findViewById(R.id.img);

        playerName=(TextView)v.findViewById(R.id.playerName);
        team=(TextView)v.findViewById(R.id.teamName);
        points=(TextView)v.findViewById(R.id.points);
        role=(TextView)v.findViewById(R.id.role);
        rl = (RelativeLayout)v.findViewById(R.id.rl);

        btnContinue=(Button)((CaptainViceCaptainActivity)context).findViewById(R.id.btnContinue);





        if(!list.get(i).getId().equals("0")) {
            playerName.setText(list.get(i).getName());
            points.setText(list.get(i).getPoints() + " pts");
            team.setText(list.get(i).getTeamname());
            role.setText(list.get(i).getRole());

            if (!list.get(i).getImage().equals(""))
                Picasso.with(context).load(list.get(i).getImage()).into(img);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ii = new Intent(context, PlayerStatsActivity.class);
                    ii.putExtra("key", list.get(i).getId());
                    ii.putExtra("PlayerName", list.get(i).getName());
                    context.startActivity(ii);
                }
            });
        }else{
            playerName.setVisibility(View.GONE);
            team.setVisibility(View.GONE);
            points.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
            role.setVisibility(View.GONE);
            c.setVisibility(View.GONE);
            vc.setVisibility(View.GONE);

            rl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,15));
            v.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        }

        if(list.get(i).getVc().equals("Y")){
            vc.setText("1.5x");
            vc.setBackgroundResource(R.drawable.captain_selected);
            vc.setTextColor(context.getResources().getColor(R.color.white));
            c.setBackgroundResource(R.drawable.captain_deselected);
            c.setTextColor(context.getResources().getColor(R.color.font_color));
        }

        if(list.get(i).getCaptain().equals("Y")){
            c.setText("2x");
            c.setBackgroundResource(R.drawable.captain_selected);
            c.setTextColor(context.getResources().getColor(R.color.white));
            vc.setBackgroundResource(R.drawable.captain_deselected);
            vc.setTextColor(context.getResources().getColor(R.color.font_color));
        }

        for(captainListGetSet zz:list){
            if(zz.getCaptain().equals("Y"))
                captain[0] = zz.getId();
            else if(zz.getVc().equals("Y"))
                vicecaptain[0] = zz.getId();
        }

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int ii=0; ii<list.size(); ii++){
                    if(i != ii){
                        list.get(ii).setCaptain("N");
                    }
                    else{
                        list.get(ii).setCaptain("Y");
                    }
                }
                notifyDataSetChanged();
            }
        });

        vc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int ii=0; ii<list.size(); ii++){
                    if(i != ii){
                        list.get(ii).setVc("N");
                    }
                    else{
                        list.get(ii).setVc("Y");
                    }
                }
                notifyDataSetChanged();
            }
        });

        if(!captain[0].equals("") && !vicecaptain[0].equals("") && !captain[0].equals(vicecaptain[0])) {
            btnContinue.setEnabled(true);
            btnContinue.setTextColor(context.getResources().getColor(R.color.white));
            btnContinue.setBackgroundDrawable(context.getDrawable(R.drawable.btn_green));
        }else {
            btnContinue.setEnabled(false);
            btnContinue.setTextColor(context.getResources().getColor(R.color.gray_text_color));
            btnContinue.setBackgroundDrawable(context.getDrawable(R.drawable.btn_gray));
        }

        Log.i("captain",captain[0]);
        Log.i("vicecaptain",vicecaptain[0]);

        return v;
    }
}
