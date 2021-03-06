package com.raj.moh.sanju.vines.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.raj.moh.sanju.vines.MainActivity;
import com.raj.moh.sanju.vines.callbacks.SnackBarEvent;
import com.raj.moh.sanju.vines.other.Data;
import com.raj.moh.sanju.vines.pojo.channellistresponse.ChannelListResponse;
import com.raj.moh.sanju.vines.pojo.channellistresponse.Item;
import com.raj.moh.sanju.vines.service.APIClient;
import com.raj.moh.sanju.vines.service.APIInterface;
import com.raj.moh.sanju.vines.utility.Constants;
import com.raj.moh.sanju.vines.utility.Util;
import com.rajmoh.allvines.R;
import com.rajmoh.allvines.databinding.ActivitySplashBinding;

import java.util.ArrayList;

import crickit.debut.com.library.CheckLatestVersion;
import crickit.debut.com.library.ShowDialog;
import crickit.debut.com.library.UpdateApplication;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private APIInterface apiInterface;
    ActivitySplashBinding activitySplashBinding;
    private static final String PLAY_STORE_LINK = "https://play.google.com/store/apps/details?id=com.rajmoh.allvines&hl=en";
    private ArrayList<Data> playlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        activitySplashBinding.setSplashActivity(this);


        init();
    }

    private void init() {
        hideStatusBar();

        // Font path
        String fontPath = "fonts/sailregular.otf";
        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        //apply font
        activitySplashBinding.textAppname.setTypeface(tf);
        apiInterface = APIClient.getClient().create(APIInterface.class);

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

  /*  private void getplayList() {




        Call call = apiInterface.doGetPlayList(Constants.PART, Constants.CHANNEL_ID, Constants.KEY);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.body() != null && response.isSuccessful()) {
                    ChannelListResponse channelListResponse = (ChannelListResponse) response.body();
                  //  Log.e("data", channelListResponse.getItems().size() + "");
                    ArrayList<Data> playlist = new ArrayList<>();
                    for (Item item : channelListResponse.getItems()) {
                        Data data = new Data(item.getSnippet().getTitle(), item.getSnippet().getThumbnails().getMedium().getUrl(), item.getId());
                        playlist.add(data);
                    }
                    Data data = new Data("More", Constants.MORE, Constants.MOREVIDEOSID);
                    playlist.add(data);
                    if (Util.getInstance().getValueFromSharedPreference(Constants.USERNAME, "", SplashActivity.this).length() == 0) {
                        Intent userNameActivity = new Intent(SplashActivity.this, UserNameActivity.class);
                        userNameActivity.putParcelableArrayListExtra("data", playlist);
                        startActivity(userNameActivity);
                    } else {
                        Intent mainactivity = new Intent(SplashActivity.this, MainActivity.class);
                        mainactivity.putParcelableArrayListExtra("data", playlist);
                        startActivity(mainactivity);

                    }
                    finish();

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
              //  Log.e("error", t.getLocalizedMessage());
                Util.getInstance().showSnackBar(activitySplashBinding.constraintlayout, t.getLocalizedMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                    @Override
                    public void retry() {
                        getplayList();
                    }
                });
            }
        });
    }
*/

    private void getplayList() {
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetPlayList(Constants.PART, Constants.CHANNEL_ID, Constants.KEY);
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
                    Log.e("total",channelListResponse.getPageInfo().getTotalResults()+"");
                    Log.e("size1",channelListResponse.getItems().size()+"");

                    if(playlist.size()<channelListResponse.getPageInfo().getTotalResults()) {
                        getMoreplayList(channelListResponse.getNextPageToken());
                    }
                    else
                    {
                        Data data = new Data("More", Constants.MORE, Constants.MOREVIDEOSID);
                        playlist.add(data);
                        moveTo();
                    }

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //  Log.e("error", t.getLocalizedMessage());
                Util.getInstance().showSnackBar(activitySplashBinding.constraintlayout, t.getLocalizedMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
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
        Log.e("token",nextpageToken);
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetPlayListMore(Constants.PART, Constants.CHANNEL_ID, Constants.KEY,nextpageToken);
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
                    Log.e("size2",channelListResponse.getItems().size()+"");

                    if(playlist.size()<channelListResponse.getPageInfo().getTotalResults()) {
                        getMoreplayList(channelListResponse.getNextPageToken());
                    }
                    else
                    {
                        Data data = new Data("More", Constants.MORE, Constants.MOREVIDEOSID);
                        playlist.add(data);
                        moveTo();
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //  Log.e("error", t.getLocalizedMessage());
                Util.getInstance().showSnackBar(activitySplashBinding.constraintlayout, t.getLocalizedMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
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
        if (Util.getInstance().getValueFromSharedPreference(Constants.USERNAME, "", SplashActivity.this).length() == 0) {
            Intent userNameActivity = new Intent(SplashActivity.this, UserNameActivity.class);
            userNameActivity.putParcelableArrayListExtra("data", playlist);
            startActivity(userNameActivity);
        } else {
            Intent mainactivity = new Intent(SplashActivity.this,MainActivity.class);
            mainactivity.putParcelableArrayListExtra("data", playlist);
            startActivity(mainactivity);

        }
        finish();
    }


}
