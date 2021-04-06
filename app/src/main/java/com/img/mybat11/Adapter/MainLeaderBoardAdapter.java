package com.img.mybat11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.img.mybat11.Activity.LeaderboardByUserActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.MainLeaderboardGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainLeaderBoardAdapter extends RecyclerView.Adapter<MainLeaderBoardAdapter.MyViewHolder> {

    Context context;
    UserSessionManager session;
    ArrayList<MainLeaderboardGetSet> list;
    MainActivity ma;
    int Series_id;

    public MainLeaderBoardAdapter(Context context, ArrayList<MainLeaderboardGetSet> list, int Series_id){
        this.context = context;
        this.list = list;
        this.Series_id = Series_id;

        session = new UserSessionManager(context);
        ma = new MainActivity();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView teamName,points,rank;
        CircleImageView userImage;
        TextView winnerBg;

        public MyViewHolder(View v) {
            super(v);
            userImage=(CircleImageView)v.findViewById(R.id.userImage);
            teamName= (TextView)v.findViewById(R.id.teamName);
            points= (TextView)v.findViewById(R.id.points);
            rank= (TextView)v.findViewById(R.id.rank);
            winnerBg=(TextView)v.findViewById(R.id.winnerBg);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_leaderboard, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        if(!list.get(i).getImage().equals(""))
            Picasso.with(context).load(list.get(i).getImage()).resize(100,100).into(holder.userImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii =new Intent(context, LeaderboardByUserActivity.class);
                ii.putExtra("user_id",list.get(i).getUserid());
                ii.putExtra("Series_id",Series_id);
                context.startActivity(ii);
            }
        });

        holder.teamName.setText(list.get(i).getTeam());
        holder.points.setText(list.get(i).getPoints()+" Points");

        if(list.get(i).getRank().equals("1")) {
            holder.winnerBg.setVisibility(View.VISIBLE);
            holder.rank.setText("\uD83D\uDC51"+"\n#"+list.get(i).getRank());
        }else {
            holder.winnerBg.setVisibility(View.GONE);
            holder.rank.setText("#"+list.get(i).getRank());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
