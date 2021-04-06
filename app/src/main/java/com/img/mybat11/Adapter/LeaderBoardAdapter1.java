package com.img.mybat11.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderBoardAdapter1 extends RecyclerView.Adapter<LeaderBoardAdapter1.MyViewHolder>{

    Context context;
    ArrayList<JoinTeamGetSet> list;
    String TAG="Edit team";
    ArrayList<SelectedPlayersGetSet> playerList;
    MainActivity ma;
    UserSessionManager session;
    GlobalVariables gv;
    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate=null,endDate = null;

    boolean download_pdf = false;

    public LeaderBoardAdapter1(Context context, ArrayList<JoinTeamGetSet> list){
        this.context = context;
        this.list = list;

        session = new UserSessionManager(context);
        ma = new MainActivity();
        gv= (GlobalVariables)context.getApplicationContext();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView teamName,teamNumber,rank,points,winning;
        ImageView indicator;
        CircleImageView userImage;

        public MyViewHolder(View v) {
            super(v);
            teamName= (TextView)v.findViewById(R.id.teamName);
            teamNumber= (TextView)v.findViewById(R.id.teamNumber);
            points= (TextView)v.findViewById(R.id.points);
            winning= (TextView)v.findViewById(R.id.winning);
            rank= (TextView)v.findViewById(R.id.rank);
            userImage = (CircleImageView)v.findViewById(R.id.userImage);

            indicator =(ImageView)v.findViewById(R.id.indicator);

        }
    }

    @Override
    public LeaderBoardAdapter1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard1, parent, false);

        return new LeaderBoardAdapter1.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LeaderBoardAdapter1.MyViewHolder holder, final int i) {

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        c.add(Calendar.MINUTE,-5);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate =gv.getMatchTime();

        Log.i("timeapna1", String.valueOf(sDate));
        Log.i("timeapna2", String.valueOf(eDate));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long diffInMs = endDate.getTime() - startDate.getTime();

        final long hours1 = 1*60*60*1000;
        final long hours4 = 4*60*60*1000;
        final long hours48 = 48*60*60*1000;


        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Log.i("Where","On finish");
                download_pdf = true;
            }
        };
        cT.start();




        if(list.get(i).getArrowname().equals("up-arrow"))
            holder.indicator.setImageResource(R.drawable.up_arrow);
        else if(list.get(i).getArrowname().equals("equal-arrow"))
            holder.indicator.setImageResource(R.drawable.up_down_arrow);
        else
            holder.indicator.setImageResource(R.drawable.down_arrow);

        holder.teamName.setText(list.get(i).getTeamname()+"(T"+list.get(i).getTeamnumber()+")");
        holder.teamNumber.setText(list.get(i).getPoints()+" Points");
        holder.rank.setText(String.valueOf(list.get(i).getGetcurrentrank()));

        if(list.get(i).getImage() != null && !list.get(i).getImage().equals("")){
            Picasso.with(context).load(list.get(i).getImage()).into(holder.userImage);
        }

        if(list.get(i).getWiningamount() != ""){
            if (session.getTeamName().equals(list.get(i).getTeamname()))
                holder.winning.setText("You Won ₹" + list.get(i).getWiningamount());
            else
                holder.winning.setText("Won ₹" + list.get(i).getWiningamount());
            holder.winning.setVisibility(View.VISIBLE);
        }else
            holder.winning.setVisibility(View.GONE);

        if(session.getTeamName().equalsIgnoreCase(list.get(i).getTeamname())){
            holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#443759a5")));
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
                if (download_pdf){
                    ViewTeam(String.valueOf(list.get(i).getTeamid()),String.valueOf(list.get(i).getTeamnumber()));
                }else {
                    ma.showToast(context, "Server Busy"); //To notify the Client that the file is being downloaded
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void ViewTeam(final String teamid, final String teamnumber){
        ma.showProgressDialog(context);

        Log.i("url","viewteam?matchkey="+gv.getMatchKey()+"&teamid="+teamid+"&teamnumber="+teamnumber);

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
