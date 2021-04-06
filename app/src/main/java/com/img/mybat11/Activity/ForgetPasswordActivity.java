package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    Button submit;
    EditText mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);



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
        title.setText("Forget Password");

        mail=(EditText)findViewById(R.id.mail);
        mail.setText(getIntent().getExtras().getString("email"));

        submit= (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mail.getText().toString();

                if(email.isEmpty()) {
                    mail.setError("Please enter valid email address");
                }
                if (email.length()!=10 && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mail.setError("Please enter valid email address");
                }else {

//                    Intent ii = new Intent(ForgetPasswordActivity.this,OTPActivity.class);
//                    ii.putExtra("email",mail.getText().toString());
//                    ii.putExtra("mobile",mail.getText().toString());
//                    ii.putExtra("password","");
//                    ii.putExtra("from","forget");
//                    startActivity(ii);
                    RequestOTP();
                }
            }
        });

    }

    public void RequestOTP(){
        ma.showProgressDialog(ForgetPasswordActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"forgotpassword";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                if(jsonObject.getBoolean("success")){
                                    session.setOtp_email(mail.getText().toString(),"","");
                                    Intent ii = new Intent(ForgetPasswordActivity.this,OTPActivity.class);
                                    ii.putExtra("email",mail.getText().toString());
                                    ii.putExtra("mobile",mail.getText().toString());
                                    ii.putExtra("password","");
                                    ii.putExtra("from","forget");
                                    startActivity(ii);
                                }else{
                                    ma.showToast(ForgetPasswordActivity.this,jsonObject.getString("msg"));
                                }

                                ma.dismissProgressDialog();
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }
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
                                ma.showToast(ForgetPasswordActivity.this,"Session Timeout");

                                startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(ForgetPasswordActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        RequestOTP();
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
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", mail.getText().toString());
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
