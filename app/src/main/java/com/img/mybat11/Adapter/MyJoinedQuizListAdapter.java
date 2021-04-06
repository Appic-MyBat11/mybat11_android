package com.img.mybat11.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.img.mybat11.Activity.HomeActivity;
import com.img.mybat11.Activity.JoinedChallengesActivity;
import com.img.mybat11.Activity.LiveChallengeActivity;
import com.img.mybat11.Activity.MyMatchesCricketActivity;
import com.img.mybat11.Activity.QuizChallengesActivity;
import com.img.mybat11.Activity.QuizJoinedLiveChallengesActivity;
import com.img.mybat11.Activity.QuizJoinedResultChallengesActivity;
import com.img.mybat11.Activity.QuizJoinedUpcomingChallengesActivity;
import com.img.mybat11.Activity.QuizPlayActivity;
import com.img.mybat11.Activity.ResultChallengeActivity;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.GetSet.MyQuizListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyJoinedQuizListAdapter extends BaseAdapter{

    Context context;
    String type;
    ArrayList<MyQuizListGetSet> List;
    GlobalVariables gv;
    String TAG="Match list";

    public MyJoinedQuizListAdapter(Context context, String type, ArrayList<MyQuizListGetSet> List){
        this.context= context;
        this.type= type;
        this.List= List;
    }

    @Override
    public int getCount() {
        return List.size();
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
    public View getView(final int i, final View view, ViewGroup viewGroup) {

        final View v;

        final TextView maindate,date,timer,noofquestion;
        ImageView imageback;
        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;

        LayoutInflater layoutInflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= layoutInflater.inflate(R.layout.quiz_list,null);

        gv= (GlobalVariables)context.getApplicationContext();




        maindate= (TextView)v.findViewById(R.id.maindate);
        date= (TextView)v.findViewById(R.id.date);
        timer= (TextView)v.findViewById(R.id.timer);
        noofquestion= (TextView)v.findViewById(R.id.noofquestion);
        imageback= (ImageView) v.findViewById(R.id.imageback);

        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(List.get(i).getStart_date()));
            maindate.setText(newDate + " Quiz");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date.setText("Quiz Date: " + List.get(i).getStart_date());
        noofquestion.setText(List.get(i).getQuestion()+" Questions");
        Picasso.with(context).load(List.get(i).getImage()).into(imageback);

        if (type.equals("0")) {

            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            int hour = c.get(Calendar.HOUR_OF_DAY);
            final int minute = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);
            int mYear1 = c.get(Calendar.YEAR);
            int mMonth1 = c.get(Calendar.MONTH);
            int mDay1 = c.get(Calendar.DAY_OF_MONTH);

            sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
            eDate = List.get(i).getStart_date();

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

                    timer.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));


                }

                public void onFinish() {
                    final Dialog stated;
                    stated= new Dialog(context);
                    stated.setContentView(R.layout.quiz_end_popup);
                    TextView quiztimeup,startpopuptime;
                    quiztimeup=(TextView) stated.findViewById(R.id.quiztimeup);
                    startpopuptime=(TextView) stated.findViewById(R.id.startpopuptime);
                    quiztimeup.setText("PLAY");
                    startpopuptime.setText("Quiz Start");
                    quiztimeup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("QuizId", String.valueOf(List.get(i).getQuiz_id()));
                            gv.setQuizid(List.get(i).getQuiz_id());
                            gv.setMatchTime(List.get(i).getStart_date());
                            gv.setQuizquestions(List.get(i).getQuestion());
                            Intent ii= new Intent(context, QuizPlayActivity.class);
                            context.startActivity(ii);
                            ((MyMatchesCricketActivity)context).finish();
                        }
                    });
                    stated.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //        Window window = stated.getWindow();
                    //        window.setGravity(Gravity.TOP);
                    stated.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    stated.setCanceledOnTouchOutside(false);
                    stated.setCancelable(false);
                    if(!((MyMatchesCricketActivity) context).isFinishing())
                    {
                        stated.show();
                    }
//                quizList.get(i).setSelected(false);
                }
            };
            cT.start();
        }else if(type.equals("1")){
            timer.setText(List.get(i).getFinal_status());
        }else if(type.equals("2")){
            if (List.get(i).getFinal_status().equals("winnerdeclared")){
                timer.setText("Completed");
            }else {
                timer.setText(List.get(i).getFinal_status());
            }
        }else{
            timer.setText(List.get(i).getFinal_status());
        }





        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(context, QuizChallengesActivity.class);
                context.startActivity(ii);

            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("QuizId", String.valueOf(List.get(i).getQuiz_id()));
                gv.setQuizid(List.get(i).getQuiz_id());
                gv.setMatchTime(List.get(i).getStart_date());
                gv.setQuizquestions(List.get(i).getQuestion());
                if(type.equals("0")){
                    Intent ii= new Intent(context, QuizJoinedUpcomingChallengesActivity.class);
                    context.startActivity(ii);
                }else if(type.equals("1")){
                    Intent ii= new Intent(context, QuizPlayActivity.class);
                    context.startActivity(ii);
                }else if(type.equals("2")){
                    gv.setStatus("complete");
                    context.startActivity(new Intent(context, QuizJoinedLiveChallengesActivity.class));
                }
            }
        });


        return v;
    }

}

