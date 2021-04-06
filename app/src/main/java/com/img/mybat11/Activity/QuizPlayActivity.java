package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.img.mybat11.Adapter.QuizJoinedChallengeListAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.PlayAnswersGetSet;
import com.img.mybat11.GetSet.PlayQuizGetSet;
import com.img.mybat11.GetSet.PlayQuestionsGetSet;
import com.img.mybat11.GetSet.PlayQuizGetSet;
import com.img.mybat11.GetSet.SelectedPlayersGetSet;
import com.img.mybat11.GetSet.captainListGetSet;
import com.img.mybat11.GetSet.msgStatusGetSet;
import com.img.mybat11.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class QuizPlayActivity extends AppCompatActivity {

    TextView timeRemaining,timeRemaining1;
    RadioButton english,hindi;
    TextView startpopuptime;
    String langauge = "0",optlan;



    ArrayList<PlayQuizGetSet> list;
    ArrayList<PlayQuestionsGetSet> questionlist;
    ArrayList<PlayAnswersGetSet> answerlist;

    ConnectionDetector cd;
    UserSessionManager session;
    GlobalVariables gv;
    MainActivity ma;
    int count = 0;
    ProgressBar EnteredPB;
    long stoptime,stoptimefinal,onstarttimer;
    CountDownTimer cTstartpouup,cTQuestiostimen,cTperQuestiontime;
    LinearLayout popupforlangage,questionlayout,popupforend;

    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate=null,endDate = null;
    String TAG="questionplay";

    TextView questionscount,question,answera,answerb,answerc,answerd,progesstext,ttlquestion,attamptquestion,misquestion,completedquizin;
    LinearLayout a,b,c,d;
    CardView gotorank,backhome;

    boolean yes = false, startonl = true, starton = false, endquiz = true, nowin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_play);




        ma = new MainActivity();
        cd= new ConnectionDetector(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();

        EnteredPB=(ProgressBar)findViewById(R.id.EnteredPB);
        progesstext = (TextView)findViewById(R.id.progesstext);
        questionscount = (TextView)findViewById(R.id.questionscount);
        ttlquestion = (TextView)findViewById(R.id.ttlquestion);
        attamptquestion = (TextView)findViewById(R.id.attamptquestion);
        misquestion = (TextView)findViewById(R.id.misquestion);
        completedquizin = (TextView)findViewById(R.id.completedquizin);
        gotorank = (CardView) findViewById(R.id.gotorank);
        backhome = (CardView) findViewById(R.id.backhome);
        question = (TextView)findViewById(R.id.question);
        answera = (TextView)findViewById(R.id.answera);
        answerb = (TextView)findViewById(R.id.answerb);
        answerc = (TextView)findViewById(R.id.answerc);
        answerd = (TextView)findViewById(R.id.answerd);
        a = (LinearLayout)findViewById(R.id.a);
        b = (LinearLayout)findViewById(R.id.b);
        c = (LinearLayout)findViewById(R.id.c);
        d = (LinearLayout)findViewById(R.id.d);
        timeRemaining = (TextView)findViewById(R.id.timeRemaining);
        timeRemaining1 = (TextView)findViewById(R.id.timeRemaining1);
        startpopuptime = (TextView)findViewById(R.id.startpopuptime);
        english = (RadioButton) findViewById(R.id.english);
        hindi = (RadioButton)findViewById(R.id.hindi);
        popupforlangage = (LinearLayout) findViewById(R.id.popupforlangage);
        questionlayout = (LinearLayout) findViewById(R.id.questionlayout);
        popupforend = (LinearLayout) findViewById(R.id.popupforend);


        answerlist= new ArrayList<PlayAnswersGetSet>();


        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        c.add(Calendar.SECOND,- (gv.getQuizquestions()*15)+10);
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
                timeRemaining1.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) +" : " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

//                if (millisUntilFinished > ((gv.getQuizquestions()*15)*1000)){
                    if (startonl) {
                        startonl = false;
                        long ask =  (gv.getQuizquestions()*15-10)*1000;
                        onstarttimer = ask -  millisUntilFinished;
                        Log.i("timer12", String.valueOf(onstarttimer));
                        Log.i("timer13", String.valueOf(ask));
                        Log.i("timer14", String.valueOf(millisUntilFinished));
                        basic(onstarttimer);
                    }

//                }else {
//                    onstarttimer =  10100;
//                }
            }

            public void onFinish() {
                Log.i("timer1","On finish");
                endquiz = false;
                yes = true;
                if (nowin) {
                    closequiz("");
                }

            }
        };
        cT.start();

        Log.i("timer1", String.valueOf(endquiz));


        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QuizPlayActivity.this,HomeActivity.class));
                finish();
            }
        });


    }

    public void basic(final long onstarttimernn) {
        if (endquiz) {

            if (onstarttimernn < 9999) {

                long diffInMs11 = 10000 - onstarttimernn;
                Log.i("timer1", String.valueOf(diffInMs11));
                Log.i("timer111", String.valueOf(onstarttimernn));
                popupforlangage.setVisibility(View.VISIBLE);
                cTstartpouup = new CountDownTimer(diffInMs11, 1000) {

                    public void onTick(long millisUntilFinished) {
                        startpopuptime.setText("Quiz start in " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)) + " sec");

                    }

                    public void onFinish() {
                        Log.i("timer3","On finish");
                        if (cd.isConnectingToInternet()) {
                            questionList();
                        }
                    }
                };
                cTstartpouup.start();
            }else {
                if (cd.isConnectingToInternet()) {
                    questionList();
                }
            }

            gotorank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (yes) {
                        startActivity(new Intent(QuizPlayActivity.this, QuizJoinedLiveChallengesActivity.class));
                        gv.setStatus("live");
                        finish();
                    } else {
                        ma.showErrorToast(QuizPlayActivity.this, "Quiz not completed");
                    }
                }
            });

            english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    langauge = "0";
                    cTstartpouup.cancel();
                    if (cd.isConnectingToInternet()) {
                        questionList();
                    }
                }
            });
            hindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    langauge = "1";
                    cTstartpouup.cancel();
                    if (cd.isConnectingToInternet()) {
                        questionList();
                    }
                }
            });
        }else {
            popupforlangage.setVisibility(View.GONE);
            popupforend.setVisibility(View.GONE);
            questionlayout.setVisibility(View.GONE);
        }
    }
    public void questionList(){
        questionlayout.setVisibility(View.GONE);
        ma.showProgressDialog(QuizPlayActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<PlayQuizGetSet>> call = apiSeitemViewice.Questionslst(String.valueOf(gv.getQuizid()),session.getUserId());
        call.enqueue(new Callback<ArrayList<PlayQuizGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<PlayQuizGetSet>> call, Response<ArrayList<PlayQuizGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();





                    if (langauge.equals("0")) {
                        questionlist = list.get(0).getEnglish();
                    } else {
                        questionlist = list.get(0).getHindi();
                    }


                    popupforlangage.setVisibility(View.GONE);
                    questionlayout.setVisibility(View.VISIBLE);

                    long diffInMscTQuestiostimen = (questionlist.size()*15) * 1000;

                    cTQuestiostimen = new CountDownTimer(diffInMscTQuestiostimen, 1000) {

                        public void onTick(long millisUntilFinished) {
                            timeRemaining.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) +" : " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                            stoptime = millisUntilFinished;

                        }

                        public void onFinish() {
//                            Toast.makeText(QuizPlayActivity.this, "ab band karo", Toast.LENGTH_SHORT).show();
                        }
                    };
                    cTQuestiostimen.start();



                    ttlquestion.setText(String.valueOf(questionlist.size()));

                    if (questionlist.size() > 0){
                        final int[] ii = {0};
                        listdata(ii[0]);
                    }else {
                        questionlayout.setVisibility(View.GONE);
                        closequiz("You already play this quiz");
                    }





                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    ma.dismissProgressDialog();

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizPlayActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            questionList();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                            finish();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<PlayQuizGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }



    public  void listdata(final int ii){

        final int[] pro = {15};

        long diffInMscTperQuestiontime =15*1000;

        cTperQuestiontime = new CountDownTimer(diffInMscTperQuestiontime, 1000) {

            public void onTick(long millisUntilFinished) {
                progesstext.setText( String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)) +" Sec");
                EnteredPB.setProgress(pro[0]);
                pro[0] = pro[0] - 1;
            }

            public void onFinish() {
                if (cd.isConnectingToInternet()){
                    if (ii+1 == questionlist.size()){
                        donehai();
                    }else{
                        listdata(ii + 1);
                    }
                }
            }
        };
        cTperQuestiontime.start();

        questionscount.setText("Question " + (1 + ii) + "/" + questionlist.size());
        question.setText(questionlist.get(ii).getQuestion());
        answera.setText(questionlist.get(ii).getA());
        answerb.setText(questionlist.get(ii).getB());
        answerc.setText(questionlist.get(ii).getC());
        answerd.setText(questionlist.get(ii).getD());

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ii+1 == questionlist.size()){
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue1());
                    answerlist.add(ob);
                    count = count + 1;
                    donehai();
                }else{
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue1());
                    answerlist.add(ob);
                    listdata(ii + 1);
                    count = count + 1;
                }
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ii+1 == questionlist.size()){
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue2());
                    answerlist.add(ob);
                    count = count + 1;
                    donehai();
                }else{
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue2());
                    answerlist.add(ob);
                    listdata(ii + 1);
                    count = count + 1;
                }
            }
        });
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ii+1 == questionlist.size()){
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue3());
                    answerlist.add(ob);
                    count = count + 1;
                    donehai();
                }else{
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue3());
                    answerlist.add(ob);
                    listdata(ii + 1);
                    count = count + 1;
                }
            }
        });
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ii+1 == questionlist.size()){
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue4());
                    answerlist.add(ob);
                    count = count + 1;
                    donehai();
                }else{
                    cTperQuestiontime.cancel();
                    PlayAnswersGetSet ob = new PlayAnswersGetSet();
                    ob.setId(questionlist.get(ii).getId());
                    ob.setAns(questionlist.get(ii).getValue4());
                    answerlist.add(ob);
                    listdata(ii + 1);
                    count = count + 1;
                }
            }
        });
    }

    public void donehai(){
        nowin = false;
        EnteredPB.setVisibility(View.GONE);
        progesstext.setVisibility(View.GONE);
        cTperQuestiontime.cancel();
        stoptimefinal = TimeUnit.MILLISECONDS.toSeconds(questionlist.size()*15 * 1000 - stoptime);
        long stoptimefinal1 = questionlist.size()*15 * 1000 - stoptime;
        Log.i("logtimer", String.valueOf(stoptimefinal));
        Log.i("logtimer", String.valueOf(stoptimefinal1));
        attamptquestion.setText(String.valueOf(count));
        misquestion.setText(String.valueOf(questionlist.size() - count));
        popupforend.setVisibility(View.VISIBLE);
        completedquizin.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(stoptimefinal1) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(stoptimefinal1))) +" : " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(stoptimefinal1) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stoptimefinal1))) + " Sec");
        questionlayout.setVisibility(View.GONE);
        QAnswr();
    }

    public void closequiz(final String playtt){
        final Dialog stated;
        stated= new Dialog(QuizPlayActivity.this);
        stated.setContentView(R.layout.quiz_end_popup);
        TextView quiztimeup,startpopuptime;
        quiztimeup=(TextView) stated.findViewById(R.id.quiztimeup);
        startpopuptime=(TextView) stated.findViewById(R.id.startpopuptime);
        if (!playtt.equals("")){
            startpopuptime.setText(playtt);
        }
        quiztimeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizPlayActivity.this, QuizJoinedLiveChallengesActivity.class));
                gv.setStatus("live");
                finish();
            }
        });
        stated.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //        Window window = stated.getWindow();
        //        window.setGravity(Gravity.TOP);
        stated.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stated.setCanceledOnTouchOutside(false);
        stated.setCancelable(false);
        stated.show();
    }


    public void QAnswr(){

        if (langauge.equals("0")){
            optlan = "english";
        }else {
            optlan = "hindi";
        }


        Log.e("listsize","rgera"+answerlist.size());
        Log.e("firstid","rgera"+answerlist.get(0).getId());


        JSONArray jsArray = new JSONArray();
        for(int i = 0; i < answerlist.size(); i++) {
            JSONObject job = new JSONObject();
            try {
                job.put("id", answerlist.get(i).getId());
                job.put("ans", answerlist.get(i).getAns());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsArray.put(job);
        }

        Log.i("jsArray",jsArray.toString());
        String toSend = jsArray.toString();
        Log.e("heell","rgera"+toSend);
        Log.e("heell","rgera"+gv.getQuizid());
        Log.e("heell","rgera"+stoptimefinal);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<msgStatusGetSet>> call;
        call = apiSeitemViewice.QuestionAnswer(session.getUserId(),toSend,String.valueOf(gv.getQuizid()),String.valueOf(stoptimefinal),optlan);
        call.enqueue(new Callback<ArrayList<msgStatusGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<msgStatusGetSet>> call, Response<ArrayList<msgStatusGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    ArrayList<msgStatusGetSet>list = response.body();
                }else {
                    AlertDialog.Builder d = new AlertDialog.Builder(QuizPlayActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            QAnswr();
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
            public void onFailure(Call<ArrayList<msgStatusGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.e(TAG, ""+t);
            }
        });
    }

    @Override
    public void onBackPressed() {
    }


}
