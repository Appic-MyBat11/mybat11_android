package com.img.mybat11.Api;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.R;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MerchantActivity extends Activity {
    PaytmPGService Service;
    String CHECKSUMHASH1,orderid,customerid,price;
    RequestQueue requestQueue;
    Dialog progressDialog;
    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        session= new UserSessionManager(getApplicationContext());

        price=getIntent().getExtras().getString("price");
        requestQueue = Volley.newRequestQueue(MerchantActivity.this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    //This is to refresh the order id: Only for the Sample App's purpose.
    @Override
    protected void onStart(){
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        onStartTransaction();
    }
    public void onStartTransaction() {
        CHECKSUMHASH1="";
        orderid="";
        customerid="";
        Random r = new Random(System.currentTimeMillis());
        orderid = getIntent().getExtras().getString("orderid");

        customerid="MyBat11-"+session.getMobile();
        CallVolley(getResources().getString(R.string.app_url) + "getPaytmCheckSum");
    }
    public void CallVolley(String a)
    {
        try {

            StringRequest strRequest = new StringRequest(Request.Method.POST, a,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());
                                CHECKSUMHASH1 = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                                CallPaytmIntegration();
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
                            Toast.makeText(MerchantActivity.this, error.toString(), Toast.LENGTH_SHORT).show();


                        }
                    })
            {
                @Override
                protected Map<String, String> getParams()
                {

                    //  TEST
//                    HashMap<String,String> paramMap=new HashMap<>();
//                    paramMap.put("MID","DIY12386817555501617"); //Provided by Paytm
//                    paramMap.put("ORDER_ID",orderid); //unique OrderId for every request
//                    paramMap.put("CUST_ID",customerid); // unique customer identifier
//                    paramMap.put("INDUSTRY_TYPE_ID", "Retail"); //Provided by Paytm
//                    paramMap.put("CHANNEL_ID", "WAP"); //Provided by Paytm
//                    paramMap.put("TXN_AMOUNT", price); // transaction amount
//                    paramMap.put("WEBSITE", "DIYtestingwap");//Provided by Paytm
//                    paramMap.put("CALLBACK_URL","https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid);//Provided by Paytm
//                    paramMap.put( "EMAIL" , session.getEmail());
//                    paramMap.put( "MOBILE_NO" , session.getMobile());
//                    Log.e("MAP",paramMap.toString());



                    //paytm live
                   HashMap<String,String> paramMap=new HashMap<>();
                    paramMap.put("MID","ZQuxYB60583337152490");
                    paramMap.put("ORDER_ID",orderid);
                    paramMap.put("CUST_ID",customerid);
                    paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                    paramMap.put("CHANNEL_ID", "WAP");
                    paramMap.put("TXN_AMOUNT", price);
                    paramMap.put("WEBSITE", "DEFAULT");
                    paramMap.put( "EMAIL" , session.getEmail());
                    paramMap.put( "MOBILE_NO" , session.getMobile());
                    paramMap.put( "MOBILE_NO" , session.getMobile());
                    paramMap.put("CALLBACK_URL","https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+orderid);//Provided by Paytm
                    Log.e("MAP",paramMap.toString());

                    return paramMap;
                }
            };
            requestQueue.add(strRequest);
        }
        catch (Exception e) {

            Toast.makeText(MerchantActivity.this, "--"+e, Toast.LENGTH_SHORT).show();
        }
    }
    public void CallPaytmIntegration() {
        Service = PaytmPGService.getProductionService();
//        Service = PaytmPGService.getStagingService();
        //Test

//        Map<String, String> paramMap = new HashMap<String,String>();
//        paramMap.put("MID","DIY12386817555501617"); //Provided by Paytm
//        paramMap.put("ORDER_ID",orderid); //unique OrderId for every request
//        paramMap.put("CUST_ID",customerid); // unique customer identifier
//        paramMap.put("INDUSTRY_TYPE_ID", "Retail"); //Provided by Paytm
//        paramMap.put("CHANNEL_ID", "WAP"); //Provided by Paytm
//        paramMap.put("TXN_AMOUNT", price); // transaction amount
//        paramMap.put("WEBSITE", "DIYtestingwap");//Provided by Paytm
//        paramMap.put( "EMAIL" , session.getEmail());
//        paramMap.put( "MOBILE_NO" , session.getMobile());
//        paramMap.put("CALLBACK_URL","https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderid);//Provided by Paytm
//        paramMap.put( "CHECKSUMHASH" , CHECKSUMHASH1);

        //paytm live ME11
        Map<String, String> paramMap = new HashMap<String,String>();
        paramMap.put("MID","ZQuxYB60583337152490");
        paramMap.put("ORDER_ID",orderid);
        paramMap.put("CUST_ID",customerid);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("TXN_AMOUNT", price);
        paramMap.put("WEBSITE", "DEFAULT");
        paramMap.put( "EMAIL" , session.getEmail());
        paramMap.put( "MOBILE_NO" , session.getMobile());
        paramMap.put("CALLBACK_URL","https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+orderid);//Provided by Paytm
        paramMap.put( "CHECKSUMHASH" , CHECKSUMHASH1);


        PaytmOrder Order = new PaytmOrder(paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(this, true, true, new PaytmPaymentTransactionCallback() {

            @Override
            public void someUIErrorOccurred(String inErrorMessage) {
                Log.d("LOG 1", "UI Error Occur.");
                Toast.makeText(MerchantActivity.this, "UI Error occured!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionResponse(Bundle inResponse) {
                Log.d("LOG 2", "Payment Transaction : " + inResponse);
                Log.e("RESPONSE",inResponse.toString());

                String status= (String) inResponse.get("STATUS");

                if(status.equals("TXN_SUCCESS")){
                    Intent ii = new Intent(MerchantActivity.this, PaymentSuccess.class);
                    ii.putExtra("payid",String.valueOf(inResponse.get("TXNID")));
                    ii.putExtra("email",session.getEmail());
                    ii.putExtra("txnid",orderid);
                    ii.putExtra("from",(String) inResponse.get("PAYMENTMODE"));
                    ii.putExtra("price",price);
                    startActivity(ii);
                    finish();
                }
                else{
                    String reason= (String)inResponse.get("RESPMSG");
                    Toast.makeText(MerchantActivity.this, reason, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                Log.d("LOG 3", "UI Error Occur.");
                Toast.makeText(MerchantActivity.this, "UI Error occured!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String inErrorMessage) {
                Log.d("LOG 4", "UI Error Occur.");

                Toast.makeText(MerchantActivity.this, "server side error "+ inErrorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                Log.d("LOG 5","web page error");
                finish();
            }
            @Override
            public void onBackPressedCancelTransaction() {
                Log.d("LOG 6","back pressed");
                finish();
            }

            @Override
            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                Log.d("LOG 7", "Payment Transaction Failed " + inErrorMessage);
                Toast.makeText(MerchantActivity.this, "Payment transaction failed", Toast.LENGTH_SHORT).show();
            }

        });
    }
}