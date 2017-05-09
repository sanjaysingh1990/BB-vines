package com.github.florent37.materialviewpager.sample.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.Toast;

import com.github.florent37.materialviewpager.sample.R;
import com.github.florent37.materialviewpager.sample.databinding.ActivityPlayerBinding;
import com.github.florent37.materialviewpager.sample.other.Config;
import com.github.florent37.materialviewpager.sample.other.Item;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_REQUEST = 1;

    ActivityPlayerBinding activityPlayerBinding;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPlayerBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        activityPlayerBinding.setPlayerActivity(this);
        init();

    }

    private void init() {
        item = (Item) getIntent().getExtras().getSerializable(Config.ITEM);
        activityPlayerBinding.textTitle.setText(item.getTitle());
        activityPlayerBinding.textSubtitle.setText(item.getSubtitle());
        activityPlayerBinding.youtubeView.initialize(Config.YOUTUBE_API_KEY, this);

    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

           player.cueVideo(item.getVideoId()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo

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


}

