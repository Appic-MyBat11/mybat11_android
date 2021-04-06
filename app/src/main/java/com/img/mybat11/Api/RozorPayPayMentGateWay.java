package com.img.mybat11.Api;



import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.AddBalanceActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.CashFree.PaymentActivity;
import com.img.mybat11.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RozorPayPayMentGateWay extends Activity implements PaymentResultWithDataListener {
    private static final String TAG = RozorPayPayMentGateWay.class.getSimpleName();

    Dialog progressDialog ;
    RequestQueue requestQueue;
    UserSessionManager session;
    ConnectionDetector cd;

    String price,razorpay_order_id,txnid;
    MainActivity ma;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rozor_pay_pay_ment_gate_way);

        requestQueue = Volley.newRequestQueue(this);
        cd = new ConnectionDetector(getApplicationContext());
        session = new UserSessionManager(getApplicationContext());
        ma = new MainActivity();
        price = getIntent().getExtras().getString("price");
        razorpay_order_id = getIntent().getStringExtra("razorpayid");
        txnid = getIntent().getStringExtra("orderid");


        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        Checkout.preload(getApplicationContext());
        startPayment();
    }

    public void startPayment() {
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "MyBat11");
            options.put("description", "Order id: "+razorpay_order_id);
            options.put("key", "rzp_live_H1yzsrPv3N8T5e");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("order_id",razorpay_order_id);
            options.put("amount", String.valueOf(Integer.parseInt(price)*100));
            options.put("payment_capture", true);

            JSONObject preFill = new JSONObject();
            preFill.put("email", session.getEmail());
            preFill.put("contact", session.getMobile());

            options.put("prefill", preFill);

            Log.i("params",options.toString());
            co.open(activity, options);
        } catch (Exception e) {
            ma.showErrorToast(RozorPayPayMentGateWay.this,"Error in payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response, PaymentData paymentData) {
        try {
            ma.showErrorToast(RozorPayPayMentGateWay.this,"Payment Failed");

        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try {
            ma.showToast(RozorPayPayMentGateWay.this,"Payment Success");
            Intent ii = new Intent(RozorPayPayMentGateWay.this, PaymentSuccess.class);
            ii.putExtra("payid",paymentData.getPaymentId());
            ii.putExtra("email",session.getEmail());
            ii.putExtra("txnid",txnid);
            ii.putExtra("from","cashfree");
            ii.putExtra("price",price);
            startActivity(ii);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }
}