package com.img.mybat11.CashFree;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
import com.img.mybat11.Activity.AddBalanceActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.MerchantActivity;
import com.img.mybat11.Api.PaymentSuccess;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.addAmountGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_NOTIFY_URL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;


public class PaymentActivity extends AppCompatActivity implements CFClientInterface {

    RequestQueue requestQueue;
    String appid,orderid,amount,phone,email,name,checksum;
    String orderNote = "Recharge Wallet";
    UserSessionManager session;
    MainActivity ma;
    ConnectionDetector cd;
    String TAG="";
    ArrayList<addAmountGetSet> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_cash_free);

        requestQueue = Volley.newRequestQueue(PaymentActivity.this);
        session = new UserSessionManager(getApplicationContext());
        ma = new MainActivity();
        cd = new ConnectionDetector(getApplicationContext());

        appid = "26600a1549d745bc0b80fc2c100662";  // live
//        appid = "4012fd724432eb7c568166632104";  //test
        amount = getIntent().getExtras().getString("price");
        orderid = getIntent().getExtras().getString("orderid");
        phone = session.getMobile();
        email = session.getEmail();
        name = session.getTeamName();

        CallVolley(getResources().getString(R.string.app_url) + "getCashfreeCheckSum");
    }

    public void CallVolley(final String a)
    {
        ma.showProgressDialog(PaymentActivity.this);
        try {

            StringRequest strRequest = new StringRequest(Request.Method.POST, a,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                ma.dismissProgressDialog();
                                JSONObject jsonObject = new JSONObject(response.toString());

                                orderid = jsonObject.getString("orderId");
                                checksum = jsonObject.getString("token");

                                triggerPayment(false);
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(PaymentActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            } else {
                                AlertDialog.Builder d = new AlertDialog.Builder(PaymentActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CallVolley(a);
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
                    map.put("appId",appid);
                    map.put("orderAmount",amount);
                    map.put("orderId",orderid);
                    map.put("orderNote",orderNote);
                    map.put( "customerPhone" ,phone);
                    map.put( "customerName" ,name);
                    map.put( "customerEmail" , email);
                    map.put( "customerEmail" , email);
                    Log.e("MAP",map.toString());

                    return map;
                }
            };
            strRequest.setShouldCache(false);
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Toast.makeText(this, "--"+e, Toast.LENGTH_SHORT).show();
        }

    }


    private void triggerPayment(boolean isUpiIntent) {
        /*
         * token can be generated from your backend by calling cashfree servers. Please
         * check the documentation for details on generating the token.
         * READ THIS TO GENERATE TOKEN: https://bit.ly/2RGV3Pp
         */
        String token = checksum;


        /*
         * stage allows you to switch between sandboxed and production servers
         * for CashFree Payment Gateway. The possible values are
         *
         * 1. TEST: Use the Test server. You can use this service while integrating
         *      and testing the CashFree PG. No real money will be deducted from the
         *      cards and bank accounts you use this stage. This mode is thus ideal
         *      for use during the development. You can use the cards provided here
         *      while in this stage: https://docs.cashfree.com/docs/resources/#test-data
         *
         * 2. PROD: Once you have completed the testing and integration and successfully
         *      integrated the CashFree PG, use this value for stage variable. This will
         *      enable live transactions
         */
        String stage = "PROD";

        /*
         * appId will be available to you at CashFree Dashboard. This is a unique
         * identifier for your app. Please replace this appId with your appId.
         * Also, as explained below you will need to change your appId to prod
         * credentials before publishing your app.
         */

        Map<String, String> params = new HashMap<>();

        params.put(PARAM_APP_ID, appid);
        params.put(PARAM_ORDER_ID, orderid);
        params.put(PARAM_ORDER_AMOUNT, amount);
        params.put(PARAM_ORDER_NOTE, orderNote);
        params.put(PARAM_CUSTOMER_NAME, name);
        params.put(PARAM_CUSTOMER_PHONE, phone);
        params.put(PARAM_CUSTOMER_EMAIL,email);
        params.put(PARAM_NOTIFY_URL,"https://api.mybat11.com/api/webhook_detail");

        Log.i("params",params.toString());

        for(Map.Entry entry : params.entrySet()) {
            Log.d("CFSKDSample", entry.getKey() + " " + entry.getValue());
        }

        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        cfPaymentService.setOrientation(0);

        if (isUpiIntent) {
            // Use the following method for initiating UPI Intent Payments
            cfPaymentService.upiPayment(this, params, token, this, stage);
        }
        else {
            // Use the following method for initiating regular Payments
            cfPaymentService.doPayment(this, params, token, this, stage);
        }

    }

    public void doPayment(View view) {
        triggerPayment(false);
    }

    public void upiPayment(View view) {
        triggerPayment(true);
    }

    @Override
    public void onSuccess(final Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Success");
        Log.i("map",map.toString());

        if(map.get("txStatus").equals("SUCCESS")){
            if(cd.isConnectingToInternet()) {
//                AddAmount(amount, map.get("referenceId"), orderid, "cashfree");

                Intent ii = new Intent(PaymentActivity.this, PaymentSuccess.class);
                ii.putExtra("payid",map.get("referenceId"));
                ii.putExtra("email",session.getEmail());
                ii.putExtra("txnid",orderid);
                ii.putExtra("from","cashfree");
                ii.putExtra("price",amount);
                startActivity(ii);
                finish();
            }
            else{
                AlertDialog.Builder d= new AlertDialog.Builder(PaymentActivity.this);
                d.setMessage("Internet Connection lost");
                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        AddAmount(amount, map.get("referenceId"),orderid,"cashfree");

                        Intent ii = new Intent(PaymentActivity.this, PaymentSuccess.class);
                        ii.putExtra("payid",map.get("referenceId"));
                        ii.putExtra("email",session.getEmail());
                        ii.putExtra("txnid",orderid);
                        ii.putExtra("from","cashfree");
                        ii.putExtra("price",amount);
                        startActivity(ii);
                        finish();
                    }
                });

                d.show();
            }
        }else{
            ma.showToast(PaymentActivity.this,map.get("txMsg"));
        }
    }

    // Success :  {txTime=, txMsg=Transaction Successful, referenceId=, paymentMode=, signature=, orderAmount=1.00, txStatus=SUCCESS, orderId=}

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        Log.i("map",map.toString());
        finish();
    }

    @Override
    public void onNavigateBack() {
        Log.d("CFSDKSample", "Back Pressed");
        SweetAlertDialog d = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.WARNING_TYPE);
        d.setContentText("Are you sure to cancel your transaction ?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(new Intent(PaymentActivity.this, AddBalanceActivity.class));
                        finish();
                    }
                })
                .setCancelText("No")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        triggerPayment(false);
                    }
                });
        d.show();
    }


    @Override
    public void onBackPressed() {
        SweetAlertDialog d = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.WARNING_TYPE);
        d.setContentText("Are you sure to cancel your transaction ?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        startActivity(new Intent(PaymentActivity.this,AddBalanceActivity.class));
                        finish();
                    }
                })
                .setCancelText("No")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        triggerPayment(false);
                    }
                });
        d.show();

    }

    public void AddAmount(final String Amount, final String txnid,final String returnid,final String from){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<addAmountGetSet>> call = apiSeitemViewice.PaymentSucess(session.getUserId());
        call.enqueue(new Callback<ArrayList<addAmountGetSet>>() {
            @Override
            public void onResponse(Call<ArrayList<addAmountGetSet>> call, retrofit2.Response<ArrayList<addAmountGetSet>> response) {
                Log.i(TAG, "Number of movies received: complete");
                Log.i(TAG, "Number of movies received: " + response.toString());
                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    list = response.body();
                    session.setWallet_amount(String.valueOf(list.get(0).getTotal_amount()));
                    finish();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(PaymentActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AddAmount(Amount,txnid,returnid,from);
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

