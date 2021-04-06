package com.img.mybat11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.Activity.AllChallengesActivity;
import com.img.mybat11.Activity.QuizAllChallengesActivity;
import com.img.mybat11.GetSet.CategoriesGetSet;
import com.img.mybat11.GetSet.challengesGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class QuizChallengeCategoryListAdapter extends RecyclerView.Adapter<QuizChallengeCategoryListAdapter.ViewHolder> {

    Context context;
    ArrayList<CategoriesGetSet> list ;

    public QuizChallengeCategoryListAdapter(Context context, ArrayList<CategoriesGetSet> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View itemView;
        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.challenge_category, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int i) {

        ArrayList<challengesGetSet> list1 = list.get(i).getContest();

        holder.title.setText(list.get(i).getCatname());
        holder.subtitle.setText(list.get(i).getSub_title());
        Picasso.with(context).load(list.get(i).getImage()).into(holder.logo);

        if (list1.size() > 3) {
            holder.viewMore.setVisibility(View.VISIBLE);
            holder.viewMore.setText((list1.size() - 3) + " more contests");
        } else
            holder.viewMore.setVisibility(View.GONE);

        holder.viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(context, QuizAllChallengesActivity.class);
                ii.putExtra("catid",String.valueOf(list.get(i).getCatid()));
                context.startActivity(ii);
            }
        });

        holder.contestsList.setLayoutManager(new LinearLayoutManager(context));
        holder.contestsList.setAdapter(new QuizChallengeListAdapter(context, list1));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, subtitle, viewMore;
        ImageView logo;
        RecyclerView contestsList;

        public ViewHolder(View v){
            super(v);

            title = (TextView) v.findViewById(R.id.title);
            subtitle = (TextView) v.findViewById(R.id.subtitle);
            viewMore = (TextView) v.findViewById(R.id.viewMore);
            logo = (ImageView) v.findViewById(R.id.logo);
            contestsList = v.findViewById(R.id.contestsList);
        }

    }
}