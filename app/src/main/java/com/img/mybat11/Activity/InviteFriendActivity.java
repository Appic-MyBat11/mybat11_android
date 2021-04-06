package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.ReferuserGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteFriendActivity extends AppCompatActivity {

    TextView code,more,totalJoined;
    String inviteText="";
    MainActivity ma;
    UserSessionManager session;
    ConnectionDetector cd;
    GlobalVariables gv;

    String TAG="refer";
    ArrayList<ReferuserGetSet> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        ma = new MainActivity();
        cd = new ConnectionDetector(getApplicationContext());
        gv=(GlobalVariables)getApplicationContext();
        session = new UserSessionManager(getApplicationContext());


        TextView title =(TextView)findViewById(R.id.title);
        title.setText("My Referrals");

        totalJoined=(TextView)findViewById(R.id.totalJoined);

        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        totalJoined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    gv.setReferList(list);
                    startActivity(new Intent(InviteFriendActivity.this, InvitedFriendsActivity.class));
                }
            }
        });

        code = (TextView)findViewById(R.id.code);
        code.setText(session.getReferalCode());

        more = (TextView)findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Think you can challenge me on "+getString(R.string.app_name)+"? Tap \n" +
                        "to download the app & use my invite code "+session.getReferalCode()+" to get a Cash Bonus of Rs.100! Let the games begin" +
                        "\nDownload Application from "+ getResources().getString(R.string.apk_url);


                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        if(cd.isConnectingToInternet())
            Count();
    }

    public void Count() {
//        ma.showProgressDialog(InviteFriendActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<ReferuserGetSet>> call = apiSeitemViewice.getreferuser(session.getUserId());
        call.enqueue(new Callback<ArrayList<ReferuserGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<ReferuserGetSet>> call, Response<ArrayList<ReferuserGetSet>> response) {

                Log.i("Response is", "Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if (response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    totalJoined.setText(list.size()+" friends Joined");

//                    ma.dismissProgressDialog();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(InviteFriendActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Count();
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
            public void onFailure(Call<ArrayList<ReferuserGetSet>> call, Throwable t) {
                // Log error here since request failed
//                ma.dismissProgressDialog();
                Log.i(TAG, t.toString());
            }
        });
    }

}
