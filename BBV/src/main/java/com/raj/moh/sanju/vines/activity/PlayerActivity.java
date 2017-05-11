package com.raj.moh.sanju.vines.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.raj.moh.sanju.vines.VideoInfoResponse.Statistics;
import com.raj.moh.sanju.vines.VideoInfoResponse.Videoinforesponse;

import com.raj.moh.sanju.vines.other.Config;
import com.raj.moh.sanju.vines.other.Item;
import com.raj.moh.sanju.vines.service.APIClient;
import com.raj.moh.sanju.vines.service.APIInterface;
import com.raj.moh.sanju.vines.utility.Util;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.rajmoh.allvines.R;
import com.rajmoh.allvines.databinding.ActivityPlayerBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private InterstitialAd mInterstitialAd;

    ActivityPlayerBinding activityPlayerBinding;
    private Item item;
    private APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPlayerBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        activityPlayerBinding.setPlayerActivity(this);
        init();

    }

    private void init() {
        item = (Item) getIntent().getExtras().getSerializable(Config.ITEM);
        if(item!=null) {
            activityPlayerBinding.textTitle.setText(item.getTitle());
            activityPlayerBinding.textSubtitle.setText(item.getSubtitle());
            activityPlayerBinding.youtubeView.initialize(Config.YOUTUBE_API_KEY, this);
            apiInterface = APIClient.getClient().create(APIInterface.class);
            getVideoInfo(item.getVideoId());
            initializeAdd();
        }
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

           player.cueVideo(item.getVideoId()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
           Log.e("videoid",item.getVideoId());
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return activityPlayerBinding.youtubeView;
    }


    private void getVideoInfo(String playlistid) {
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetVideoInfo(item.getVideoId(),Config.YOUTUBE_API_KEY,"items(id,snippet(channelId,title,categoryId),statistics)","snippet,statistics");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null && response.isSuccessful()) {
                    Videoinforesponse videoinforesponse= (Videoinforesponse) response.body();
                    Statistics statistics=videoinforesponse.getItems().get(0).getStatistics();
                    Log.e("vinfo",  videoinforesponse.getItems().get(0).getStatistics().getCommentCount()+ "");
                    activityPlayerBinding.textViewCount.setText(Util.getInstance().coolFormat(Double.parseDouble(statistics.getCommentCount()),0));
                    activityPlayerBinding.textLikeCount.setText(String.valueOf(statistics.getLikeCount()));
                    activityPlayerBinding.textDislikeCount.setText(String.valueOf(statistics.getDislikeCount()));
                    activityPlayerBinding.textCommentCount.setText(String.valueOf(statistics.getCommentCount()));

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
            }
        });
    }

    private void initializeAdd() {

        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstital_ads_id));

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Toast.makeText(PlayerActivity.this,"loaded",Toast.LENGTH_LONG).show();
                mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                /**
                 *********************** make sure to advertisement will seen only once for the match ***********
                 */

            }

            @Override
            public void onAdFailedToLoad(int errorCode)   { Toast.makeText(PlayerActivity.this,"failed"+errorCode,Toast.LENGTH_LONG).show();

        }

            @Override
            public void onAdLeftApplication() {

            }

            @Override
            public void onAdOpened() {
            }
        });
    }


}

