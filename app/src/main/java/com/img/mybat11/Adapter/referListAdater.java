package com.img.mybat11.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.img.mybat11.GetSet.ReferuserGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class referListAdater extends BaseAdapter{

    Context context;
    ArrayList<ReferuserGetSet> list;

    public referListAdater(Context context, ArrayList<ReferuserGetSet> list) {
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

        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.referlist_single,null);

        CircleImageView image;
        TextView name,amount;

        image=(CircleImageView)v.findViewById(R.id.image);
        name =(TextView)v.findViewById(R.id.name);
        amount =(TextView)v.findViewById(R.id.amount);

        if(!list.get(i).getImage().equals(""))
            Picasso.with(context).load(list.get(i).getImage()).into(image);
        name.setText(list.get(i).getUsername());
        amount.setText("Received : ₹"+list.get(i).getAmount());

        return v;
    }
}
