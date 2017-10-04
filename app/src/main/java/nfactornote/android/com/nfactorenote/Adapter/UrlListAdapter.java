package nfactornote.android.com.nfactorenote.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nfactornote.android.com.nfactorenote.Activity.BusinessUrllist;
import nfactornote.android.com.nfactorenote.Activity.ChartDetails;
import nfactornote.android.com.nfactorenote.Activity.ResultsChart;
import nfactornote.android.com.nfactorenote.Activity.ResultsListConsumer;
import nfactornote.android.com.nfactorenote.Models.UrlDetails;
import nfactornote.android.com.nfactorenote.R;

/**
 * Created by User on 18-08-2017.
 */

public class UrlListAdapter extends BaseAdapter {
    Context _context;
    Holder holder;
    ArrayList<UrlDetails> urlDetailses;
    public static EditText selected_url_id;
    public UrlListAdapter(ResultsChart resultsChart, ArrayList<UrlDetails> urldetails) {
        this._context=resultsChart;
        this.urlDetailses=urldetails;

    }

    public UrlListAdapter(BusinessUrllist businessUrllist, ArrayList<UrlDetails> flatlist) {
        this._context=businessUrllist;
        this.urlDetailses=flatlist;
    }

    @Override
    public int getCount() {
        return urlDetailses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }
    public class Holder{
        TextView tv_customurl,tv_orinialUrl,tv_clicks,tv_dates;
        Button bt_share,bt_viewUrl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UrlDetails _pos=urlDetailses.get(position);
        holder=new Holder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) _context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.urldetails, parent, false);
            holder.tv_customurl=(TextView)convertView.findViewById(R.id.tv_customurl);
            holder.tv_orinialUrl=(TextView)convertView.findViewById(R.id.tv_originalUrl);
            holder.tv_dates=(TextView)convertView.findViewById(R.id.tv_date);
            holder.bt_share=(Button) convertView.findViewById(R.id.bt_shareUrl);
            holder.bt_viewUrl=(Button) convertView.findViewById(R.id.bt_viewUrl);
            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }
        holder.tv_customurl.setTag(position);
        holder.tv_orinialUrl.setTag(position);
        holder.tv_dates.setTag(position);
        holder.bt_share.setTag(position);
        holder.bt_viewUrl.setTag(position);

        holder.tv_customurl.setText("http://dellnote.in/"+_pos.getCustomizetext());
        holder.tv_orinialUrl.setText(_pos.getOrginalUrl());
        String date =_pos.getDatentime();
        //2017-08-10 15:14:34
        SimpleDateFormat input = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM");
        try {
            Date oneWayTripDate = input.parse(date);                 // parse input
            holder.tv_dates.setText(output.format(oneWayTripDate));    // format output
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.bt_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = _pos.getCustomizetext();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                _context.startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));

            }
        });
        holder.bt_viewUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context,ChartDetails.class);
                intent.putExtra("URL_ID",_pos.getUrl_id());
                _context.startActivity(intent);
            }
        });
        return convertView;
    }
}
