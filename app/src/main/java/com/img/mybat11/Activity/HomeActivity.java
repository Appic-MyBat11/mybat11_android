package com.img.mybat11.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.Fragment.CricketHomeFragment;
import com.img.mybat11.Fragment.FootballHomeFragment;
import com.img.mybat11.Fragment.QuizHomeFragment;
import com.img.mybat11.GetSet.ContestMatchListGetSet;
import com.img.mybat11.GetSet.MatchListGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    TabLayout hometab;
    ViewPager hometabview;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;
    CardView inviteBanner;
    CircleImageView profileImage,userImage;
    String android_id = "";
    ViewPager vpAdvertisments;
    TabLayout indicator;

    ImageView notification;
    ArrayList<MatchListGetSet> list,matchListLive,matchListUpcoming,matchListEnded;
    ArrayList<ContestMatchListGetSet> joinedList;
    LinearLayout homeLL,contestLL,meLL,myProfile,moreLL,wallet,rewards,invite,settings,pointSystem;
    ImageView home;
    TextView homeText,teamName,btnAddCash,viewall;

    String TAG="team name";
    DrawerLayout drawer;

    ExpandableHeightListView matchList;
    TextView noMatch;
    RelativeLayout joinedRL;
    ViewPager joinedVp;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        hometab = (TabLayout) findViewById(R.id.hometab);
        hometabview = (ViewPager) findViewById(R.id.hometabview);
 
        drawer=(DrawerLayout)findViewById(R.id.drawer);


        teamName=(TextView) findViewById(R.id.teamName);
        teamName.setText(session.getTeamName());

        noMatch=(TextView)findViewById(R.id.noMatch);
        notification=(ImageView)findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,NotificationActivity.class));
            }
        });


        meLL= (LinearLayout)findViewById(R.id.profileLL);
        myProfile= (LinearLayout)findViewById(R.id.myProfile);
        wallet= (LinearLayout)findViewById(R.id.wallet);
        rewards= (LinearLayout)findViewById(R.id.rewards);
        invite= (LinearLayout)findViewById(R.id.invite);
        settings= (LinearLayout)findViewById(R.id.settings);
        pointSystem= (LinearLayout)findViewById(R.id.pointSystem);
        inviteBanner=(CardView)findViewById(R.id.inviteBanner);

        vpAdvertisments= (ViewPager)findViewById(R.id.vpAdvertisments);

        meLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(HomeActivity.this,MeActivity.class));
                finishAffinity();
            }
        });

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(HomeActivity.this,MeActivity.class));
                finishAffinity();
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(HomeActivity.this,BalanceActivity.class));
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
                startActivity(new Intent(HomeActivity.this,InviteFriendActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(HomeActivity.this,PersonalDetailsActivity.class));
            }
        });
//
        pointSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                startActivity(new Intent(HomeActivity.this,FantasyPointSystemActivity.class));
            }
        });


        hometab.setupWithViewPager(hometabview);
        hometabview.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));


        if (gv.getPlateformmode().equals("cricket")){
            hometabview.setCurrentItem(0);
        }else{
            hometabview.setCurrentItem(1);
        }
        hometabview.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                if (i == 0){
                    gv.setPlateformmode("cricket");
                }else if (i == 1){
                    gv.setPlateformmode("quiz");
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        profileImage=(CircleImageView)findViewById(R.id.profileImage);

        userImage=(CircleImageView)findViewById(R.id.userImage);

        if (session.getImage().equals("")){
            LogOut();
        }else {
            Picasso.with(HomeActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(profileImage);
            Picasso.with(HomeActivity.this).load(session.getImage()).placeholder(R.drawable.avtar).into(userImage);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        btnAddCash=(TextView)findViewById(R.id.btnAddCash);
        btnAddCash.setText("₹"+session.getWallet_amount());

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);




        if (cd.isConnectingToInternet()) {
            Details();
        }

        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.e("andriodid", android_id);
    }


    @Override
    protected void onResume() {
        super.onResume();
        popup_notify();

    }


    public void LogOut(){
        ma.showProgressDialog(HomeActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"logout?appkey="+session.getNotificationToken();
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                new JSONArray(response.toString()).getJSONObject(0);

                                ma.dismissProgressDialog();

                                session.logoutUser();
                                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                finishAffinity();

                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(HomeActivity.this,"Session Timeout");

                                session.logoutUser();
                                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(HomeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LogOut();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                            }
                        }
                    })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }

            };
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }
    }

    public void MatchList(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Log.i("Authkey",session.getUserId());

        Call<ArrayList<MatchListGetSet>> call = apiSeitemViewice.matchList(session.getUserId());
        call.enqueue(new Callback<ArrayList<MatchListGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MatchListGetSet>> call, Response<ArrayList<MatchListGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    Collections.sort(list, new Comparator<MatchListGetSet>() {
                        @Override
                        public int compare(MatchListGetSet lhs, MatchListGetSet rhs) {
                            return rhs.getTime_start().compareTo(lhs.getTime_start());
                        }
                    });

                    matchListUpcoming = new ArrayList<>();
                    joinedList =  new ArrayList<>();

                    if(list.size() !=0){
                        for(MatchListGetSet zz:list){
                            matchListUpcoming.add(zz);

                            if(zz.getJoinedcontest() > 0){
                                ContestMatchListGetSet ob = new ContestMatchListGetSet();

                                ob.setFinal_status(zz.getWinnerstatus());
                                ob.setLaunch_status(zz.getLaunch_status());
                                ob.setTeam2logo(zz.getTeam2logo());
                                ob.setTeam1logo(zz.getTeam1logo());
                                ob.setTeam2display(zz.getTeam2name());
                                ob.setTeam1display(zz.getTeam1name());
                                ob.setTeam2color(zz.getTeam2color());
                                ob.setTeam1color(zz.getTeam1color());
                                ob.setStatus(zz.getMatchopenstatus());
                                ob.setStart_date(zz.getTime_start());
                                ob.setMatchkey(zz.getMatchkey());
                                ob.setName(zz.getName());
                                ob.setSeries_name(zz.getSeriesname());
                                ob.setJoinedcontest(zz.getJoinedcontest());

                                joinedList.add(ob);
                            }

                        }
                    }

                    if(matchListUpcoming.size()>0){
                        Collections.sort(matchListUpcoming, new Comparator<MatchListGetSet>() {
                            @Override
                            public int compare(MatchListGetSet lhs, MatchListGetSet rhs) {
                                return lhs.getTime_start().compareTo(rhs.getTime_start());
                            }
                        });



                    }else {

                    }

//                    MyMatches();

//                    tabLayout.setupWithViewPager(viewPager);
//                    viewPager.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
                }else {

                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(HomeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MatchList();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<MatchListGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                finishAffinity();
            }
        });
    }


    public void Details(){

        try {

            final String url = getResources().getString(R.string.app_url)+"userfulldetails";
            Log.i("url1111",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);
                                session.setEmail(jsonObject.getString("email"));
                                session.setName(jsonObject.getString("username"));
                                session.setMobile(jsonObject.getString("mobile"));
                                session.setDob(jsonObject.getString("dob"));
                                session.setImage(jsonObject.getString("image"));
                                session.setWallet_amount(jsonObject.getString("walletamaount"));
                                session.setTeamName(jsonObject.getString("team"));
                                session.setState(jsonObject.getString("state"));
                                session.setReferalCode(jsonObject.getString("refer_code"));
                                if(jsonObject.getInt("verified") ==1)
                                    session.setVerified(true);
                                else
                                    session.setVerified(false);
                                Log.i("here0",url);


                                if(session.getTeamName().equals("") || session.getState().equals("")){
                                    startActivity(new Intent(HomeActivity.this,UserFirstDetailsActivity.class));
                                    finishAffinity();
                                }else {
                                    MatchList();
                                }


                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    Log.i("here1",url);
                                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                                    session.logoutUser();
                                    finishAffinity();
                                }else {
                                    Log.i("here2",url);
                                }
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                                session.logoutUser();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finishAffinity();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(HomeActivity.this,"Session Timeout");

                                session.logoutUser();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(HomeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Details();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                            }
                        }
                    })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }

            };
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
            session.logoutUser();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finishAffinity();
        }

    }




    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CricketHomeFragment();
                default:
                    return new QuizHomeFragment();
            }
        }
        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CRICKET";
                default:
                    return "QUIZ";
            }
        }
    }

    public void popup_notify(){

        try {

            final String url = getResources().getString(R.string.app_url)+"getpopupimage";
            Log.i("url1111",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONArray jArray = new JSONArray(response);
                                if(jArray.length() > 0) {
                                    JSONObject jsonObject = jArray.getJSONObject(0);
                                    if (jsonObject.getInt("status") == 0) {
                                        if (!session.getPopupurl().equals(jsonObject.getString("image"))) {
                                            final Dialog d = new Dialog(HomeActivity.this);
                                            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            d.setCancelable(false);
                                            d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                            d.setContentView(R.layout.popup_noti_dialog);

                                            ImageView popupImage = d.findViewById(R.id.popupImage);
                                            ImageView btnCancel = d.findViewById(R.id.btnCancel);
                                            TextView close = d.findViewById(R.id.closepop);
                                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    d.dismiss();
                                                }
                                            });
                                            final String url = jsonObject.getString("url");
                                            final String im = jsonObject.getString("image");
                                            popupImage.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    d.dismiss();
                                                    if(!url.equals("") && url.equals("payment")){
                                                        Intent i = new Intent(HomeActivity.this, AddBalanceActivity.class);
                                                        i.putExtra("popup","popup");
                                                        startActivity(i);
                                                    }else if(!url.equals("") && url.equals("quick_support")){
                                                        String contact = "+91 8824128177"; // use country code with your phone number
                                                        String url1 = "https://api.whatsapp.com/send?phone=" + contact;
                                                        try {
                                                            PackageManager pm = getPackageManager();
                                                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                                            i.setData(Uri.parse(url1));
                                                            startActivity(i);
                                                        } catch (PackageManager.NameNotFoundException e) {
                                                            Toast.makeText(HomeActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                                                            e.printStackTrace();
                                                        }
                                                    }else if(!url.equals("") && url.equals("invite_support")){
                                                        Intent i = new Intent(HomeActivity.this, InviteFriendActivity.class);
                                                        startActivity(i);
                                                    }else if(!url.equals("") && url.equals("verify_your_account")){
                                                        Intent i = new Intent(HomeActivity.this, VerifyAccountActivity.class);
                                                        startActivity(i);
                                                    }
                                                }
                                            });
                                            close.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    d.dismiss();
                                                }
                                            });


                                            Picasso.with(HomeActivity.this).load(jsonObject.getString("image")).into(popupImage, new com.squareup.picasso.Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    session.setPopupurl(im);
                                                    d.show();
                                                }

                                                @Override
                                                public void onError() {

                                                }
                                            });
//                                            d.show();
                                        }

                                    }

                                }

                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(HomeActivity.this,"Session Timeout");
                                session.logoutUser();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(HomeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Details();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                            }
                        }
                    })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }

            };
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

}
