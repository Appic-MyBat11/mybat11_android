package com.img.mybat11.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.ChooseTeamActivity;
import com.img.mybat11.Activity.CreateTeamActivity;
import com.img.mybat11.Activity.DetailsActivity;
import com.img.mybat11.Activity.JoinContestActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.FinalQuestionGetSet;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuizFinalQuestionsAdapter extends RecyclerView.Adapter<QuizFinalQuestionsAdapter.ViewHolder> {


    Context context;
    ArrayList<FinalQuestionGetSet> list;
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;

    private BottomSheetBehavior mBottomSheetBehavior1;
    ExpandableHeightListView priceCard;

    public QuizFinalQuestionsAdapter(Context context, ArrayList<FinalQuestionGetSet> list){
        this.context=context;
        this.list= list;
        this.context=context;

        gv= (GlobalVariables)context.getApplicationContext();
        session= new UserSessionManager(context);
        cd = new ConnectionDetector(context);

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView qno,question,sahianswer,attamptanswer,points;

        public ViewHolder(View v){
            super(v);

            qno=(TextView)v.findViewById(R.id.qno);
            question=(TextView)v.findViewById(R.id.question);
            attamptanswer=(TextView)v.findViewById(R.id.attamptanswer);
            sahianswer=(TextView)v.findViewById(R.id.sahianswer);
            points=(TextView)v.findViewById(R.id.points);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.quizfinalquestion,viewGroup,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {
        holder.qno.setText(""+(i+1));
        holder.question.setText(list.get(i).getQuestion());
        holder.sahianswer.setText(list.get(i).getAnswer());
        if (!list.get(i).getGiveanswer().equals("0")) {
            if (list.get(i).getGiveanswer().equals(list.get(i).getAnswer())){
                holder.attamptanswer.setText(list.get(i).getAnswer());
                holder.attamptanswer.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90EE90")));
                holder.attamptanswer.setTextColor(Color.parseColor("#25ba39"));
            }else {
                holder.attamptanswer.setText(list.get(i).getGiveanswer());
                holder.attamptanswer.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffcccb")));
                holder.attamptanswer.setTextColor(Color.parseColor("#C20a0a"));
            }
        }else {
            holder.attamptanswer.setText("Missed");
            holder.attamptanswer.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFF99")));
            holder.attamptanswer.setTextColor(Color.parseColor("#f59a23"));
        }
        holder.points.setText(list.get(i).getPoint());


    }

    @Override
    public int getItemCount() {
            return list.size();
    }

 }
