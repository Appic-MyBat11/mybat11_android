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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Activity.HomeActivity;
import com.img.mybat11.Activity.LoginActivity;
import com.img.mybat11.Activity.MainActivity;
import com.img.mybat11.Activity.MeActivity;
import com.img.mybat11.Adapter.AvatarAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.FileUtils;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.avatarGetSet;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.Constants;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class mobileVarificationFragment extends Fragment {

    TextInputLayout email, mobile;
    TextView verifyEmail, verifyMobile;
    Context context;
    UserSessionManager session;
    ConnectionDetector cd;
    String imgurl="";
    CardView emailVerified, emailVerify;
    TextView emailText;
    CardView mobileVerified, mobileVerify;
    TextView mobileText;
    MainActivity ma;
    RequestQueue requestQueue;
    View v;
    CircleImageView userImage;
    Dialog d;
    LinearLayout camera;
//    File Simage;
    String Simage = "";
    TextView reson,imagetitle;
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    public mobileVarificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_mobile_varification, container, false);
        context = getActivity();

        ma = new MainActivity();

        requestQueue = Volley.newRequestQueue(context);
        session = new UserSessionManager(context);
        cd = new ConnectionDetector(context);

        email = (TextInputLayout) v.findViewById(R.id.email);
        mobile = (TextInputLayout) v.findViewById(R.id.mobileNumber);

        emailVerified = (CardView) v.findViewById(R.id.emailVerified);
        emailVerify = (CardView) v.findViewById(R.id.emailVerify);

        email.getEditText().setText(session.getEmail());
        mobile.getEditText().setText(session.getMobile());

        emailText = (TextView) v.findViewById(R.id.emailText);

        emailText.setText(session.getEmail());

        mobileVerified = (CardView) v.findViewById(R.id.mobileVerified);
        mobileVerify = (CardView) v.findViewById(R.id.mobileVerify);

        mobileText = (TextView) v.findViewById(R.id.mobileText);
        reson = (TextView) v.findViewById(R.id.reson);
        imagetitle = (TextView) v.findViewById(R.id.imagetitle);

        mobileText.setText(session.getMobile());

        verifyEmail = (TextView) v.findViewById(R.id.verifyEmail);
        verifyMobile = (TextView) v.findViewById(R.id.verifyMobile);
        userImage = (CircleImageView) v.findViewById(R.id.userImage);
        camera = (LinearLayout) v.findViewById(R.id.camera);

        verifyMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobile.getEditText().getText().toString().length() != 10)
                    mobile.setError("Please enter valid mobile number");
                else {
                    verifyMobile(mobile.getEditText().getText().toString());
                }
            }
        });

        camera =(LinearLayout) v.findViewById(R.id.camera);

//        credentialsProvider();
//        setTransferUtility();

        if(cd.isConnectingToInternet()) {
            Allverify();
            userimg();
        }
        return v;
    }


//    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
//        if (sCredProvider == null) {
//            sCredProvider = new CognitoCachingCredentialsProvider(
//                    context.getApplicationContext(),
//                    "ap-south-1:28fa930f-0308-48cd-9d3e-6cf804f0ea0a",
//                    Regions.fromName(Regions.AP_SOUTH_1));
//        }
//        return sCredProvider;
//    }


    @Override
    public void onResume() {
        super.onResume();
        if (session.isMobileVerified()) {
            session.setMobileVerified(true);
            mobileVerified.setVisibility(View.VISIBLE);
            mobileVerify.setVisibility(View.GONE);
            mobileText.setText(session.getMobile());
        }else {
            session.setMobileVerified(false);
            mobileVerified.setVisibility(View.GONE);
            mobileVerify.setVisibility(View.VISIBLE);
        }

        if (session.isEmailVerified()) {
            session.setEmailVerified(true);
            emailVerified.setVisibility(View.VISIBLE);
            emailVerify.setVisibility(View.GONE);
            emailText.setText(session.getEmail());
        }else {
            emailVerified.setVisibility(View.GONE);
            emailVerify.setVisibility(View.VISIBLE);
            session.setEmailVerified(false);
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            if (session.isMobileVerified()) {
                session.setMobileVerified(true);
                mobileVerified.setVisibility(View.VISIBLE);
                mobileVerify.setVisibility(View.GONE);
                mobileText.setText(session.getMobile());
            }else {
                session.setMobileVerified(false);
                mobileVerified.setVisibility(View.GONE);
                mobileVerify.setVisibility(View.VISIBLE);
            }

            if (session.isEmailVerified()) {
                session.setEmailVerified(true);
                emailVerified.setVisibility(View.VISIBLE);
                emailVerify.setVisibility(View.GONE);
                emailText.setText(session.getEmail());
            }else {
                emailVerified.setVisibility(View.GONE);
                emailVerify.setVisibility(View.VISIBLE);
                session.setEmailVerified(false);
            }
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
                userImage.setImageBitmap(bitmap);
                userImage.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
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
                userImage.setImageBitmap(image);
                userImage.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            }
        }

        if(!Simage.equals("")) {
            filedone();
        }else{
            Log.i("FIle","Not Exists");
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

//    public void selectImage(){
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
//                    userImage.setImageBitmap(bitmap);
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
//                    userImage.setImageBitmap(myBitmap);
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
//        if(Simage.exists()) {
//            Log.i("File", Simage.getPath());
//            Log.i("File", Simage.getAbsolutePath());
//            UploadImage();
//        }        else{
//            Log.i("FIle","Not Exists");
//        }
//    }
//    public void UploadImage() {
////        setFileToUpload();
//
//        ma.showProgressDialog(context);
//
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
//                    imgurl = "https://mybat11.nyc3.digitaloceanspaces.com/" + time;
//                    filedone(imgurl);
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
//
//    }





    //bigyarcode
//    public void UploadImage() {
//        ma.showProgressDialog(context);
//
//
//
//final CognitoCachingCredentialsProvider credentialsProvider;
//    credentialsProvider = new CognitoCachingCredentialsProvider(
//            context.getApplicationContext(),
//                "ap-south-1:09b04c28-0b49-4349-8137-0bd0e5849027", // Identity Pool ID
//    Regions.AP_SOUTH_1 // Region
//        );
//
//    AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
//        s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.AP_SOUTH_1));
//
//    final File file = Simage;
//    Long tsLong = System.currentTimeMillis()/1000;
//    String ts = tsLong.toString();
//    final String time = ""+ts+session.getReferalCode()+file.getName();
//    final TransferUtility transferUtility = new TransferUtility(s3, context);
//    final TransferObserver observer = transferUtility.upload(
//            "bigyarbucket",  //this is the bucket name on S3
//            time,
//            file
//    );
//        observer.setTransferListener(new TransferListener() {
//        @Override
//        public void onStateChanged(int id, TransferState state) {
//            Log.e("onStateChanged", id + state.name());
//            if (state.equals(TransferState.COMPLETED)) {
//                Log.i("logdata", "https://bigyarbucket.s3.ap-south-1.amazonaws.com/" + time);
//                imgurl = "https://bigyarbucket.s3.ap-south-1.amazonaws.com/" + time;
//                filedone(imgurl);
//            } else if (state.equals(TransferState.FAILED)) {
//                ma.showErrorToast(context,"Slow network connection please try again");
//            }
//
//        }
//
//        @Override
//        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//
//        }
//
//        @Override
//        public void onError(int id, Exception ex) {
//
//            Log.e("error", String.valueOf(ex));
//        }
//    });
//
//
//    }



//    public void UploadImage() {
//        ma.showProgressDialog(context);
//
//final CognitoCachingCredentialsProvider credentialsProvider;
//    credentialsProvider = new CognitoCachingCredentialsProvider(
//            context.getApplicationContext(),
//                "ap-south-1:28fa930f-0308-48cd-9d3e-6cf804f0ea0a", // Identity Pool ID
//    Regions.AP_SOUTH_1 // Region
//        );
//
//    AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
//        s3.setRegion(com.amazonaws.regions.Region.getRegion(Regions.AP_SOUTH_1));
//
//    final File file = Simage;
//    Long tsLong = System.currentTimeMillis()/1000;
//    String ts = tsLong.toString();
//    final String time = ""+ts+session.getReferalCode()+file.getName();
//    final TransferUtility transferUtility = new TransferUtility(s3, context);
//    final TransferObserver observer = transferUtility.upload(
//            "mybat11",  //this is the bucket name on S3
//            time,
//            file
//    );
//        observer.setTransferListener(new TransferListener() {
//        @Override
//        public void onStateChanged(int id, TransferState state) {
//            Log.e("onStateChanged", id + state.name());
//            if (state.equals(TransferState.COMPLETED)) {
//                Log.i("logdata", "https://mybat11.s3.ap-south-1.amazonaws.com/" + time);
//                imgurl = "https://mybat11.s3.ap-south-1.amazonaws.com/" + time;
//                filedone(imgurl);
//            } else if (state.equals(TransferState.FAILED)) {
//                ma.showErrorToast(context,"Slow network connection please try again");
//            }
//
//        }
//
//        @Override
//        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//
//        }
//
//        @Override
//        public void onError(int id, Exception ex) {
//
//            Log.e("error", String.valueOf(ex));
//        }
//    });
//
//
//
//    }

    public void filedone(){


        if (Simage.length() > 0) {
            try {
                String url = getResources().getString(R.string.app_url) + "imageUploadUser";
//                String url = getResources().getString(R.string.app_url) + "imageUploadLink";
                Log.i("url", url);
                StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.i("Response is", response.toString());
                                    JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);
                                    ma.showToast(context, jsonObject.getString("msg"));
//                                    session.setImage(jsonObject.getString("url"));
                                    ma.dismissProgressDialog();
                                    Allverify();
                                    userimg();
                                } catch (JSONException je) {
                                    je.printStackTrace();
                                }
                                ma.dismissProgressDialog();
                            }
                        },
                        new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("ErrorResponce", error.toString());
                                NetworkResponse networkResponse = error.networkResponse;
                                if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                    // HTTP Status Code: 401 Unauthorized
                                    ma.showToast(context, "Session Timeout");

                                    session.logoutUser();
                                    getActivity().finishAffinity();
                                } else {
                                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                                    d.setTitle("Something went wrong");
                                    d.setCancelable(false);
                                    d.setMessage("Something went wrong, Please try again");
                                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            filedone();
                                        }
                                    });
                                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            getActivity().finish();
                                        }
                                    });
                                }
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                        params.put("Authorization", session.getUserId());
                        Log.i("Header", params.toString());

                        return params;
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("image", Simage);
                        Log.i("Header", params.toString());

                        return params;
                    }
                };
                requestQueue.add(strRequest);
            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
            }
        }else {
            ma.showProgressDialog(context);
        }

    }

    public void verifyEmail(final String email){
        Log.e("emailcheck", email);
        ma.showProgressDialog(context);
        try {

            String url = getResources().getString(R.string.app_url)+"verifyEmail";
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
                                    d = new Dialog(context);
                                    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    d.setContentView(R.layout.otp_dialog);
                                    d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    final String[] otp = {""};

                                    final EditText otp1,otp2,otp3,otp4;
                                    otp1=(EditText)d.findViewById(R.id.otp1);
                                    otp2=(EditText)d.findViewById(R.id.otp2);
                                    otp3=(EditText)d.findViewById(R.id.otp3);
                                    otp4=(EditText)d.findViewById(R.id.otp4);

                                    otp1.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            if(count==1){
                                                otp2.setText("");
                                                otp2.requestFocus();
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });
                                    otp2.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(count==1){
                                                otp3.setText("");
                                                otp3.requestFocus();
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });
                                    otp3.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(count==1){
                                                otp4.setText("");
                                                otp4.requestFocus();
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });
                                    otp4.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }
                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(count==1){
                                                otp[0] = ""+otp1.getText()+otp2.getText()+otp3.getText()+s;
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });

                                    TextView btnSend = (TextView) d.findViewById(R.id.btnSend);
                                    btnSend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(otp[0].equals("")){
                                                ma.showToast(context,"Please enter valid otp");
                                            }else
                                                sendOTP(otp[0],"email",email);
                                        }
                                    });

                                    d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            ma.dismissProgressDialog();
                                        }
                                    });

                                    d.show();
                                }else{
                                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText(jsonObject.getString("msg"))
                                            .show();
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
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }
                        }
                    })
            {

                @Override
                protected Map<String,String> getParams(){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("email",email);
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

    public void verifyMobile(final String mobile){
        ma.showProgressDialog(context);
        try {
            String url = getResources().getString(R.string.app_url)+"verifyMobileNumber";
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
                                    d = new Dialog(context);
                                    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    d.setContentView(R.layout.otp_dialog);
                                    d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                    final String[] otp = {""};

                                    TextView t = (TextView)d.findViewById(R.id.t);

                                    final EditText otp1,otp2,otp3,otp4;
                                    otp1=(EditText)d.findViewById(R.id.otp1);
                                    otp2=(EditText)d.findViewById(R.id.otp2);
                                    otp3=(EditText)d.findViewById(R.id.otp3);
                                    otp4=(EditText)d.findViewById(R.id.otp4);

                                    otp1.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                            if(count==1){
                                                otp2.setText("");
                                                otp2.requestFocus();
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });
                                    otp2.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(count==1){
                                                otp3.setText("");
                                                otp3.requestFocus();
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });
                                    otp3.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(count==1){
                                                otp4.setText("");
                                                otp4.requestFocus();
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });
                                    otp4.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                        }
                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if(count==1){
                                                otp[0] = ""+otp1.getText()+otp2.getText()+otp3.getText()+s;
                                            }
                                        }
                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });

                                    TextView btnSend = (TextView) d.findViewById(R.id.btnSend);
                                    btnSend.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(otp[0].equals("")){
                                                ma.showToast(context,"Please enter valid otp");
                                            }else
                                                sendOTP(otp[0],"mobile",mobile);
                                        }
                                    });

                                    d.show();
                                }else{
                                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                            .setContentText(jsonObject.getString("msg"))
                                            .show();
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
                                // HTTP Status Code: 401 Unauthorized
                                ma.showToast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }
                        }
                    })
            {

                @Override
                protected Map<String,String> getParams(){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("mobile",mobile);
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

    public void sendOTP(final String code, final String type, final String value){
        ma.showProgressDialog(context);
        try {
            String url = getResources().getString(R.string.app_url)+"verifyCode";
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
                                    if(type.equals("email")) {
                                        session.setEmailVerified(true);
                                        session.setEmail(value);
                                        emailVerify.setVisibility(View.GONE);
                                        emailVerified.setVisibility(View.VISIBLE);
                                        emailText.setText("✔ Your Email address is verified.\nEmail : "+session.getEmail());
                                    }else{
                                        session.setMobileVerified(true);
                                        session.setMobile(value);
                                        mobileVerify.setVisibility(View.GONE);
                                        mobileVerified.setVisibility(View.VISIBLE);

                                        mobileText.setText("✔ Your Mobile Number is verified.\nMobile No. : "+session.getMobile());
                                    }
                                    Allverify();
                                    userimg();
                                    d.dismiss();
                                }else{
                                    ma.showToast(context,jsonObject.getString("msg"));
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
                    map.put("username",value);
                    map.put("code",code);
                    if(type.equals("email"))
                        map.put("email",value);
                    else
                        map.put("mobile",value);

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

    public void Allverify(){
        ma.showProgressDialog(context);
        try {

            String url = getResources().getString(R.string.app_url)+"allverify";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                final int mobile_verify = jsonObject.getInt("mobile_verify");
                                final int email_verify = jsonObject.getInt("email_verify");
                                int bank_verify = jsonObject.getInt("bank_verify");
                                int pan_verify = jsonObject.getInt("pan_verify");
                                int profile_image_verify = jsonObject.getInt("profile_image_verify");


                                camera.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (mobile_verify == 0){
                                                ma.showToast(context,"Please Verify Your Mobile Number First");
                                            }else if (email_verify == 0){
                                                ma.showToast(context,"Please Verify Email Id First");
                                            }else {
                                                selectImage();
                                            }
                                        }
                                    });

                                userImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (mobile_verify == 0){
                                            ma.showToast(context,"Please Verify Your Mobile Number First");
                                        }else if (email_verify == 0){
                                            ma.showToast(context,"Please Verify Email Id First");
                                        }else {
                                            selectImage();
                                        }
                                    }
                                });
                                    verifyEmail.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (email.getEditText().getText().toString().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getEditText().getText().toString()).matches())
                                                email.setError("Please enter valid email address");
                                            else {
                                                verifyEmail(email.getEditText().getText().toString());
                                            }
                                        }
                                    });


                                if (profile_image_verify == 0) {
                                    imagetitle.setText("User Image");
                                    reson.setVisibility(View.VISIBLE);

//                                    reson.setTextColor(context.getColor(R.color.main_green_color));
                                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                        reson.setTextColor(ContextCompat.getColor(context, R.color.main_green_color));
                                    } else {
                                        reson.setTextColor(context.getColor(R.color.main_green_color));
                                    }

                                    reson.setText("Profile image sent for verification! if you want change please upload again");
                                }else if (profile_image_verify == 1){
                                    imagetitle.setText("User Image");
                                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                        reson.setTextColor(ContextCompat.getColor(context, R.color.main_green_color));
                                    } else {
                                        reson.setTextColor(context.getColor(R.color.main_green_color));
                                    }
//                                    reson.setTextColor(context.getColor(R.color.main_green_color));
                                    reson.setVisibility(View.VISIBLE);
                                    reson.setText("Profile image verified! if you want change please upload again");
                                }else if (profile_image_verify == -1){
                                    imagetitle.setText("Upload User Image");
                                    reson.setVisibility(View.VISIBLE);
                                    reson.setText("Upload profile image");
                                }else if (profile_image_verify == 2){
                                    reson.setVisibility(View.VISIBLE);
                                    imagetitle.setText("Upload User Image");
                                    reson.setText(jsonObject.getString("comment")+"");
                                }

                                if (mobile_verify == 1) {
                                    session.setMobileVerified(true);
                                    mobileVerified.setVisibility(View.VISIBLE);
                                    mobileVerify.setVisibility(View.GONE);
                                    mobileText.setText(session.getMobile());
                                }else {
                                    session.setMobileVerified(false);
                                    mobileVerified.setVisibility(View.GONE);
                                    mobileVerify.setVisibility(View.VISIBLE);
                                }

                                if (email_verify == 1) {
                                    session.setEmailVerified(true);
                                    emailVerified.setVisibility(View.VISIBLE);
                                    emailVerify.setVisibility(View.GONE);
                                    emailText.setText(session.getEmail());
                                }else {
                                    emailVerified.setVisibility(View.GONE);
                                    emailVerify.setVisibility(View.VISIBLE);
                                    session.setEmailVerified(false);
                                }

                                session.setBankVerified(String.valueOf(bank_verify));
                                session.setPANVerified(String.valueOf(pan_verify));
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
                                ma.showToast(context,"Session Timeout");

                                startActivity(new Intent(context, LoginActivity.class));
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
    public void userimg(){
        try {

            String url = getResources().getString(R.string.app_url)+"getProfileImage";
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

                                Log.i("ppppppppppppppppppp",jsonObject.getString("image"));
                                Picasso.with(context).load(jsonObject.getString("image")).into(userImage);
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


}