package com.img.mybat11.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Adapter.LeaderBoardAdapter1;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.JoinTeamGetSet;
import com.img.mybat11.GetSet.LiveChallengesGetSet;
import com.img.mybat11.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardLiveFramgent extends Fragment {

    Context context;
    int challenge_id;

    RecyclerView leardboard;
    RecyclerView.LayoutManager mLayoutManager;
    UserSessionManager session;
    ConnectionDetector cd;
    GlobalVariables gv;
    MainActivity ma;
    SwipeRefreshLayout swipeReferesh;

    ArrayList<LiveChallengesGetSet> list = new ArrayList();
    ArrayList<JoinTeamGetSet> teams,teams1;
    String TAG = "Data";
    LeaderBoardAdapter1 adapter;

    TextView totalTeams;
    String maxusers;
    Button btnDownloadPdf;
    String sDate = "2017-09-08 10:05:00";
    String eDate = "2017-09-10 12:05:00";
    Date startDate=null,endDate = null;

    boolean download_pdf = false;

    @SuppressLint("ValidFragment")
    public LeaderboardLiveFramgent(String maxusers) {
        this.maxusers = maxusers;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_leaderboard_live_framgent, container, false);
        context = getActivity();

        session = new UserSessionManager(context);
        cd = new ConnectionDetector(context);
        gv = (GlobalVariables)context.getApplicationContext();
        ma = new MainActivity();

        challenge_id = gv.getChallengeId();

        leardboard = v.findViewById(R.id.leardboard);
        mLayoutManager = new LinearLayoutManager(context);
        leardboard.setLayoutManager(mLayoutManager);

        swipeReferesh = v.findViewById(R.id.swipeReferesh);
        swipeReferesh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cd.isConnectingToInternet())
                    LeaderBoard();

                swipeReferesh.setRefreshing(false);
            }
        });

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        c.add(Calendar.MINUTE,-15);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate =gv.getMatchTime();

        Log.i("timeapna1", String.valueOf(sDate));
        Log.i("timeapna2", String.valueOf(eDate));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long diffInMs = endDate.getTime() - startDate.getTime();

        final long hours1 = 1*60*60*1000;
        final long hours4 = 4*60*60*1000;
        final long hours48 = 48*60*60*1000;


        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Log.i("Where","On finish");
                download_pdf = true;
            }
        };
        cT.start();


        totalTeams = v.findViewById(R.id.totalTeams);
        btnDownloadPdf = v.findViewById(R.id.btnDownloadPdf);

        btnDownloadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (download_pdf){

                    String Pdflink = getResources().getString(R.string.app_url).replace("/api/", "/") + "getPdfDownload?matchkey="+gv.getMatchKey()+"&challengeid="+challenge_id;

                    Log.i("url is", Pdflink);

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Request request = new Request(Uri.parse(Pdflink));

                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//Notify client once download is completed!
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MyBat11-challenge-"+challenge_id+".pdf");
                        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        ma.showToast(context, "Downloading File"); //To notify the Client that the file is being downloaded
                    } else {
                        String[] PERMISSIONS_STORAGE = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        ActivityCompat.requestPermissions((Activity) context, PERMISSIONS_STORAGE, 1);
                    }

                }else {
                    ma.showToast(context, "Wait! Pdf Generate takes several minutes"); //To notify the Client that the file is being downloaded
                }

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet())
            LeaderBoard();

    }

    public void LeaderBoard(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<LiveChallengesGetSet>> call = apiSeitemViewice.liveScores(session.getUserId(),gv.getMatchKey(),gv.getChallengeId());
        call.enqueue(new Callback<ArrayList<LiveChallengesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LiveChallengesGetSet>> call, Response<ArrayList<LiveChallengesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    teams = list.get(0).getJointeams();

                    totalTeams.setText(maxusers+" Teams");

                    if (teams != null) {
                        if(teams.size()>50)
                            teams1 = new ArrayList<>(teams.subList(0,50));
                        else
                            teams1 = teams;

                        adapter = new LeaderBoardAdapter1(context, teams1);
                        Log.i("size is", String.valueOf(teams.size()));
                        leardboard.setAdapter(adapter);

                        leardboard.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (!recyclerView.canScrollVertically(1)) {
                                    if (teams.size() > teams1.size()) {
                                        int x, y;
                                        if ((teams.size() - teams1.size()) >= 50) {
                                            x = teams1.size();
                                            y = x + 50;
                                        } else {
                                            x = teams1.size();
                                            y = x + teams.size() - teams1.size();
                                        }
                                        for (int i = x; i < y; i++) {
                                            teams1.add(teams.get(i));
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        });

                    }
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LeaderBoard();
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
            public void onFailure(Call<ArrayList<LiveChallengesGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }
}
