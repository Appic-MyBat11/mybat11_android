package com.img.mybat11.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.AllChallengesActivity;
import com.img.mybat11.Activity.ChooseTeamActivity;
import com.img.mybat11.Activity.CreateTeamActivity;
import com.img.mybat11.Activity.DetailsActivity;
import com.img.mybat11.Activity.JoinContestActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinChallengeBalanceGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.GetSet.priceCardGetSet;
import com.img.mybat11.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeListAdapter extends BaseAdapter {

    Context context;
    ArrayList<challengesGetSet> list;
    GlobalVariables gv;
    ConnectionDetector cd;
    RequestQueue requestQueue;
    double availableB,usableB;
    Dialog teamd;
    String joinFee="";
    String TAG="dialog";
    Dialog d;
    UserSessionManager session;
    MainActivity ma;
    View v;
    String type;
    private BottomSheetBehavior mBottomSheetBehavior1;
    ExpandableHeightListView priceCard;
    TextView totalPrizeAmount;

    ArrayList<MyTeamsGetSet> selectedteamList;

    public ChallengeListAdapter(Context context, ArrayList<challengesGetSet> list,String type){
        this.list= list;
        this.type= type;
        this.context=context;

        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(context);
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

        TextView btnJoin;
        TextView prizeMoney,numWinners,teamsLeft,totalTeams,m,c,b,bonusPer,jointxt,prizetxt;
        LinearLayout winnerLL;
        ProgressBar teamEnteredPB;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.challenge_list,null);

        gv= (GlobalVariables)context.getApplicationContext();
        session= new UserSessionManager(context);
        cd = new ConnectionDetector(context);

        jointxt=(TextView)v.findViewById(R.id.jointxt);
        prizetxt=(TextView)v.findViewById(R.id.prizetxt);
        prizeMoney=(TextView)v.findViewById(R.id.prizeMoney);
        numWinners=(TextView)v.findViewById(R.id.numWinners);
        teamsLeft=(TextView)v.findViewById(R.id.teamsLeft);
        totalTeams=(TextView)v.findViewById(R.id.totalTeams);
        bonusPer=(TextView)v.findViewById(R.id.bonusPer);
        winnerLL=(LinearLayout)v.findViewById(R.id.winnerLL);
        teamEnteredPB=(ProgressBar)v.findViewById(R.id.teamEnteredPB);

        m=(TextView)v.findViewById(R.id.m);
        c=(TextView)v.findViewById(R.id.c);
        b=(TextView)v.findViewById(R.id.b);


        View bottomSheet = ((Activity)context).findViewById(R.id.bottom_sheet2);
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

        final NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)nf2).applyPattern("##,##,###.##");


        if(list.get(i).getMulti_entry()==1)
            m.setVisibility(View.VISIBLE);
        if(list.get(i).getConfirmed_challenge()==1)
            c.setVisibility(View.VISIBLE);
        if(list.get(i).getIs_bonus()==1) {
            b.setVisibility(View.VISIBLE);
            bonusPer.setVisibility(View.VISIBLE);
            if (list.get(i).getBonus_type().equals("Amount")) {
                bonusPer.setText("₹ "+list.get(i).getBonus_percentage() + " Bonus");
            }else {
                bonusPer.setText(list.get(i).getBonus_percentage() + "% Bonus");
            }
        }

        if(list.get(i).getContest_type().equals("Amount")){
            if(list.get(i).getTotalwinners()==1)
                numWinners.setText("1");
            else
                numWinners.setText(nf2.format(list.get(i).getTotalwinners())+" ▼");

            int left= (list.get(i).getMaximum_user()) - (list.get(i).getJoinedusers());
            if(left!=0) {
                if(left == 1)
                    teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " team left");
                else
                    teamsLeft.setText("Only " + String.valueOf(nf2.format(left)) + " teams left");
            }else
                teamsLeft.setText("Contest Completed");
            totalTeams.setText(nf2.format(list.get(i).getMaximum_user())+ " Teams");

            teamEnteredPB.setMax(list.get(i).getMaximum_user());
            teamEnteredPB.setProgress(list.get(i).getJoinedusers());

        }else {
            numWinners.setText(list.get(i).getWinning_percentage() + " %");
            if(list.get(i).getJoinedusers() == 1)
                teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" team joined");
            else
                teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" teams joined");
            totalTeams.setText("∞ Teams");

            teamEnteredPB.setMax(list.get(i).getJoinedusers());
            teamEnteredPB.setProgress(list.get(i).getJoinedusers()/2);
        }

        btnJoin=(TextView)v.findViewById(R.id.btnJoin);

        if(list.get(i).getIsselected()){
            btnJoin.setText("Invite");
        }else {
            btnJoin.setText("₹ "+nf2.format(list.get(i).getEntryfee())+"");
        }


        if(list.get(i).getIsselected()){
            btnJoin.setText("Invite");
        }

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinFee ="";
                if(list.get(i).getIsselected()){
                    String shareBody ="You’ve been challenged! \n" +
                            "\n" +
                            "Think you can beat me? Join the contest on "+context.getResources().getString(R.string.app_name)+" for the "+gv.getTeam1()+" vs "+gv.getTeam2()+" match and prove it!\n" +
                            "\n" +
                            "Use Contest Code "+list.get(i).getRefercode()+" & join the action NOW!"+
                            "\nDownload Application from "+ context.getResources().getString(R.string.apk_url);

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }else{
                    joinFee =String.valueOf(list.get(i).getEntryfee());
                    MyTeams(i);
                }
            }
        });


        if(list.get(i).getWin_amount()!=0)
            prizeMoney.setText("₹ "+nf2.format(list.get(i).getWin_amount())+"");
        else {
            prizeMoney.setText("Net Practice");
            prizeMoney.setTextSize(12);

            if(list.get(i).getIsselected()){
                btnJoin.setText("Invite");
            }else {
                btnJoin.setText("Join");
            }
            jointxt.setVisibility(View.GONE);
            prizetxt.setVisibility(View.GONE);
        }
        numWinners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii= new Intent(context, DetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getId());
                ii.putExtra("tabPos",0);
                context.startActivity(ii);
            }
        });


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getId());
                Intent ii= new Intent(context, DetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getId());
                context.startActivity(ii);
            }
        });

        return v;
    }

    public void MyTeams(final int pos){
        ma.showProgressDialog(context);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.getMyTeams1(session.getUserId(),gv.getMatchKey(),list.get(pos).getType());
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    ma.dismissProgressDialog();
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    selectedteamList = new ArrayList<>();
                    selectedteamList = response.body();
                    int total = selectedteamList.size();
                    int count =0;
                    int teamid =0;
                    for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isSelected())
                            count++;
                        else
                            teamid = zz.getTeamid();
                    }
                    int result = total-count;

                    if(result == 0){
                        Intent ii= new Intent(context,CreateTeamActivity.class);
                        ii.putExtra("teamNumber",selectedteamList.size()+1);
                        ii.putExtra("challengeId",list.get(pos).getId());
                        ii.putExtra("type",type);
                        gv.setMulti_entry(String.valueOf(list.get(pos).getMulti_entry()));
                        ii.putExtra("entryFee",list.get(pos).getEntryfee());
                        context.startActivity(ii);
                    }else if (result == 1) {
                        Intent intent = new Intent(context, JoinContestActivity.class);
                        intent.putExtra("challenge_id", list.get(pos).getId());
                        intent.putExtra("team", String.valueOf(teamid));
                        intent.putExtra("entryFee", joinFee);
                        gv.setMulti_entry(String.valueOf(list.get(pos).getMulti_entry()));
                        context.startActivity(intent);
                    } else {
                        gv.setSelectedTeamList(selectedteamList);
                        Intent intent = new Intent(context, ChooseTeamActivity.class);
                        intent.putExtra("type", "join");
                        intent.putExtra("challengeId", list.get(pos).getId());
                        intent.putExtra("entryFee", list.get(pos).getEntryfee());
                        context.startActivity(intent);
                        gv.setMulti_entry(String.valueOf(list.get(pos).getMulti_entry()));
//                        TeamSelectDialog(pos);
                    }
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeams(pos);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<MyTeamsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }
}
