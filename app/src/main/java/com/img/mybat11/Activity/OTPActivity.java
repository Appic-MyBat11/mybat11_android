package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.broooapps.otpedittext2.OtpEditText;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;
import com.mukesh.OtpView;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    RequestQueue requestQueue;
    UserSessionManager session;
    MainActivity ma;

    TextView email,timer;
    Button btnVerify;
    boolean resend = false;
    CountDownTimer cT;
    String type;
    OtpView otp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);


        type = getIntent().getExtras().getString("from");



        if(type.equals("register"))
            title.setText("Register & Play");
        else if(type.equals("forget"))
            title.setText("Reset password");
        else if(type.equals("mobile"))
            title.setText("Verify Account");
        else if(type.equals("login"))
            title.setText("LOG IN");

        email = (TextView)findViewById(R.id.email);
        otp=(OtpView)findViewById(R.id.otp);

        timer= (TextView)findViewById(R.id.timer);
        btnVerify = (Button)findViewById(R.id.btnVerify);

        email.setText("OTP sent to "+getIntent().getExtras().getString("mobile"));

        long diffInMs =60*1000;

        cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText( "00 : "+ String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
            }

            public void onFinish() {
                resend = true;
                timer.setText("Did not receive OTP? Resend OTP");
            }
        };
        cT.start();

/*
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                Log.i("message",messageText);

                char ar[] = messageText.toCharArray();
                otp1.setText(String.valueOf(ar[0]));
                otp2.setText(String.valueOf(ar[1]));
                otp3.setText(String.valueOf(ar[2]));
                otp4.setText(String.valueOf(ar[3]));
            }
        });
*/

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resend) {
                    Resend();
                }
            }
        });
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (otp.length() == 4){

                    if(type.equals("register"))
                        submit("registerusers");
                    else if(type.equals("forget"))
                        submit("matchCodeForReset");
                    else if(type.equals("mobile"))
                        submit("verifyCode");
                    else if(type.equals("login"))
                        submit("loginotp");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (otp.length() == 4){

                    if(type.equals("register"))
                        submit("registerusers");
                    else if(type.equals("forget"))
                        submit("matchCodeForReset");
                    else if(type.equals("mobile"))
                        submit("verifyCode");
                    else if(type.equals("login"))
                        submit("loginotp");
                }
            }
        });
//        btnVerify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.i("TextLength", String.valueOf());
//
//                if(otp.getText().toString().equals("")){
//                    ma.showToast(OTPActivity.this,"Please enter valid otp");
//                }else{
//                    if(type.equals("register"))
//                        submit("registerusers");
//                    else if(type.equals("forget"))
//                        submit("matchCodeForReset");
//                    else if(type.equals("mobile"))
//                        submit("verifyCode");
//                    else if(type.equals("login"))
//                        submit("loginotp");
//                }
//            }
//        });

    }


    public void Resend(){
        ma.showProgressDialog(OTPActivity.this);

        try {

            String url = getResources().getString(R.string.app_url)+"resendotp";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            resend = false;
                            cT.start();
                            ma.dismissProgressDialog();
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
                                ma.showToast(OTPActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(OTPActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Resend();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(OTPActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Resend();
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
                    if(getIntent().hasExtra("Auth"))
                        params.put("Authorization", getIntent().getExtras().getString("Auth"));
                    Log.i("Header",params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    if(type.equals("register")) {
                        params.put("tempuser", getIntent().getExtras().getString("tempuser"));
                    }else if(type.equals("mobile")){
                        params.put("mobile",getIntent().getExtras().getString("mobile"));
                    }else if(type.equals("login")){
                        params.put("mobile",getIntent().getExtras().getString("mobile"));
                    }else if(type.equals("forget")){
                        params.put("username", getIntent().getExtras().getString("mobile"));
                    }

                    Log.i("Header",params.toString());

                    return params;
                }
            };
            strRequest.setShouldCache(false);
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    public void submit(final String url_text){

//        startActivity(new Intent(OTPActivity.this, LoginActivity.class));
//        finishAffinity();

        ma.showProgressDialog(OTPActivity.this);

        try {
            String url = getResources().getString(R.string.app_url)+url_text;
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());

                                JSONObject jsonObject;
                                if(type.equals("forget"))
                                    jsonObject= new JSONObject(response.toString());
                                else
                                    jsonObject= new JSONArray(response.toString()).getJSONObject(0);

                                if(type.equals("register")) {
                                    if(jsonObject.getBoolean("status")){
                                        startActivity(new Intent(OTPActivity.this, LoginActivity.class));
                                        ma.showToast(OTPActivity.this,jsonObject.getString("msg"));
                                        finishAffinity();
                                    }else
                                        ma.showToast(OTPActivity.this,jsonObject.getString("msg"));
                                }else if(type.equals("forget")){
                                    if(jsonObject.getInt("status") ==1){
                                        Intent ii = new Intent(OTPActivity.this, ResetPasswordActivity.class);
                                        ii.putExtra("code",otp.getText().toString());
                                        ii.putExtra("token",jsonObject.getString("token"));
                                        startActivity(ii);
                                    }else
                                        ma.showToast(OTPActivity.this,jsonObject.getString("msg"));
                                }else if(type.equals("login")){
                                    if(jsonObject.getBoolean("status")){
                                        session.createUserLoginSession(true,"bearer "+jsonObject.getString("auth_key"),email.getText().toString());
                                        ma.dismissProgressDialog();
                                        Details();
                                    }else{
                                        ma.errorDialog(OTPActivity.this, jsonObject.getString("msg"), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ma.dismissDialog();
                                            }
                                        });
                                    }
                                }else if(type.equals("mobile")){
                                    ma.dismissProgressDialog();
                                    if(jsonObject.getBoolean("status")){
                                        session.createUserLoginSession(true,getIntent().getExtras().getString("Auth"),email.getText().toString());
                                        ma.dismissProgressDialog();
                                        Details();
                                    }else{
                                        ma.errorDialog(OTPActivity.this, jsonObject.getString("msg"), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ma.dismissDialog();
                                            }
                                        });
                                    }
                                }

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
                                ma.showToast(OTPActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(OTPActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        submit(url_text);
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(OTPActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        submit(url_text);
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
                    if(getIntent().hasExtra("Auth"))
                        params.put("Authorization", getIntent().getExtras().getString("Auth"));
                    Log.i("Header",params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    if(type.equals("register")) {
                        params.put("tempuser", getIntent().getExtras().getString("tempuser"));
                        params.put("otp", otp.getText().toString());
                        params.put("refercode",session.getReferalCode());
                    }else if(type.equals("mobile")){
                        params.put("mobile",getIntent().getExtras().getString("mobile"));
                        params.put("code",otp.getText().toString());
                    }else if(type.equals("login")){
                        params.put("mobile",getIntent().getExtras().getString("mobile"));
                        params.put("otp",otp.getText().toString());
                        params.put("appid",session.getNotificationToken());
                    }else if(type.equals("forget")){
                        params.put("username", getIntent().getExtras().getString("mobile"));
                        params.put("code", otp.getText().toString());
                    }

                    Log.i("Header",params.toString());

                    return params;
                }
            };
            strRequest.setShouldCache(false);
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    public void Details(){
        ma.showProgressDialog(OTPActivity.this);
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

                                session.setEmail(jsonObject.getString("email"));
                                session.setName(jsonObject.getString("username"));
                                session.setMobile(jsonObject.getString("mobile"));
                                session.setDob(jsonObject.getString("dob"));
                                session.setImage(jsonObject.getString("image"));
                                session.setWallet_amount(jsonObject.getString("walletamaount"));
                                session.setTeamName(jsonObject.getString("team"));
                                session.setReferalCode(jsonObject.getString("refer_code"));
                                if(jsonObject.getInt("verified") ==1)
                                    session.setVerified(true);
                                else
                                    session.setVerified(false);
                                ma.dismissProgressDialog();

                                startActivity(new Intent(OTPActivity.this,HomeActivity.class));

                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    startActivity(new Intent(OTPActivity.this,LoginActivity.class));
                                    finishAffinity();
                                }
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
                                ma.showToast(OTPActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(OTPActivity.this);
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
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(OTPActivity.this);
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
            strRequest.setShouldCache(false);
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

}
