package com.img.mybat11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.Activity.DetailsActivity;
import com.img.mybat11.Activity.DetailsChallengeActivity;
import com.img.mybat11.Activity.LiveChallengeActivity;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinedChallengesGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;


public class LiveChallengeListAdapter extends BaseAdapter {

    Context context;
    ArrayList<JoinedChallengesGetSet> list;
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;

    private BottomSheetBehavior mBottomSheetBehavior1;
    ExpandableHeightListView priceCard;

    public LiveChallengeListAdapter(Context context, ArrayList<JoinedChallengesGetSet> list){
        this.context=context;
        this.list= list;
//        this.list1= list1;
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
        TextView prizeMoney,numWinners,entryFee,joinedWith,points,rank;
        LinearLayout winnerLL;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.live_challenge_list,null);

        cd= new ConnectionDetector(context);
        session= new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();

        prizeMoney=(TextView)v.findViewById(R.id.prizeMoney);
        numWinners=(TextView)v.findViewById(R.id.numWinners);
        entryFee=(TextView)v.findViewById(R.id.entryFee);
        winnerLL=(LinearLayout)v.findViewById(R.id.winnerLL);
        joinedWith=(TextView)v.findViewById(R.id.joinedWith);
        rank=(TextView)v.findViewById(R.id.rank);
        points=(TextView)v.findViewById(R.id.points);

        if(list.get(i).getWinamount()!=0)
            prizeMoney.setText("₹ "+new Double(list.get(i).getWinamount()).intValue());
        else {
            prizeMoney.setText("Net Practice");
            prizeMoney.setTextSize(12);
            winnerLL.setVisibility(View.GONE);
        }

        entryFee.setText("₹ "+new Double( list.get(i).getEntryfee()).intValue());

        View bottomSheet = ((LiveChallengeActivity)context).findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        priceCard=(ExpandableHeightListView)((LiveChallengeActivity)context).findViewById(R.id.priceCard);
        priceCard.setExpanded(true);

        if(list.get(i).getContest_type().equals("Amount")){
            if(list.get(i).getTotalwinners()==1){
                numWinners.setText("1");
            }
            else {
                numWinners.setText(list.get(i).getTotalwinners() + " ▼");
            }
        }else {
            numWinners.setText(list.get(i).getWinning_percentage() + " %");
        }

        rank.setText("#"+list.get(i).getUserrank());
        points.setText(list.get(i).getUserpoints()+"");
        joinedWith.setText("Team "+list.get(i).getUserteamnumber());



        winnerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii= new Intent(context, DetailsChallengeActivity.class);
                gv.setChallengeId(list.get(i).getChallenge_id());
                ii.putExtra("tabPos",0);
                if(!list.get(i).getName().equals(""))
                    ii.putExtra("title",list.get(i).getName());
                else{
                    if(list.get(i).getEntryfee() >0)
                        ii.putExtra("title","Cash Contest");
                    else
                        ii.putExtra("title","Practice Contest");
                }
                context.startActivity(ii);
            }
        });



        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii= new Intent(context, DetailsChallengeActivity.class);
                gv.setChallengeId(list.get(i).getChallenge_id());
                if(!list.get(i).getName().equals(""))
                    ii.putExtra("title",list.get(i).getName());
                else{
                    if(list.get(i).getEntryfee() >0)
                        ii.putExtra("title","Cash Contest");
                    else
                        ii.putExtra("title","Practice Contest");
                }
                context.startActivity(ii);
            }
        });

        return v;
    }
}
