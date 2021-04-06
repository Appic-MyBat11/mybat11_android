package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.Adapter.notificationAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightListView;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.NotificationGetSet;
import com.img.mybat11.GetSet.NotificationSingleGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    ExpandableHeightListView todayNotification,olderNotification;
    LinearLayout noNotifications;
    ArrayList<NotificationSingleGetSet> today,previous;
    String TAG= "Notifications";
    MainActivity ma;
    UserSessionManager session;
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ma = new MainActivity();
        session = new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title=(TextView)findViewById(R.id.title);
        title.setText("Your Notifications");

        todayNotification=(ExpandableHeightListView)findViewById(R.id.todayNotification);
        olderNotification=(ExpandableHeightListView)findViewById(R.id.olderNotification);

        todayNotification.setExpanded(true);
        olderNotification.setExpanded(true);

        noNotifications=(LinearLayout)findViewById(R.id.noNotifications);
        Notifications();
    }

    public void Notifications(){
        ma.showProgressDialog(NotificationActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<NotificationGetSet>> call = apiSeitemViewice.Notification(session.getUserId(),0,20);
        call.enqueue(new Callback<ArrayList<NotificationGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<NotificationGetSet>> call, Response<ArrayList<NotificationGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    ArrayList<NotificationGetSet> list = response.body();

                    if(list.get(0).getToday() != null) {
                        today = list.get(0).getToday();
                        todayNotification.setAdapter(new notificationAdapter(NotificationActivity.this, today));
                    }else{
                        noNotifications.setVisibility(View.VISIBLE);
                        todayNotification.setVisibility(View.GONE);
                    }
                    if(list.get(0).getPrevious() != null) {
                        previous = list.get(0).getPrevious();
                        olderNotification.setAdapter(new notificationAdapter(NotificationActivity.this, previous));
                    }

                    ma.dismissProgressDialog();

                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(NotificationActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Notifications();
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
            public void onFailure(Call<ArrayList<NotificationGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

}
