package nfactornote.android.com.nfactorenote.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class RegistrationPage extends AppCompatActivity {
    EditText et_firstname,et_lastname,et_email,et_phone;
    RelativeLayout rel_registration;
    Button bt_proceed,bt_exit;
    String devideid;
    ProgressBar loader;
    String dev_userid,dev_usertype,server_message;
    String fcm_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        et_firstname=(EditText)findViewById(R.id.et_firstName);
        et_lastname=(EditText)findViewById(R.id.et_lastName);
        et_email=(EditText)findViewById(R.id.et_email);
        et_phone=(EditText)findViewById(R.id.et_phone);
        rel_registration=(RelativeLayout)findViewById(R.id.rel_registration);
        bt_proceed=(Button)findViewById(R.id.bt_proceed);
        bt_exit=(Button)findViewById(R.id.bt_exit);
        loader=(ProgressBar)findViewById(R.id.loader);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            devideid = extras.getString("IMEI");
        }

        bt_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckInternet.getNetworkConnectivityStatus(RegistrationPage.this)){
                    validate();
                }
                else{
                    SHowsnachbar("No Internet");
                }
            }
        });

        fcm_id = RegistrationPage.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.FCM_ID, null);

    }

    private void validate() {
        if (et_firstname.getText().toString().trim().length() <= 0) {
            SHowsnachbar("Enter First Name");
        } else if (et_lastname.getText().toString().trim().length() <= 0) {
            SHowsnachbar("Enter Last Name");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()) {
            SHowsnachbar("Enter valid Email");
        } else if (et_phone.getText().toString().trim().length()<10) {
            SHowsnachbar("Enter valid Phone Number");
        }
        else if (devideid.isEmpty()) {
            SHowsnachbar("Mobile Not Supported");
        }
        else{
            new DeviceRegister().execute(et_firstname.getText().toString().trim(),et_lastname.getText().toString().trim(),
                                            et_email.getText().toString().trim(),et_phone.getText().toString().trim(),devideid);
        }
    }
    private void SHowsnachbar(String s) {
        Snackbar snackbar = Snackbar
                .make(rel_registration, s, Snackbar.LENGTH_LONG);
        snackbar.show();    }
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
                String _firstName = params[0];
                String _lastName=params[1];
                String _email=params[2];
                String _phone=params[3];
                String _imei=params[4];
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
                        .appendQueryParameter("mobile_number", _phone)
                        .appendQueryParameter("device_type", "mobile")
                        .appendQueryParameter("email_id", _email)
                        .appendQueryParameter("full_name", _firstName+" "+_lastName)
                        .appendQueryParameter("user_type", "consumers");

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
                        dev_userid=res.getString("user_id");
                        dev_usertype=res.getString("user_type");
                        server_message= res.getString("message");
                        // DeviceDetails dlist=new DeviceDetails(dev_userid,dev_usertype);

                    }
                    else if(server_status==2) {
                        dev_userid=res.getString("user_id");
                        dev_usertype=res.getString("user_type");
                        server_message= res.getString("message");
                        // DeviceDetails dlist=new DeviceDetails(dev_userid,dev_usertype);

                    }
                    else if(server_status==3) {
                        dev_userid=res.getString("user_id");
                        dev_usertype=res.getString("user_type");
                        server_message= res.getString("message");
                        // DeviceDetails dlist=new DeviceDetails(dev_userid,dev_usertype);

                    }
                    else{
                        server_message="Error in Data Load";
                    }
                }

                return null;
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
                SharedPreferences sharedPreferences = RegistrationPage.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0); // 0 - for private mode
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.N_UNIQ_ID,dev_userid);
                editor.putString(Constants.N_USERtYPE,dev_usertype);
                editor.commit();
                if(dev_usertype.contentEquals("consumers")) {
                    Intent intent = new Intent(RegistrationPage.this, ShareActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(RegistrationPage.this, BusinessActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
            else if(server_status==2) {
                SharedPreferences sharedPreferences = RegistrationPage.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0); // 0 - for private mode
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.N_UNIQ_ID,dev_userid);
                editor.putString(Constants.N_USERtYPE,dev_usertype);
                editor.commit();
                if(dev_usertype.contentEquals("consumers")) {
                    Intent intent = new Intent(RegistrationPage.this, ShareActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(RegistrationPage.this, BusinessActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
            else {
                // Toast.makeText(Login_Activity.this, server_message, Toast.LENGTH_LONG).show();
                SHowsnachbar(server_message);
            }
        }
    }

}
