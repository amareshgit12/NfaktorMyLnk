package nfactornote.android.com.nfactorenote;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import nfactornote.android.com.nfactorenote.Activity.BusinessActivity;
import nfactornote.android.com.nfactorenote.Activity.RegistrationPage;
import nfactornote.android.com.nfactorenote.Activity.ShareActivity;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar1;
    int progressStatus = 0;
    Boolean Sup_permission;
    RelativeLayout splashrelative;
    Handler handler = new Handler();
    String uniquid,deviceid,mobilenum,usertype;
    String response_user_type;
    private TelephonyManager mTelephonyManager;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};
    int i=0;
    String fcm_id;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        splashrelative = (RelativeLayout) findViewById(R.id.spassrel);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        uniquid = SplashScreen.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_UNIQ_ID, null);
        usertype = SplashScreen.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_USERtYPE, null);
        //checkverion();
        fcm_id = SplashScreen.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.FCM_ID, null);

        checkpermission();

    }

    private void checkverion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // here it is checking whether the permission is granted previously or not
            if (!hasPermissions(this, PERMISSIONS)) {
                //Permission is granted
                checkpermission();

            }
        }
        else {
            checkpermission();
        }
    }

    private void checkpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasPermissions(this, PERMISSIONS)) {
            proceed();
        }
        else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M ){
            proceed();
        }
        else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                    builder.setMessage("Please Approve the permission to run the app")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // FIRE ZE MISSILES!
                                    ActivityCompat.requestPermissions(SplashScreen.this, PERMISSIONS, 1);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            // Start your app main activity
                                           checkpermission();

                                            // close this activity
                                        }
                                    }, 3000);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    SplashScreen.this.finish();
                                }
                            });

                    // Create the AlertDialog object and return it
                    builder.show();
        }
    }

    private void proceed(){
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceid = mTelephonyManager.getDeviceId();

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar1.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (progressStatus == 100) {
                    if (uniquid == null) {
                        //Intent i = new Intent(SplashScreen.this, MainActivity.class);
                        Intent i = new Intent(SplashScreen.this, RegistrationPage.class);
                        i.putExtra("IMEI", deviceid);
                        i.putExtra("MOBILE", mobilenum);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(i);
                    }
                    else {
                        checkUserType();

                    }
                }
            }
        }).start();

    }

    private void checkUserType() {
        if(CheckInternet.getNetworkConnectivityStatus(SplashScreen.this)){
            new UserType().execute(uniquid,"normal");
        }
        else{
            SHowsnachbar("No Interent");
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private void SHowsnachbar(String s) {
        Snackbar snackbar = Snackbar
                .make(splashrelative, s, Snackbar.LENGTH_LONG);
        snackbar.show();
    }



    private class UserType extends AsyncTask<String, Void, Void> {

        private static final String TAG = "Check User";
        int server_status;
        String server_message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // onPreExecuteTask();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                String _user_id = params[0];
                String _export_type = params[1];
                InputStream in = null;
                int resCode = -1;

                String link = Constants.MAINURL + Constants.GETUSERS;
                URL url = new URL(link);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setAllowUserInteraction(false);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestMethod("POST");

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("platform_user_id", _user_id)
                        .appendQueryParameter("export_type", _export_type);

                //.appendQueryParameter("deviceid", deviceid);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();
                resCode = conn.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = conn.getInputStream();
                }
                if (in == null) {
                    return null;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "", data = "";

                while ((data = reader.readLine()) != null) {
                    response += data + "\n";
                }

                /*
                * {
    "accounts": [
        {
            "business_user_id": "userId_59aaa910aa4ae",
            "full_name": "Neel",
            "email_id": "neel@gmail.com",
            "mobile_number": "9776513976",
            "user_type": "consumers"
        }
    ],
    "status": 1
}*/

                Log.i(TAG, "Response : " + response);
                if (response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response.trim());
                    server_status = res.getInt("status");
                    if (server_status == 1) {
                        JSONArray jsonArray=res.getJSONArray("accounts");{
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject o_list_obj = jsonArray.getJSONObject(i);
                                response_user_type = o_list_obj.getString("user_type");

                            }

                            }

                        // DeviceDetails dlist=new DeviceDetails(dev_userid,dev_usertype);

                    } else {
                        server_message = "Error in User Type";
                    }
                }

                return null;
            } catch (Exception exception) {
                server_message = "Network Error";
                Log.e(TAG, "SynchMobnum : doInBackground", exception);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void user) {
            super.onPostExecute(user);
            if(server_status==1){
                if(response_user_type.contentEquals("consumers")){
                    Intent i = new Intent(SplashScreen.this, ShareActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                }
                else if(response_user_type.contentEquals("business-users")){
                    Intent i = new Intent(SplashScreen.this, BusinessActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                }
                else{
                    SHowsnachbar("Invalid Usertype");
                }
            }
            else{
                SHowsnachbar(server_message);
            }
        }
    }
}
