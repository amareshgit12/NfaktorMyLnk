package nfactornote.android.com.nfactorenote.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.design.widget.Snackbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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

import nfactornote.android.com.nfactorenote.Models.CustomUrl;
import nfactornote.android.com.nfactorenote.Models.DeviceDetails;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

import static android.R.attr.action;
import static android.R.attr.lines;
import static android.R.attr.type;

public class ShareActivity extends AppCompatActivity {
    private Button bt_customize,copy,share;
    private LinearLayout customize,copyshare;
    private RelativeLayout mainsharelay;
    private EditText et_originalUrl,et_custom;
    private RadioGroup radiogroup;
    private String urlink;
    private String uniquid;
    private String org_url;
    KeyListener variable;
    private Toolbar myToolbar;
    ImageView im_profile,im_url;

    private String text_cutom="no-data",urlid="no-data";
    String sortentext="no-data",ipAdd="no-data";
    private ProgressBar shareprogress;
    String  userActiontype="customize";

    private String UserAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
         myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        uniquid = ShareActivity.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_UNIQ_ID, null);
        im_profile=(ImageView)myToolbar.findViewById(R.id.im_consprof);
        im_url=(ImageView)myToolbar.findViewById(R.id.im_conurls);
        bt_customize=(Button)findViewById(R.id.bt_customize);
        copy=(Button)findViewById(R.id.bt_copy);
        share=(Button)findViewById(R.id.bt_share);
        shareprogress=(ProgressBar)findViewById(R.id.shareprogress);
        customize=(LinearLayout)findViewById(R.id.customlay);
        copyshare=(LinearLayout)findViewById(R.id.share_copy_lay);
        mainsharelay=(RelativeLayout)findViewById(R.id.mainsharelay);
        et_originalUrl=(EditText)findViewById(R.id.et_originalUrl);
        et_custom=(EditText)findViewById(R.id.et_custom);
        variable = et_custom.getKeyListener();
        radiogroup=(RadioGroup) findViewById(R.id.radio);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else {
                showSnackBar("Unreadable Data");
            }
        }
        im_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShareActivity.this,BusinessActivity.class);
                startActivity(intent);
            }
        });
        im_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShareActivity.this,ResultsListConsumer.class);
                startActivity(intent);
            }
        });

        bt_customize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_originalUrl.getText().toString().trim().length()<=0){
                    showSnackBar("Noting to Customize");
                }
                else if( Patterns.WEB_URL.matcher(et_originalUrl.getText().toString().trim()).matches()==false){
                    showSnackBar("Enter valid Url");
                }
                else if(et_custom.getText().toString().trim().length()<=0){
                    showSnackBar("Enter Custom Data");

                }
                    else {
                    if (CheckInternet.getNetworkConnectivityStatus(ShareActivity.this)) {
                        // Toast.makeText(Signup.this, R.string.otp_sent, Toast.LENGTH_LONG).show();
                        org_url=et_originalUrl.getText().toString().trim();
                        text_cutom=et_custom.getText().toString().trim();
                        shareprogress.setVisibility(View.VISIBLE);
                        urlink=et_originalUrl.getText().toString().trim();
                        new FetchWebsiteData().execute();
                    }else {
                        showSnackBar("No Internet");
                    }

                    /*customize.setVisibility(View.GONE);
                    copyshare.setVisibility(View.VISIBLE);*/
                }
            }
        });
        et_originalUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(et_originalUrl.getText().toString().trim().length()<=0){
                copyshare.setVisibility(View.GONE);
                customize.setVisibility(View.VISIBLE);
            }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId==R.id.rd_short){
                    if(Patterns.WEB_URL.matcher(et_originalUrl.getText().toString().trim()).matches()==false){
                        showSnackBar("Enter valid URL");
                    }
                    else {
                        userActiontype = "shorten";
                        customize.setVisibility(View.GONE);
                        et_custom.setKeyListener(null);


                        //et_custom.setText("A1WESDAZ");
                        // Toast.makeText(Signup.this, R.string.otp_sent, Toast.LENGTH_LONG).show();
                        org_url = et_originalUrl.getText().toString().trim();
                        shareprogress.setVisibility(View.VISIBLE);
                        urlink = et_originalUrl.getText().toString().trim();
                        new FetchWebsiteData().execute();
                    }

                }
                else{
                    et_custom.setKeyListener(variable);
                    userActiontype="customize";
                    et_custom.setText("");
                    if(customize.getVisibility()==View.GONE){
                        copyshare.setVisibility(View.GONE);
                        customize.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = et_custom.getText().toString().trim();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied",et_custom.getText().toString().trim());
                clipboard.setPrimaryClip(clip);
                showSnackBar("Copied to Clipboard");
            }
        });

    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbarmenu, menu);

        // show menu only when home fragment is selected
        *//*if (navItemIndex == 0) {
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }*//*
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_viewAll) {
            Intent intent=new Intent(ShareActivity.this,ResultsListConsumer.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/
    private class FetchWebsiteData extends AsyncTask<Void, Void, Void> {
        String websiteTitle, websiteDescription, imgurl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Connect to website
            Document document = null;
            try {
                document = Jsoup.connect(urlink).userAgent(UserAgent).get();
            } catch (IOException e) {
                e.printStackTrace();
                showSnackBar(e.toString());
            }
            // Get the html document title
            websiteTitle = document.title();
            Elements description = document.select("meta[name=description]");
            // Locate the content attribute
            websiteDescription = description.attr("content");
            String ogImage = null;
            Elements metaOgImage = document.select("meta[property=og:image]");
            if (metaOgImage != null) {
                imgurl = metaOgImage.first().attr("content");
                System.out.println("src :<<<------>>> " + ogImage);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
      //  Toast.makeText(ShareActivity.this,websiteTitle,Toast.LENGTH_LONG).show();
            makecustom(websiteTitle);
        }

    }

    private void makecustom(String websiteTitle) {
        Makecustom makecustomurl=new Makecustom();
        makecustomurl.execute(uniquid,org_url,text_cutom,sortentext,websiteTitle,ipAdd,userActiontype,urlid);
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
            et_originalUrl.setText(sharedText.toString().trim());
        }
        else{
            showSnackBar("Unreadable Data");
        }
    }
    void showSnackBar(String message){
        Snackbar snackbar = Snackbar
                .make(mainsharelay, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

/*
* Custom url Asyntask
* */

    private class Makecustom extends AsyncTask<String, Void, Void> {

        private static final String TAG = "Register device";
        int server_status;
        String id,mobile,name;
        String server_message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // onPreExecuteTask();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                String _uid = params[0];
                String _orgUrl=params[1];
                String _customtext=params[2];
                String _sortentext=params[3];
                String _webtitle=params[4];
                String _ipadd=params[5];
                String _uaction=params[6];
                String _urlid=params[7];
                InputStream in = null;
                int resCode = -1;

                String link = Constants.MAINURL+Constants.URLCUSTOM;
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
                        .appendQueryParameter("user_id", _uid)
                        .appendQueryParameter("original_url", _orgUrl)
                        .appendQueryParameter("customize_text", _customtext)
                        .appendQueryParameter("original_url_title", _webtitle)
                        .appendQueryParameter("ip_address", _ipadd)
                        .appendQueryParameter("userActionType", _uaction)
                        .appendQueryParameter("shorten_text", _sortentext);
                   //     .appendQueryParameter("url_id", _urlid);

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
                if(response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response.trim());
                    server_status = res.getInt("status");
                    if(server_status==1) {
                        urlid=res.getString("url_id");
                        sortentext=res.getString("shorten_text");
                        new CustomUrl().setShortentext(sortentext);
                        new CustomUrl().setUrlid(urlid);

                    }
                    else{
                        server_message="Error in Data";
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
            shareprogress.setVisibility(View.GONE);

            //  progressDialog.cancel();
            if(server_status==1) {
                    customize.setVisibility(View.GONE);
                    copyshare.setVisibility(View.VISIBLE);
                if(userActiontype.contentEquals("customize")) {
                    String sourceString = "http://dell-note.in/"+"<b>" + text_cutom + "</b> ";
                    et_custom.setText(Html.fromHtml(sourceString));
                    et_custom.setKeyListener(null);
                }
                else{
                    String sourceString = "http://dell-note.in/"+"<b>" + sortentext.trim() + "</b> ";
                    et_custom.setText(Html.fromHtml(sourceString));
                    et_custom.setKeyListener(null);

                }
            }
            else {
                showSnackBar(server_message);
            }
        }
    }
}
