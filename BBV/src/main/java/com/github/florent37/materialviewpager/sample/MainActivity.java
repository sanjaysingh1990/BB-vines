package com.github.florent37.materialviewpager.sample;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.github.florent37.materialviewpager.sample.fragment.RecyclerViewFragment;
import com.github.florent37.materialviewpager.sample.other.Colors;
import com.github.florent37.materialviewpager.sample.other.Data;
import com.github.florent37.materialviewpager.sample.utility.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends DrawerActivity {

    @BindView(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    private ArrayList<Data> mPlayerList;
    private ArrayList<RecyclerViewFragment> mlistFragments;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        ButterKnife.bind(this);

        final Toolbar toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
         mPlayerList=getIntent().getParcelableArrayListExtra("data");
        mlistFragments=new ArrayList<>();
         Log.e("id",mPlayerList.get(0).playListId+"");
        for(int i=0;i<mPlayerList.size();i++)
        {
            mlistFragments.add(RecyclerViewFragment.newInstance(mPlayerList.get(i).playListId,mPlayerList.get(i).playListUrl));
        }
        mViewPager.getViewPager().setAdapter(new myPagerAdapter(getSupportFragmentManager()));


        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
               /* switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                            R.color.green,
                            "http://phandroid.s3.amazonaws.com/wp-content/uploads/2014/06/android_google_moutain_google_now_1920x1080_wallpaper_Wallpaper-HD_2560x1600_www.paperhi.com_-640x400.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                            R.color.blue,
                            "http://www.hdiphonewallpapers.us/phone-wallpapers/540x960-1/540x960-mobile-wallpapers-hd-2218x5ox3.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                            R.color.cyan,
                            "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 3:
                        return HeaderDesign.fromColorResAndUrl(
                            R.color.red,
                            "http://www.tothemobile.com/wp-content/uploads/2014/07/original.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;*/

              // get an array of all the cards
                Colors[]cards= Colors.values();
                int num=Util.getInstance().getRadomNumber();
                Log.e("value",cards[num].getColorCode()+"");
                return HeaderDesign.fromColorAndUrl(
                        Color.parseColor(cards[num].getColorCode()),
                        mPlayerList.get(page).playListUrl);


            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        final View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.notifyHeaderChanged();
                    Toast.makeText(getApplicationContext(), "Yes, the title is clickable", Toast.LENGTH_SHORT).show();
                }
            });
        }

        mViewPager.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
              }

            @Override
            public void onPageSelected(int position) {
                Log.e("pos",position+"");
                mlistFragments.get(position).loadData();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//initialize navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();
                mDrawerLayout.closeDrawers();

                switch (id) {
                    case R.id.home:
                        Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(), "Trash", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.about:
                        String url = "http://www.lootntrick.com/youtuber/bhuvan-bam-bb-ki-vines-wiki-age-weight-height-biography-net-income";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    case R.id.logout:
                        finish();

                }
                return true;
            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //load data to first framgnet instance
                if(mlistFragments.size()>0)
                {
                    mlistFragments.get(0).loadData();
                }
            }
        }, 200);
    }

    class myPagerAdapter extends FragmentStatePagerAdapter
    {


        public myPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mlistFragments.get(position);
        }

        @Override
        public int getCount() {
            return mPlayerList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mPlayerList.get(position).playListName.toString();
        }
    }
}
