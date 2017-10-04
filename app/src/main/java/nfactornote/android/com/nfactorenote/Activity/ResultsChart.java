package nfactornote.android.com.nfactorenote.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nfactornote.android.com.nfactorenote.Adapter.UrlListAdapter;
import nfactornote.android.com.nfactorenote.Models.UrlDetails;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class ResultsChart extends AppCompatActivity implements OnChartValueSelectedListener {
    RelativeLayout openlistDraw,listviewlay,closelistview,relResultchart;
    Animation slideUp,slideDown;
    BarChart barChart;
    PieChart pieChart,tdpiechart;
    ProgressBar progress;
    String uniquid="userId_598ab9cddb6be",server_response;
    int server_status;
    EditText editText;
    private EditText pieStartdate,pieEndDate;
    Calendar myCalendar=Calendar.getInstance();
    Calendar myCalendar2=Calendar.getInstance();
    Spinner piechatspinner;
    ArrayList<UrlDetails> urldetails;
    ListView iv_urlLts;
    ImageView menuopen,menuclose;
    private Toolbar myToolbar;
    SearchView smallsearch;
    UrlListAdapter uadapter;
    TextView empty_message;
    ScrollView scrollchart;
    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<Entry> yvalues = new ArrayList<Entry>();
    ArrayList<String> xVals = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_chart);
        //uniquid = ResultsChart.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_UNIQ_ID, null);
        myToolbar = (Toolbar) findViewById(R.id.my_charttoolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);
        smallsearch=(SearchView)findViewById(R.id.smallsearch);
        smallsearch.setQueryHint("Original URL");
        menuopen=(ImageView)myToolbar.findViewById(R.id.menuopen);
        menuclose=(ImageView)myToolbar.findViewById(R.id.menuclose);
        empty_message=(TextView)findViewById(R.id.empty_message);
        scrollchart=(ScrollView)findViewById(R.id.scrollchart);
        editText=(EditText) findViewById(R.id.et_id);
        UrlListAdapter.selected_url_id=editText;
        loadTheChart();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(editText.getText().toString().length()>1){
                    if(iv_urlLts.getVisibility()==View.VISIBLE) {
                        empty_message.setVisibility(View.GONE);
                        menuclose.setVisibility(View.GONE);
                        menuopen.setVisibility(View.VISIBLE);
                        slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                        // slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                        if(listviewlay.getVisibility()== View.VISIBLE){

                            listviewlay.startAnimation(slideDown);
                            listviewlay.setVisibility(View.GONE);
                        }
                    }
                    loadTheChart();

                }



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_urlLts=(ListView)findViewById(R.id.iv_urlLts);
        progress=(ProgressBar)findViewById(R.id.progress);
        relResultchart=(RelativeLayout)findViewById(R.id.relResultchart);
        openlistDraw=(RelativeLayout)findViewById(R.id.openlistDraw);
        listviewlay=(RelativeLayout)findViewById(R.id.listviewlay);
        closelistview=(RelativeLayout)findViewById(R.id.closelistview);
        pieStartdate=(EditText)findViewById(R.id.piestartdate);
        pieEndDate=(EditText)findViewById(R.id.pieEndDate);
        piechatspinner=(Spinner)findViewById(R.id.piechatspinner);
        List<String> sp_types = new ArrayList<String>();
        sp_types.add("Search By");
        sp_types.add("Browser Name");
        sp_types.add("Browser Version");
        sp_types.add("Country Code");
        sp_types.add("Country Name");
        sp_types.add("City Name");
        sp_types.add("Platform");
        sp_types.add("State Name");
        sp_types.add("Zip Code");
        ArrayAdapter<String> adapte_visitors = new ArrayAdapter<String>(ResultsChart.this, android.R.layout.simple_spinner_item, sp_types);
        adapte_visitors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        piechatspinner.setAdapter(adapte_visitors);
        // geListofURL
        urldetails=new ArrayList<>();
        getURList();

        //search
        smallsearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
        //*** setOnQueryTextListener ***
        smallsearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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

        //calender open
        pieStartdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateStartDate();
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(ResultsChart.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                //  datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });
        pieEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pieStartdate.getText().toString().isEmpty()){
                    showSnackBar("Select Start Date");
                }
                else {
                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar2.set(Calendar.YEAR, year);
                            myCalendar2.set(Calendar.MONTH, monthOfYear);
                            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateEndDate();
                        }
                    };
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ResultsChart.this, date, myCalendar2
                            .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                            myCalendar2.get(Calendar.DAY_OF_MONTH));
                    //  datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                    datePickerDialog.show();
                }
            }
        });

        //barchart starts
        barChart = (BarChart) findViewById(R.id.chart);
        barChart.setDescription("");
       /* entries.add(new BarEntry(Float.valueOf("2"), 0));
        entries.add(new BarEntry(Float.valueOf("5"), 1));
        entries.add(new BarEntry(Float.valueOf("6"), 2));
        entries.add(new BarEntry(Float.valueOf("1"), 3));
        entries.add(new BarEntry(Float.valueOf("7"), 4));
        entries.add(new BarEntry(Float.valueOf("2"), 5));*/
       /* BarDataSet dataset = new BarDataSet(entries, "Number of  Calls");
       *//* labels.add("Jan");
        labels.add("Feb");
        labels.add("Mar");
        labels.add("Apr");
        labels.add("May");
        labels.add("Jun");*//*
        BarData data = new BarData(labels, dataset);
        dataset.setColor(Color.rgb(255, 102, 0));
        barChart.setData(data);
        barChart.animateY(3000);*/

        // bar chaer end


        // pie chat starts
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
       /* yvalues.add(new Entry(8f, 0));
        yvalues.add(new Entry(15f, 1));
        yvalues.add(new Entry(12f, 2));
        yvalues.add(new Entry(25f, 3));
        yvalues.add(new Entry(23f, 4));*/

       /* PieDataSet dataSet = new PieDataSet(yvalues, "(Browser Results)");


        xVals.add("Chrome");
        xVals.add("Opera");
        xVals.add("Safari");
        xVals.add("Firefox");
        xVals.add("Unknown");

        PieData pieDatadata = new PieData(xVals, dataSet);
        // In Percentage term
        pieDatadata.setValueFormatter(new PercentFormatter());
        // Default value
        //data.setValueFormatter(new DefaultValueFormatter(0));
        pieChart.setData(pieDatadata);
        pieChart.setDescription("");
        //for aa hole in center
        pieChart.setDrawHoleEnabled(false);
        pieChart.setTransparentCircleRadius(25f);
        pieChart.setHoleRadius(25f);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        //dataSet.setValueTextSize(13f);
        dataSet.setValueTextColor(Color.DKGRAY);
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.animateXY(1400, 1400);*/

        //pie chart end
        // 3dpie chat starts
        tdpiechart = (PieChart) findViewById(R.id.tdpiechart);
        tdpiechart.setUsePercentValues(true);
        ArrayList<Entry> tdyvaluestd = new ArrayList<Entry>();
        tdyvaluestd.add(new Entry(18f, 0));
        tdyvaluestd.add(new Entry(20f, 1));
        tdyvaluestd.add(new Entry(22f, 2));
        tdyvaluestd.add(new Entry(5f, 3));
        tdyvaluestd.add(new Entry(33f, 4));

        PieDataSet tddataSet = new PieDataSet(tdyvaluestd, "(Social Media)");

        ArrayList<String> tdxVals = new ArrayList<String>();



        PieData tdpieDatadata = new PieData(tdxVals, tddataSet);
        // In Percentage term
        tddataSet.setValueFormatter(new PercentFormatter());
        // Default value
        //data.setValueFormatter(new DefaultValueFormatter(0));
        tdpiechart.setData(tdpieDatadata);
        tdpiechart.setDescription("");
        //for aa hole in center
        tdpiechart.setDrawHoleEnabled(true);
        tdpiechart.setTransparentCircleRadius(25f);
        tdpiechart.setHoleRadius(25f);

        tddataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
       // tddataSet.setValueTextSize(13f);
        tddataSet.setValueTextColor(Color.DKGRAY);
        tdpiechart.setOnChartValueSelectedListener(this);

        tdpiechart.animateXY(1400, 1400);

        //3dpie chart end


        menuopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // listviewlay.setVisibility(View.VISIBLE);
                menuopen.setVisibility(View.GONE);
                menuclose.setVisibility(View.VISIBLE);
                slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
               // slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                if(listviewlay.getVisibility()== View.GONE){

                    listviewlay.startAnimation(slideUp);
                    listviewlay.setVisibility(View.VISIBLE);
                }
                openlistDraw.setVisibility(View.GONE);
            }
        });
        menuclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuclose.setVisibility(View.GONE);
                menuopen.setVisibility(View.VISIBLE);
                slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                // slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
                if(listviewlay.getVisibility()== View.VISIBLE){

                    listviewlay.startAnimation(slideDown);
                    listviewlay.setVisibility(View.GONE);
                }
               /* new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openlistDraw.setVisibility(View.VISIBLE);
                    }
                }, 1000);*/
            }
        });
    }

    private void loadTheChart() {
        if (CheckInternet.getNetworkConnectivityStatus(ResultsChart.this)) {
            new loardCHART().execute(editText.getText().toString().trim(),"default");

        }
        else{
            showSnackBar("No Internet");
        }
    }

    private void getURList() {
        if (CheckInternet.getNetworkConnectivityStatus(ResultsChart.this)) {
            new urlList().execute(uniquid,"consumers");

        }
        else{
            showSnackBar("No Internet");
        }
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
        uadapter = new UrlListAdapter(ResultsChart.this, flatlist_search);
        iv_urlLts.setAdapter(uadapter);
        //  mAdapter.notifyDataSetChanged();

        uadapter.notifyDataSetChanged();



    }
    private void updateEndDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        pieEndDate.setText(sdf.format(myCalendar2.getTime()));
    }

    private void updateStartDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        pieStartdate.setText(sdf.format(myCalendar.getTime()));

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {

    }
    /*
* GET URL LIST ASYNTASK*/
    private class urlList extends AsyncTask<String, Void, Void> {

        private static final String TAG = "SyncDetails";

        @Override
        protected void onPreExecute() {
        progress.setVisibility(View.VISIBLE);
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
                uadapter = new UrlListAdapter(ResultsChart.this,urldetails );
                iv_urlLts.setAdapter(uadapter);
            }
            else{
               showSnackBar(server_response);
            }
            progress.setVisibility(View.GONE);

        }
    }
    /*
* GET URL STATISTICS ASYNTASK*/
    private class loardCHART extends AsyncTask<String, Void, Void> {

        private static final String TAG = "SyncDetails";

        @Override
        protected void onPreExecute() {
           // progress.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(String... params) {


            try {
                String _url_id=params[0];
                String _searchType=params[1];
                InputStream in = null;
                int resCode = -1;
                String link = Constants.MAINURL + Constants.STATISTICS;
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
                        .appendQueryParameter("link_url_id", "url_id_598c2dafdb1ef")
                        .appendQueryParameter("start_date", "2017-08-15")
                        .appendQueryParameter("end_date", "2017-08-20")
                        .appendQueryParameter("search_type", "default");
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
    "clicks_report": [
        {
            "clicked_date": "2017-08-15",
            "clicked_count": "2"
        },
        {
            "clicked_date": "2017-08-16",
            "clicked_count": "4"
        },
        {
            "clicked_date": "2017-08-17",
            "clicked_count": "3"
        },
        {
            "clicked_date": "2017-08-20",
            "clicked_count": "1"
        },
        {
            "clicked_date": "2017-08-21",
            "clicked_count": "2"
        },
        {
            "clicked_date": "2017-08-22",
            "clicked_count": "6"
        },
        {
            "clicked_date": "2017-08-23",
            "clicked_count": "1"
        },
        {
            "clicked_date": "2017-08-25",
            "clicked_count": "1"
        }
    ],
    "browser_details": [
        {
            "browser_details": "Android 5.1.1",
            "count": "2"
        },
        {
            "browser_details": "Google Chrome 60.0.3112.101",
            "count": "9"
        },
        {
            "browser_details": "Internet Explorer 11",
            "count": "1"
        },
        {
            "browser_details": "Microsoft Edge 12.0",
            "count": "1"
        },
        {
            "browser_details": "Mozilla Firefox 54.0.1",
            "count": "4"
        },
        {
            "browser_details": "Opera 46.0",
            "count": "3"
        }
    ],
    "status": 1,
    "message": "Successfully",
    "referrer_details": [
        {
            "referrer_details": "direct",
            "count": "14"
        },
        {
            "referrer_details": "http://www.soundofproductivity.com/version-two/",
            "count": "1"
        },
        {
            "referrer_details": "http://www.win-rar.com/predownload.html?&f=winrar-x64-550.exe&spV=true&subD=true&L=0",
            "count": "1"
        },
        {
            "referrer_details": "https://www.google.co.in/",
            "count": "3"
        },
        {
            "referrer_details": "https://www.google.com/",

                    },*/

                if (response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response);
                    server_status=res.getInt("status");
                    if(server_status==1) {
                        JSONArray clickArray = res.getJSONArray("clicks_report");
                        for (int i = 0; i < clickArray.length(); i++) {
                            JSONObject o_list_obj = clickArray.getJSONObject(i);
                            String clicked_count=o_list_obj.optString("clicked_count");
                            String clicked_date=o_list_obj.optString("clicked_date");
                            entries.add(new BarEntry(Float.valueOf(clicked_count), i));
                            labels.add(clicked_date);
                        }
                        JSONArray browser_details = res.getJSONArray("browser_details");
                        for (int i = 0; i < browser_details.length(); i++) {
                            JSONObject o_list_obj = browser_details.getJSONObject(i);
                            String browsername=o_list_obj.optString("browser_details");
                            String count=o_list_obj.optString("count");
                            yvalues.add(new Entry(Float.valueOf(count), 1));
                            xVals.add(browsername);

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
                scrollchart.setVisibility(View.VISIBLE);

                //bar chart
                BarDataSet dataset = new BarDataSet(entries, "Number of  Calls");
                BarData data = new BarData(labels, dataset);
                dataset.setColor(Color.rgb(255, 102, 0));
                barChart.setData(data);
                barChart.animateY(3000);

                // piechart

                PieDataSet dataSet = new PieDataSet(yvalues, " ");
                PieData pieDatadata = new PieData(xVals,dataSet);
                // In Percentage term
                pieDatadata.setValueFormatter(new PercentFormatter());
                // Default value
                //data.setValueFormatter(new DefaultValueFormatter(0));
                pieChart.setData(pieDatadata);
                pieChart.setDescription("");
                //for aa hole in center
                pieChart.setDrawHoleEnabled(false);
                pieChart.setTransparentCircleRadius(25f);
                pieChart.setHoleRadius(25f);

                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                //dataSet.setValueTextSize(13f);
                dataSet.setValueTextColor(Color.DKGRAY);
                pieChart.setOnChartValueSelectedListener(ResultsChart.this);
                pieChart.animateXY(1400, 1400);

            }
            else{
                scrollchart.setVisibility(View.GONE);
                showSnackBar(server_response);
            }
         //   progress.setVisibility(View.GONE);

        }
    }
    void showSnackBar(String message){
        Snackbar snackbar = Snackbar
                .make(relResultchart, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
