package com.img.mybat11.Api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mybat11.GetSet.addAmountGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentSuccess extends AppCompatActivity {

    Toolbar toolBar;
    TextView textID,textEmail,textAmnt,textMsg;
    TextView t1,t2,t3;
    String email,payID,price;
    LinearLayout succll;
    UserSessionManager session;
    ConnectionDetector cd;
    String TAG="cat",from="",txnid="";
    ImageView back;
    TextView title;
    ArrayList<addAmountGetSet> list;
    Dialog progressDialog;
    TextView headertitle;
    GlobalVariables globalVariables;
    ImageView backimage, headerwalletimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        session = new UserSessionManager(getApplicationContext());

        globalVariables= (GlobalVariables)getApplicationContext();
        cd= new ConnectionDetector(getApplicationContext());


        email= getIntent().getExtras().getString("email");
        payID= getIntent().getExtras().getString("payid");
        price= getIntent().getExtras().getString("price");
        from= getIntent().getExtras().getString("from");
        txnid= getIntent().getExtras().getString("txnid");

        setSupportActionBar(toolBar);

        succll = (LinearLayout)findViewById(R.id.successll);

        textEmail = (TextView)findViewById(R.id.useremail);
        textID = (TextView)findViewById(R.id.paymentID);
        textAmnt = (TextView)findViewById(R.id.payamonut);
        textMsg = (TextView)findViewById(R.id.msg);
        t1 = (TextView)findViewById(R.id.t1);
        t2 = (TextView)findViewById(R.id.t2);
        t3 = (TextView)findViewById(R.id.t3);

        textEmail.setText(email);
        textID.setText(payID);
        textAmnt.setText("₹ "+price);

        Payment();

    }

    public void Payment(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);
        Log.i("id",session.getUserId());
        Log.i("amount",price);
        Log.i("paymentby",from);
        Call<ArrayList<addAmountGetSet>> call = apiSeitemViewice.PaymentSucess(session.getUserId());
        call.enqueue(new Callback<ArrayList<addAmountGetSet>>() {
            @Override
            public void onResponse(Call<ArrayList<addAmountGetSet>> call, Response<ArrayList<addAmountGetSet>> response) {
                Log.i(TAG, "Number of movies received: complete");
                Log.i(TAG, "Number of movies received: " + response.toString());
                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    list = response.body();
                    session.setWallet_amount(String.valueOf(list.get(0).getTotal_amount()));
                    finish();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(PaymentSuccess.this);
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
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<addAmountGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }

}
