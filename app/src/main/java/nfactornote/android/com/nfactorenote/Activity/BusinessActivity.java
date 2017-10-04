package nfactornote.android.com.nfactorenote.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

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

import nfactornote.android.com.nfactorenote.Adapter.AccountListAdapter;
import nfactornote.android.com.nfactorenote.Models.Accounts;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class BusinessActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    TextView tv_noaccounts;
    ListView lv_accountlist;
    RelativeLayout rel_busuniess;
    ArrayList<Accounts> accountsArrayList;
    AccountListAdapter accountAdapters;
    String user_id;
    ProgressBar progress;
    ImageView im_shareicon,im_conurls;
    int server_status;
    String server_response,user_type;
    SearchView accountsearch;
    FloatingActionButton add_accounts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
        myToolbar = (Toolbar) findViewById(R.id.myb_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        im_shareicon=(ImageView)myToolbar.findViewById(R.id.im_shareicon);
        im_conurls=(ImageView)myToolbar.findViewById(R.id.im_conurls);
        add_accounts=(FloatingActionButton)findViewById(R.id.add_accounts);
        rel_busuniess=(RelativeLayout)findViewById(R.id.rel_busuniess);
        progress=(ProgressBar)findViewById(R.id.progress);
        tv_noaccounts=(TextView)findViewById(R.id.tv_empty_accounts);
        lv_accountlist=(ListView)findViewById(R.id.lv_accountlist);
        accountsearch=(SearchView)findViewById(R.id.accsearch);
        user_id = BusinessActivity.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_UNIQ_ID, null);
        user_type = BusinessActivity.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_USERtYPE, null);

        if(CheckInternet.getNetworkConnectivityStatus(BusinessActivity.this)){
            getAllAccounts();
        }
        else{
            Snackbar snackbar = Snackbar
                    .make(rel_busuniess, "No Internet", Snackbar.LENGTH_LONG);
            snackbar.show();        }

        im_shareicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BusinessActivity.this,ShareActivity.class);
                startActivity(intent);
            }
        });
        if(user_type.contains("consumers")){
            im_conurls.setVisibility(View.VISIBLE);
        }
        else{
            im_conurls.setVisibility(View.GONE);
        }
        im_conurls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BusinessActivity.this,ResultsListConsumer.class);
                startActivity(intent);
            }
        });

        //*** setOnQueryTextListener ***
        accountsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setAccountList(newText);
                return false;
            }
        });
        add_accounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(BusinessActivity.this,Addaccounts.class);
                startActivity(intent);
            }
        });

    }

    private void setAccountList(String filterText) {
        final ArrayList<Accounts> list_search = new ArrayList<>();
        if (filterText != null && filterText.trim().length() > 0) {
            for (int i = 0; i < accountsArrayList.size(); i++) {
                String q_title = accountsArrayList.get(i).getFull_name();
                if (q_title != null && filterText != null &&
                        q_title.toLowerCase().contains(filterText.toLowerCase())) {
                    list_search.add(accountsArrayList.get(i));
                }
            }
        } else {
            list_search.addAll(accountsArrayList);
        }
        // create an Object for
        accountAdapters = new AccountListAdapter(BusinessActivity.this, list_search);
        lv_accountlist.setAdapter(accountAdapters);
        //  mAdapter.notifyDataSetChanged();

        accountAdapters.notifyDataSetChanged();
    }

    private void getAllAccounts() {
        new AccountListAsyntask().execute(user_id);
    }
    /*
* GET URL LIST ASYNTASK*/
    private class AccountListAsyntask extends AsyncTask<String, Void, Void> {

        private static final String TAG = "SyncDetails";

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(String... params) {


            try {
                String _userid=params[0];
                InputStream in = null;
                int resCode = -1;
                String link = Constants.MAINURL + Constants.ACCOUNTlIST;
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
                        .appendQueryParameter("export_type", "ALL");
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
                {
                "accounts": [
                {
               "business_user_id": "userId_598d31605699c",
            "full_name": "Munna",
            "email_id": "Email@muuna.com",
            "mobile_number": "9620057995"
                }
                ],
                "status": 1
                }

                    },*/

                if (response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response);
                    server_status=res.getInt("status");
                    accountsArrayList=new ArrayList<>();
                    if(server_status==1) {
                        JSONArray urlListArray = res.getJSONArray("accounts");
                        for (int i = 0; i < urlListArray.length(); i++) {
                            JSONObject o_list_obj = urlListArray.getJSONObject(i);
                            String full_name = o_list_obj.getString("full_name");
                            String business_user_id = o_list_obj.getString("business_user_id");
                            String email_id = o_list_obj.getString("email_id");
                            String mobile_number = o_list_obj.getString("mobile_number");
                            Accounts list1 = new Accounts(full_name,business_user_id,email_id,mobile_number);
                            accountsArrayList.add(list1);
                        }
                    }
                    else{
                        server_response="No Accounts found";
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
                accountAdapters = new AccountListAdapter(BusinessActivity.this,accountsArrayList );
                lv_accountlist.setAdapter(accountAdapters);
            }
            else{
                lv_accountlist.setVisibility(View.GONE);
                tv_noaccounts.setVisibility(View.VISIBLE);
                //showSnackBar(server_response);
            }
            progress.setVisibility(View.GONE);

        }
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
            Intent intent=new Intent(BusinessActivity.this,ResultsChart.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }*/
}
