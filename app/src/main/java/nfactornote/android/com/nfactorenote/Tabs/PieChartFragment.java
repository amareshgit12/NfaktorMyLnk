package nfactornote.android.com.nfactorenote.Tabs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
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
import java.util.Locale;

import nfactornote.android.com.nfactorenote.Activity.ChartDetails;
import nfactornote.android.com.nfactorenote.Activity.ResultsChart;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.CheckInternet;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class PieChartFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    PieChart pieChart;
    FrameLayout pie_frame;
    private EditText pieStartdate,pieEndDate;
    Calendar myCalendar=Calendar.getInstance();
    Calendar myCalendar2=Calendar.getInstance();
    String server_response,startDate="2017-08-15",endDate="2017-08-20";
    int server_status;
    ArrayList<Entry> yvalues = new ArrayList<Entry>();
    ArrayList<String> xVals = new ArrayList<String>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PieChartFragment() {
        // Required empty public constructor
    }

    public static PieChartFragment newInstance(String param1, String param2) {
        PieChartFragment fragment = new PieChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_pie_chart, container, false);
        pie_frame=(FrameLayout)view.findViewById(R.id.pie_frame);
        pieChart = (PieChart) view.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieStartdate=(EditText)view.findViewById(R.id.piestartdate);
        pieEndDate=(EditText)view.findViewById(R.id.pieEndDate);
        //calender open
        loadTheChart();
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), date, myCalendar2
                            .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                            myCalendar2.get(Calendar.DAY_OF_MONTH));
                    //  datePickerDialog.getDatePicker().setMinDate(myCalendar.getTimeInMillis());
                    datePickerDialog.show();
                }
            }
        });
        return view;
    }
    private void loadTheChart() {
       // startDate=pieStartdate.getText().toString().trim();
        //endDate=pieEndDate.getText().toString().trim();
        if (CheckInternet.getNetworkConnectivityStatus(getActivity())) {
            new loardCHART().execute(ChartDetails.url_id,"default",startDate,endDate);

        }
        else{
            showSnackBar("No Internet");
        }
    }
    void showSnackBar(String message){
        Snackbar snackbar = Snackbar
                .make(pie_frame, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    private void updateEndDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        pieEndDate.setText(sdf.format(myCalendar2.getTime()));
        loadTheChart();
    }

    private void updateStartDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        pieStartdate.setText(sdf.format(myCalendar.getTime()));

    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                String _start=params[2];
                String _end=params[3];
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
                        .appendQueryParameter("link_url_id", _url_id)
                        .appendQueryParameter("search_type", _searchType)
                        .appendQueryParameter("start_date", _start)
                        .appendQueryParameter("end_date", _end);
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

                    },*/

                if (response != null && response.length() > 0) {
                    JSONObject res = new JSONObject(response);
                    server_status=res.getInt("status");
                    if(server_status==1) {
                        JSONArray browser_details = res.getJSONArray("browser_details");
                        for (int i = 0; i < browser_details.length(); i++) {
                            JSONObject o_list_obj = browser_details.getJSONObject(i);
                            String browsername=o_list_obj.optString("browser_details");
                            String count=o_list_obj.optString("count");
                            yvalues.add(new Entry(Float.valueOf(count), i));
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

                PieDataSet dataSet = new PieDataSet(yvalues," ");
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
                pieChart.getLegend().setEnabled(false);

                dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                dataSet.setValueTextSize(10f);
                dataSet.setValueTextColor(Color.DKGRAY);
              //  pieChart.setOnChartValueSelectedListener((OnChartValueSelectedListener) getActivity());
                pieChart.animateXY(1400, 1400);
            }
            else{
                //showSnackBar(server_response);
            }
            //   progress.setVisibility(View.GONE);

        }
    }
}
