package com.raj.moh.sanju.vines.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.raj.moh.sanju.vines.DashBoard;
import com.raj.moh.sanju.vines.callbacks.SnackBarEvent;
import com.raj.moh.sanju.vines.other.Data;
import com.raj.moh.sanju.vines.pojo.channellistresponse.ChannelListResponse;
import com.raj.moh.sanju.vines.pojo.channellistresponse.Item;
import com.raj.moh.sanju.vines.service.APIClient;
import com.raj.moh.sanju.vines.service.APIInterface;
import com.raj.moh.sanju.vines.utility.Constants;
import com.raj.moh.sanju.vines.utility.TypewriterView;
import com.raj.moh.sanju.vines.utility.Util;
import com.rajmoh.allvines.R;
import com.rajmoh.allvines.databinding.MysteriousWorldSplashActivityBinding;

import java.util.ArrayList;

import crickit.debut.com.library.CheckLatestVersion;
import crickit.debut.com.library.ShowDialog;
import crickit.debut.com.library.UpdateApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MySterousWorldSplashActivity extends AppCompatActivity {

    MysteriousWorldSplashActivityBinding mysteriousWorldSplashActivityBinding;
    private APIInterface apiInterface;
    private  ArrayList<Data> playlist;
     private static final String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.rajmoh.allvines&hl=en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mysteriousWorldSplashActivityBinding = DataBindingUtil.setContentView(this, R.layout.mysterious_world_splash_activity);
        mysteriousWorldSplashActivityBinding.setMysterioussplashActivity(this);
        init();
    }

    private void init()


    {
        hideStatusBar();

        /*mHeading1= (TextView) findViewById(R.id.text_heading1);
        mHeading2= (TextView) findViewById(R.id.text_heading2);*/
        // Font path
        String fontPath = "fonts/ghostparty.ttf";
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        //apply font
       // mHeading1.setTypeface(tf);
       // mHeading2.setTypeface(tf);
       final TypewriterView typewriterView = (TypewriterView)findViewById(R.id.tagline_typewriter);
       typewriterView.setTypeface(tf);
        typewriterView.setupSound(this);
        typewriterView.setEnabled(false);

        typewriterView.pause(400)
                .type("M").pause()
                .type("Y").pause()
                .type("S").pause()
                .type("T").pause()
                .type("E").pause()
                .type("R").pause()
                .type("I").pause()
                .type("O").pause()
                .type("U").pause()
                .type("S").pause()
                .type("\n\n").pause(400)
                .type("W").pause()
                .type("O").pause()
                .type("R").pause()
                .type("L").pause()
                .type("D").pause()

                .type("!").pause(200)
                .run(new Runnable() {
                    @Override
                    public void run() {
                        // Finalize the text if user fiddled with it during animation.
                        //typewriterView.setText("MYTERIOUS WORLD");
                        typewriterView.setEnabled(false);
                        mysteriousWorldSplashActivityBinding.progressBar.setVisibility(View.VISIBLE);
                        checkNewVersion();
                    }
                });

        apiInterface = APIClient.getClient().create(APIInterface.class);





    }

    private void checkNewVersion()
    {
        if (Util.getInstance().isOnline(this)) {
            CheckLatestVersion checkLatestVersion = new CheckLatestVersion(this);
            final ShowDialog showDialog = new ShowDialog(this);
            checkLatestVersion.getCurrentVersion(PLAY_STORE_LINK, new UpdateApplication() {
                @Override
                public void newVersionFound(String latesversion) {

                    showDialog.showForceUpdateDialog();

                }

                @Override
                public void noUpdate(String message) {
                    // Toast.makeText(SplashActivity.this,message,Toast.LENGTH_LONG).show();
                    getplayList();
                }
            });
        } else {
            Util.getInstance().showToast(this, getResources().getString(R.string.no_internet_connecton));
            finish();
        }
    }

    private void getplayList() {
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetPlayList(Constants.PART, Constants.CHANNEL_ID_MYSTERIOUS_WORLD, Constants.KEY);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null && response.isSuccessful()) {
                    ChannelListResponse channelListResponse = (ChannelListResponse) response.body();
                    //  Log.e("data", channelListResponse.getItems().size() + "");
                     playlist = new ArrayList<>();
                    for (Item item : channelListResponse.getItems()) {
                        Data data = new Data(item.getSnippet().getTitle(), item.getSnippet().getThumbnails().getMedium().getUrl(), item.getId());
                        playlist.add(data);
                    }
                    if(playlist.size()<channelListResponse.getPageInfo().getTotalResults()) {
                        getMoreplayList(channelListResponse.getNextPageToken());
                    }
                    else
                    {
                        Data data = new Data("More", Constants.MORE, Constants.MOREVIDEOSID_MYSTERIOUS_WORLD);
                        playlist.add(data);
                        moveTo();
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //  Log.e("error", t.getLocalizedMessage());
                Util.getInstance().showSnackBar(mysteriousWorldSplashActivityBinding.constraintlayout, t.getLocalizedMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                    @Override
                    public void retry() {
                        getplayList();
                    }
                });
            }
        });
    }

    public void hideStatusBar() {
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void showStatusBar() {
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void getMoreplayList(String nextpageToken) {
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetPlayListMore(Constants.PART, Constants.CHANNEL_ID_MYSTERIOUS_WORLD, Constants.KEY,nextpageToken);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null && response.isSuccessful()) {
                    ChannelListResponse channelListResponse = (ChannelListResponse) response.body();
                    //  Log.e("data", channelListResponse.getItems().size() + "");
                     for (Item item : channelListResponse.getItems()) {
                        Data data = new Data(item.getSnippet().getTitle(), item.getSnippet().getThumbnails().getMedium().getUrl(), item.getId());
                        playlist.add(data);
                    }
                    if(playlist.size()<channelListResponse.getPageInfo().getTotalResults()) {
                        getMoreplayList(channelListResponse.getNextPageToken());
                    }
                    else
                    {
                        Data data = new Data("More", Constants.MORE, Constants.MOREVIDEOSID_MYSTERIOUS_WORLD);
                        playlist.add(data);
                        moveTo();
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //  Log.e("error", t.getLocalizedMessage());
                Util.getInstance().showSnackBar(mysteriousWorldSplashActivityBinding.constraintlayout, t.getLocalizedMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                    @Override
                    public void retry() {
                        getplayList();
                    }
                });
            }
        });
    }

    private void moveTo()
    {
        if (Util.getInstance().getValueFromSharedPreference(Constants.USERNAME, "", MySterousWorldSplashActivity.this).length() == 0) {
            Intent userNameActivity = new Intent(MySterousWorldSplashActivity.this, UserNameActivity.class);
            userNameActivity.putParcelableArrayListExtra("data", playlist);
            startActivity(userNameActivity);
        } else {
            Intent mainactivity = new Intent(MySterousWorldSplashActivity.this,DashBoard.class);
            mainactivity.putParcelableArrayListExtra("data", playlist);
            startActivity(mainactivity);

        }
        finish();
    }

}
