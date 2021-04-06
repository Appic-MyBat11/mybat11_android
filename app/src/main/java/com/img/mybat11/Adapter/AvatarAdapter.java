package com.img.mybat11.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.GetSet.avatarGetSet;
import com.img.mybat11.GetSet.filterGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AvatarAdapter extends BaseAdapter{

    Context context;
    ArrayList<avatarGetSet> list;

    public AvatarAdapter(Context context, ArrayList<avatarGetSet> list){
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v;
        CircleImageView imagesup;
        LinearLayout lin;
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.avatar_item,null);


        imagesup = (CircleImageView) v.findViewById(R.id.imagesup);
        lin = (LinearLayout) v.findViewById(R.id.lin);


        Picasso.with(context).load(list.get(i).getImage()).placeholder(R.drawable.logo).into(imagesup);


        if(list.get(i).isSelected())
            lin.setBackground(context.getResources().getDrawable(R.drawable.avtar_selected));
        else
            lin.setBackground(context.getResources().getDrawable(R.drawable.avtar_deselected));

        final int finalI = i;
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(finalI).isSelected())
                    list.get(finalI).setSelected(false);
                else
                    list.get(finalI).setSelected(true);

                notifyDataSetChanged();
            }
        });

        return v;
    }
}
