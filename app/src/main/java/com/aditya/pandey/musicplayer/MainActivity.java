package com.aditya.pandey.musicplayer;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.orm.SugarContext;
import com.orm.SugarRecord;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbars);
        setSupportActionBar(toolbar);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.containers);
        ViewPagerAdapter obj = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(obj);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabss);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_action_audiotrack);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_history);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_action_icons8_heart_50);
        SugarContext.init(getApplicationContext());
        SugarRecord.executeQuery(" CREATE TABLE IF NOT EXISTS SONG_LIST (" +
                "ID INTEGER PRIMARY KEY, " +
                "TIME REAL NOT NULL," +
                "SONG TEXT NOT NULL, " +
                "URL TEXT NOT NULL, " +
                "ARTISTS TEXT NOT NULL, " +
                "COVERIMAGE TEXT NOT NULL, " +
                "FAV INTEGER NOT NULL, " +
                "DOWN INTEGER NOT NULL);");
    }


   public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

       @Override
       public Fragment getItem(int position) {
           if (position == 0) {
               return SearchFragment.newInstance(position + 1);
           } else if(position == 1) {
               return HistoryFragment.newInstance(position + 1);
           } else if(position == 2) {
               return Favorite.newInstance(position + 1);
           }
           return null;
       }
       @Override
       public int getCount() {
           // Show 3 total pages.
           return 3;
       }

       @Override
       public CharSequence getPageTitle(int position) {
           switch (position) {
               case 0:
                   return "Search";
               case 1:
                   return "Download";
               case 2:
                   return "Favorite";
           }
           return null;
       }
    }
}
