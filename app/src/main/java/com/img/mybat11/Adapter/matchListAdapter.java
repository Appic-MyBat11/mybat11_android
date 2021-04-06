package com.img.mybat11.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.img.mybat11.Activity.ChallengesActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.MatchListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class matchListAdapter extends RecyclerView.Adapter<matchListAdapter.MyViewHolder> {

    Context context;
    String type;
    String stype;
    ArrayList<MatchListGetSet> matchList;
    GlobalVariables gv;
    String TAG="Match list";
    UserSessionManager sessionManager;
    ConnectionDetector cd;
    MainActivity ma;


    public matchListAdapter(Context context, String type, ArrayList<MatchListGetSet> matchList,String stype){
        this.context= context;
        this.type= type;
        this.stype= stype;
        this.matchList= matchList;
        gv = (GlobalVariables)context.getApplicationContext();
        sessionManager= new UserSessionManager(context);
        cd = new ConnectionDetector(context);
        ma = new MainActivity();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout tiemrLL;
        final TextView seriesName,timeLeft,team1,team2,vs,team1color,team2color,numJoined,joinTxt,lefttext,righttext;
        ImageView logo1,logo2;
        LinearLayout mainli;
        ImageView lineupStatus;
        TextView enabledBG;

        public MyViewHolder(View v) {
            super(v);

            lineupStatus= (ImageView) v.findViewById(R.id.lineupStatus);
            tiemrLL= (LinearLayout)v.findViewById(R.id.timerLL);
//        status= (TextView)v.findViewById(R.id.status);
            lefttext= (TextView)v.findViewById(R.id.lefttext);
            righttext= (TextView)v.findViewById(R.id.righttext);
            team1= (TextView)v.findViewById(R.id.team1);
            team2= (TextView)v.findViewById(R.id.team2);
            team1color= (TextView)v.findViewById(R.id.team1color);
            team2color= (TextView)v.findViewById(R.id.team2color);
            vs= (TextView)v.findViewById(R.id.vs);
            mainli= (LinearLayout) v.findViewById(R.id.mainli);
            seriesName= (TextView)v.findViewById(R.id.seriesName);
            timeLeft=(TextView)v.findViewById(R.id.timeLeft);
            numJoined=(TextView)v.findViewById(R.id.numJoined);
            joinTxt=(TextView)v.findViewById(R.id.joinTxt);
            enabledBG=(TextView)v.findViewById(R.id.enabledBG);
            logo1=(ImageView) v.findViewById(R.id.logo1);
            logo2=(ImageView) v.findViewById(R.id.logo2);
        }
    }

    @Override
    public matchListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.match_list, parent, false);

        return new matchListAdapter.MyViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        return matchList.size();
    }


    @Override
    public void onBindViewHolder(final matchListAdapter.MyViewHolder holder, final int i) {

        holder.numJoined.setText("");
        holder.joinTxt.setText("");

        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;



//        GradientDrawable drawable1 = (GradientDrawable) holder.team1color.getBackground();
//        drawable1.setColor(Color.parseColor(matchList.get(i).getTeam1color()));
//        GradientDrawable drawable2 = (GradientDrawable) holder.team2color.getBackground();
//        drawable2.setColor(Color.parseColor(matchList.get(i).getTeam2color()));

        holder.team1color.setBackgroundColor(Color.parseColor(matchList.get(i).getTeam1color()));
        holder.team2color.setBackgroundColor(Color.parseColor(matchList.get(i).getTeam2color()));

        holder.lefttext.setBackgroundColor(Color.parseColor(matchList.get(i).getTeam1color()));
        holder.righttext.setBackgroundColor(Color.parseColor(matchList.get(i).getTeam2color()));


        holder.seriesName.setText(matchList.get(i).getSeriesname());

        Picasso.with(context).load(matchList.get(i).getTeam1logo()).into(holder.logo1);
        Picasso.with(context).load(matchList.get(i).getTeam2logo()).into(holder.logo2);

        holder.team1.setText(matchList.get(i).getTeam1name());
        holder.team2.setText(matchList.get(i).getTeam2name());
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate = matchList.get(i).getTime_start();
        Log.i("matchtime", matchList.get(i).getTime_start());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
                startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long diffInMs = endDate.getTime() - startDate.getTime();

        final long hours1 = 1 * 60 * 60 * 1000;
        final long hours4 = 4 * 60 * 60 * 1000;
        final long hours48 = 48 * 60 * 60 * 1000;


        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {

                holder.timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));


            }

            public void onFinish() {
                matchList.get(i).setSelected(false);
            }
        };
        cT.start();

        if (stype.equals("0")) {
            holder.vs.setVisibility(View.GONE);
            holder.tiemrLL.setVisibility(View.VISIBLE);
        }else {
            holder.vs.setVisibility(View.VISIBLE);
            holder.tiemrLL.setVisibility(View.GONE);
        }
        if (matchList.get(i).getLaunch_status().equals("pending")) {
            holder.enabledBG.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ma.showErrorToast(context,"Contest for this match will open soon. stay tuned!");
                }
            });
        } else {
            holder.enabledBG.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (matchList.get(i).isSelected()) {
                        Log.i("matchkey", matchList.get(i).getMatchkey());
                        gv.setMatchKey(matchList.get(i).getMatchkey());
                        gv.setTeam1(matchList.get(i).getTeam1name().toUpperCase());
                        gv.setTeam2(matchList.get(i).getTeam2name().toUpperCase());
//                if(type.equals("0")){
                        gv.setMatchTime(matchList.get(i).getTime_start());
                        gv.setSeries(matchList.get(i).getSeries());
                        gv.setTeam1Image(matchList.get(i).getTeam1logo());
                        gv.setTeam2image(matchList.get(i).getTeam2logo());
                        Intent ii = new Intent(context, ChallengesActivity.class);
                        ii.putExtra("name", matchList.get(i).getName());
                        context.startActivity(ii);
//                }
                    }else {
                        ma.showToast(context,"Match Started!");
                    }
                }
            });
        }

        if (stype.equals("0")) {
            if (matchList.get(i).getPlaying11_status().equals("1")) {
                holder.lineupStatus.setVisibility(View.VISIBLE);
                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                anim.setDuration(500); //You can manage the blinking time with this parameter
                anim.setStartOffset(20);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                holder.lineupStatus.startAnimation(anim);
            }
        }


    }
}
