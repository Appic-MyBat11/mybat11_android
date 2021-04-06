package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.MainLeaderBoardAdapter;
import com.img.mybat11.Adapter.SpinnerAdapter_small;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.MainLeaderboardGetSet;
import com.img.mybat11.GetSet.seriesGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardActivity extends AppCompatActivity {

    GlobalVariables gv;
    MainActivity ma;
    UserSessionManager session;
    ConnectionDetector cd;
    RequestQueue requestQueue;

    String TAG="Series";

    Spinner seriesSpinner;
    RecyclerView leaderboard;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<MainLeaderboardGetSet> list,list1;
    MainLeaderBoardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        gv= (GlobalVariables)getApplicationContext();
        ma = new MainActivity();
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        seriesSpinner=(Spinner)findViewById(R.id.seriesSpinner);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Leaderboard");


        leaderboard= (RecyclerView) findViewById(R.id.leaderboard);
        mLayoutManager = new LinearLayoutManager(this);
        leaderboard.setLayoutManager(mLayoutManager);

        if(cd.isConnectingToInternet())
            SeriesList();
    }

    public void SeriesList(){
        ma.showProgressDialog(LeaderboardActivity.this);

        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<seriesGetSet>> call = apiSeitemViewice.series(session.getUserId());
        call.enqueue(new Callback<ArrayList<seriesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<seriesGetSet>> call, Response<ArrayList<seriesGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    final ArrayList<seriesGetSet> list = response.body();

                    if(list.size()>0) {
                        String Ar[] = new String[list.size()];
                        for (int ii = 0; ii < list.size(); ii++) {
                            Ar[ii] = list.get(ii).getName();
                        }
                        seriesSpinner.setAdapter(new SpinnerAdapter_small(LeaderboardActivity.this, Ar));

                        seriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ViewLeaderboard(list.get(position).getId());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(LeaderboardActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SeriesList();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<seriesGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }

    public void ViewLeaderboard(final int id){
        ma.showProgressDialog(LeaderboardActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MainLeaderboardGetSet>> call = apiSeitemViewice.getleaderboard(session.getUserId(),id);
        call.enqueue(new Callback<ArrayList<MainLeaderboardGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MainLeaderboardGetSet>> call, Response<ArrayList<MainLeaderboardGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    list = response.body();

                    if (list != null) {
                        if(list.size()>50)
                            list1 = new ArrayList<>(list.subList(0,50));
                        else
                            list1 = list;

                        adapter = new MainLeaderBoardAdapter(LeaderboardActivity.this,list1,id);
                        leaderboard.setAdapter(adapter);

                        leaderboard.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (!recyclerView.canScrollVertically(1)) {
                                    if (list.size() > list1.size()) {
                                        int x, y;
                                        if ((list.size() - list1.size()) >= 50) {
                                            x = list1.size();
                                            y = x + 50;
                                        } else {
                                            x = list1.size();
                                            y = x + list.size() - list1.size();
                                        }
                                        for (int i = x; i < y; i++) {
                                            list1.add(list.get(i));
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        });

                    }

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(LeaderboardActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ViewLeaderboard(id);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ma.dismissProgressDialog();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<MainLeaderboardGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }

}

