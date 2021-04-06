package com.img.mybat11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.img.mybat11.Activity.AddBalanceActivity;
import com.img.mybat11.Activity.InviteFriendActivity;
import com.img.mybat11.Activity.MoreActivity;
import com.img.mybat11.Activity.VerifyAccountActivity;
import com.img.mybat11.CashFree.PaymentActivity;
import com.img.mybat11.GetSet.bannersGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SlideBannerAdapter extends PagerAdapter {


    LayoutInflater inflater;
    Context context;
    ArrayList<bannersGetSet> list;


    public SlideBannerAdapter(Context context, ArrayList<bannersGetSet> list )
    {
        this.context=context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        ImageView imgflag;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.banner_item,null);
        imgflag = (ImageView) itemView.findViewById(R.id.imageView);

        Picasso.with(context).load(list.get(position).getImage()).into(imgflag);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = list.get(position).getUrl();
                if(!url.equals("") && url.equals("payment")){
                    Intent i = new Intent(context, AddBalanceActivity.class);
                    context.startActivity(i);
                }else if(!url.equals("") && url.equals("quick_support")){
                    String contact = "+91 8824128177"; // use country code with your phone number
                    String url1 = "https://api.whatsapp.com/send?phone=" + contact;
                    try {
                        PackageManager pm = context.getPackageManager();
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url1));
                        context.startActivity(i);
                    } catch (PackageManager.NameNotFoundException e) {
                        Toast.makeText(context, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }else if(!url.equals("") && url.equals("invite_support")){
                    Intent i = new Intent(context, InviteFriendActivity.class);
                    context.startActivity(i);
                }else if(!url.equals("") && url.equals("verify_your_account")){
                    Intent i = new Intent(context, VerifyAccountActivity.class);
                    context.startActivity(i);
                }
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);
    }
}
