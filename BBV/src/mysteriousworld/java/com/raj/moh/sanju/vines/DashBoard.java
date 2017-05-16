package com.raj.moh.sanju.vines;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.raj.moh.sanju.vines.Model.UpdateFCMToken;
import com.raj.moh.sanju.vines.activity.AboutAppActivity;

import com.raj.moh.sanju.vines.activity.FavoriteActivity;
import com.raj.moh.sanju.vines.activity.FeedBackActivity;
import com.raj.moh.sanju.vines.activity.SplashActivity;
import com.raj.moh.sanju.vines.fragment.RecyclerViewFragment;
import com.raj.moh.sanju.vines.other.Colors;
import com.raj.moh.sanju.vines.other.Data;
import com.raj.moh.sanju.vines.utility.Constants;
import com.raj.moh.sanju.vines.utility.Util;
import com.kobakei.ratethisapp.RateThisApp;
import com.rajmoh.allvines.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashBoard extends DrawerActivity {

    @BindView(R.id.materialViewPager)
    MaterialViewPager mViewPager;
    private ArrayList<Data> mPlayerList;
    private ArrayList<RecyclerViewFragment> mlistFragments;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    private DatabaseReference mDatabase;
    private boolean mDoubleBackToExitPressedOnce = false;

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
        //  Log.e("id",mPlayerList.get(0).playListId+"");
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
                //    Log.e("value",cards[num].getColorCode()+"");
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
                //    Log.e("pos",position+"");
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
                    case R.id.share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                getResources().getString(R.string.share_download));
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);   break;
                    case R.id.feedback:
                        startActivity(new Intent(DashBoard.this, FeedBackActivity.class));

                        break;
                    case R.id.appinfo:
                        startActivity(new Intent(DashBoard.this, AboutAppActivity.class));
                        break;
                   /* case R.id.about:
                        String url = "http://www.lootntrick.com/youtuber/bhuvan-bam-bb-ki-vines-wiki-age-weight-height-biography-net-income";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;*/
                    case R.id.logout:
                        Util.getInstance().clearValues(DashBoard.this);
                        Intent intent=new Intent(DashBoard.this, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(new Intent(DashBoard.this, SplashActivity.class));
                        finish();
                        break;
                    case R.id.favorite:
                        startActivity(new Intent(DashBoard.this, FavoriteActivity.class));

                        break;

                }
                return true;
            }
        });

        View headerLayout = navigationView.getHeaderView(0);
        TextView mTxtUname= (TextView) headerLayout.findViewById(R.id.tv_name);
        TextView mTxtImage= (TextView) headerLayout.findViewById(R.id.text_image);
        String uname=Util.getInstance().getValueFromSharedPreference(Constants.USERNAME,"",this);
        mTxtUname.setText(uname);
        if(uname.length()>0)
        {
            String[] data=uname.split(" ");
            if(data.length==1)
            {
                mTxtImage.setText((data[0].substring(0,1)).toUpperCase());

            }
            if (data.length>1)
            {
                mTxtImage.setText((data[0].substring(0,1)+data[1].substring(0,1)).toUpperCase());

            }
        }

        rateappDialog(); //initialize rate app dialog
        init();
        updateFcmDeviceToken();
    }
    private void init()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

    private void rateappDialog()
    {

        // Set callback (optional)
        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {

            }

            @Override
            public void onNoClicked() {

            }

            @Override
            public void onCancelClicked() {
                RateThisApp.stopRateDialog(DashBoard.this);//show never until app not reinstalled
            }
        });


        // Set custom title and message
        RateThisApp.Config config = new RateThisApp.Config(3, 5);
        config.setTitle(R.string.rate_app_title);
        config.setMessage(R.string.rate_app_message);
        RateThisApp.init(config);


        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // Show a dialog if criteria is satisfied
        RateThisApp.showRateDialogIfNeeded(this);


    }

    private void updateFcmDeviceToken() {
        final String devicetoken = FirebaseInstanceId.getInstance().getToken();
        if(devicetoken==null)
            Log.e("status","null");
        if(devicetoken!=null&&Util.getInstance().getValueFromSharedPreference(Constants.FCM_DEVICE_TOKEN,"",this).compareTo(devicetoken)!=0) {
            Log.e("device token", devicetoken + "");

            //get device id
            String android_id = Util.getInstance().getDeviceId(DashBoard.this);
            UpdateFCMToken updateFCMToken = new UpdateFCMToken();
            updateFCMToken.setFcmToken(devicetoken);
            updateFCMToken.setUpdateAt(Util.getInstance().getCurrentTime());
            String deviceTokenbranch="DeviceToken";
            if(getApplicationContext().getPackageName().compareToIgnoreCase("com.rajmoh.mysteriousworld")==0)
            {
                deviceTokenbranch="DeviceTokenMysetriousWorld";
            }
            mDatabase.child(deviceTokenbranch).child(android_id).setValue(updateFCMToken).addOnCompleteListener(DashBoard.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                }
            }).addOnFailureListener(DashBoard.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                       Log.e("error", e.getMessage() + "");
                }
            }).addOnSuccessListener(DashBoard.this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //save current device token
                    Util.getInstance().saveValueToSharedPreference(Constants.FCM_DEVICE_TOKEN,devicetoken,DashBoard.this);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {

        if (mDoubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.mDoubleBackToExitPressedOnce = true;
        //snackbar just to show message no events
        Util.getInstance().showSnackBar(mDrawerLayout,getResources().getString(R.string.double_back_press),"",false,null);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                mDoubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }


}
