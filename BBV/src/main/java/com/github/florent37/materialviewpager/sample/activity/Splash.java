package com.github.florent37.materialviewpager.sample.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.florent37.materialviewpager.sample.MainActivity;
import com.github.florent37.materialviewpager.sample.R;
import com.github.florent37.materialviewpager.sample.databinding.ActivitySplashBinding;
import com.github.florent37.materialviewpager.sample.other.Colors;
import com.github.florent37.materialviewpager.sample.other.Data;
import com.github.florent37.materialviewpager.sample.pojo.channellistresponse.ChannelListResponse;
import com.github.florent37.materialviewpager.sample.pojo.channellistresponse.Item;
import com.github.florent37.materialviewpager.sample.service.APIClient;
import com.github.florent37.materialviewpager.sample.service.APIInterface;
import com.github.florent37.materialviewpager.sample.utility.Constants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {
    private APIInterface apiInterface;
    ActivitySplashBinding activitySplashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        activitySplashBinding.setSplashActivity(this);
        init();
    }

    private void init() {
        apiInterface = APIClient.getClient().create(APIInterface.class);





        getplayList();

    }

    private void getplayList() {
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetPlayList(Constants.PART, Constants.CHANNEL_ID, Constants.KEY);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.body()!=null&&response.isSuccessful())
                {
                    ChannelListResponse channelListResponse= (ChannelListResponse) response.body();
                    Log.e("data",channelListResponse.getItems().size()+"");
                    ArrayList<Data> playlist=new ArrayList<>();
                    for(Item item:channelListResponse.getItems())
                    {
                      Data data=new Data(item.getSnippet().getTitle(),item.getSnippet().getThumbnails().getMedium().getUrl(),item.getId());
                       playlist.add(data);
                    }
                    Data data=new Data("More",Constants.MORE,Constants.MOREVIDEOSID);
                    playlist.add(data);
                    Intent mainactivity=new Intent(Splash.this,MainActivity.class);
                    mainactivity.putParcelableArrayListExtra("data",playlist);
                    startActivity(mainactivity);
                    finish();

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("error", t.getLocalizedMessage());
            }
        });
    }
}
