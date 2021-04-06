package com.img.mybat11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.img.mybat11.Activity.DetailsActivity;
import com.img.mybat11.Activity.QuizDetailsActivity;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class QuizJoinedChallengeListAdapter extends BaseAdapter {

    Context context;
    ArrayList<JoinedChallengesGetSet> list;

    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;
    String datacode="";

    public QuizJoinedChallengeListAdapter(Context context, ArrayList<JoinedChallengesGetSet> list){
        this.context=context;
        this.list= list;
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
        TextView btnJoin;
        TextView prizeMoney,numWinners,teamsLeft,totalTeams,m,c,b,jointxt,prizetxt;
        LinearLayout winnerLL,ll;
        ProgressBar teamEnteredPB;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.challenge_list,null);

        cd= new ConnectionDetector(context);
        session= new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();

        prizeMoney=(TextView)v.findViewById(R.id.prizeMoney);
        numWinners=(TextView)v.findViewById(R.id.numWinners);
        winnerLL=(LinearLayout)v.findViewById(R.id.winnerLL);
        ll=(LinearLayout)v.findViewById(R.id.ll);
        teamsLeft=(TextView)v.findViewById(R.id.teamsLeft);
        totalTeams=(TextView)v.findViewById(R.id.totalTeams);
        btnJoin=(TextView)v.findViewById(R.id.btnJoin);
        teamEnteredPB=(ProgressBar)v.findViewById(R.id.teamEnteredPB);

        m=(TextView)v.findViewById(R.id.m);
        c=(TextView)v.findViewById(R.id.c);
        b=(TextView)v.findViewById(R.id.b);
        jointxt=(TextView)v.findViewById(R.id.jointxt);
        prizetxt=(TextView)v.findViewById(R.id.prizetxt);



        if(list.get(i).getMulti_entry()==1)
            m.setVisibility(View.VISIBLE);
        if(list.get(i).getConfirmed()==1)
            c.setVisibility(View.VISIBLE);
        if(list.get(i).getIs_bonus()==1) {
            b.setVisibility(View.VISIBLE);
        }

        final NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)nf2).applyPattern("##,##,###.##");


        if(list.get(i).getWinamount()!=0)
            prizeMoney.setText("₹ "+nf2.format(new Double(list.get(i).getWinamount()).intValue()));
        else {
            prizeMoney.setText("Net Practice");
            prizeMoney.setTextSize(12);
            jointxt.setVisibility(View.GONE);
            prizetxt.setVisibility(View.GONE);
        }

        final int left= (list.get(i).getMaximum_user()) - (list.get(i).getJoinedusers());

        if(list.get(i).getContest_type().equals("Amount")){
            if(list.get(i).getPricecardstatus()==1)
                numWinners.setText("1");
            else
                numWinners.setText(nf2.format(list.get(i).getTotalwinners())+" ▼");

            if(left!=0) {
                if(left == 1)
                    teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " team left");
                else
                    teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " teams left");
            }else
                teamsLeft.setText("Contest Completed");
            totalTeams.setText(nf2.format(list.get(i).getMaximum_user())+ " teams");

            teamEnteredPB.setMax(list.get(i).getMaximum_user());
            teamEnteredPB.setProgress(list.get(i).getJoinedusers());

        }else {
            numWinners.setText(list.get(i).getWinning_percentage() + " %");
            if(list.get(i).getJoinedusers() == 1)
                teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" Entries Joined");
            else
                teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" Entries Joined");
            totalTeams.setText("∞ Entries");

            teamEnteredPB.setMax(list.get(i).getJoinedusers());
            teamEnteredPB.setProgress(list.get(i).getJoinedusers());
        }
        btnJoin.setText("₹ "+nf2.format(list.get(i).getEntryfee())+"");


        teamEnteredPB.setMax(list.get(i).getMaximum_user());
        teamEnteredPB.setProgress(list.get(i).getJoinedusers());
        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(gv.getMatchTime()));
            datacode = newDate + " Quiz";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnJoin.setText("Invite");
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(left !=0) {
                    String code = list.get(i).getRefercode();
                    String shareBody ="You’ve been challenged! \n" +
                            "\n" +
                            "Think you can beat me? Join the contest on "+context.getResources().getString(R.string.app_name)+" for the "+datacode+" and prove it!\n" +
                            "\n" +
                            "Use Contest Code "+list.get(i).getRefercode()+" & join the action NOW!"+
                            "\nDownload Application from "+ context.getResources().getString(R.string.apk_url);

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            }
        });

        if(left == 0)
            btnJoin.setText("Joined");



        numWinners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getChallenge_id());
                Intent ii= new Intent(context, QuizDetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getChallenge_id());
                ii.putExtra("tabPos",0);
                context.startActivity(ii);
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getChallenge_id());
                Intent ii= new Intent(context, QuizDetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getChallenge_id());
                context.startActivity(ii);
            }
        });

        return v;
    }

}
