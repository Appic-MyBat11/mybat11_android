package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReferCodeActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;

    Button btnRegister;
    TextInputLayout email,mobile,password,inviteCode;
    ImageView seePassword;
    boolean seePass = false;
    TextView tc;
    GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    String FGname="",FGemail="",FGimage="",dob="";
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    String TokenId="",email1="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_code);






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
        title.setText("Register & Play");

        tc=(TextView)findViewById(R.id.tc);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        mobile=(TextInputLayout) findViewById(R.id.mobile);
        email=(TextInputLayout) findViewById(R.id.email);
        password=(TextInputLayout) findViewById(R.id.password);
        inviteCode=(TextInputLayout) findViewById(R.id.inviteCode);
        seePassword=(ImageView) findViewById(R.id.seePassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mobile.getEditText().getText().toString().length() != 10)
                    mobile.setError("Please enter mobile number");
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText()).matches())
                    email.setError("Please enter email");
                else if(!validPassword(password.getEditText().getText().toString()))
                    password.setError("Please enter valid password with Min 8 character & 1 number/symbol");
                else {
                    if(cd.isConnectingToInternet()) {
                        Signup();
                    }
                }
            }
        });

        seePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seePass){
                    password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
                    seePass = false;
                    seePassword.setImageResource(R.drawable.ic_eye_accent);
                }else{
                    password.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    seePass = true;
                    seePassword.setImageResource(R.drawable.ic_eye_font);
                }
            }
        });


        tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReferCodeActivity.this,TermAndConditionsActivity.class));
            }
        });

    }

    public boolean validPassword(String password){
        String pattern = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Za-z]).*$";
        if(password.matches(pattern))
            return true;
        else
            return false;
    }






    public void Signup(){
        ma.showProgressDialog(ReferCodeActivity.this);

        try {

            String url = getResources().getString(R.string.app_url)+"tempregisteruser";
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
                                    session.setReferalCode(inviteCode.getEditText().getText().toString());
                                    Intent ii = new Intent(ReferCodeActivity.this,OTPActivity.class);
                                    ii.putExtra("email",email.getEditText().getText().toString());
                                    ii.putExtra("mobile",mobile.getEditText().getText().toString());
                                    ii.putExtra("tempuser",jsonObject.getString("tempuser"));
                                    ii.putExtra("password",password.getEditText().getText().toString());
                                    ii.putExtra("from","register");
                                    startActivity(ii);
                                }else {
                                    ma.errorDialog(ReferCodeActivity.this, jsonObject.getString("msg"), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ma.dismissDialog();
                                        }
                                    });
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
                            if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(ReferCodeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Signup();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(ReferCodeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Signup();
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
                    params.put("email", email.getEditText().getText().toString());
                    params.put("mobile", mobile.getEditText().getText().toString());
                    params.put("password", password.getEditText().getText().toString());
                    params.put("refercode",inviteCode.getEditText().getText().toString());
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



    public void LOGIN(View v){
        startActivity(new Intent(ReferCodeActivity.this,LoginActivity.class));
        finishAffinity();
    }
}
