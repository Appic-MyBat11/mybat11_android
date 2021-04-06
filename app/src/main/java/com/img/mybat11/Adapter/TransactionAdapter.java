package com.img.mybat11.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.WithdrawActivity;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.TransactionGetSet;
import com.img.mybat11.GetSet.msgStatusGetSet;
import com.img.mybat11.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionAdapter extends BaseAdapter {

    Context context;
    ArrayList<TransactionGetSet> list;
    ArrayList<String> name,image,id,gender,fatherName,bloodGroup;
    GlobalVariables gv;
    ConnectionDetector cd;
    UserSessionManager session;
    MainActivity ma;
    RequestQueue requestQueue;

    public TransactionAdapter(Context context, ArrayList<TransactionGetSet> list){
        this.list= list;
        this.context = context;


        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(context);
        gv= (GlobalVariables)context.getApplicationContext();
        session= new UserSessionManager(context);
        cd = new ConnectionDetector(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        View v;
        final ImageView expand;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.transaction_list,null);

        final LinearLayout infoLL;
        CardView doneinvoice;
        TextView credit,transactionID,date,status,teamName;

        expand=(ImageView)v.findViewById(R.id.expand);
        credit=(TextView)v.findViewById(R.id.credit);
        transactionID=(TextView)v.findViewById(R.id.transactionID);
        teamName=(TextView)v.findViewById(R.id.teamName);
        date=(TextView)v.findViewById(R.id.date);
        status=(TextView)v.findViewById(R.id.status);
        doneinvoice=(CardView) v.findViewById(R.id.doneinvoice);

        date.setText(list.get(i).getDate_time());

        credit.setText("₹ "+list.get(i).getAmount());
        status.setText(list.get(i).getType());

        transactionID.setText("Transaction id: "+list.get(i).getTxnid());
        teamName.setText("Team : "+list.get(i).getTeam());
        if (list.get(i).getType().equals("Contest Joining Fee")){
            doneinvoice.setVisibility(View.VISIBLE);
        }else {
            doneinvoice.setVisibility(View.GONE);
        }

        infoLL= (LinearLayout)v.findViewById(R.id.infoLL);
        infoLL.setVisibility(View.GONE);

        doneinvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invoice(list.get(i).getId());
            }
        });
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoLL.getVisibility()==View.VISIBLE){
                    infoLL.setVisibility(View.GONE);
                    expand.setImageResource(R.drawable.ic_expand_white);
                }
                else{
                    infoLL.setVisibility(View.VISIBLE);
                    expand.setImageResource(R.drawable.ic_collapse_white);
                }
            }
        });

        return v;
    }


    public void invoice(final String id){
        ma.showProgressDialog(context);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<msgStatusGetSet>> call;
        call = apiSeitemViewice.invoicegenerate(session.getUserId(),id);
        call.enqueue(new Callback<ArrayList<msgStatusGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<msgStatusGetSet>> call, Response<ArrayList<msgStatusGetSet>> response) {


                if(response.code() == 200) {
                    ArrayList<msgStatusGetSet>list = response.body();

                    ma.showToast(context,list.get(0).getMsg());

                    ma.dismissProgressDialog();
                }else {
                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            invoice(id);
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
            public void onFailure(Call<ArrayList<msgStatusGetSet>>call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
            }
        });
    }

}
