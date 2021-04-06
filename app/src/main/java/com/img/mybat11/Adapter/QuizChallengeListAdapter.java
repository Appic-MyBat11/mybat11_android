package com.img.mybat11.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.AddBalanceActivity;
import com.img.mybat11.Activity.ChooseTeamActivity;
import com.img.mybat11.Activity.CreateTeamActivity;
import com.img.mybat11.Activity.DetailsActivity;
import com.img.mybat11.Activity.JoinContestActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.QuizChallengesActivity;
import com.img.mybat11.Activity.QuizCheckBalanceActivity;
import com.img.mybat11.Activity.QuizDetailsActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuizChallengeListAdapter extends RecyclerView.Adapter<QuizChallengeListAdapter.ViewHolder> {

    Context context;
    ArrayList<challengesGetSet> list;
    GlobalVariables gv;
    ConnectionDetector cd;
    RequestQueue requestQueue;
    double availableB;
    Dialog teamd;
    String joinFee="";
    String TAG="dialog";
    Dialog d;
    UserSessionManager session;
    MainActivity ma;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ExpandableHeightListView priceCard;
    TextView totalPrizeAmount;
    String datacode="";

    ArrayList<MyTeamsGetSet> selectedteamList;
    double usableB,joiningB;

    ArrayList<JoinChallengeBalanceGetSet> balancelist;

    public QuizChallengeListAdapter(Context context, ArrayList<challengesGetSet> list){
        this.list= list;
        this.context=context;

        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(context);
        gv= (GlobalVariables)context.getApplicationContext();
        session= new UserSessionManager(context);
        cd = new ConnectionDetector(context);

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView btnJoin;
        TextView prizeMoney,numWinners,teamsLeft,totalTeams,m,c,b,bonusPer,jointxt,prizetxt;
        LinearLayout winnerLL;
        ProgressBar teamEnteredPB;
        LinearLayout mainl;

        public ViewHolder(View v){
            super(v);

            prizeMoney=(TextView)v.findViewById(R.id.prizeMoney);
            jointxt=(TextView)v.findViewById(R.id.jointxt);
            prizetxt=(TextView)v.findViewById(R.id.prizetxt);
            numWinners=(TextView)v.findViewById(R.id.numWinners);
            teamsLeft=(TextView)v.findViewById(R.id.teamsLeft);
            totalTeams=(TextView)v.findViewById(R.id.totalTeams);
            bonusPer=(TextView)v.findViewById(R.id.bonusPer);
            winnerLL=(LinearLayout)v.findViewById(R.id.winnerLL);
            teamEnteredPB=(ProgressBar)v.findViewById(R.id.teamEnteredPB);
            mainl=(LinearLayout) v.findViewById(R.id.mainl);

            m=(TextView)v.findViewById(R.id.m);
            c=(TextView)v.findViewById(R.id.c);
            b=(TextView)v.findViewById(R.id.b);

            btnJoin=(TextView)v.findViewById(R.id.btnJoin);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.challenge_list,viewGroup,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {

        final NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)nf2).applyPattern("##,##,###.##");

        View bottomSheet = ((Activity)context).findViewById(R.id.bottom_sheet1);
        bottomSheet.setVisibility(View.VISIBLE);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        priceCard=(ExpandableHeightListView)((Activity)context).findViewById(R.id.priceCard);
        priceCard.setExpanded(true);

        totalPrizeAmount = ((Activity)context).findViewById(R.id.totalPrizeAmount);


        ((Activity)context).findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        if(list.get(i).getMulti_entry()==1)
            holder.m.setVisibility(View.VISIBLE);
        if(list.get(i).getConfirmed_challenge()==1)
            holder.c.setVisibility(View.VISIBLE);
        if(list.get(i).getIs_bonus()==1) {
            holder.b.setVisibility(View.VISIBLE);
            holder.bonusPer.setVisibility(View.VISIBLE);
            if (list.get(i).getBonus_type().equals("Amount")) {
                holder.bonusPer.setText("₹ "+list.get(i).getBonus_percentage() + " Bonus");
            }else {
                holder.bonusPer.setText(list.get(i).getBonus_percentage() + "% Bonus");
            }
        }

        if(list.get(i).getContest_type().equals("Amount")){
            if(list.get(i).getTotalwinners()==1)
                holder.numWinners.setText("1");
            else
                holder. numWinners.setText(nf2.format(list.get(i).getTotalwinners())+" ▼");

            int left= (list.get(i).getMaximum_user()) - (list.get(i).getJoinedusers());
            if(left!=0) {
                    holder.teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " Left");
            }else
                holder. teamsLeft.setText("Contest Completed");
            holder.totalTeams.setText(nf2.format(list.get(i).getMaximum_user())+ " Entries");

            holder.teamEnteredPB.setMax(list.get(i).getMaximum_user());
            holder. teamEnteredPB.setProgress(list.get(i).getJoinedusers());

        }else {
            holder. numWinners.setText(nf2.format(list.get(i).getWinning_percentage()) + " %");
            if(list.get(i).getJoinedusers() == 1)
                holder. teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" Entries Joined");
            else
                holder. teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" Entries Joined");
            holder.totalTeams.setText("∞ Entries");

            holder.teamEnteredPB.setMax(list.get(i).getJoinedusers());
            holder.teamEnteredPB.setProgress(list.get(i).getJoinedusers());
        }
        if(list.get(i).getIsselected()){
            holder.btnJoin.setText("Invite");
        }else {
            holder.btnJoin.setText("₹ "+nf2.format(list.get(i).getEntryfee())+"");
        }

        if(list.get(i).getIsselected()){
            holder.btnJoin.setText("Invite");
        }

        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(gv.getMatchTime()));
            datacode = newDate + " Quiz";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinFee ="";
                if(list.get(i).getIsselected()){

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
                }else{
                    Intent intent = new Intent(context, QuizCheckBalanceActivity.class);
                    intent.putExtra("challenge_id", list.get(i).getId());
                    context.startActivity(intent);
                }
            }
        });

        if(list.get(i).getWin_amount()!=0)
            holder.prizeMoney.setText("₹ "+nf2.format(list.get(i).getWin_amount())+"");
        else {
            holder.prizeMoney.setText("Net Practice");
            holder.prizeMoney.setTextSize(12);
            if(list.get(i).getIsselected()){
                holder.btnJoin.setText("Invite");
            }else {
                holder.btnJoin.setText("Join");
            }
            holder.jointxt.setVisibility(View.GONE);
            holder.prizetxt.setVisibility(View.GONE);
        }



        holder.numWinners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii= new Intent(context, QuizDetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getId());
                ii.putExtra("tabPos",0);
                context.startActivity(ii);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getId());
                Intent ii= new Intent(context, QuizDetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getId());
                context.startActivity(ii);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(list.size() < 3)
            return list.size();
        else
            return 3;
    }




}
