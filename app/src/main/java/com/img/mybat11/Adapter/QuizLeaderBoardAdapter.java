package com.img.mybat11.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.Activity.ChooseTeamActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.TeamPreviewActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.SelectedPlayersGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.GetSet.teamsGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizLeaderBoardAdapter extends RecyclerView.Adapter<QuizLeaderBoardAdapter.MyViewHolder> {

    Context context;
    String TAG="Edit team";
    ArrayList<teamsGetSet> list;
    ArrayList<SelectedPlayersGetSet> playerList;
    MainActivity ma;
    UserSessionManager session;
    GlobalVariables gv;

    ArrayList<MyTeamsGetSet> selectedteamList;
    int size =1;
    String jid;
    int challenge_id;

    public  QuizLeaderBoardAdapter(Context context, ArrayList<teamsGetSet> list){
        this.context = context;
        this.list = list;
        this.jid = jid;
        this.challenge_id = challenge_id;

        session = new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();
        ma = new MainActivity();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView teamName;
        CircleImageView userImage;

        public MyViewHolder(View v) {
            super(v);
            userImage=(CircleImageView)v.findViewById(R.id.userImage);
            teamName= (TextView)v.findViewById(R.id.teamName);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quizleaderboard, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        if(!list.get(i).getImage().equals(""))
            Picasso.with(context).load(list.get(i).getImage()).resize(100,100).into(holder.userImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //previewTeam
                if(session.getTeamName().equalsIgnoreCase(list.get(i).getTeam())){
//                    ViewTeam(String.valueOf(list.get(i).getTeamid()),String.valueOf(list.get(i).getTeamnumber()));
                }else{
                    ma.showToast(context,"You can preview other teams only after deadline");
                }
            }
        });

        if(session.getTeamName().equalsIgnoreCase(list.get(i).getTeam())){
            holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#443759a5")));
        }else{
            if(i%2 ==0){
                holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
            }else{
                holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f6f6f6")));
            }
        }

        holder.teamName.setText(list.get(i).getTeam());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
