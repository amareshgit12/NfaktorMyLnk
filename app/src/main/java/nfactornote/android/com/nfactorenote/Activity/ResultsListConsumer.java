package nfactornote.android.com.nfactorenote.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;

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
import java.util.ArrayList;

import nfactornote.android.com.nfactorenote.Adapter.UrlListAdapter;
import nfactornote.android.com.nfactorenote.Adapter.UrlListConsumerAdapter;
import nfactornote.android.com.nfactorenote.Models.UrlDetails;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class ResultsListConsumer extends AppCompatActivity {
    String uniquid;
    ListView lv_conUrl;
    SearchView searchView;
    RelativeLayout conurlLay;
    String server_response;
    int server_status;
    private Toolbar myToolbar;
    ImageView im_shareicon,im_profile;
    private ArrayList<UrlDetails> urldetails;
    UrlListConsumerAdapter uadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_list_consumer);
        myToolbar=(Toolbar)findViewById(R.id.my_consumerurltoolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        im_shareicon=(ImageView)myToolbar.findViewById(R.id.im_shareicon);
        im_profile=(ImageView)myToolbar.findViewById(R.id.im_profile);
        lv_conUrl=(ListView)findViewById(R.id.lv_conUrl);
        searchView=(SearchView)findViewById(R.id.searchView1);
        searchView.setQueryHint("Search by Original URL");
        conurlLay=(RelativeLayout)findViewById(R.id.conurlLay);

        uniquid = ResultsListConsumer.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_UNIQ_ID, null);
       urldetails=new ArrayList<>();
        getURList();
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        im_shareicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResultsListConsumer.this,ShareActivity.class);
                startActivity(intent);
            }
        });
        im_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResultsListConsumer.this,BusinessActivity.class);
                startActivity(intent);
            }
        });
        //*** setOnQueryTextListener ***
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setQuestionList(newText);
                return false;
            }
        });

    }
    private void setQuestionList(String filterText) {

        final ArrayList<UrlDetails> flatlist_search = new ArrayList<>();
        if (filterText != null && filterText.trim().length() > 0) {
            for (int i = 0; i < urldetails.size(); i++) {
                String q_title = urldetails.get(i).getOrginalUrl();
                if (q_title != null && filterText != null &&
                        q_title.toLowerCase().contains(filterText.toLowerCase())) {
                    flatlist_search.add(urldetails.get(i));
                }
            }
        } else {
            flatlist_search.addAll(urldetails);
        }
        // create an Object for
        uadapter = new UrlListConsumerAdapter(ResultsListConsumer.this, flatlist_search);
        lv_conUrl.setAdapter(uadapter);
        //  mAdapter.notifyDataSetChanged();

        uadapter.notifyDataSetChanged();



    }
    private void getURList() {
        if (CheckInternet.getNetworkConnectivityStatus(ResultsListConsumer.this)) {
            new urlList().execute(uniquid,"consumers");

        }
        else{
            showSnackBar("No Internet");
        }
    }
    /*
* GET URL LIST ASYNTASK*/
    private class urlList extends AsyncTask<String, Void, Void> {

        private static final String TAG = "SyncDetails";

        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Void doInBackground(String... params) {


            try {
                String _userid=params[0];
                String _u_type=params[1];
                InputStream in = null;
                int resCode = -1;
                String link = Constants.MAINURL + Constants.URLlIST;
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
                        .appendQueryParameter("platform_user_id", _userid)
                        .appendQueryParameter("user_type", _u_type);
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

                Log.i(TAG, "Response : " + response);

                /*
                *
                * {
   {
    "urls": [
        {

            "url_id": "url_id_598c2b02e077c",
            "original_url": "http://nqload.in/jsdhsdsds",
            "customize_text": "ddjsdksjdksdjssd",
            "shorten_text": "12G5D5YJ",
            "original_url_title": "ip_address title",
            "total_clicks": "0",
            "date_time": "2017-08-10 15:14:34"
        },

    "status": 1,
    "message": "Successfully"
}
                    },*/

                if (response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response);
                    server_status=res.getInt("status");
                    if(server_status==1) {
                        JSONArray urlListArray = res.getJSONArray("urls");
                        for (int i = 0; i < urlListArray.length(); i++) {
                            JSONObject o_list_obj = urlListArray.getJSONObject(i);
                            String orginalUrl = o_list_obj.getString("original_url");
                            String totalclick = o_list_obj.getString("total_clicks");
                            String url_id = o_list_obj.getString("url_id");
                            String customizetext = o_list_obj.getString("customize_text");
                            String shortText = o_list_obj.getString("shorten_text");
                            String datentime = o_list_obj.getString("date_time");
                            String url_title = o_list_obj.getString("original_url_title");
                            UrlDetails list1 = new UrlDetails(orginalUrl,totalclick,url_id,customizetext,shortText,datentime,url_title);
                            urldetails.add(list1);
                        }
                    }
                    else{
                        server_response="No Url found";
                    }

                }
                return null;
            } catch (Exception exception) {
                Log.e(TAG, "LoginAsync : doInBackground", exception);
                server_response="Network Error";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void user) {
            super.onPostExecute(user);
            if(server_status==1) {
                 uadapter = new UrlListConsumerAdapter(ResultsListConsumer.this,urldetails );
                lv_conUrl.setAdapter(uadapter);
            }
            else{
                showSnackBar(server_response);
            }

        }
    }
    void showSnackBar(String message){
        Snackbar snackbar = Snackbar
                .make(conurlLay, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
