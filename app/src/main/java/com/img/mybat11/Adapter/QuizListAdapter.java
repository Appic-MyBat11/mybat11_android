package com.img.mybat11.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.img.mybat11.Activity.ChallengesActivity;
import com.img.mybat11.Activity.HomeActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.MyMatchesCricketActivity;
import com.img.mybat11.Activity.QuizChallengesActivity;
import com.img.mybat11.Activity.QuizPlayActivity;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.QuizListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class   QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.MyViewHolder> {

    Context context;
    ArrayList<QuizListGetSet> quizList;
    GlobalVariables gv;
    String TAG="Match list";
    UserSessionManager sessionManager;
    ConnectionDetector cd;
    MainActivity ma;


    public QuizListAdapter(Context context, ArrayList<QuizListGetSet> quizList){
        this.context= context;
        this.quizList= quizList;
        gv = (GlobalVariables)context.getApplicationContext();
        sessionManager= new UserSessionManager(context);
        cd = new ConnectionDetector(context);
        ma = new MainActivity();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView maindate,date,timer,noofquestion;
        ImageView  imageback;

        public MyViewHolder(View v) {
            super(v);

            maindate= (TextView)v.findViewById(R.id.maindate);
            date= (TextView)v.findViewById(R.id.date);
            timer= (TextView)v.findViewById(R.id.timer);
            noofquestion= (TextView)v.findViewById(R.id.noofquestion);
            imageback= (ImageView) v.findViewById(R.id.imageback);
        }
    }

    @Override
    public QuizListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quiz_list, parent, false);

        return new QuizListAdapter.MyViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        return quizList.size();
    }


    @Override
    public void onBindViewHolder(final QuizListAdapter.MyViewHolder holder, final int i) {


        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;



//        GradientDrawable drawable1 = (GradientDrawable) holder.team1color.getBackground();
//        drawable1.setColor(Color.parseColor(quizList.get(i).getTeam1color()));
//        GradientDrawable drawable2 = (GradientDrawable) holder.team2color.getBackground();
//        drawable2.setColor(Color.parseColor(quizList.get(i).getTeam2color()));

        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(quizList.get(i).getStart_date()));
            holder.maindate.setText(newDate + " Quiz");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.date.setText("Quiz Date: " + quizList.get(i).getStart_date());
        holder.noofquestion.setText(quizList.get(i).getQuestion()+" Questions");

        Picasso.with(context).load(quizList.get(i).getImage()).into(holder.imageback);
//        holder.imageback.setBackground(Drawable.createFromPath(quizList.get(i).getImage()));
//        BitmapDrawable drawableBitmap=new BitmapDrawable(getBitmap(quizList.get(i).getImage()));
//        holder.imageback.setBackgroundDrawable(drawableBitmap);

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate = quizList.get(i).getStart_date();

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

                holder.timer.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + "h : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + "m : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "s"));


            }

            public void onFinish() {

                if (quizList.get(i).getJoined() > 0) {
                    final Dialog stated;
                    stated = new Dialog(context);
                    stated.setContentView(R.layout.quiz_end_popup);
                    TextView quiztimeup, startpopuptime;
                    quiztimeup = (TextView) stated.findViewById(R.id.quiztimeup);
                    startpopuptime = (TextView) stated.findViewById(R.id.startpopuptime);
                    quiztimeup.setText("PLAY");
                    startpopuptime.setText("Quiz Start");
                    quiztimeup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("QuizId", String.valueOf(quizList.get(i).getId()));
                            gv.setQuizid(quizList.get(i).getId());
                            gv.setMatchTime(quizList.get(i).getStart_date());
                            gv.setQuizquestions(quizList.get(i).getQuestion());
                            Intent ii = new Intent(context, QuizPlayActivity.class);
                            context.startActivity(ii);
                            ((HomeActivity) context).finish();
                        }
                    });
                    stated.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    //        Window window = stated.getWindow();
                    //        window.setGravity(Gravity.TOP);
                    stated.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    stated.setCanceledOnTouchOutside(false);
                    stated.setCancelable(false);
                    if(!((HomeActivity) context).isFinishing())
                    {
                        stated.show();
                    }
                }
            }
        };
        cT.start();



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Log.i("QuizId", String.valueOf(quizList.get(i).getId()));
                    gv.setQuizid(quizList.get(i).getId());
                    gv.setMatchTime(quizList.get(i).getStart_date());
                    gv.setQuizquestions(quizList.get(i).getQuestion());
                    Intent ii = new Intent(context, QuizChallengesActivity.class);
                    context.startActivity(ii);

            }
        });



    }

//    private String getBitmap(String url) {
//        try {
//            Bitmap bitmap=null;
//            URL imageUrl = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
//            conn.setConnectTimeout(30000);
//            conn.setReadTimeout(30000);
//            conn.setInstanceFollowRedirects(true);
//            InputStream is=conn.getInputStream();
//            OutputStream os = new FileOutputStream(f);
//            Utils.CopyStream(is, os);
//            os.close();
//            bitmap = decodeFile(f);
//            return bitmap;
//        } catch (Exception ex){
//            ex.printStackTrace();
//            return null;
//        }
//    }
}
