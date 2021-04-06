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

import com.img.mybat11.Fragment.BankVerificationFragment;
import com.img.mybat11.Fragment.PanValidationFragment;
import com.img.mybat11.Fragment.mobileVarificationFragment;
import com.img.mybat11.R;

public class VerifyAccountActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TextView title;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        title= (TextView)findViewById(R.id.title);
        title.setText("Verify Account");

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tabLayout= (TabLayout)findViewById(R.id.tabLayout);
        viewPager= (ViewPager)findViewById(R.id.vp);
        tabLayout.setupWithViewPager(viewPager);
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
                    return new mobileVarificationFragment();
                case 1:
                    return new PanValidationFragment();
                default:
                    return new BankVerificationFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Mobile & Email";
                case 1:
                    return "PAN Card";
                default:
                    return "Bank";
            }
        }
    }

}
