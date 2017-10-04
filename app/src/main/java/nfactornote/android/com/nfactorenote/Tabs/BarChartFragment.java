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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BarChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BarChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BarChartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    BarChart barChart;
    FrameLayout bar_frame;
    String server_response;
    int server_status;
    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labels = new ArrayList<String>();
    private EditText barStartdate,barEndDate;
    String startDate="2017-08-15",endDate="2017-08-20";
    Calendar myCalendar=Calendar.getInstance();
    Calendar myCalendar2=Calendar.getInstance();


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BarChartFragment() {
        // Required empty public constructor
    }
    public static BarChartFragment newInstance(String param1, String param2) {
        BarChartFragment fragment = new BarChartFragment();
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
        View view=inflater.inflate(R.layout.fragment_bar_chart, container, false);
        bar_frame=(FrameLayout)view.findViewById(R.id.bar_frame);
        barChart = (BarChart) view.findViewById(R.id.bar_chart);
        barChart.setDescription("Bar chart");
        barStartdate=(EditText)view.findViewById(R.id.barstartdate);
        barEndDate=(EditText)view.findViewById(R.id.barEndDate);
        //calender open
        loadTheChart();

        barStartdate.setOnClickListener(new View.OnClickListener() {
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
        barEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(barStartdate.getText().toString().isEmpty()){
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void loadTheChart() {
      //  startDate=barStartdate.getText().toString().trim();
       // endDate=barEndDate.getText().toString().trim();
        if (CheckInternet.getNetworkConnectivityStatus(getActivity())) {
            new loardCHART().execute(ChartDetails.url_id,"default",startDate,endDate);

        }
        else{
            showSnackBar("No Internet");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
    void showSnackBar(String message){
        Snackbar snackbar = Snackbar
                .make(bar_frame, message, Snackbar.LENGTH_LONG);
        snackbar.show();
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
    private void updateEndDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        barEndDate.setText(sdf.format(myCalendar2.getTime()));
        loadTheChart();
    }

    private void updateStartDate() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        barStartdate.setText(sdf.format(myCalendar.getTime()));

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
                        JSONArray clickArray = res.getJSONArray("clicks_report");
                        for (int i = 0; i < clickArray.length(); i++) {
                            JSONObject o_list_obj = clickArray.getJSONObject(i);
                            String clicked_count=o_list_obj.optString("clicked_count");
                            String clicked_date=o_list_obj.optString("clicked_date");
                            entries.add(new BarEntry(Float.valueOf(clicked_count), i));
                            labels.add(clicked_date);
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

                //bar chart
                BarDataSet dataset = new BarDataSet(entries, " ");
                BarData data = new BarData(labels, dataset);
                dataset.setColor(Color.rgb(255, 102, 0));
                barChart.setData(data);
                barChart.animateY(3000);
            }
            else{
                //showSnackBar(server_response);
            }
            //   progress.setVisibility(View.GONE);

        }
    }

}
