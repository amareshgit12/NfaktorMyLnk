package nfactornote.android.com.nfactorenote.Activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import nfactornote.android.com.nfactorenote.Adapter.ViewPagerAdapter;
import nfactornote.android.com.nfactorenote.R;
import nfactornote.android.com.nfactorenote.Tabs.BarChartFragment;
import nfactornote.android.com.nfactorenote.Tabs.PieChartFragment;
import nfactornote.android.com.nfactorenote.Tabs.ThreePieChartFragment;

public class ChartDetails extends AppCompatActivity {
    ImageView backtoburl;
    Toolbar my_detailstoolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static String url_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_details);
        backtoburl=(ImageView)findViewById(R.id.backtoburl);
        backtoburl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            ChartDetails.super.onBackPressed();
            }
        });
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url_id = extras.getString("URL_ID");
        }
        final int[] ICONS = new int[]{
                R.mipmap.ic_insert_chart_white_24dp,
                R.mipmap.ic_pie_chart_white_24dp,
                R.mipmap.ic_pie_chart_outlined_white_24dp
        };
        my_detailstoolbar=(Toolbar)findViewById(R.id.my_charttoolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BarChartFragment(), "Monthly Report");
        adapter.addFragment(new PieChartFragment(), "Browsers");
        adapter.addFragment(new ThreePieChartFragment(), "Referrers");
        viewPager.setAdapter(adapter);
    }
}
