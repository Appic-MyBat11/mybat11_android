package com.img.mybat11.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class JoinedMatchesViewPageAdapter extends PagerAdapter {


    LayoutInflater inflater;
    Context context;
    ArrayList<ContestMatchListGetSet> matchList;
    GlobalVariables gv;
    MainActivity ma;

    public JoinedMatchesViewPageAdapter(Context context, ArrayList<ContestMatchListGetSet> matchList )
    {
        this.context=context;
        this.matchList = matchList;
        ma = new MainActivity();
    }

    @Override
    public int getCount() {
        if (matchList.size()>5){
            return 5;
        }else {
            return matchList.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int i) {

        final View v;
        LinearLayout tiemrLL,afterwinning;
        final TextView seriesName,timeLeft,team1,team2,vs,team1color,team2color,status,numJoined,joinTxt,lefttext,righttext,afterwinningtext;
        ImageView logo1,logo2;
        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = inflater.inflate(R.layout.match_list,null);

        gv= (GlobalVariables)context.getApplicationContext();

        afterwinning= (LinearLayout)v.findViewById(R.id.afterwinning);
        afterwinningtext= (TextView)v.findViewById(R.id.afterwinningtext);
        tiemrLL= (LinearLayout)v.findViewById(R.id.timerLL);
        status= (TextView)v.findViewById(R.id.status);
        lefttext= (TextView)v.findViewById(R.id.lefttext);
        righttext= (TextView)v.findViewById(R.id.righttext);
        team1= (TextView)v.findViewById(R.id.team1);
        team2= (TextView)v.findViewById(R.id.team2);
        team1color= (TextView)v.findViewById(R.id.team1color);
        team2color= (TextView)v.findViewById(R.id.team2color);
        vs= (TextView)v.findViewById(R.id.vs);
        seriesName= (TextView)v.findViewById(R.id.seriesName);
        numJoined=(TextView)v.findViewById(R.id.numJoined);
        joinTxt=(TextView)v.findViewById(R.id.joinTxt);
        timeLeft=(TextView)v.findViewById(R.id.timeLeft);
        logo1=(ImageView) v.findViewById(R.id.logo1);
        logo2=(ImageView) v.findViewById(R.id.logo2);

        team1.setText(matchList.get(i).getTeam1display().toUpperCase());
        team2.setText(matchList.get(i).getTeam2display().toUpperCase());

        GradientDrawable drawable1 = (GradientDrawable) team1color.getBackground();
        drawable1.setColor(Color.parseColor(matchList.get(i).getTeam1color()));

        GradientDrawable drawable2 = (GradientDrawable) team2color.getBackground();
        drawable2.setColor(Color.parseColor(matchList.get(i).getTeam2color()));

        numJoined.setText(String.valueOf(matchList.get(i).getTotaljoinedcontest()));

        String type = "0";

        if (matchList.get(i).getStatus().equals("opened"))
           type="0";
        else if (matchList.get(i).getStatus().equals("closed") && (matchList.get(i).getFinal_status().equals("pending") || matchList.get(i).getFinal_status().equals("IsReviewed")))
            type="1";
        else if (matchList.get(i).getStatus().equals("closed") && (matchList.get(i).getFinal_status().equals("winnerdeclared") || matchList.get(i).getFinal_status().equals("IsAbandoned") || matchList.get(i).getFinal_status().equals("IsCanceled")))
           type="2";


        if(type.equals("0")){
            vs.setVisibility(View.GONE);
            tiemrLL.setVisibility(View.VISIBLE);
        }else{
            vs.setVisibility(View.VISIBLE);
            tiemrLL.setVisibility(View.GONE);
        }

        if(type.equals("1")) {
            status.setVisibility(View.VISIBLE);
            if (matchList.get(i).getFinal_status().equals("IsReviewed")) {
                status.setText("Under Review");
            } else if (matchList.get(i).getFinal_status().equals("pending")){
                status.setText("In progress");
            } else {
                status.setText(matchList.get(i).getFinal_status());
            }
        }else if(type.equals("2")){
            status.setVisibility(View.VISIBLE);
            if (!matchList.get(i).getTotalwinningamount().equals("0")) {
                afterwinning.setVisibility(View.VISIBLE);
                afterwinningtext.setText("You won ₹" + matchList.get(i).getTotalwinningamount());
            }
            if(matchList.get(i).getFinal_status().equals("winnerdeclared")) {
                status.setText("Completed");
            }else if(matchList.get(i).getFinal_status().equals("IsAbandoned")) {
                status.setText("Abandoned");
            }else if(matchList.get(i).getFinal_status().equals("IsCanceled")) {
                status.setText("Cancelled");
            } else {
                status.setText(matchList.get(i).getFinal_status());
            }
        }

        seriesName.setText(matchList.get(i).getSeries_name());
        lefttext.setBackgroundColor(Color.parseColor(matchList.get(i).getTeam1color()));
        righttext.setBackgroundColor(Color.parseColor(matchList.get(i).getTeam2color()));


        Picasso.with(context).load(matchList.get(i).getTeam1logo()).into(logo1);
        Picasso.with(context).load(matchList.get(i).getTeam2logo()).into(logo2);

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate =matchList.get(i).getStart_date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
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

                timeLeft.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));

            }

            public void onFinish() {
                matchList.get(i).setSelected(false);
            }
        };
        cT.start();

        final String finalType = type;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setMatchKey(matchList.get(i).getMatchkey());
                gv.setTeam1(matchList.get(i).getTeam1display().toUpperCase());
                gv.setTeam2(matchList.get(i).getTeam2display().toUpperCase());
                gv.setTeam1Image(matchList.get(i).getTeam1logo());
                gv.setTeam2image(matchList.get(i).getTeam2logo());
                gv.setMatchTime(matchList.get(i).getStart_date());
                gv.setSeries(matchList.get(i).getSeries_name());
                if(finalType.equals("0")){
                    if (matchList.get(i).isSelected()) {
                        Intent ii = new Intent(context, JoinedChallengesActivity.class);
                        context.startActivity(ii);
                    }else {
                        ma.showToast(context,"Match Started!");
                    }
                }else if(finalType.equals("1")){
                    if (matchList.get(i).getFinal_status().equals("IsReviewed")) {
                        gv.setStatus("Under Review");
                    } else if (matchList.get(i).getFinal_status().equals("pending")){
                        gv.setStatus("In Progress");
                    }
                    context.startActivity(new Intent(context, LiveChallengeActivity.class));
                }else if(finalType.equals("2")){
                    context.startActivity(new Intent(context, ResultChallengeActivity.class));
                }
            }
        });


        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);
    }
}