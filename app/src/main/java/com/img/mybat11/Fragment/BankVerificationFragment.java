package com.img.mybat11.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Adapter.SpinnerAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.FileUtils;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.VerificationGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankVerificationFragment extends Fragment {

    TextView btnUpload,btnSubmit,bankText,holderText,holderName,withtype,withmobile;
    TextInputLayout name,accountNumber,vaccountNumber,branchName,ifscCode,bankName,mobileNumber,upiname;
    Spinner stateName,typeSpinner;
    UserSessionManager session;
    MainActivity ma;
    RequestQueue requestQueue;
    ConnectionDetector cd;
    Context context;
    ImageView uimage;
    String TAG="Bank";
    LinearLayout mobilety,upity;
    CardView invalidBank,bankNotVerified,bankVerified;
    String bankImage;
    String Simage = "";
//    File Simage = new File("");
    TextView comment;
    String state="",type="",condition="";
    View v;
    ArrayList<VerificationGetSet> verifyList;
    CheckBox iagree;
    CardView bankDetails;
    ImageView passbook,img;
    TextView t,accText,accNo,ifscText,ifsccode,bankNameText,bnkName,branchText,branch,stateText,stateNme,imageText,upinametext;

    public BankVerificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.fragment_bank_verification, container, false);

        context= getActivity();
        cd= new ConnectionDetector(context);
        session= new UserSessionManager(context);
        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(context);

        invalidBank=(CardView) v.findViewById(R.id.invalidBank);
        bankNotVerified=(CardView) v.findViewById(R.id.bankNotVerified);
        bankVerified=(CardView) v.findViewById(R.id.bankVerified);

        withtype=(TextView)v.findViewById(R.id.withtype);
        upinametext=(TextView)v.findViewById(R.id.upinametext);
        mobilety=(LinearLayout) v.findViewById(R.id.mobilety);
        upity=(LinearLayout) v.findViewById(R.id.upity);
        withmobile=(TextView)v.findViewById(R.id.withmobile);
        bankText=(TextView)v.findViewById(R.id.bankText);

        bankDetails=(CardView) v.findViewById(R.id.bankDetails);
        passbook=(ImageView)v.findViewById(R.id.passbook);
        img=(ImageView)v.findViewById(R.id.img);

        passbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(context);
                d.setContentView(R.layout.image_dialog);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView i = (ImageView) d.findViewById(R.id.image);
                Picasso.with(context).load(bankImage).into(i);

                ImageView bnCancel=(ImageView)d.findViewById(R.id.bnCancel);
                bnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });

                d.show();
            }
        });

        t= (TextView)v.findViewById(R.id.t);
        holderName= (TextView)v.findViewById(R.id.holderName);
        holderText= (TextView)v.findViewById(R.id.holderText);
        accNo= (TextView)v.findViewById(R.id.accNo);
        accText= (TextView)v.findViewById(R.id.accText);
        ifsccode= (TextView)v.findViewById(R.id.number);
        ifscText= (TextView)v.findViewById(R.id.ifscText);
        bankNameText= (TextView)v.findViewById(R.id.bankNameText);
        bnkName= (TextView)v.findViewById(R.id.bnkName);
        branchText= (TextView)v.findViewById(R.id.branchText);
        branch= (TextView)v.findViewById(R.id.branch);
        stateText= (TextView)v.findViewById(R.id.stateText);
        stateNme= (TextView)v.findViewById(R.id.state);
        imageText= (TextView)v.findViewById(R.id.imageText);
        comment= (TextView)v.findViewById(R.id.comment);

        btnUpload=(TextView)v.findViewById(R.id.btnUpload);
        btnSubmit=(TextView)v.findViewById(R.id.btnSubmit);
        uimage=(ImageView)v.findViewById(R.id.uimage);

        mobileNumber=(TextInputLayout)v.findViewById(R.id.mobileNumber);
        name=(TextInputLayout)v.findViewById(R.id.name);
        accountNumber=(TextInputLayout)v.findViewById(R.id.accountNumber);
        vaccountNumber=(TextInputLayout)v.findViewById(R.id.VaccountNumber);
        branchName=(TextInputLayout)v.findViewById(R.id.branchName);
        ifscCode=(TextInputLayout)v.findViewById(R.id.ifscCode);
        bankName=(TextInputLayout) v.findViewById(R.id.bankName);
        upiname=(TextInputLayout) v.findViewById(R.id.upiname);
        iagree=(CheckBox) v.findViewById(R.id.iagree);

        typeSpinner=(Spinner)v.findViewById(R.id.typeSpinner);
        stateName=(Spinner)v.findViewById(R.id.stateSpinner);
        final String typeAr[]= getActivity().getResources().getStringArray(R.array.withraw_type);
        typeSpinner.setAdapter(new SpinnerAdapter(getActivity(),typeAr));

        final String stateAr[]= getActivity().getResources().getStringArray(R.array.india_states);
        stateName.setAdapter(new SpinnerAdapter(getActivity(),stateAr));

        stateName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state= stateAr[i];
                // ((TextView) stateName.getSelectedView()).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type= typeAr[i];
                if(typeSpinner.getSelectedItemPosition()!=0) {
                    mobileNumber.setVisibility(View.VISIBLE);
                    upiname.setVisibility(View.VISIBLE);
                }else {
                    mobileNumber.setVisibility(View.GONE);
                    upiname.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
        if (cd.isConnectingToInternet())
            Allverify();


        Details();

        return v;
    }

    public void Details(){

        try {

            String url = getResources().getString(R.string.app_url)+"userfulldetails";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);


                                if (jsonObject.getString("username").equals("") || jsonObject.getString("username") == null){
                                    name.setEnabled(true);
                                }else {
                                    name.setEnabled(false);
                                    name.getEditText().setText(jsonObject.getString("username"));
                                }


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
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Details();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                            }
                        }
                    })
            {

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

    public void Allverify(){
        ma.showProgressDialog(context);
        try {

            String url = getResources().getString(R.string.app_url)+"allverify";
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

                                int bank_verify = jsonObject.getInt("bank_verify");
                                int pan_verify = jsonObject.getInt("pan_verify");

                                session.setBankVerified(String.valueOf(bank_verify));
                                session.setPANVerified(String.valueOf(pan_verify));
                                ma.dismissProgressDialog();

                                if(pan_verify == -1){
                                    invalidBank.setVisibility(View.VISIBLE);
                                    bankNotVerified.setVisibility(View.GONE);
                                    bankVerified.setVisibility(View.GONE);
                                }else if(bank_verify == -1){
                                    invalidBank.setVisibility(View.GONE);
                                    bankNotVerified.setVisibility(View.VISIBLE);
                                    bankVerified.setVisibility(View.GONE);
                                }else if(session.getBankVerified().equals("0")){
                                    invalidBank.setVisibility(View.GONE);
                                    comment.setVisibility(View.GONE);
                                    bankVerified.setVisibility(View.VISIBLE);
                                    bankText.setText("Your Bank details are sent for verification.");
                                    bankNotVerified.setVisibility(View.GONE);
                                    if(cd.isConnectingToInternet())
                                        BankDetails();
                                    else{
                                        ma.showToast(context,"Internet Connection Lost");
                                        context.startActivity(new Intent(context,MainActivity.class));
                                        ((Activity)context).finishAffinity();
                                    }
                                }else if(session.getBankVerified().equals("1")){
                                    invalidBank.setVisibility(View.GONE);
                                    bankNotVerified.setVisibility(View.GONE);
                                    bankVerified.setVisibility(View.VISIBLE );
                                    if(cd.isConnectingToInternet())
                                        BankDetails();
                                    else{
                                        ma.showToast(context,"Internet Connection Lost");
                                        context.startActivity(new Intent(context,MainActivity.class));
                                        ((Activity)context).finishAffinity();
                                    }
                                }else{
                                    invalidBank.setVisibility(View.GONE);
                                    bankNotVerified.setVisibility(View.VISIBLE);
                                    comment.setVisibility(View.VISIBLE);
                                    bankVerified.setVisibility(View.VISIBLE);
                                    bankText.setText("Your Bank details are Rejected.");
                                    bankText.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    if(cd.isConnectingToInternet())
                                        BankDetails();
                                    else{
                                        ma.showToast(context,"Internet Connection Lost");
                                        context.startActivity(new Intent(context,MainActivity.class));
                                        ((Activity)context).finishAffinity();
                                    }
                                }
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
                                ma.showToast(context,"Session Timeout");
                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Allverify();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ((Activity)context).finish();
                                    }
                                });
                            }
                        }
                    })
            {
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
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    @Override
    public void onResume() {
        super.onResume();

//
//
//        int pan_verify = Integer.parseInt(session.getPANVerified());
//        int bank_verify = Integer.parseInt(session.getBankVerified());
//
//        if(pan_verify == -1){
//            invalidBank.setVisibility(View.VISIBLE);
//            bankNotVerified.setVisibility(View.GONE);
//            bankVerified.setVisibility(View.GONE);
//        }else if(bank_verify == -1){
//            invalidBank.setVisibility(View.GONE);
//            bankNotVerified.setVisibility(View.VISIBLE);
//            bankVerified.setVisibility(View.GONE);
//        }else if(session.getBankVerified().equals("0")){
//            invalidBank.setVisibility(View.GONE);
//            bankVerified.setVisibility(View.VISIBLE);
//            bankText.setText("Your Bank details are sent for verification.");
//            bankNotVerified.setVisibility(View.GONE);
//            if(cd.isConnectingToInternet())
//                BankDetails();
//            else{
//                ma.showToast(context,"Internet Connection Lost");
//                context.startActivity(new Intent(context,MainActivity.class));
//                ((Activity)context).finishAffinity();
//            }
//        }else if(session.getBankVerified().equals("1")){
//            invalidBank.setVisibility(View.GONE);
//            bankNotVerified.setVisibility(View.GONE);
//            bankVerified.setVisibility(View.GONE);
//            if(cd.isConnectingToInternet())
//                BankDetails();
//            else{
//                ma.showToast(context,"Internet Connection Lost");
//                context.startActivity(new Intent(context,MainActivity.class));
//                ((Activity)context).finishAffinity();
//            }
//        }else{
//            invalidBank.setVisibility(View.GONE);
//            bankNotVerified.setVisibility(View.VISIBLE);
//            bankVerified.setVisibility(View.GONE);
//            comment.setVisibility(View.VISIBLE);
//            if(cd.isConnectingToInternet())
//                BankDetails();
//            else{
//                ma.showToast(context,"Internet Connection Lost");
//                context.startActivity(new Intent(context,MainActivity.class));
//                ((Activity)context).finishAffinity();
//            }
//        }

    }

    @Override public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()) {
            int pan_verify = Integer.parseInt(session.getPANVerified());
            int bank_verify = Integer.parseInt(session.getBankVerified());

            if(pan_verify == -1){
                invalidBank.setVisibility(View.VISIBLE);
                bankNotVerified.setVisibility(View.GONE);
            }else if(bank_verify == -1){
                invalidBank.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.VISIBLE);
            }else if(session.getBankVerified().equals("0")){
                invalidBank.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.GONE);
                if(cd.isConnectingToInternet())
                    BankDetails();
                else{
                    ma.showToast(context,"Internet Connection Lost");
                    context.startActivity(new Intent(context,MainActivity.class));
                    ((Activity)context).finishAffinity();
                }
            }else if(session.getBankVerified().equals("1")){
                invalidBank.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.GONE);
                t.setText("Bank Documents Verified");
                if(cd.isConnectingToInternet())
                    BankDetails();
                else{
                    ma.showToast(context,"Internet Connection Lost");
                    context.startActivity(new Intent(context,MainActivity.class));
                    ((Activity)context).finishAffinity();
                }
            }else{
                invalidBank.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.VISIBLE);
                comment.setVisibility(View.VISIBLE);
                if(cd.isConnectingToInternet())
                    BankDetails();
                else{
                    ma.showToast(context,"Internet Connection Lost");
                    context.startActivity(new Intent(context,MainActivity.class));
                    ((Activity)context).finishAffinity();
                }
            }
        }
    }


    public void validate(){
        if(vaccountNumber.getEditText().getText().toString().length()<1)
            vaccountNumber.setError("Please enter valid account number");
        else if(accountNumber.getEditText().getText().toString().length()<1)
            accountNumber.setError("Please enter valid account number");
        else if(!vaccountNumber.getEditText().getText().toString().equals(accountNumber.getEditText().getText().toString()))
            vaccountNumber.setError("Your account number and verify account number not matched");
        else if(!validIfsc(ifscCode.getEditText().getText().toString()))
            ifscCode.setError("Please enter valid IFSC Code");
        else if(branchName.getEditText().getText().toString().length()<1)
            branchName.setError("Please enter valid branch name");
        else if(bankName.getEditText().getText().toString().length()<1)
            bankName.setError("Please enter valid bank name");
        else if(stateName.getSelectedItemPosition()==0)
            Toast.makeText(context, "Please select your state", Toast.LENGTH_SHORT).show();
        else if(Simage.equals("")) {
            ma.showToast(context,"Please click a image of passbook first");
        }else if(typeSpinner.getSelectedItemPosition()!=0 && mobileNumber.getEditText().getText().toString().length()<1) {
                mobileNumber.setError("Please enter valid mobile number");
        }else if(typeSpinner.getSelectedItemPosition()!=0 && upiname.getEditText().getText().toString().length()<3)
            upiname.setError("Please enter valid name");
        else{
            if(cd.isConnectingToInternet()) {
                if(iagree.isChecked()){
                    condition = "1";
//                    Toast.makeText(context, condition, Toast.LENGTH_SHORT).show();
                    VerifyBankDetails();
                }else {
                    condition = "0";
//                    Toast.makeText(context, condition, Toast.LENGTH_SHORT).show();
                    ma.showErrorToast(context, "Please Agree with details");
                }
            }
            else{
                ma.showErrorToast(context,"No Internet");
            }
        }
    }

    public boolean validIfsc(String text){
        // The IFSC is an 11-character code with the
        // first four alphabetic characters representing the bank name,
        // and the last six characters (usually numeric, but can be alphabetic) representing the branch.
        // The fifth character is 0 (zero) and reserved for future use.

        String pattern = "^[A-Za-z]{4}[0][a-zA-Z0-9]{6}$";

        if(text.matches(pattern))
            return true;
        return false;
    }


    public void selectImage()
    {

        String[] perarr = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.VIBRATE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.DELETE_CACHE_FILES,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.INSTALL_PACKAGES,
                Manifest.permission.REQUEST_INSTALL_PACKAGES
        };
        ActivityCompat.requestPermissions(getActivity(), perarr, 1);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a image");

        final String [] items = {"Take Photo..","Choose Image From Library"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                switch (position){
                    case 0:
                        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(cameraIntent, 4);
                        break;
                    case 1:
                        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(gallaryIntent, 1);
                        break;
                }
            }
        });
        final AlertDialog alert = builder.create();
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri image = data.getData();

                Bitmap bitmap=null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bitmap = getResizedBitmap(bitmap, 2000);// 400 is for example, replace with desired size
//                passbook.setImageBitmap(bitmap);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) img.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            }
            else {

            }
        }
        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
//                passbook.setImageBitmap(image);
                img.setImageBitmap(image);
                img.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) img.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }




//    public void selectImage()
//    {
//
//        String[] perarr = new String[]{
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.INTERNET,
//                Manifest.permission.VIBRATE,
//                Manifest.permission.GET_ACCOUNTS,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.DELETE_CACHE_FILES,
//                Manifest.permission.CALL_PHONE,
//                Manifest.permission.MANAGE_DOCUMENTS,
//                Manifest.permission.INSTALL_PACKAGES,
//                Manifest.permission.REQUEST_INSTALL_PACKAGES
//        };
//        ActivityCompat.requestPermissions(getActivity(), perarr, 1);
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Select a image");
//
//        final String [] items = {"Take Photo..","Choose From Library.."};
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int position) {
//                switch (position){
//                    case 0:
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
//                                context.getApplicationContext().getPackageName() + ".provider", f));
//                        startActivityForResult(intent, 1);
//                        break;
//                    case 1:
//                        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(gallaryIntent, 2);
//                        break;
//                }
//            }
//        });
//        final AlertDialog alert = builder.create();
//        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                alert.dismiss();
//            }
//        });
//        alert.show();
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1) {   // image camera
//            if (resultCode == Activity.RESULT_OK) {
//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
//                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//                            bitmapOptions);
//
//                    String path = android.os.Environment
//                            .getExternalStorageDirectory()
//                            + File.separator
//                            + "" ;
//// f.delete();
//
//                    OutputStream outFile = null;
//                    Simage = new File(path, "temp.jpg");
//
//                    String path1 = Simage.getAbsolutePath();
//                    passbook.setImageBitmap(bitmap);
//                    img.setImageBitmap(bitmap);
//
//                    Log.d("filePath", path1);
//
//                    try {
//                        outFile = new FileOutputStream(Simage);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outFile);
//                        outFile.flush();
//                        outFile.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }
//
//        if (requestCode == 2) {   // image gallery
//            if (resultCode == Activity.RESULT_OK) {
//
//                Uri image = data.getData();
//                String[] filePath = { MediaStore.Images.Media.DATA };
//                Cursor cursor = context.getContentResolver().query(image, filePath, null, null, null);
////                cursor.moveToFirst();
//
//                Log.d("Image", DatabaseUtils.dumpCursorToString(cursor));
//
//// String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
//                String imagePath = new FileUtils().getRealPath(context,image);
//
////                cursor.close();
//                Log.i("image is",imagePath);
//
//                Simage= new File(imagePath);
//                if(Simage.exists()){
//                    Bitmap myBitmap = BitmapFactory.decodeFile(Simage.getAbsolutePath());
//                    passbook.setImageBitmap(myBitmap);
//                    img.setImageBitmap(myBitmap);
//                }
//
//                Log.d("picUri", image.toString());
//                Log.d("filePath", imagePath);
//
//            }
//            else {
//
//            }
//        }
//
//    }

















//    public void renameFile(File file,String newName) {
//
//        String ext = FilenameUtils.getExtension(file.getAbsolutePath());
//        File dir = file.getParentFile();
//
//        if(dir.exists()){
//            File from = new File(dir,file.getName());
//            String name = file.getName();
//            int pos = name.lastIndexOf(".");
//            if (pos > 0) {
//                name = name.substring(0, pos);
//            }
//            File to = new File(dir,newName+"."+ext);
//            if(from.exists()) {
//                from.renameTo(to);
//            }
//            Log.i("Check to FIle",to.toString());
//
//        }
//
//    }



//    public void UploadImage() {
//
//        ma.showProgressDialog(context);
//
//
//        AmazonS3Client s3;
//        BasicAWSCredentials credentials;
//        TransferUtility transferUtility;
//        final TransferObserver observer;
//        String key = "ZTKIP7EJTSKPOXGX3LH3";
//        String secret = "F5amdzSi5ebMN3aAa+hgDogZT0tixrC3uOLHjGeB5dw";
//
//
//        credentials = new BasicAWSCredentials(key, secret);
//        s3 = new AmazonS3Client(credentials);
//        s3.setEndpoint("https://nyc3.digitaloceanspaces.com/");
//
//        transferUtility = new TransferUtility(s3, context);
//        CannedAccessControlList filePermission = CannedAccessControlList.PublicRead;
//        final File file = Simage;
//        Long tsLong = System.currentTimeMillis()/1000;
//        String ts = tsLong.toString();
//        final String time = ""+ts+session.getReferalCode()+file.getName();
//        observer = transferUtility.upload(
//                "mybat11", //empty bucket name, included in endpoint
//                time,
//                file, //a File object that you want to upload
//                filePermission
//        );
//
//        observer.setTransferListener(new TransferListener() {
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (state.COMPLETED.equals(observer.getState())) {
////                    Toast.makeText(context, "Space upload completed !!", Toast.LENGTH_SHORT).show();
//                    Log.i("logdata", "https://mybat11.nyc3.digitaloceanspaces.com/" + time);
//                    String imgurl = "https://mybat11.nyc3.digitaloceanspaces.com/" + time;
//                    VerifyBankDetails(imgurl);
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                Toast.makeText(context, "Space upload error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });




    //AWS
//        final CognitoCachingCredentialsProvider credentialsProvider;
//        credentialsProvider = new CognitoCachingCredentialsProvider(
//                context.getApplicationContext(),
//                "ap-south-1:28fa930f-0308-48cd-9d3e-6cf804f0ea0a", // Identity Pool ID
//                Regions.AP_SOUTH_1 // Region
//        );
//
//        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
//
//        final File file = Simage;
//        Long tsLong = System.currentTimeMillis()/1000;
//        String ts = tsLong.toString();
//        final String time = ""+ts+session.getReferalCode()+file.getName();
//        TransferUtility transferUtility = new TransferUtility(s3, context);
//        final TransferObserver observer = transferUtility.upload(
//                "mybat11",  //this is the bucket name on S3
//                time,
//                file
//        );
//        observer.setTransferListener(new TransferListener() {
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                Log.e("onStateChanged", id + state.name());
//                if (state.equals(TransferState.COMPLETED)) {
//                    Log.i("logdata", "https://mybat11.s3.ap-south-1.amazonaws.com/" + time);
//                    String imgurl = "https://mybat11.s3.ap-south-1.amazonaws.com/" + time;
//                    VerifyBankDetails(imgurl);
//                } else if (state.equals(TransferState.FAILED)) {
//                    ma.dismissProgressDialog();
//                    ma.showErrorToast(context,"Slow network connection please try again");
//                }
//
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//
//                Log.e("error", String.valueOf(ex));
//            }
//        });

//    }

    public void BankDetails(){
        ma.showProgressDialog(context);
        try {

            String url = getResources().getString(R.string.app_url)+"seebankdetails";
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

                                accNo.setText(jsonObject.getString("accno"));
                                ifsccode.setText(jsonObject.getString("ifsc"));
                                bnkName.setText(jsonObject.getString("bankname"));
                                branch.setText(jsonObject.getString("bankbranch"));
                                stateNme.setText(jsonObject.getString("state"));
                                holderName.setText(jsonObject.getString("accountholdername"));
                                withtype.setText(jsonObject.getString("type"));
                                if (!jsonObject.getString("mobile").equals("")) {
                                    mobilety.setVisibility(View.VISIBLE);
                                    upity.setVisibility(View.VISIBLE);
                                    withmobile.setText(jsonObject.getString("mobile"));
                                    if (jsonObject.getString("gname").equals("")) {
                                        upity.setVisibility(View.GONE);
                                    }else {
                                        upinametext.setText(jsonObject.getString("gname"));
                                    }
                                }

                                bankImage = jsonObject.getString("image");
                                if (!bankImage.equals("")) {
                                    Picasso.with(context).load(bankImage).into(passbook);
                                }
                                if(jsonObject.has("comment"))
                                    comment.setText(jsonObject.getString("comment"));

                                bankDetails.setVisibility(View.VISIBLE);
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
                            ma.dismissProgressDialog();
                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }
                        }
                    })
            {

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


    public void VerifyBankDetails(){
        try {
            String url = getResources().getString(R.string.app_url)+"bankrequest1";
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

                                ma.dismissProgressDialog();
                                if(jsonObject.getBoolean("status")){

                                    session.setBankVerified("0");
                                    invalidBank.setVisibility(View.GONE);
                                    bankVerified.setVisibility(View.VISIBLE);
                                    bankText.setText("Your Bank details are sent for verification.");
                                    bankNotVerified.setVisibility(View.GONE);
                                    comment.setVisibility(View.GONE);


                                    ma.dismissProgressDialog();
                                    BankDetails();
                                }
                                ma.showToast(context,jsonObject.getString("msg"));
                                ma.dismissProgressDialog();
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                                ma.dismissProgressDialog();
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
                                ma.showToast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }
                        }
                    })
            {

                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> map = new HashMap<>();
                    map.put("accountholder",name.getEditText().getText().toString());
                    map.put("accno",accountNumber.getEditText().getText().toString());
                    map.put("ifsc",ifscCode.getEditText().getText().toString());
                    map.put("bankname",bankName.getEditText().getText().toString());
                    map.put("bankbranch",branchName.getEditText().getText().toString());
                    map.put("state",state);
                    map.put("image",Simage);
                    map.put("mobile",mobileNumber.getEditText().getText().toString());
                    map.put("gname",upiname.getEditText().getText().toString());
                    map.put("iagree",condition);
                    map.put("type",type);

                    Log.i("params",map.toString());

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
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

}
