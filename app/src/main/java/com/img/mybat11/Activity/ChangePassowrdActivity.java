package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePassowrdActivity extends AppCompatActivity {

    Button submit;
    TextInputLayout password,cpassword,oldpassword;
    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passowrd);

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
        title.setText("Change password");

        cpassword=(TextInputLayout)findViewById(R.id.cpassword);
        password=(TextInputLayout)findViewById(R.id.password);
        oldpassword=(TextInputLayout)findViewById(R.id.oldpassword);

        submit= (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Verify();
            }
        });

    }

    public void Verify(){
        String cpass,pass,opass;

        opass= oldpassword.getEditText().getText().toString();
        cpass= cpassword.getEditText().getText().toString();
        pass= password.getEditText().getText().toString();

        if(opass.length()<1)
            oldpassword.setError("please enter your old password");
        if(pass.length()<8)
            password.setError("Password should be more than 8 char long");
        else if(!validPassword(password.getEditText().getText().toString()))
            password.setError("Please enter valid password with Min 8 character & 1 number/symbol");
        else if(!pass.equals(cpass))
            cpassword.setError("Password & Confirm password not matched");
        else{
            if (cd.isConnectingToInternet()) {
                ChangePassword();
            } else {
                new SweetAlertDialog(ChangePassowrdActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("No Internet Connection")
                        .setContentText("You don't have internet connection")
                        .show();
            }
        }
    }

    public boolean validPassword(String password){
        String pattern = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Za-z]).*$";
        if(password.matches(pattern))
            return true;
        else
            return false;
    }


    public void ChangePassword(){
        ma.showProgressDialog(ChangePassowrdActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"changepassword";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                if(jsonObject.getBoolean("status")) {
                                    finish();
                                }
                                ma.showToast(ChangePassowrdActivity.this,jsonObject.getString("msg"));

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
                                ma.showToast(ChangePassowrdActivity.this,"Session Timeout");

                                startActivity(new Intent(ChangePassowrdActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(ChangePassowrdActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ChangePassword();
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
                    params.put("oldpassword", oldpassword.getEditText().getText().toString());
                    params.put("newpassword", password.getEditText().getText().toString());
                    params.put("confirmpassword", cpassword.getEditText().getText().toString());
                    Log.i("params",params.toString());

                    return params;
                }

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
