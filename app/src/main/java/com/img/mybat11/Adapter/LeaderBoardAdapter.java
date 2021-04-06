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

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder> {

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

    public LeaderBoardAdapter(Context context, ArrayList<teamsGetSet> list, String jid, int challenge_id){
        this.context = context;
        this.list = list;
        this.jid = jid;
        this.challenge_id = challenge_id;

        session = new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();
        ma = new MainActivity();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView teamName,teamNumber;
        CircleImageView userImage;
        ImageView switchTeam;

        public MyViewHolder(View v) {
            super(v);
            userImage=(CircleImageView)v.findViewById(R.id.userImage);
            teamName= (TextView)v.findViewById(R.id.teamName);
            teamNumber= (TextView)v.findViewById(R.id.teamNumber);
            switchTeam = v.findViewById(R.id.switchTeam);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard, parent, false);

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
                    ViewTeam(String.valueOf(list.get(i).getTeamid()),String.valueOf(list.get(i).getTeamnumber()));
                }else{
                    ma.showToast(context,"You can preview other teams only after deadline");
                }
            }
        });

        holder.switchTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size = 0;

                for(teamsGetSet zz:list){
                    if(session.getTeamName().equalsIgnoreCase(zz.getTeam()))
                        size++;
                }
                findJoinedTeam(String.valueOf(list.get(i).getJid()),challenge_id);
            }
        });

        if(session.getTeamName().equalsIgnoreCase(list.get(i).getTeam())){
            holder.switchTeam.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#443759a5")));
        }else{
            holder.switchTeam.setVisibility(View.GONE);
            if(i%2 ==0){
                holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
            }else{
                holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f6f6f6")));
            }
        }

        holder.teamName.setText(list.get(i).getTeam());
        holder.teamNumber.setText("Team "+list.get(i).getTeamnumber());
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

                    Intent ii = new Intent(context, TeamPreviewActivity.class);
                    ii.putExtra("team_name","Team "+teamnumber);
                    gv.setCaptainList(captainList);
                    ii.putExtra("teamNumber",teamnumber);
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

    public void findJoinedTeam(final String joinid, final int challenge_id){
        ma.showProgressDialog(context);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.getMyTeams2(session.getUserId(),gv.getMatchKey(),challenge_id);
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    ma.dismissProgressDialog();
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    selectedteamList = response.body();

                    Log.i("Size",String.valueOf(selectedteamList.size()));

                    if(selectedteamList.size()>0) {
                        gv.setSelectedTeamList(selectedteamList);
                        Intent intent = new Intent(context, ChooseTeamActivity.class);
                        intent.putExtra("type","switch");
                        intent.putExtra("challengeId",challenge_id);
                        intent.putExtra("size",size);
                        intent.putExtra("joinid",joinid);
                        context.startActivity(intent);
                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            findJoinedTeam(joinid,challenge_id);
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
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

}
