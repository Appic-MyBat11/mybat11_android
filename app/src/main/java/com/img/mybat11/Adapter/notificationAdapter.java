package com.img.mybat11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.GetSet.NotificationSingleGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

public class notificationAdapter extends BaseAdapter{

    Context context;
    ArrayList<NotificationSingleGetSet> list;

    public notificationAdapter(Context context, ArrayList<NotificationSingleGetSet> list){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.notification_single,null);

        TextView title,date;
        ImageView image;

        title=(TextView)v.findViewById(R.id.title);
        date=(TextView)v.findViewById(R.id.date);
        image =(ImageView)v.findViewById(R.id.image);

        title.setText(list.get(position).getMessage());
        date.setText(list.get(position).getCreated_at());
        image.setImageDrawable(context.getDrawable(R.drawable.logo));

        return v;
    }
}
