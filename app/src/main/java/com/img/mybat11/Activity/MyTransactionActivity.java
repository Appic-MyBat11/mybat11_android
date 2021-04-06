package com.img.mybat11.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mybat11.Adapter.TransactionAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.TransactionGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTransactionActivity extends AppCompatActivity {

    String TAG="Wallet";
    TextView title;
    UserSessionManager session;
    ConnectionDetector cd;
    GlobalVariables gv;
    MainActivity ma;

    ListView transactionList;
    TextView credit,debit;
    LinearLayout noTransactionLL;
    ArrayList<TransactionGetSet> Translist;
    String sortByC="asc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_transaction);

        ma = new MainActivity();
        session= new UserSessionManager(getApplicationContext());
        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("My Transactions");

        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        transactionList = (ListView) findViewById(R.id.transactionList);
        noTransactionLL=(LinearLayout)findViewById(R.id.noTransactionLL);

        credit=(TextView)findViewById(R.id.credit);
        debit=(TextView)findViewById(R.id.debit);

        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sortByC.equals("asc")){
                    Collections.sort(Translist, new Comparator<TransactionGetSet>() {
                        @Override
                        public int compare(TransactionGetSet ob1, TransactionGetSet ob2) {
                            return (Double.parseDouble(ob1.getAmount()) > Double.parseDouble(ob2.getAmount())) ? -1: (Double.parseDouble(ob1.getAmount()) > Double.parseDouble(ob2.getAmount())) ? 1:0 ;
                        }
                    });
                    sortByC="dsc";
                }else{
                    Collections.sort(Translist, new Comparator<TransactionGetSet>() {
                        @Override
                        public int compare(TransactionGetSet ob1, TransactionGetSet ob2) {
                            return (Double.parseDouble(ob1.getAmount()) < Double.parseDouble(ob2.getAmount())) ? -1: (Double.parseDouble(ob1.getAmount()) > Double.parseDouble(ob2.getAmount())) ? 1:0 ;
                        }
                    });
                    sortByC="asc";
                }
                transactionList.setAdapter(new TransactionAdapter(MyTransactionActivity.this,Translist));
            }
        });

        if(cd.isConnectingToInternet()) {
            MyTransaction();
        }
    }

    public void MyTransaction(){
        ma.showProgressDialog(MyTransactionActivity.this);

        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<TransactionGetSet>> call = apiSeitemViewice.transactionList(session.getUserId());
        call.enqueue(new Callback<ArrayList<TransactionGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<TransactionGetSet>> call, Response<ArrayList<TransactionGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");
                Log.i(TAG, "Number of movies received: " + response.toString());
                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    Translist = response.body();
                    if (Translist.get(0).getStatus().equals("0")) {
                        transactionList.setVisibility(View.GONE);
                        noTransactionLL.setVisibility(View.VISIBLE);
                    } else {
                        transactionList.setAdapter(new TransactionAdapter(MyTransactionActivity.this, Translist));
                    }
                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(MyTransactionActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTransaction();
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
            public void onFailure(Call<ArrayList<TransactionGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }

}
