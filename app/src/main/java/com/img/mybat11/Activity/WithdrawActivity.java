package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.msgStatusGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithdrawActivity extends AppCompatActivity {

    TextView availableWining,add10,add50,add100,btnWithdraw;
    TextInputLayout amount;
    UserSessionManager session;
    String TAG = "Withdraw";
    ConnectionDetector cd;
    MainActivity ma;
    String paytype="Bank";
    RequestQueue requestQueue;
    String android_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        cd = new ConnectionDetector(getApplicationContext());

        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        TextView title=(TextView)findViewById(R.id.title);
        title.setText("Withdraw");

        session = new UserSessionManager(getApplicationContext());
        ma = new MainActivity();

        availableWining=(TextView)findViewById(R.id.availableWining);
        btnWithdraw=(TextView)findViewById(R.id.btnWithdraw);

        add10=(TextView)findViewById(R.id.add10);
        add50=(TextView)findViewById(R.id.add50);
        add100=(TextView)findViewById(R.id.add100);
        amount=(TextInputLayout)findViewById(R.id.amount);


        availableWining.setText("Available Winning Balance : ₹"+session.getWinningAmount());




        add10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!amount.getEditText().getText().toString().equals("")){
                    amount.getEditText().setText(String.valueOf((Integer.parseInt(amount.getEditText().getText().toString())+200)));
                }else{
                    amount.getEditText().setText("200");
                }
            }
        });

        add50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!amount.getEditText().getText().toString().equals("")){
                    amount.getEditText().setText(String.valueOf((Integer.parseInt(amount.getEditText().getText().toString())+500)));
                }else{
                    amount.getEditText().setText("500");
                }
            }
        });

        add100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!amount.getEditText().getText().toString().equals("")){
                    amount.getEditText().setText(String.valueOf((Integer.parseInt(amount.getEditText().getText().toString())+1000)));
                }else{
                    amount.getEditText().setText("1000");
                }
            }
        });

        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (amount.getEditText().getText().toString().equals(""))
                    amount.setError("Please enter amount to withdraw");
                else if (Integer.parseInt(amount.getEditText().getText().toString()) < 300) {
                    ma.showToast(WithdrawActivity.this,"Minimum Amount to withdraw is ₹300");
                }else{
                    if (Double.parseDouble(session.getWinningAmount()) >= 200.0) {
                        ma.showProgressDialog(WithdrawActivity.this);
                        Payment();
                    } else {
                        ma.showToast(WithdrawActivity.this,"Insufficient Fund to Withdraw");
                    }
                }
            }
        });

        if(cd.isConnectingToInternet()) {
            Details();
        }

        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
    public void Details(){

        try {

            String url = getResources().getString(R.string.app_url)+"userfulldetails";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    startActivity(new Intent(WithdrawActivity.this,LoginActivity.class));
                                    finishAffinity();
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
                                ma.showToast(WithdrawActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(WithdrawActivity.this);
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

    public void Payment(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<msgStatusGetSet>> call;
//        if(type.equals("Paytm"))
//            call = apiSeitemViewice.WithdrawCash(session.getUserId(),amount.getEditText().getText().toString(),type,"wining",number);
//        else
        call = apiSeitemViewice.WithdrawCash(session.getUserId(),amount.getEditText().getText().toString(),"wining",android_id);
        call.enqueue(new Callback<ArrayList<msgStatusGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<msgStatusGetSet>> call, Response<ArrayList<msgStatusGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");
                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    ArrayList<msgStatusGetSet>list = response.body();




                    ma.showToast(WithdrawActivity.this,list.get(0).getMsg());


                    finish();

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(WithdrawActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Payment();
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
            public void onFailure(Call<ArrayList<msgStatusGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }

}
