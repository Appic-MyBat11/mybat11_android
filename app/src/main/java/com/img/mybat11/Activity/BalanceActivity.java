package com.img.mybat11.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BalanceActivity extends AppCompatActivity {

    TextView untilizedCash,btnAddCash,totalBalance,winning,btnWithdraw,bonus,myTrsancations,verifyEtext;
    UserSessionManager session;
    MainActivity ma;
    RequestQueue requestQueue;
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);


        cd = new ConnectionDetector(getApplicationContext());
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        ma = new MainActivity();
        session = new UserSessionManager(getApplicationContext());

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("My Account");

        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        verifyEtext=(TextView)findViewById(R.id.verifyEtext);
        myTrsancations=(TextView)findViewById(R.id.myTrsancations);
        untilizedCash=(TextView)findViewById(R.id.untilizedCash);
        winning=(TextView)findViewById(R.id.winning);
        bonus=(TextView)findViewById(R.id.bonus);
        totalBalance=(TextView)findViewById(R.id.totalBalance);

        btnAddCash=(TextView)findViewById(R.id.btnAddCash);
        btnWithdraw=(TextView)findViewById(R.id.btnWithdraw);

        btnAddCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceActivity.this,AddBalanceActivity.class));
            }
        });

        myTrsancations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceActivity.this,MyTransactionActivity.class));
            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BalanceActivity.this,WithdrawActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyWallet();
    }

    public void MyWallet(){
        ma.showProgressDialog(BalanceActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"mywalletdetails";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.getString("allverify").equals("1")){
                                    verifyEtext.setVisibility(View.GONE);
                                }
                                winning.setText("₹"+String.format("%.2f",jsonObject.getDouble("winning")));
                                untilizedCash.setText("₹"+String.format("%.2f",jsonObject.getDouble("balance")));
                                bonus.setText("₹"+String.format("%.2f",jsonObject.getDouble("bonus")));

                                if(jsonObject.getDouble("winning") >= 300)
                                    btnWithdraw.setVisibility(View.VISIBLE);

//                                if (jsonObject.getInt("allverify") == 1){
//                                    verifyEtext.setVisibility(View.GONE);
//                                }else {
//                                    verifyEtext.setVisibility(View.VISIBLE);
//                                }

                                totalBalance.setText("₹"+String.format("%.2f",jsonObject.getDouble("totalamount")));

                                session.setWinningAmount(String.format("%.2f",jsonObject.getDouble("winning")));
                                ma.dismissProgressDialog();
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
                                ma.showToast(BalanceActivity.this,"Session Timeout");

                                startActivity(new Intent(BalanceActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(BalanceActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MyWallet();
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
