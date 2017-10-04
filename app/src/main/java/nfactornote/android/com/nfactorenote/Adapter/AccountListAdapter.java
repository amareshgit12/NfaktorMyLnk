package nfactornote.android.com.nfactorenote.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import nfactornote.android.com.nfactorenote.Activity.BusinessActivity;
import nfactornote.android.com.nfactorenote.Activity.BusinessUrllist;
import nfactornote.android.com.nfactorenote.Activity.ShareActivity;
import nfactornote.android.com.nfactorenote.Models.Accounts;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.Constants;

/**
 * Created by User on 31-08-2017.
 */

public class AccountListAdapter extends BaseAdapter {
    Context _context;
    ArrayList<Accounts> accountsList;
    Holder holder;
    String userType;
    public AccountListAdapter(BusinessActivity businessActivity, ArrayList<Accounts> accountsArrayList) {
        _context=businessActivity;
        accountsList=accountsArrayList;
    }

    @Override
    public int getCount() {
        return accountsList.size();
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
        TextView name,designation;
        Button open,remove;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Accounts _pos=accountsList.get(position);
        holder=new Holder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) _context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.accountlist, parent, false);
            userType= _context.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_USERtYPE, null);
            holder.name=(TextView)convertView.findViewById(R.id.tv_accname);
            holder.designation=(TextView)convertView.findViewById(R.id.tv_accdesig);
            holder.open=(Button) convertView.findViewById(R.id.bt_open);
            holder.remove=(Button) convertView.findViewById(R.id.bt_remove);
            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }
        holder.name.setTag(position);
        holder.designation.setTag(position);
        holder.open.setTag(position);
        holder.remove.setTag(position);

        holder.name.setText(_pos.getFull_name());
        holder.designation.setText(_pos.getEmail_id());
        if(userType.contains("consumers")){
            holder.open.setVisibility(View.GONE);
        }

        holder.open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String platform_user_id=_pos.getBusiness_user_id();
               // String user_type = _pos.ge
                String user_type = "business_users";
                Intent intent=new Intent(_context,BusinessUrllist.class);
                intent.putExtra("PL_US_ID",platform_user_id);
                intent.putExtra("USER_TYPE",user_type);
                _context.startActivity(intent);
            }
        });

        return convertView;
    }
}
