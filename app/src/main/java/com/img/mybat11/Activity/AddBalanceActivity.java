package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.MerchantActivity;
import com.img.mybat11.Api.RozorPayPayMentGateWay;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.CashFree.PaymentActivity;
import com.img.mybat11.GetSet.offersGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBalanceActivity extends AppCompatActivity {

    TextView add10,add50,add100;
    TextInputLayout addMoney;
    ImageView back;
    Button btnAddCash,btnAddCashfree,btnAddRazorpay;
    TextView title;
    RequestQueue requestQueue;
    UserSessionManager session;
    ConnectionDetector cd;
    public static Activity fa;
    String type="";
    GlobalVariables globalVariables;
    MainActivity ma;

    ArrayList<offersGetSet> offerList;
    TextView offersText;
    HorizontalScrollView offers;
    LinearLayout offersLL;
    String offerid= "";
    int min=0,max=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);
        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(AddBalanceActivity.this);

        fa= this;
        globalVariables= (GlobalVariables)getApplicationContext();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        title=(TextView)findViewById(R.id.title);
        title.setText("Add Balance");

        session= new UserSessionManager(getApplicationContext());
        cd = new ConnectionDetector(getApplicationContext());

        add10=(TextView)findViewById(R.id.add10);
        add50=(TextView)findViewById(R.id.add50);
        add100=(TextView)findViewById(R.id.add100);
        btnAddCash=(Button) findViewById(R.id.btnAddCash);
        btnAddCashfree=(Button) findViewById(R.id.btnAddCashfree);
        btnAddRazorpay=(Button) findViewById(R.id.btnAddRazorpay);
        addMoney=(TextInputLayout)findViewById(R.id.addMoney);

        offersText=(TextView)findViewById(R.id.offersText);
        offers=(HorizontalScrollView)findViewById(R.id.offers);
        offersLL =(LinearLayout)findViewById(R.id.offersLL);

        add10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    addMoney.getEditText().setText(String.valueOf((Integer.parseInt(addMoney.getEditText().getText().toString())+100)));
                }else{
                    addMoney.getEditText().setText("100");
                }
            }
        });

        add50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    addMoney.getEditText().setText(String.valueOf((Integer.parseInt(addMoney.getEditText().getText().toString())+200)));
                }else{
                    addMoney.getEditText().setText("200");
                }
            }
        });

        add100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    addMoney.getEditText().setText(String.valueOf((Integer.parseInt(addMoney.getEditText().getText().toString())+500)));
                }else{
                    addMoney.getEditText().setText("500");
                }
            }
        });

        btnAddCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    if(cd.isConnectingToInternet()){
                        AddAmount(addMoney.getEditText().getText().toString(), "paytm");
                    }
                }
            }
        });
        btnAddCashfree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    if(cd.isConnectingToInternet()){
                        AddAmount(addMoney.getEditText().getText().toString(), "cashfree");
                    }
                }
            }
        });
        btnAddRazorpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    if(cd.isConnectingToInternet()){
                        AddAmount(addMoney.getEditText().getText().toString(), "razorpay");
                    }
                }
            }
        });

        Offers();
    }

    public void AddAmount(final String price, final String from){
        ma.showProgressDialog(AddBalanceActivity.this);
        try {

            String url = getResources().getString(R.string.app_url)+"requestaddcash";
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

                                if(jsonObject.getBoolean("success")) {
                                    String txnId = jsonObject.getString("txnid");

                                    // add Payment gateway hare



                                    if(from.equals("paytm")) {
                                        Intent i = new Intent(AddBalanceActivity.this, MerchantActivity.class);
                                        i.putExtra("price", price);
                                        i.putExtra("orderid", txnId);
                                        startActivity(i);
                                       finish();
                                    }else if(from.equals("cashfree")) {
                                        Intent i = new Intent(AddBalanceActivity.this, PaymentActivity.class);
                                        i.putExtra("price", price);
                                        i.putExtra("orderid", txnId);
                                        startActivity(i);
                                        finish();
                                    }else if(from.equals("razorpay")) {
                                        Intent i = new Intent(AddBalanceActivity.this, RozorPayPayMentGateWay.class);
                                        i.putExtra("price", price);
                                        i.putExtra("orderid", txnId);
                                        i.putExtra("razorpayid", jsonObject.getString("orderId"));
                                        startActivity(i);
                                        finish();
                                    }

                                }else{
                                    ma.showToast(AddBalanceActivity.this,jsonObject.getString("message"));
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
                                ma.showToast(AddBalanceActivity.this,"Session Timeout");

                                session.logoutUser();
                                startActivity(new Intent(AddBalanceActivity.this,LoginActivity.class));
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(AddBalanceActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddAmount(price,from);
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
                protected Map<String, String> getParams()
                {
                    HashMap<String,String> map=new HashMap<>();
                    map.put("amount",price);
                    map.put("paymentby",from);
                    map.put("offerid",offerid);
                    Log.i("Params",map.toString());

                    return map;
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

            strRequest.setShouldCache(false);
            strRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    public void Offers() {
        ma.showProgressDialog(AddBalanceActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<offersGetSet>> call = apiSeitemViewice.offers(session.getUserId());
        call.enqueue(new Callback<ArrayList<offersGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<offersGetSet>> call, Response<ArrayList<offersGetSet>> response) {

                Log.i("", "Number of movies received: complete");

                Log.i("", "Number of movies received: " + response.toString());

                ma.dismissProgressDialog();
                if (response.code() == 200) {
                    offerList = response.body();

                    AddOffers(offerList);
                } else {
                    Log.i("", "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(AddBalanceActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Offers();
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
            public void onFailure(Call<ArrayList<offersGetSet>> call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
                Log.i("", t.toString());
            }
        });
    }

    public void AddOffers(final ArrayList<offersGetSet> list){

        offersLL.removeAllViews();
        for(int i=0; i<list.size(); i++){
            View v;
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.offers,null);

            final TextView bonus,amount,validTill,selected,promoCode;

            bonus =(TextView)v.findViewById(R.id.bonus);
            amount =(TextView)v.findViewById(R.id.amount);
            promoCode =(TextView)v.findViewById(R.id.promoCode);
            validTill =(TextView)v.findViewById(R.id.validTill);
            selected =(TextView)v.findViewById(R.id.selected);

            if(list.get(i).getBonus_type().equals("per")) {
                if(list.get(i).getType().equals("bonus")){
                    bonus.setText("\uD83C\uDF81 Get " + list.get(i).getBonus() + "% Bonus \uD83C\uDF81");
                }else{
                    bonus.setText("\uD83C\uDF81 Get " + list.get(i).getBonus() + "% Cash \uD83C\uDF81");
                }
            }
            else {
                if(list.get(i).getType().equals("bonus")){
                    bonus.setText("\uD83C\uDF81 Get ₹" + list.get(i).getBonus() + " Bonus \uD83C\uDF81");
                }else{
                    bonus.setText("\uD83C\uDF81 Get ₹" + list.get(i).getBonus() + " Cash \uD83C\uDF81");
                }
            }
            amount.setText("Deposit ₹"+list.get(i).getMinamount()+" - ₹"+list.get(i).getAmount());
            validTill.setText("Offer valid Till : "+list.get(i).getExpire_date());
            promoCode.setText(list.get(i).getCode());

            if(list.get(i).isSelected())
                selected.setVisibility(View.VISIBLE);
            else
                selected.setVisibility(View.GONE);

            final int finalI = i;

            if (getIntent().hasExtra("popup")) {
                if (finalI == 0) {
                    if (addMoney.getEditText().getText().toString().equals("")) {
                        addMoney.getEditText().setText(list.get(0).getMinamount().toString());
                    }

                    offerid = list.get(0).getOfferid();
                    min = Integer.parseInt(list.get(0).getMinamount());
                    max = Integer.parseInt(list.get(0).getAmount());
                    selected.setVisibility(View.VISIBLE);
                }
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addMoney.getEditText().getText().toString().equals("")) {
                        addMoney.getEditText().setText(list.get(finalI).getMinamount().toString());
                    }

                    for(offersGetSet zz:list)
                        zz.setSelected(false);

                    offerid = list.get(finalI).getOfferid();
                    min = Integer.parseInt(list.get(finalI).getMinamount());
                    max = Integer.parseInt(list.get(finalI).getAmount());
                    list.get(finalI).setSelected(true);
                    AddOffers(list);
                }
            });


            offersLL.addView(v);
        }
    }

}
