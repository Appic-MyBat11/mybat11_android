package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.QuizFinalQuestionsAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.FinalQuestionGetSet;
import com.img.mybat11.GetSet.FinalQuizResultGetSet;
import com.img.mybat11.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizResultQuestionsActivity extends AppCompatActivity {
    TextView title;
    ImageView back;
    public static Activity fa;
    RequestQueue requestQueue;
    String userid,earnpoints;
    RecyclerView challengeList;
    ArrayList<FinalQuizResultGetSet> list;
    ArrayList<FinalQuestionGetSet> qlist;
    String TAG="Challenge list";
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;
    TextView noChallenge;
    MainActivity ma;
    NestedScrollView nestedscroll;
    TextView match_name,timeRemaining,timeRemaining1,ttlquestion,attamptquestion,rightanswers,misquestion,ttlpnts,yourscore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_questions);



        ma = new MainActivity();
        fa = this;
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(QuizResultQuestionsActivity.this);
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        title =(TextView)findViewById(R.id.title);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userid = getIntent().getExtras().getString("userid");
        earnpoints = getIntent().getExtras().getString("earnpoints");

        nestedscroll=(NestedScrollView) findViewById(R.id.nestedscroll);
        nestedscroll.setNestedScrollingEnabled(false);
        yourscore=(TextView)findViewById(R.id.yourscore);
        timeRemaining1=(TextView)findViewById(R.id.timeRemaining1);
        ttlquestion=(TextView)findViewById(R.id.ttlquestion);
        attamptquestion=(TextView)findViewById(R.id.attamptquestion);
        rightanswers=(TextView)findViewById(R.id.rightanswers);
        misquestion=(TextView)findViewById(R.id.misquestion);
        ttlpnts=(TextView)findViewById(R.id.ttlpnts);
        match_name=(TextView)findViewById(R.id.match_name);
        timeRemaining=(TextView)findViewById(R.id.timeRemaining);
        noChallenge=(TextView)findViewById(R.id.noChallenge);



        SimpleDateFormat d1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat d2 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        try {
            String newDate = d2.format(d1.parse(gv.getMatchTime()));
            title.setText(newDate + " Quiz");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeRemaining.setText(gv.getQuizquestions()+" Questions");
        match_name.setText("Finished");

        challengeList= (RecyclerView) findViewById(R.id.challengeList);
        challengeList= (RecyclerView) findViewById(R.id.challengeList);
        challengeList.setLayoutManager(new LinearLayoutManager(QuizResultQuestionsActivity.this));

        if(cd.isConnectingToInternet()){
            ma.showProgressDialog(QuizResultQuestionsActivity.this);
            finalq();
        }

    }

    public void finalq(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<FinalQuizResultGetSet>> call = apiSeitemViewice.quizFinalResult(session.getUserId(),userid, String.valueOf(gv.getQuizid()));
        call.enqueue(new Callback<ArrayList<FinalQuizResultGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<FinalQuizResultGetSet>> call, Response<ArrayList<FinalQuizResultGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();



                    ttlquestion.setText(list.get(0).getTotal_question());
                    attamptquestion.setText(list.get(0).getAttemp_question());
                    rightanswers.setText(list.get(0).getRight_answer());
                    misquestion.setText(list.get(0).getMissed_question());
                    ttlpnts.setText(list.get(0).getTotal_point());
                    yourscore.setText(list.get(0).getTotal_point());


                    long millisUntilFinished = Long.parseLong(list.get(0).getTime()) * 1000;

                    timeRemaining1.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) +" : " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                    qlist = list.get(0).getLanguage();
                    if (list.size() >0) {
                        challengeList.setAdapter(new QuizFinalQuestionsAdapter(QuizResultQuestionsActivity.this, qlist));
                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(QuizResultQuestionsActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finalq();
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
            public void onFailure(Call<ArrayList<FinalQuizResultGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }


}
