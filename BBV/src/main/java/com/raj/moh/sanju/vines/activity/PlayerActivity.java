package com.raj.moh.sanju.vines.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.raj.moh.sanju.vines.callbacks.SnackBarEvent;
import com.raj.moh.sanju.vines.other.Config;
import com.raj.moh.sanju.vines.other.Item;
import com.raj.moh.sanju.vines.service.APIClient;
import com.raj.moh.sanju.vines.service.APIInterface;
import com.raj.moh.sanju.vines.singlevideoinforesponse.SingleVideoInfoResponse;
import com.raj.moh.sanju.vines.singlevideoinforesponse.Snippet;
import com.raj.moh.sanju.vines.utility.SingleTonAds;
import com.raj.moh.sanju.vines.utility.Util;
import com.rajmoh.allvines.R;
import com.rajmoh.allvines.databinding.ActivityPlayerBinding;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;
    private InterstitialAd mInterstitialAd;

    ActivityPlayerBinding activityPlayerBinding;
    private String mVideoId="";
    private APIInterface apiInterface;
    private YouTubePlayer youTubePlayer;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPlayerBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        activityPlayerBinding.setPlayerActivity(this);
        activityPlayerBinding.setClickEvent(new ClickEvent());

        init();

    }

    private void init() {
        mVideoId=getIntent().getExtras().getString(Config.VIDEO_ID);
        if (mVideoId!= null&&mVideoId.length()>0) {
              activityPlayerBinding.youtubeView.initialize(Config.YOUTUBE_API_KEY, this);
            apiInterface = APIClient.getClient().create(APIInterface.class);

                if(SingleTonAds.getInstance().getTotalCount()%4==0) {
                initializeAdd();
                SingleTonAds.getInstance().incrementCount();
            }
            else
            {
                SingleTonAds.getInstance().incrementCount();
            }
            checkFavorite(mVideoId);
            loadData();

        }
    }

    /**
     ********************** load data for single video *********************
     */
    private void loadData()
    {
        if(Util.getInstance().isOnline(this)) {
            getVideoInfo(mVideoId);

        }
        else
        {
            Util.getInstance().showSnackBar(activityPlayerBinding.constraintlayout, getResources().getString(R.string.no_internet_connecton), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                @Override
                public void retry() {
                   loadData();
                }
            });
        }
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            //player.cueVideo(item.getVideoId()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
            player.loadVideo(mVideoId);
            youTubePlayer = player;
            // Hiding player controls
            // player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
           // Log.e("videoid", mVideoId+"");
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
        activityPlayerBinding.progressbar.setVisibility(View.VISIBLE);
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetVideoInfo(mVideoId, Config.YOUTUBE_API_KEY, "items(id,snippet(publishedAt,title,description,thumbnails),statistics)", "snippet,statistics");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                activityPlayerBinding.progressbar.setVisibility(View.GONE);

                if (response.body() != null && response.isSuccessful()) {
                    SingleVideoInfoResponse videoinforesponse = (SingleVideoInfoResponse) response.body();
                    //check if info found
                    if(videoinforesponse.getItems().size()>0) {
                        item=new Item();
                        com.raj.moh.sanju.vines.singlevideoinforesponse.Statistics statistics = videoinforesponse.getItems().get(0).getStatistics();
                        Snippet snippet=videoinforesponse.getItems().get(0).getSnippet();
                     //   Log.e("vinfo", videoinforesponse.getItems().get(0).getStatistics().getCommentCount() + "");
                        activityPlayerBinding.textviewcount.setText(Util.getInstance().Format(Integer.parseInt(statistics.getViewCount())));
                        activityPlayerBinding.texttotallikes.setText(Util.getInstance().Format(Integer.parseInt(statistics.getLikeCount())));
                        activityPlayerBinding.texttotaldislikes.setText(Util.getInstance().Format(Integer.parseInt(statistics.getDislikeCount())));
                        activityPlayerBinding.texttotalcomments.setText(Util.getInstance().Format(Integer.parseInt(statistics.getCommentCount())));
                        activityPlayerBinding.textTitle.setText(snippet.getTitle());
                        activityPlayerBinding.textSubtitle.setText(snippet.getDescription());
                        item.setCreateAt(snippet.getPublishedAt());
                        item.setTitle(snippet.getTitle());
                        item.setSubtitle(snippet.getDescription());
                        item.setVideoId(videoinforesponse.getItems().get(0).getId());
                        item.setUrl(snippet.getThumbnails().getDefault().getUrl());
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
               // Log.e("error", t.getLocalizedMessage());
                Util.getInstance().showSnackBar(activityPlayerBinding.constraintlayout,t.getLocalizedMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                    @Override
                    public void retry() {
                        getVideoInfo(mVideoId);
                    }
                });
                activityPlayerBinding.progressbar.setVisibility(View.GONE);


            }
        });
    }

    private void initializeAdd() {

        mInterstitialAd = new InterstitialAd(this);
        // set the ad unit ID
        mInterstitialAd.setAdUnitId(getString(R.string.interstital_ads_id));

        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                //.addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                  mInterstitialAd.show();
            }

            @Override
            public void onAdClosed() {


            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdLeftApplication() {

            }

            @Override
            public void onAdOpened() {
            }
        });

    }


    public class ClickEvent {
        /**
         * ********************** BACK BUTTON ***************************
         */
        public void share() {

            if (mVideoId != null) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mVideoId));
                intent.putExtra("VIDEO_ID",mVideoId);
                startActivity(intent);
            }
        }

        public void setFavorite() {
         checkRemoveOrAddFavorite(mVideoId);
               }


    }

    private void checkFavorite(String videoid)
    {

        Item item = Util.getInstance().getRelam(this).where(Item.class).equalTo("videoId",videoid).findFirst();

        if (item != null) {
            // Exists
            Log.e("status","already saved");
            activityPlayerBinding.floatingActionButton.setImageResource(R.drawable.ic_favorite);
        } else {

            // Not exist
            Log.e("status","not already saved");
            activityPlayerBinding.floatingActionButton.setImageResource(R.drawable.ic_favorite_empty);


        }
    }

    private void checkRemoveOrAddFavorite(String videoid)
    {

          if(PlayerActivity.this.item==null)
          {

              return;
          }



        Item item = Util.getInstance().getRelam(this).where(Item.class).equalTo("videoId",videoid).findFirst();

        if (item == null) {//add item to favorite list
            // Exists
             //Log.e("status","not already saved");
             activityPlayerBinding.floatingActionButton.setImageResource(R.drawable.ic_favorite);
             addToFavorite();

        } else {

            // Not exist
          //  Log.e("status"," already saved");
            activityPlayerBinding.floatingActionButton.setImageResource(R.drawable.ic_favorite_empty);
            removeFromFavorites();

        }
    }

    private void removeFromLocalFavorites()
    {
        Item item = Util.getInstance().getRelam(this).where(Item.class).equalTo("videoId",mVideoId).findFirst();

        Util.getInstance().getRelam(this).beginTransaction();
        item.removeFromRealm();
        Util.getInstance().getRelam(this).commitTransaction();
        Util.getInstance().showToast(PlayerActivity.this,"removed from favorite");

    }

    private void removeFromFavorites()
    {
        //get device id
        String android_id = Util.getInstance().getDeviceId(PlayerActivity.this);

        Util.getInstance().getDatabaseReference().child("UserFavorites").child(android_id+"Favorites").child(item.getVideoId()).removeValue().addOnCompleteListener(PlayerActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(PlayerActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // Log.e("error", e.getMessage() + "");
            }
        }).addOnSuccessListener(PlayerActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //remove data from local database releam
               removeFromLocalFavorites();
            }
        });
    }

    /**
     ************************** add favorite to list *****************
     */

    private void addToFavorite()
    {
        //get device id
        String android_id = Util.getInstance().getDeviceId(PlayerActivity.this);

        Util.getInstance().getDatabaseReference().child("UserFavorites").child(android_id+"Favorites").child(item.getVideoId()).setValue(item).addOnCompleteListener(PlayerActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(PlayerActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             //   Log.e("error", e.getMessage() + "");
            }
        }).addOnSuccessListener(PlayerActivity.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //save data to local database releam
                addToLocalFavorites();
             }
        });
    }

    private void addToLocalFavorites()
    {
        //save to favorite to database
        Realm myRealm=Util.getInstance().getRelam(PlayerActivity.this);
        myRealm.beginTransaction();
        // Create an object
        Item itemsave= myRealm.createObject(Item.class);
        itemsave.setSubtitle(PlayerActivity.this.item.getSubtitle());
        itemsave.setTitle(PlayerActivity.this.item.getTitle());
        itemsave.setUrl(PlayerActivity.this.item.getUrl());
        itemsave.setVideoId(PlayerActivity.this.item.getVideoId());
        itemsave.setCreateAt(PlayerActivity.this.item.getCreateAt());
        myRealm.commitTransaction();
        Util.getInstance().showToast(PlayerActivity.this,"saved as favorite");

    }

}

