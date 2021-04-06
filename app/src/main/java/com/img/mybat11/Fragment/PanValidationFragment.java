package com.img.mybat11.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.os.Build;
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
import android.widget.DatePicker;
import android.widget.ImageView;
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
import com.img.mybat11.Activity.LoginActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.PersonalDetailsActivity;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class PanValidationFragment extends Fragment {

    TextInputLayout name, panNumber;
    TextView date, btnUpload, btnSubmit;
    Context context;
    String dob;
    MainActivity ma;
    RequestQueue requestQueue;
    ConnectionDetector cd;
    ArrayList<VerificationGetSet> verifyList;
    UserSessionManager session;
    CardView invalidRequest, cardNotVerified, cardVerified, cardDetails;
    ImageView pancard,img;
    TextView t, nameText, panname, numberText, number, dobText, pandob, imageText;
    TextView panVerified;
    String TAG = "Pan validation";
//    File Simage = new File("");
    String Simage = "";
    String panImage;
    TextView comment;
    View v;

    public PanValidationFragment() {
    }

    public void pickDate(final TextView dialog) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                String d = selectedyear + "-" + (selectedmonth) + "-" + selectedday;

                Date date1;
                String date2 = null;
                try {
                    DateFormat d1 = new SimpleDateFormat("yyyy-MM-dd");
                    date1 = d1.parse(d);
                    DateFormat d2 = new SimpleDateFormat("dd-MMM-yyyy");
                    date2 = d2.format(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dialog.setText("Date : " + date2);
                dob = d;

            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMaxDate((long) (System.currentTimeMillis() - (5.681e+11)));
        mDatePicker.setTitle("Select Birth Date");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDatePicker.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        }
        mDatePicker.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_pan_validation, container, false);
        context = getActivity();

        cd = new ConnectionDetector(context);
        session = new UserSessionManager(context);
        ma = new MainActivity();
        requestQueue = Volley.newRequestQueue(context);

        name = (TextInputLayout) v.findViewById(R.id.name);
        panNumber = (TextInputLayout) v.findViewById(R.id.panNumber);
        btnUpload = (TextView) v.findViewById(R.id.btnUpload);
        btnSubmit = (TextView) v.findViewById(R.id.btnSubmit);
        date = (TextView) v.findViewById(R.id.dob);
        comment = (TextView) v.findViewById(R.id.comment);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(date);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        invalidRequest = (CardView) v.findViewById(R.id.invalidRequest);
        cardNotVerified = (CardView) v.findViewById(R.id.cardNotVerified);
        cardVerified = (CardView) v.findViewById(R.id.cardVerified);
        cardDetails = (CardView) v.findViewById(R.id.cardDetails);

        pancard = (ImageView) v.findViewById(R.id.pancard);
        img = (ImageView) v.findViewById(R.id.img);
        pancard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(context);
                d.setContentView(R.layout.image_dialog);
                d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView i = (ImageView) d.findViewById(R.id.image);
                Picasso.with(context).load(panImage).into(i);

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

        t = (TextView) v.findViewById(R.id.t);
        nameText = (TextView) v.findViewById(R.id.nameText);
        panname = (TextView) v.findViewById(R.id.panname);
        numberText = (TextView) v.findViewById(R.id.numberText);
        number = (TextView) v.findViewById(R.id.number);
        dobText = (TextView) v.findViewById(R.id.dobText);
        pandob = (TextView) v.findViewById(R.id.pandob);
        imageText = (TextView) v.findViewById(R.id.imageText);

        panVerified = (TextView) v.findViewById(R.id.panVerified);


        if(session.isMobileVerified() && session.isEmailVerified()) {
            if (cd.isConnectingToInternet())
                Allverify();
        }

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

                                if (String.valueOf(pan_verify).equals("0")) {
                                    panVerified.setText("Your PAN Card details are sent for verification.");
                                    cardVerified.setVisibility(View.VISIBLE);
                                    invalidRequest.setVisibility(View.GONE);
                                    cardNotVerified.setVisibility(View.GONE);

                                    if(cd.isConnectingToInternet())
                                        PANDetails();
                                    else{
                                        ma.showToast(context,"Internet Connection Lost");
                                        context.startActivity(new Intent(context,MainActivity.class));
                                        ((Activity)context).finishAffinity();
                                    }
                                } else if (String.valueOf(pan_verify).equals("1")) {
                                    invalidRequest.setVisibility(View.GONE);
                                    cardNotVerified.setVisibility(View.GONE);
                                    cardVerified.setVisibility(View.VISIBLE);
                                    if(cd.isConnectingToInternet())
                                        PANDetails();
                                    else{
                                        ma.showToast(context,"Internet Connection Lost");
                                    }
                                } else if (String.valueOf(pan_verify).equals("-1")) {
                                    if(jsonObject.getInt("mobile_verify") ==1 && jsonObject.getInt("email_verify") ==1){
                                        invalidRequest.setVisibility(View.GONE);
                                        cardVerified.setVisibility(View.GONE);
                                        cardNotVerified.setVisibility(View.VISIBLE);
                                    }else {
                                        invalidRequest.setVisibility(View.VISIBLE);
                                        cardVerified.setVisibility(View.GONE);
                                        cardNotVerified.setVisibility(View.GONE);
                                    }
                                } else {
                                    invalidRequest.setVisibility(View.GONE);
                                    cardNotVerified.setVisibility(View.VISIBLE);
                                    comment.setVisibility(View.VISIBLE);
                                    panVerified.setText("Your PAN Card details are Rejected.");
                                    panVerified.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    cardVerified.setVisibility(View.VISIBLE);
                                    if(cd.isConnectingToInternet())
                                        PANDetails();
                                    else{
                                        ma.showToast(context,"Internet Connection Lost");
                                    }
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
//        if(session.isMobileVerified() && session.isEmailVerified()) {
//            if (cd.isConnectingToInternet())
//                Allverify();
//
//            String pan_verify = session.getPANVerified();
//
//            if (String.valueOf(pan_verify).equals("0")) {
//                panVerified.setText("Your PAN Card details are sent for verification.");
//                invalidRequest.setVisibility(View.GONE);
//                cardVerified.setVisibility(View.VISIBLE);
//                cardNotVerified.setVisibility(View.GONE);
//
//                if(cd.isConnectingToInternet())
//                    PANDetails();
//
//            } else if (String.valueOf(pan_verify).equals("1")) {
//                invalidRequest.setVisibility(View.GONE);
//                cardNotVerified.setVisibility(View.GONE);
//                if(cd.isConnectingToInternet())
//                    PANDetails();
//                else{
//                    ma.showToast(context,"Internet Connection Lost");
//                }
//            } else if (String.valueOf(pan_verify).equals("-1")) {
//                if(session.isMobileVerified() && session.isEmailVerified()){
//                    invalidRequest.setVisibility(View.GONE);
//                    cardVerified.setVisibility(View.GONE);
//                    cardNotVerified.setVisibility(View.VISIBLE);
//                }else {
//                    invalidRequest.setVisibility(View.VISIBLE);
//                    cardVerified.setVisibility(View.GONE);
//                    cardNotVerified.setVisibility(View.GONE);
//                }
//            } else {
//                invalidRequest.setVisibility(View.GONE);
//                cardNotVerified.setVisibility(View.VISIBLE);
//                comment.setVisibility(View.VISIBLE);
//                if(cd.isConnectingToInternet())
//                    PANDetails();
//                else{
//                    ma.showToast(context,"Internet Connection Lost");
//                }
//            }
//
//        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            if(session.isMobileVerified() && session.isEmailVerified()) {

                String pan_verify = session.getPANVerified();

                if (String.valueOf(pan_verify).equals("0")) {
                    invalidRequest.setVisibility(View.GONE);
                    cardNotVerified.setVisibility(View.GONE);

                    if(cd.isConnectingToInternet())
                        PANDetails();
                    else{
                        ma.showToast(context,"Internet Connection Lost");
                        ((Activity)context).finish();
                    }
                } else if (String.valueOf(pan_verify).equals("1")) {
                    invalidRequest.setVisibility(View.GONE);
                    cardNotVerified.setVisibility(View.GONE);
                    t.setText("PAN Card Verified");
                    if(cd.isConnectingToInternet())
                        PANDetails();
                    else{
                        ma.showToast(context,"Internet Connection Lost");
                        ((Activity)context).finish();
                    }
                } else if (String.valueOf(pan_verify).equals("-1")) {
                    if(session.isMobileVerified() && session.isEmailVerified()){
                        invalidRequest.setVisibility(View.GONE);
                        cardNotVerified.setVisibility(View.VISIBLE);
                    }else {
                        invalidRequest.setVisibility(View.VISIBLE);
                        cardNotVerified.setVisibility(View.GONE);
                    }
                } else {
                    invalidRequest.setVisibility(View.GONE);
                    cardNotVerified.setVisibility(View.VISIBLE);
                    comment.setVisibility(View.VISIBLE);
                    if(cd.isConnectingToInternet())
                        PANDetails();
                    else{
                        ma.showToast(context,"Internet Connection Lost");
                        ((Activity)context).finish();
                    }
                }

            }
        }
    }

    public boolean validPan() {
        String text = panNumber.getEditText().getText().toString();
        if (text.length() != 10) {
            return false;
        } else if (!text.matches("(([A-Za-z]{5})([0-9]{4})([a-zA-Z]))")) {
            return false;
        } else
            return true;
    }

    public void validate() {
        if (name.getEditText().getText().toString().length() < 4)
            name.setError("Please enter a valid name");
        else if (!validPan())
            panNumber.setError("Please enter a valid PAN Number");
        else if (date.getText().toString().equals("Date of birth*"))
            date.setError("Please enter your Date of birth");
        else if (Simage.equals("")) {
            ma.showToast(context,"Upload PAN Card Image");
        } else {
            VerifyPanDetails();
        }

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
//                    pancard.setImageBitmap(bitmap);
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
//                    pancard.setImageBitmap(myBitmap);
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




    //AWS
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



//        public void UploadImage() {
//
//            ma.showProgressDialog(context);
//
//
//            AmazonS3Client s3;
//            BasicAWSCredentials credentials;
//            TransferUtility transferUtility;
//            final TransferObserver observer;
//            String key = "ZTKIP7EJTSKPOXGX3LH3";
//            String secret = "F5amdzSi5ebMN3aAa+hgDogZT0tixrC3uOLHjGeB5dw";
//
//
//            credentials = new BasicAWSCredentials(key, secret);
//            s3 = new AmazonS3Client(credentials);
//            s3.setEndpoint("https://nyc3.digitaloceanspaces.com/");
//
//            transferUtility = new TransferUtility(s3, context);
//            CannedAccessControlList filePermission = CannedAccessControlList.PublicRead;
//            final File file = Simage;
//            Long tsLong = System.currentTimeMillis()/1000;
//            String ts = tsLong.toString();
//            final String time = ""+ts+session.getReferalCode()+file.getName();
//            observer = transferUtility.upload(
//                    "mybat11", //empty bucket name, included in endpoint
//                    time,
//                    file, //a File object that you want to upload
//                    filePermission
//            );
//
//            observer.setTransferListener(new TransferListener() {
//                @Override
//                public void onStateChanged(int id, TransferState state) {
//                    if (state.COMPLETED.equals(observer.getState())) {
////                    Toast.makeText(context, "Space upload completed !!", Toast.LENGTH_SHORT).show();
//                        Log.i("logdata", "https://mybat11.nyc3.digitaloceanspaces.com/" + time);
//                        String imgurl = "https://mybat11.nyc3.digitaloceanspaces.com/" + time;
//                        VerifyPanDetails(imgurl);
//                    }
//                }
//
//                @Override
//                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                }
//
//                @Override
//                public void onError(int id, Exception ex) {
//                    Toast.makeText(context, "Space upload error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }






    public void PANDetails(){
        try {
            String url = getResources().getString(R.string.app_url)+"getpandetails";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            ma.dismissProgressDialog();
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                panname.setText(jsonObject.getString("panname"));
                                number.setText(jsonObject.getString("pannumber"));
                                pandob.setText(jsonObject.getString("pandob"));

                                panImage = jsonObject.getString("image");
                                if (!panImage.equals("")) {
                                    Picasso.with(context).load(panImage).into(pancard);
                                }

                                if(jsonObject.has("comment"))
                                    comment.setText(jsonObject.getString("comment"));

                                cardDetails.setVisibility(View.VISIBLE);
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

    public void VerifyPanDetails(){
        try {
            String url = getResources().getString(R.string.app_url)+"panrequest1";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                ma.dismissProgressDialog();
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);
                                if(jsonObject.getBoolean("status")){
                                    session.setPANVerified("0");
                                    panVerified.setText("Your PAN Card details are sent for verification.");
                                    invalidRequest.setVisibility(View.GONE);
                                    cardVerified.setVisibility(View.VISIBLE);
                                    cardNotVerified.setVisibility(View.GONE);
                                    comment.setVisibility(View.GONE);

                                    PANDetails();
                                }
                                ma.showToast(context,jsonObject.getString("msg"));
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
                    map.put("panname",name.getEditText().getText().toString());
                    map.put("pannumber",panNumber.getEditText().getText().toString());
                    map.put("dob",dob);
                    map.put("image",Simage);
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
