package nfactornote.android.com.nfactorenote.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import nfactornote.android.com.nfactorenote.Models.DeviceDetails;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class MainActivity extends AppCompatActivity {
    private Button bt_consumers,bt_busers;
    private String devideid,mobilenum;
    private RelativeLayout mainrel;
    private String dev_userid,dev_usertype;
    private ProgressBar loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            devideid = extras.getString("IMEI");
            mobilenum = extras.getString("MOBILE");
        }
        mainrel=(RelativeLayout)findViewById(R.id.mainrel);
        loader=(ProgressBar)findViewById(R.id.loader);
        bt_consumers=(Button)findViewById(R.id.bt_consumers);
        bt_busers=(Button)findViewById(R.id.bt_busers);
        bt_consumers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckInternet.getNetworkConnectivityStatus(MainActivity.this)) {
                    // Toast.makeText(Signup.this, R.string.otp_sent, Toast.LENGTH_LONG).show();
                    DeviceRegister deviceregister=new DeviceRegister();
                    deviceregister.execute(devideid,mobilenum,"consumers");

                }else {
                    showsnackbar("No Internet");
                }
                /*Intent intent=new Intent(MainActivity.this,ShareActivity.class);
                startActivity(intent);*/
            }
        });
        bt_busers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternet.getNetworkConnectivityStatus(MainActivity.this)) {
                    // Toast.makeText(Signup.this, R.string.otp_sent, Toast.LENGTH_LONG).show();
                  /*  DeviceRegister deviceregister=new DeviceRegister();
                    deviceregister.execute(devideid,mobilenum,"business_users");*/
                    Intent intent = new Intent(MainActivity.this, BusinessActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);

                }else {
                    showsnackbar("No Internet");
                }
            }
        });
    }

    private void showsnackbar(String Message) {
        Snackbar snackbar = Snackbar
                .make(mainrel, Message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

/*
* Store device Asyntask
* */

    private class DeviceRegister extends AsyncTask<String, Void, Void> {

        private static final String TAG = "Register device";
        int server_status;
        String id,mobile,name;
        String server_message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // onPreExecuteTask();
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                String _imei = params[0];
                String _mobilenum=params[1];
                String _utype=params[2];
                InputStream in = null;
                int resCode = -1;

                String link = Constants.MAINURL+Constants.STOREUSER;
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
                        .appendQueryParameter("device_unique_id", _imei)
                        .appendQueryParameter("mobile_number", _mobilenum)
                        .appendQueryParameter("device_type", "mobile")
                        .appendQueryParameter("user_type", _utype);

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
                if(in == null){
                    return null;
                }
                BufferedReader reader =new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String response = "",data="";

                while ((data = reader.readLine()) != null){
                    response += data + "\n";
                }

                Log.i(TAG, "Response : "+response);

                /**
                 * {
                 {
                 "status": 1,
                 "userId": "userId_598acd0f97e3d",
                 "userType": "consumers",
                 "message": "unique-id-exists"
                 }
                 }
                 * */
                if(response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response.trim());
                    server_status = res.getInt("status");
                    if(server_status==1) {
                         dev_userid=res.getString("userId");
                         dev_usertype=res.getString("userType");
                        new DeviceDetails().setDev_userid(dev_userid);
                        new DeviceDetails().setDev_userid(dev_usertype);
                       // DeviceDetails dlist=new DeviceDetails(dev_userid,dev_usertype);

                    }
                    else{
                        server_message="Error in Data Load";
                    }
                }

                return null;

            } catch(SocketTimeoutException exception){
                server_message="Network Error";
                Log.e(TAG, "SynchMobnum : doInBackground", exception);
            } catch(ConnectException exception){
                server_message="Network Error";
                Log.e(TAG, "SynchMobnum : doInBackground", exception);
            } catch(MalformedURLException exception){
                server_message="Network Error";
                Log.e(TAG, "SynchMobnum : doInBackground", exception);
            } catch (IOException exception){
                server_message="Network Error";
                Log.e(TAG, "SynchMobnum : doInBackground", exception);
            } catch(Exception exception){
                server_message="Network Error";
                Log.e(TAG, "SynchMobnum : doInBackground", exception);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void user) {
            super.onPostExecute(user);
            loader.setVisibility(View.GONE);

            //  progressDialog.cancel();
            if(server_status==1) {
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0); // 0 - for private mode
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.N_UNIQ_ID,dev_userid);
                editor.putString(Constants.N_USERtYPE,dev_usertype);
                editor.commit();
                if(dev_usertype.contentEquals("consumers")) {
                    Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(MainActivity.this, BusinessActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
            else {
                // Toast.makeText(Login_Activity.this, server_message, Toast.LENGTH_LONG).show();
                showsnackbar(server_message);
            }
        }
    }
}
