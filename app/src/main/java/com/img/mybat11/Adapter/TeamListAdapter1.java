package com.img.mybat11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


import com.img.mybat11.Activity.TeamPreviewActivity;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.SelectedPlayersGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;


public class TeamListAdapter1 extends BaseAdapter{

    GlobalVariables gv;
    Context context;
    ArrayList<MyTeamsGetSet> list;
    String type;

    public TeamListAdapter1(Context context, ArrayList<MyTeamsGetSet> list, String type){
        this.context= context;
        this.list=list;
        this.type = type;

        gv = (GlobalVariables)context.getApplicationContext();
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v;
        RadioButton teamName;
        TextView captainName,viceCaptainName,btnPreview,allreadyselected;
        LinearLayout ll;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.team_list1,null);

        teamName=(RadioButton) v.findViewById(R.id.teamName);
        captainName=(TextView)v.findViewById(R.id.captainName);
        viceCaptainName=(TextView)v.findViewById(R.id.viceCaptainName);
        btnPreview=(TextView)v.findViewById(R.id.btnPreview);
        allreadyselected=(TextView)v.findViewById(R.id.allreadyselected);

        ll = (LinearLayout)v.findViewById(R.id.ll);

        captainName.setText(list.get(i).getCaptain());
        viceCaptainName.setText(list.get(i).getVicecaptain());

        teamName.setText("Team "+list.get(i).getTeamnumber());
        if(list.get(i).isSelected()) {
            teamName.setText("Team " + list.get(i).getTeamnumber() + " (ALREADY JOINED)");
            ll.setBackground(context.getResources().getDrawable(R.drawable.green_border_small_radius));
            allreadyselected.setVisibility(View.VISIBLE);
            ll.setEnabled(false);
            teamName.setEnabled(false);
        }else {
            ll.setEnabled(true);
            teamName.setEnabled(true);
            allreadyselected.setVisibility(View.GONE);
        }

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<captainListGetSet> captainList;
                ArrayList<SelectedPlayersGetSet> playerList;

                playerList= list.get(i).getPlayer();

                captainList= new ArrayList<>();
                for(SelectedPlayersGetSet zz:playerList){

                    Log.i("Selected team ",zz.getPlayer_name());
                    Log.i("captain",zz.getCaptain());
                    Log.i("Vice captain",zz.getVicecaptain());

                    captainListGetSet ob = new captainListGetSet();
                    ob.setTeamcolor(zz.getTeamcolor());
                    ob.setTeam(zz.getTeam());
                    ob.setName(zz.getPlayer_name());
                    ob.setCredit(zz.getCredit());
                    ob.setImage(zz.getImage());
                    ob.setPlayingstatus(zz.getPlayingstatus());
                    if(zz.getRole().equals("keeper")) {
                        ob.setRole("Wk");
                    }if(zz.getRole().equals("batsman")) {
                        ob.setRole("Bat");
                    }if(zz.getRole().equals("bowler")) {
                        ob.setRole("Bow");
                    }if(zz.getRole().equals("allrounder")) {
                        ob.setRole("AR");
                    }
                    ob.setId(zz.getId());
                    ob.setCaptain(String.valueOf(zz.getCaptain()));
                    ob.setVc(String.valueOf(zz.getVicecaptain()));

                    captainList.add(ob);
                }

                Intent ii= new Intent(context, TeamPreviewActivity.class);
                ii.putExtra("team_name","Team "+list.get(i).getTeamnumber());
                gv.setCaptainList(captainList);
                context.startActivity(ii);
            }
        });

        if(list.get(i).isPicked()){
            teamName.setChecked(true);
            ll.setBackground(context.getResources().getDrawable(R.drawable.green_border_small_radius));
        }else {
            teamName.setChecked(false);
        }

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("join")) {
                    if (list.get(i).isPicked())
                        list.get(i).setPicked(false);
                    else
                        list.get(i).setPicked(true);
                }else {
                    if (list.get(i).isPicked())
                        list.get(i).setPicked(false);
                    else
                        list.get(i).setPicked(true);
                }
                notifyDataSetChanged();
            }
        });

        teamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type.equals("join")) {
                    if (list.get(i).isPicked())
                        list.get(i).setPicked(false);
                    else
                        list.get(i).setPicked(true);
                }else {
                    if (list.get(i).isPicked())
                        list.get(i).setPicked(false);
                    else
                        list.get(i).setPicked(true);
                }
                notifyDataSetChanged();
            }
        });
//        v.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(type.equals("join")) {
//                    for (MyTeamsGetSet zz : list) {
//                        zz.setPicked(false);
//                    }
//                    list.get(i).setPicked(true);
//                }else {
//
//                    if (list.get(i).isPicked())
//                        list.get(i).setPicked(false);
//                    else
//                        list.get(i).setPicked(true);
//                }
//                notifyDataSetChanged();
//            }
//        });
//
//        teamName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(type.equals("join")) {
//                    for (MyTeamsGetSet zz : list) {
//                        zz.setPicked(false);
//                    }
//                    list.get(i).setPicked(true);
//                }else {
//
//                    if (list.get(i).isPicked())
//                        list.get(i).setPicked(false);
//                    else
//                        list.get(i).setPicked(true);
//                }
//                notifyDataSetChanged();
//            }
//        });

        return v;
    }
}
