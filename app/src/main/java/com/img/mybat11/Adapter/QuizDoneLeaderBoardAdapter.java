package com.img.mybat11.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.PreviewActivity;
import com.img.mybat11.Activity.QuizJoinedLiveChallengesActivity;
import com.img.mybat11.Activity.QuizJoinedUpcomingChallengesActivity;
import com.img.mybat11.Activity.QuizPlayActivity;
import com.img.mybat11.Activity.QuizResultQuestionsActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinTeamGetSet;
import com.img.mybat11.GetSet.SelectedPlayersGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuizDoneLeaderBoardAdapter extends RecyclerView.Adapter<QuizDoneLeaderBoardAdapter.MyViewHolder>{

    Context context;
    ArrayList<JoinTeamGetSet> list;
    String TAG="Edit team";
    UserSessionManager session;
    ArrayList<SelectedPlayersGetSet> playerList;
    MainActivity ma;
    GlobalVariables gv;

    public QuizDoneLeaderBoardAdapter(Context context, ArrayList<JoinTeamGetSet> list){
        this.context = context;
        this.list = list;

        session = new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();
        ma = new MainActivity();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView teamName,points,winning,t1,t2,rank,trophy,timeRemaining1;
        CircleImageView img;
        LinearLayout ql;

        public MyViewHolder(View v) {
            super(v);
            timeRemaining1= (TextView)v.findViewById(R.id.timeRemaining1);
            teamName= (TextView)v.findViewById(R.id.teamName);
            points= (TextView)v.findViewById(R.id.points);
            winning= (TextView)v.findViewById(R.id.winning);
            rank= (TextView)v.findViewById(R.id.rank);
            trophy= (TextView)v.findViewById(R.id.trophy);

            t1=(TextView)v.findViewById(R.id.t1);
            t2=(TextView)v.findViewById(R.id.t2);

            img =(CircleImageView) v.findViewById(R.id.img);
            ql =(LinearLayout) v.findViewById(R.id.ql);
        }
    }

    @Override
    public QuizDoneLeaderBoardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quizdoneleaderboard, parent, false);

        return new QuizDoneLeaderBoardAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(QuizDoneLeaderBoardAdapter.MyViewHolder holder, final int i) {
        NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)nf2).applyPattern("##,##,###");

        if (gv.getStatus().equals("live")){
            holder.ql.setVisibility(View.GONE);
        }else {
            holder.ql.setVisibility(View.VISIBLE);
        }

        holder.teamName.setText(list.get(i).getTeamname()+"");
        holder.points.setText(String.valueOf(list.get(i).getPoints()));
        if(!list.get(i).getWiningamount().equals(""))
            holder.winning.setText("₹"+nf2.format(Double.parseDouble(list.get(i).getWiningamount())));
        else
            holder.winning.setText("-");
        holder.rank.setText("#"+list.get(i).getGetcurrentrank());

        holder.trophy.setTypeface(Typeface.createFromAsset(context.getAssets(),"fontawesome-webfont.ttf"));

        if(!list.get(i).getImage().equals("")) {
            Picasso.with(context).load(list.get(i).getImage()).into(holder.img);
        }

        long millisUntilFinished = Long.parseLong(list.get(i).getTime()) * 1000;

        holder.timeRemaining1.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) +" : " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))+" Sec");


        if(list.get(i).getGetcurrentrank() != 1){
            holder.trophy.setVisibility(View.GONE);
            holder.t2.setVisibility(View.GONE);

            holder.t2.setTypeface(Typeface.DEFAULT);
        }else{
            holder.t2.setTypeface(Typeface.DEFAULT_BOLD);

            holder.trophy.setVisibility(View.VISIBLE);
            holder.t2.setVisibility(View.VISIBLE);
        }

        if(session.getTeamName().equalsIgnoreCase(list.get(i).getTeamname())){
            holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#44c51d23")));
        }else{
            if(i%2 ==0){
                holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
            }else{
                holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f6f6f6")));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii= new Intent(context, QuizResultQuestionsActivity.class);
                ii.putExtra("userid",String.valueOf(list.get(i).getUserid()));
                ii.putExtra("earnpoints",String.valueOf(list.get(i).getPoints()));
                context.startActivity(ii);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
