package com.img.mybat11.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mybat11.Adapter.AvatarAdapter;
import com.img.mybat11.Adapter.ChallengeListAdapter;
import com.img.mybat11.Adapter.FilterGridAdapter;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.ExpandableHeightGridView;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.MyTeamsGetSet;
import com.img.mybat11.GetSet.avatarGetSet;
import com.img.mybat11.GetSet.msgStatusGetSet;
import com.img.mybat11.R;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeActivity extends AppCompatActivity {
    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    MainActivity ma;
    CircleImageView userImage;
    TextView btnAddCash,btnWithdraw,inviteFriend,leaderboard,verifyEtext;
    TextView teamName,verifyText,moreA,untilizedCash,winning,bonus,recent;
    TextView totalContests,totalmatches,totalSeries,totalWins;
    TextView fullProfile,name,email,phone,changePssword,logout;
    LinearLayout verifiedLL,verifyLL,verifyNowLL,camera;
    ScrollView scroll;
    ImageView profileBanner;
    String Simage="";
    private BottomSheetBehavior mBottomSheetBehavior;
    ExpandableHeightGridView allImgs;
    String TAG="profile",selectedteam="";
    LinearLayout homeLL,contestLL,meLL,moreLL;
    ImageView me;
    TextView meText;
    Button btnApply;
    ArrayList<avatarGetSet> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        ma = new MainActivity();
        scroll = findViewById(R.id.scroll);
        TextView title =(TextView)findViewById(R.id.title);
        title.setText("My Profile");
        ImageView back =(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,HomeActivity.class));
                finishAffinity();
            }
        });
        profileBanner=(ImageView)findViewById(R.id.profileBanner);
        verifiedLL=(LinearLayout)findViewById(R.id.verifiedLL);
        verifyLL=(LinearLayout)findViewById(R.id.verifyLL);
        verifyNowLL=(LinearLayout)findViewById(R.id.verifyNowLL);
        verifyNowLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,VerifyAccountActivity.class));
            }
        });
        homeLL= (LinearLayout)findViewById(R.id.homeLL);
        contestLL= (LinearLayout)findViewById(R.id.contestLL);
        meLL= (LinearLayout)findViewById(R.id.meLL);
        moreLL= (LinearLayout)findViewById(R.id.moreLL);
        me=(ImageView) findViewById(R.id.me);
        meText=(TextView) findViewById(R.id.meText);
        verifyEtext=(TextView) findViewById(R.id.verifyEtext1);
        meText.setTextColor(getResources().getColor(R.color.blue));
        me.setImageResource(R.drawable.me_selected);
        homeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,HomeActivity.class));
                finishAffinity();
            }
        });
        contestLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,MyMatchesCricketActivity.class));
                finishAffinity();
            }
        });
        meLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        moreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,MoreActivity.class));
                finishAffinity();
            }
        });
        userImage =(CircleImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        camera =(LinearLayout) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        verifyText=(TextView)findViewById(R.id.verifyText);
        name=(TextView)findViewById(R.id.name);
        email =(TextView)findViewById(R.id.email);
        phone =(TextView)findViewById(R.id.phone);
        changePssword =(TextView)findViewById(R.id.changePssword);
        logout =(TextView)findViewById(R.id.logout);
        moreA =(TextView)findViewById(R.id.moreA);
        recent =(TextView)findViewById(R.id.recent);
        leaderboard =(TextView)findViewById(R.id.leaderboard);
        name.setText(session.getName());
        email.setText(session.getEmail());
        phone.setText(session.getMobile());
        teamName=(TextView)findViewById(R.id.teamName);
        teamName.setText(session.getTeamName());
        untilizedCash=(TextView)findViewById(R.id.untilizedCash);
        winning=(TextView)findViewById(R.id.winning);
        bonus=(TextView)findViewById(R.id.bonus);
        btnAddCash=(TextView)findViewById(R.id.btnAddCash);
        btnWithdraw=(TextView)findViewById(R.id.btnWithdraw);
        inviteFriend =(TextView)findViewById(R.id.inviteFriend);

        totalContests=(TextView)findViewById(R.id.totalContests);
        totalmatches=(TextView)findViewById(R.id.totalmatches);
        totalSeries=(TextView)findViewById(R.id.totalSeries);
        totalWins=(TextView)findViewById(R.id.totalWins);

        fullProfile=(TextView)findViewById(R.id.fullProfile);
        fullProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,PersonalDetailsActivity.class));
            }
        });
        btnAddCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,AddBalanceActivity.class));
            }
        });
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,WithdrawActivity.class));
            }
        });
        inviteFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,InviteFriendActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ma.showProgressDialog(MeActivity.this);
                LogOut();
            }
        });
        changePssword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,ChangePassowrdActivity.class));
            }
        });

        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeActivity.this,MyTransactionActivity.class));
            }
        });
        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MeActivity.this,LeaderboardActivity.class));
            }
        });
        if (!session.isEmailVerified()==true || !session.isMobileVerified()==true || !session.getPANVerified().equalsIgnoreCase("1") || !session.getBankVerified().equalsIgnoreCase("1")){
            verifiedLL.setVisibility(View.GONE);
            verifyLL.setVisibility(View.VISIBLE);
        }else{
            verifiedLL.setVisibility(View.VISIBLE);
            verifyLL.setVisibility(View.GONE);
            verifyText.setText("View Details");
        }
        View bottomSheet = findViewById(R.id.profile_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        allImgs =(ExpandableHeightGridView)findViewById(R.id.allImgs);
        allImgs.setExpanded(true);


        btnApply=(Button)findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                for(avatarGetSet zz:list){
                    if(zz.isSelected()) {
                        selectedteam = String.valueOf(zz.getId());
                        count++;
                    }
                }
                if (count == 1) {
                    addprofile();
                }else {
                    Toast.makeText(MeActivity.this, "please select any one avtar", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(cd.isConnectingToInternet()) {
            MyWallet();
            Details();
            viewprofile();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(MeActivity.this,HomeActivity.class));
        finishAffinity();
    }


    public void addprofile(){
        ma.showProgressDialog(MeActivity.this);
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<msgStatusGetSet>> call;
        call = apiSeitemViewice.AddAvatar(session.getUserId(),selectedteam);
        call.enqueue(new Callback<ArrayList<msgStatusGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<msgStatusGetSet>> call, Response<ArrayList<msgStatusGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");
                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    ArrayList<msgStatusGetSet>list = response.body();
                    if (list.get(0).isSuccess()){
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        viewprofile();
                    }

                    ma.showToast(MeActivity.this,list.get(0).getMsg());

                    ma.dismissProgressDialog();
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(MeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addprofile();
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
                Log.i(TAG, t.toString());
                ma.dismissProgressDialog();
            }
        });
    }
    public void viewprofile(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<msgStatusGetSet>> call;
        call = apiSeitemViewice.vAvatar(session.getUserId());
        call.enqueue(new Callback<ArrayList<msgStatusGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<msgStatusGetSet>> call, Response<ArrayList<msgStatusGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");
                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));
                    ArrayList<msgStatusGetSet>list = response.body();
                    if (list.get(0).isSuccess()){
                        Picasso.with(MeActivity.this).load(list.get(0).getImage()).into(userImage);
                    }
                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(MeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addprofile();
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
            public void onFailure(Call<ArrayList<msgStatusGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }

    public void selectImage()
    {
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<avatarGetSet>> call = apiSeitemViewice.profileAvatar(session.getUserId());
        call.enqueue(new Callback<ArrayList<avatarGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<avatarGetSet>> call, Response<ArrayList<avatarGetSet>> response) {
                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if (response.code() == 200) {
                    list = response.body();

                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    allImgs.setAdapter(new AvatarAdapter(MeActivity.this,list));

                } else {

                    AlertDialog.Builder d = new AlertDialog.Builder(MeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectImage();
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
            public void onFailure(Call<ArrayList<avatarGetSet>> call, Throwable t) {
                // Log error here since request failed
                ma.dismissProgressDialog();
            }
        });



//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Select a image");
//
//        final String [] items = {"Take Photo..","Choose From Library.."};
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int position) {
//                switch (position){
//                    case 0:
//                        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//                        startActivityForResult(cameraIntent, 4);
//                        break;
//                    case 1:
//                        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        startActivityForResult(gallaryIntent, 1);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri image = data.getData();

                Bitmap bitmap=null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
//                    bitmap = modifyOrientation(bitmap,data.getData().getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bitmap = getResizedBitmap(bitmap, 2000);// 2000 is for example, replace with desired size
                userImage.setImageBitmap(bitmap);

                Bitmap imageB = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            }
            else {

            }
        }
        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                image = getResizedBitmap(image,2000);
                userImage.setImageBitmap(image);

                Bitmap imageB = ((BitmapDrawable) userImage.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            }
        }

        UploadImage();
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    public void UploadImage(){
//        ma.showProgressDialog(MeActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"imageUploadUser";
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
                                ma.showToast(MeActivity.this,jsonObject.getString("msg"));
                                session.setImage(jsonObject.getString("url"));
//                                ma.dismissProgressDialog();
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
                                ma.showToast(MeActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UploadImage();
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("image",Simage);
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

    public void MyWallet(){
        ma.showProgressDialog(MeActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"mywalletdetails";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                winning.setText("₹"+String.format("%.2f",jsonObject.getDouble("winning")));
                                untilizedCash.setText("₹"+String.format("%.2f",jsonObject.getDouble("balance")));
                                bonus.setText("₹"+String.format("%.2f",jsonObject.getDouble("bonus")));

                                totalContests.setText(jsonObject.getString("totaljoinedcontest"));
                                totalmatches.setText(jsonObject.getString("totaljoinedmatches"));
                                totalSeries.setText(jsonObject.getString("totaljoinedseries"));
                                totalWins.setText("₹"+jsonObject.getString("totatotalamountwonlwon"));
								
								
                                if(jsonObject.getDouble("winning") >= 300)
                                    btnWithdraw.setVisibility(View.VISIBLE);

//                                if (jsonObject.getInt("allverify") == 1){
//                                    verifyEtext.setVisibility(View.GONE);
//                                }else {
//                                    verifyEtext.setVisibility(View.VISIBLE);
//                                }

                                session.setWinningAmount(String.format("%.2f",jsonObject.getDouble("winning")));
//                                ma.dismissProgressDialog();
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
                                ma.showToast(MeActivity.this,"Session Timeout");

                                startActivity(new Intent(MeActivity.this,LoginActivity.class));
                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MyWallet();
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

    public void Details(){
//        ma.showProgressDialog(MeActivity.this);

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

                                session.setEmail(jsonObject.getString("email"));
                                session.setName(jsonObject.getString("username"));
                                session.setMobile(jsonObject.getString("mobile"));
                                session.setDob(jsonObject.getString("dob"));
                                session.setImage(jsonObject.getString("image"));
                                session.setWallet_amount(jsonObject.getString("walletamaount"));
                                session.setTeamName(jsonObject.getString("team"));
                                session.setReferalCode(jsonObject.getString("refer_code"));
                                if(jsonObject.getInt("verified") ==1) {
                                    session.setVerified(true);
                                    verifiedLL.setVisibility(View.VISIBLE);
                                    verifyLL.setVisibility(View.GONE);
                                    verifyText.setText("View Details");
                                }else {
                                    session.setVerified(false);
                                    verifiedLL.setVisibility(View.GONE);
                                    verifyLL.setVisibility(View.VISIBLE);
                                }

                                if(jsonObject.has("banner"))
                                    Picasso.with(MeActivity.this).load(jsonObject.getString("banner")).into(profileBanner);

                                teamName.setText(jsonObject.getString("team"));
                                name.setText(jsonObject.getString("username"));
                                email.setText(jsonObject.getString("email"));
                                phone.setText(jsonObject.getString("mobile"));


                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    startActivity(new Intent(MeActivity.this,LoginActivity.class));
                                    finishAffinity();
                                }
                                ma.dismissProgressDialog();
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
                                ma.showToast(MeActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MeActivity.this);
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

                                        finish();
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

    public void LogOut(){
        ma.showProgressDialog(MeActivity.this);
        try {
            String url = getResources().getString(R.string.app_url)+"logout?appkey="+session.getNotificationToken();
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                new JSONArray(response.toString()).getJSONObject(0);

                                ma.dismissProgressDialog();

                                session.logoutUser();
                                startActivity(new Intent(MeActivity.this,LoginActivity.class));
                                finishAffinity();

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
                                ma.showToast(MeActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LogOut();
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
