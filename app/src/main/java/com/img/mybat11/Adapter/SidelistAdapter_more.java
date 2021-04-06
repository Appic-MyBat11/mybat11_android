package com.img.mybat11.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.img.mybat11.R;


public class SidelistAdapter_more extends BaseAdapter{

    Context context;
    int img[]= {
            R.drawable.invite_friends,
            R.drawable.contest_invite,
            R.drawable.whatsapp_update,
            R.drawable.point_system_more,
            R.drawable.how_play_more,
            R.drawable.helpdesk_more,
            R.drawable.job_more,
            R.drawable.about_more,
            R.drawable.legality_more,
            R.drawable.terms_more,
    };
    String name[]= {
            "Invite Friends",
            "Contest Invite Code",
            "WhatsApp Updates",
            "Fantasy Cricket Point System",
            "How to Play",
            "Helpdesk",
            "Jobs",
            "About Us",
            "Legality",
            "Terms and conditions"
    };

    public SidelistAdapter_more(Context context){
        this.context= context;
    }

    @Override
    public int getCount() {
        return name.length;
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
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v;
        TextView icon;
        TextView title;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.sidelist_items,null);

        icon=(TextView) v.findViewById(R.id.icon);
        title=(TextView)v.findViewById(R.id.title);

        icon.setText(img[i]);
        icon.setTypeface(Typeface.createFromAsset(context.getAssets(),"fontawesome-webfont.ttf"));
        title.setText(name[i]);

        return v;
    }
}
