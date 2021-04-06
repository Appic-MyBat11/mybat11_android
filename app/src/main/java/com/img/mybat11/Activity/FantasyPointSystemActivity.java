package com.img.mybat11.Activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.Fragment.FantasyODIFragment;
import com.img.mybat11.Fragment.FantasyT10Fragment;
import com.img.mybat11.Fragment.FantasyT20Fragment;
import com.img.mybat11.Fragment.FantasyTESTFragment;
import com.img.mybat11.R;

public class FantasyPointSystemActivity extends AppCompatActivity {

    TabLayout tab;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasy_point_system);



        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("Fantasy Point System");

        tab=(TabLayout)findViewById(R.id.tab);
        viewPager=(ViewPager) findViewById(R.id.viewPager);
        tab.setupWithViewPager(viewPager);
        viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FantasyT20Fragment();
                case 1:
                    return new FantasyODIFragment();
                case 2:
                    return new FantasyTESTFragment();
                default:
                    return new FantasyT10Fragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "T20";
                case 1:
                    return "ODI";
                case 2:
                    return "TEST";
                default:
                    return "T10";
            }
        }
    }

}