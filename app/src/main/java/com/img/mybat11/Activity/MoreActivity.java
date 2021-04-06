package com.img.mybat11.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.img.mybat11.Adapter.SidelistAdapter_more;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreActivity extends AppCompatActivity {

    TextView title;
    ListView sideList;
    UserSessionManager session;
    GlobalVariables gv;
    String TAG="team name";
    MainActivity ma;
    ImageView back;

    LinearLayout homeLL,contestLL,meLL,moreLL,feedLL,profileLL;
    ImageView more;
    TextView moreText;

    CircleImageView userImage,profileImage;
    TextView teamName,btnAddCash;

    DrawerLayout drawer;
    LinearLayout myProfile,wallet,rewards,invite,settings,pointSystem;

    LinearLayout invitefriends,contestcode,whatsapp,ponitsystem,howtoplay,helpdesk,job,aboutus,legality,terms,faq,fairplay,privacypolicy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        ma= new MainActivity();
        gv= (GlobalVariables)getApplicationContext();
        session= new UserSessionManager(getApplicationContext());


        drawer=(DrawerLayout)findViewById(R.id.drawer);



        invitefriends= (LinearLayout)findViewById(R.id.invitefriends);
        contestcode= (LinearLayout)findViewById(R.id.contestcode);
        whatsapp= (LinearLayout)findViewById(R.id.whatsapp);
        ponitsystem= (LinearLayout)findViewById(R.id.ponitsystem);
        howtoplay= (LinearLayout)findViewById(R.id.howtoplay);
        helpdesk= (LinearLayout)findViewById(R.id.helpdesk);
        job= (LinearLayout)findViewById(R.id.job);
        aboutus= (LinearLayout)findViewById(R.id.aboutus);
        legality= (LinearLayout)findViewById(R.id.legality);
        terms= (LinearLayout)findViewById(R.id.terms);
        faq= (LinearLayout)findViewById(R.id.faq);
        fairplay= (LinearLayout)findViewById(R.id.fairplay);
        privacypolicy= (LinearLayout)findViewById(R.id.privacypolicy);


        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,FaqActivity.class));
            }
        });
        fairplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,FairPlayActivity.class));
            }
        });
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,PrivacyPolicyActivity.class));
            }
        });
        howtoplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,HowToPlayActivity.class));
            }
        });
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,AboutUsActivity.class));
            }
        });
        legality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,LegalityActivity.class));
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,TermAndConditionsActivity.class));
            }
        });


        profileLL= (LinearLayout)findViewById(R.id.profileLL);
        homeLL= (LinearLayout)findViewById(R.id.homeLL);
        contestLL= (LinearLayout)findViewById(R.id.contestLL);
        meLL= (LinearLayout)findViewById(R.id.meLL);
        moreLL= (LinearLayout)findViewById(R.id.moreLL);
        feedLL= (LinearLayout)findViewById(R.id.feedLL);

        more=(ImageView) findViewById(R.id.more);
        moreText=(TextView) findViewById(R.id.moreText);

        moreText.setTextColor(getResources().getColor(R.color.colorPrimary));
        more.setImageResource(R.drawable.ic_more_selected);

        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,HomeActivity.class));
                finish();
            }
        });

        profileLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,MeActivity.class));
                finishAffinity();
            }
        });

        contestLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,MyMatchesCricketActivity.class));
                finish();
            }
        });

        feedLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoreActivity.this,MeActivity.class));
                finish();
            }
        });

        moreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        profileImage=(CircleImageView)findViewById(R.id.profileImage);

        Picasso.with(MoreActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(profileImage);
        userImage=(CircleImageView)findViewById(R.id.userImage);
        Picasso.with(MoreActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(userImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });



        teamName=(TextView) findViewById(R.id.teamName);
        teamName.setText(session.getTeamName());

        btnAddCash=(TextView)findViewById(R.id.btnAddCash);
        btnAddCash.setText("₹"+session.getWallet_amount());

        myProfile= (LinearLayout)findViewById(R.id.myProfile);
        wallet= (LinearLayout)findViewById(R.id.wallet);
        rewards= (LinearLayout)findViewById(R.id.rewards);
        invite= (LinearLayout)findViewById(R.id.invite);
        settings= (LinearLayout)findViewById(R.id.settings);
        pointSystem= (LinearLayout)findViewById(R.id.pointSystem);

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,MeActivity.class));
                finishAffinity();
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,BalanceActivity.class));
            }
        });

        rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();

            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,InviteFriendActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,PersonalDetailsActivity.class));
            }
        });

        pointSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,FantasyPointSystemActivity.class));
            }
        });





        invitefriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,InviteFriendActivity.class));
            }
        });
        ponitsystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(MoreActivity.this,FantasyPointSystemActivity.class));
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contact = "+91 8824128177"; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(MoreActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.START))
            drawer.closeDrawers();
        else {
            startActivity(new Intent(MoreActivity.this, HomeActivity.class));
            finishAffinity();
        }
    }

}
