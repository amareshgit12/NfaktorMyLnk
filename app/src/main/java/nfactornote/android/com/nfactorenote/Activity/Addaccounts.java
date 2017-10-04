package nfactornote.android.com.nfactorenote.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Utils.Constants;

public class Addaccounts extends AppCompatActivity {
    ImageView im_backtoacc;
    Spinner sp_usertype;
    EditText et_email,et_phone;
    String user_id,user_type;
    Button bt_add,bt_cancel;
    RelativeLayout rel_reladdacc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaccounts);
        user_id = Addaccounts.this.getSharedPreferences(Constants.SHAREDPREFERENCE_KEY, 0).getString(Constants.N_UNIQ_ID, null);
        sp_usertype=(Spinner)findViewById(R.id.sp_usertype);
        et_email=(EditText)findViewById(R.id.et_email);
        et_phone=(EditText)findViewById(R.id.et_phone);
        bt_add=(Button)findViewById(R.id.bt_add);
        bt_cancel=(Button)findViewById(R.id.bt_cancel);
        rel_reladdacc=(RelativeLayout)findViewById(R.id.rel_reladdacc);
        im_backtoacc=(ImageView)findViewById(R.id.backtoacc);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Addaccounts.this.finish();
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SHowsnachbar("Invalid user");
            }
        });
        im_backtoacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Addaccounts.this,BusinessActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });



    }
    private void SHowsnachbar(String s) {
        Snackbar snackbar = Snackbar
                .make(rel_reladdacc, s, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
