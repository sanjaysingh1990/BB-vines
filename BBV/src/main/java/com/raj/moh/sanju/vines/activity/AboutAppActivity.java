package com.raj.moh.sanju.vines.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.rajmoh.allvines.R;
import com.rajmoh.allvines.databinding.ActivityAboutAppBinding;


public class AboutAppActivity extends AppCompatActivity {

    ActivityAboutAppBinding activityAboutAppBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAboutAppBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_app);
        activityAboutAppBinding.setAboutappActivity(this);
        activityAboutAppBinding.setClickEvent(new ClickEvent());

    }

    public class ClickEvent {
        /**
         * ********************** BACK BUTTON ***************************
         */
        public void back() {
            finish();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeBannerAds();
    }

    private void initializeBannerAds() {
        //for banner add
        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
               // .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();
        activityAboutAppBinding.adView.loadAd(adRequest);
        activityAboutAppBinding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
               // Log.e("adderror", i + "");
            }

            @Override
            public void onAdOpened() {
                //Log.e("add", "opened");
            }

            @Override
            public void onAdLoaded() {
                //Log.e("add", "loaded");
            }
        });
    }
}
