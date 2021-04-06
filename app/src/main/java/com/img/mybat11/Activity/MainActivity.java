package com.img.mybat11.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.img.mybat11.Api.ApiClient;
import com.img.mybat11.Api.ApiInterface;
import com.img.mybat11.Api.ConnectionDetector;
import com.img.mybat11.Api.GlobalVariables;
import com.img.mybat11.Api.UserSessionManager;
import com.img.mybat11.GetSet.versionGetSet;
import com.img.mybat11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    ConnectionDetector connectionDetector;
    UserSessionManager userSessionManager;
    Context context;
    MainActivity mainActivity;
    ArrayList<versionGetSet> versionList;
    String TAG = "";
    Dialog progressDialog, d;
    int currentVersion;
    RequestQueue requestQueue;
    TextView textid;
    LinearLayout paypar;
    GlobalVariables gv;

    final static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        checkpoint();

        textid = (TextView) findViewById(R.id.textid);
        gv= (GlobalVariables)getApplicationContext();
        paypar = (LinearLayout) findViewById(R.id.paypar);

        this.context = context;
        connectionDetector = new ConnectionDetector(getApplicationContext());
        userSessionManager = new UserSessionManager(getApplicationContext());
        mainActivity = new MainActivity();



//        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(Integer.parseInt(SD_YeaR), Integer.parseInt(SD_MontH), Integer.parseInt(SD_DaY), Integer.parseInt(SD_HouR), Integer.parseInt(SD_MinutE));
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(Integer.parseInt(SD_YeaR), Integer.parseInt(SD_MontH), Integer.parseInt(SD_DaY), Integer.parseInt(SD_HouR), Integer.parseInt(SD_MinutE));
//        Intent intent = new Intent(Intent.ACTION_INSERT)
//                .setData(CalendarContract.Events.CONTENT_URI)
//                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
//                .putExtra(CalendarContract.Events.TITLE, "Yoga")
//                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
//                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
//                .putExtra(CalendarContract.Events.HAS_ALARM, 1)
//                .putExtra(CalendarContract.Events., 1)
//                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//        startActivity(intent);



    }


    public void CheckCode() {
        if (!connectionDetector.isConnectingToInternet()) {
            android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MainActivity.this);
            d.setTitle("Internet connection");
            d.setCancelable(false);
            d.setMessage("Please check your internet connection");
            android.app.AlertDialog.Builder retry = d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                private DialogInterface dialog;
                private int which;

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    this.dialog = dialog;
                    this.which = which;
                    CheckCode();
                }
            });
            d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            d.show();
        } else {
            getVersionApi();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

// permission was granted

                    new Handler().postDelayed(new Runnable() {
                        // Using handler with postDelayed called runnable run method
                        @Override
                        public void run() {
                            try {
                                currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                                CheckCode();
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            Log.i("Current Version", String.valueOf(currentVersion));

                        }

                    }, 3000);

                } else {
                // permission denied
                // ask again or ignore
                    finish();


                }
                return;
            }

// other 'case' lines to check for other
// permissions this app might request
        }
    }

    private void getVersionApi() {

        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<versionGetSet>> call = apiSeitemViewice.checkVersion();
        call.enqueue(new Callback<ArrayList<versionGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<versionGetSet>> call, Response<ArrayList<versionGetSet>> response) {


                if (response.code() == 200) {
                    try {
                        Log.i("helo", response.toString());
                        versionList = response.body();
                        int onlineVersion = versionList.get(0).getStatus();
                        Log.i("online", String.valueOf(onlineVersion));
                        Log.i("current", String.valueOf(currentVersion));
//                        if (versionList.get(0).getMaintanance() == 1) {
                        textid.setVisibility(View.GONE);
                        if (currentVersion < onlineVersion) {

                            Log.i("check123", "version inside");

                            final Dialog d = new Dialog(MainActivity.this);
                            d.setContentView(R.layout.update_android);
                            d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            TextView updationText = (TextView) d.findViewById(R.id.updationText);
                            updationText.setText(Html.fromHtml(versionList.get(0).getPoint()));

                            Button btnUpdate = (Button) d.findViewById(R.id.btnUpdate);
                            btnUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.dismiss();
                                    new DownloadFile().execute("https://mybat11.com/apk/mybat11.apk");
                                }
                            });

                            d.setCanceledOnTouchOutside(false);
                            d.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    d.dismiss();
                                    finishAffinity();
                                }
                            });
                            d.show();

                        } else {


                            Log.i("check123456", userSessionManager.getUserId());
                            if (!userSessionManager.isUserLoggedIn() || userSessionManager.getUserId().equals("Bearer ")) {
                                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                finish();
                            }
                        }
//                        }
//                        else {
//                            textid.setVisibility(View.VISIBLE);
//                        }
                    } catch (Exception e) {
                        Log.i("errortest", "challenges activity");
                        android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MainActivity.this);
                        d.setTitle("Something went wrong");
                        d.setCancelable(false);
                        d.setMessage("Something went wrong, Please try again");
                        android.app.AlertDialog.Builder retry = d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            private DialogInterface dialog;
                            private int which;

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                this.dialog = dialog;
                                this.which = which;
                                CheckCode();
                            }
                        });
                        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        d.show();
                    }

                } else {
                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MainActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CheckCode();
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
            public void onFailure(Call<ArrayList<versionGetSet>> call, Throwable t) {
                // Log error here since request failed
                Log.i("check1234", "error" + t);
            }
        });

    }

    public void checkpoint() {

        String[] perarr = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.VIBRATE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.DELETE_CACHE_FILES,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.MANAGE_DOCUMENTS,
                Manifest.permission.INSTALL_PACKAGES,
                Manifest.permission.REQUEST_INSTALL_PACKAGES,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_CALENDAR
        };
        ActivityCompat.requestPermissions(MainActivity.this, perarr, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);



    }

    public void UPDATE(View v) {
        if (verifyStoragePermissions(MainActivity.this)) {
            new DownloadFile().execute("https://mybat11.com/apk/mybat11.apk");
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 1);
        }
    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(MainActivity.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();
                Log.i("logfile1", String.valueOf(lengthOfFile));


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                //String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                //fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "" + getResources().getString(R.string.app_name) + "/";

                Log.i("logfile2", folder);
                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    Log.i("logfile3", "directory not exist");
                    directory.mkdirs();

                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d("Dowoading..."
                            , "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                MediaScannerConnection.scanFile(MainActivity.this, new String[]{String.valueOf(directory)}, new String[]{"image/jpeg"}, null);
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(MainActivity.this, "Downloaded at: " + folder + fileName, Toast.LENGTH_SHORT).show();
            String PATH = Environment.getExternalStorageDirectory() + File.separator + "" + getResources().getString(R.string.app_name) + "/";
            Log.i("logfile4", PATH);
            if (new File(PATH + "mybat11.apk").exists())
                install_apk(new File(PATH + "mybat11.apk"));
            else
                Log.i("logfile4", "Apk not found");

           /* try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.fromFile(new File(PATH+"myexpert11.apk")));
                intent.setType("application/android.com.app");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }catch (Exception e){
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }*/

        }
    }


    public void install_apk(File file) {

        Log.i("File", file.getAbsolutePath());

        try {
            if (file.exists()) {
                String[] fileNameArray = file.getName().split(Pattern.quote("."));
                if (fileNameArray[fileNameArray.length - 1].equals("apk")) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri downloaded_apk = getFileUri(MainActivity.this, file);
                        Log.i("downloaded apk", downloaded_apk.getPath());
                        Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(downloaded_apk,
                                "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file),
                                "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            } else {
                Log.e("Check", "Here");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Uri getFileUri(Context context, File file) {
        return FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider"
                , file);
    }

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void CANCEL(View v) {
        finish();
    }


    public boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user

            return false;

        }

        return true;
    }


    public void showProgressDialog(Context context) {
        progressDialog = new Dialog(context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_bg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public void showToast(Context context, String toast_text) {
        LayoutInflater inflater1 = ((Activity) context).getLayoutInflater();
        View layout = inflater1.inflate(R.layout.custom_toast1,
                (ViewGroup) ((Activity) context).findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(toast_text);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, +50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void showErrorToast(Context context, String toast_text) {
        LayoutInflater inflater1 = ((Activity) context).getLayoutInflater();
        View layout = inflater1.inflate(R.layout.custom_toasterror,
                (ViewGroup) ((Activity) context).findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(toast_text);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, +50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public void successDialog(Context context, String text, final View.OnClickListener listener) {
        d = new Dialog(context);
        d.setContentView(R.layout.custom_dialog_success);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogtext = (TextView) d.findViewById(R.id.dialogtext);
        dialogtext.setText(text);

        Button custom_msg_button = (Button) d.findViewById(R.id.custom_msg_button);
        custom_msg_button.setOnClickListener(listener);
    
        d.setCancelable(false);
        d.show();
    }

    public void successDialog(Context context, String text) {
        d = new Dialog(context);
        d.setContentView(R.layout.custom_dialog_success);
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogtext = (TextView) d.findViewById(R.id.dialogtext);
        dialogtext.setText(text);

        Button custom_msg_button = (Button) d.findViewById(R.id.custom_msg_button);
        custom_msg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.setCancelable(false);
        d.show();
    }

    public void dismissDialog() {
        if (d != null)
            d.dismiss();
    }

    public void errorDialog(Context context, String text, final View.OnClickListener listener) {
        d = new Dialog(context);
        d.setContentView(R.layout.custom_layout_error);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView dialogtext = (TextView) d.findViewById(R.id.errordialogtext);
        dialogtext.setText(text);

        Button custom_msg_button = (Button) d.findViewById(R.id.custor_error_msg_button);
        custom_msg_button.setOnClickListener(listener);

        d.setCancelable(false);
        d.show();
    }
}
