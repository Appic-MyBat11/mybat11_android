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
import android.widget.TextView;

import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.PreviewActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LeaderBoardAdapter2 extends RecyclerView.Adapter<LeaderBoardAdapter2.MyViewHolder>{

    Context context;
    ArrayList<JoinTeamGetSet> list;
    String TAG="Edit team";
    UserSessionManager session;
    ArrayList<SelectedPlayersGetSet> playerList;
    MainActivity ma;
    GlobalVariables gv;

    public LeaderBoardAdapter2(Context context, ArrayList<JoinTeamGetSet> list){
        this.context = context;
        this.list = list;

        session = new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();
        ma = new MainActivity();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView teamName,points,winning,t1,t2,rank,trophy;
        CircleImageView img;

        public MyViewHolder(View v) {
            super(v);
            teamName= (TextView)v.findViewById(R.id.teamName);
            points= (TextView)v.findViewById(R.id.points);
            winning= (TextView)v.findViewById(R.id.winning);
            rank= (TextView)v.findViewById(R.id.rank);
            trophy= (TextView)v.findViewById(R.id.trophy);

            t1=(TextView)v.findViewById(R.id.t1);
            t2=(TextView)v.findViewById(R.id.t2);

            img =(CircleImageView) v.findViewById(R.id.img);
        }
    }

    @Override
    public LeaderBoardAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard2, parent, false);

        return new LeaderBoardAdapter2.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LeaderBoardAdapter2.MyViewHolder holder, final int i) {
        NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)nf2).applyPattern("##,##,###");

        holder.teamName.setText(list.get(i).getTeamname()+" (T"+list.get(i).getTeamnumber()+")");
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
                ViewTeam(String.valueOf(list.get(i).getTeamid()),String.valueOf(list.get(i).getTeamnumber()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void ViewTeam(final String teamid, final String teamnumber){
        ma.showProgressDialog(context);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<SelectedPlayersGetSet>> call = apiSeitemViewice.viewteam(session.getUserId(),gv.getMatchKey(),teamid,teamnumber);
        call.enqueue(new Callback<ArrayList<SelectedPlayersGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<SelectedPlayersGetSet>> call, Response<ArrayList<SelectedPlayersGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    playerList = response.body();
                    ma.dismissProgressDialog();

                    ArrayList<captainListGetSet>captainList= new ArrayList<>();
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
                        ob.setPoints(zz.getPoints());
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
                        ob.setPlayingstatus(zz.getPlayingstatus());

                        captainList.add(ob);
                    }

                    Intent ii= new Intent(context, PreviewActivity.class);
                    ii.putExtra("team_name","Team "+teamnumber);
                    ii.putExtra("TeamID",Integer.parseInt(teamid));
                    gv.setCaptainList(captainList);
                    context.startActivity(ii);

                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewTeam(teamid,teamnumber);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<SelectedPlayersGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

}
