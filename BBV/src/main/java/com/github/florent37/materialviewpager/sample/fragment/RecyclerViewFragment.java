package com.github.florent37.materialviewpager.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.github.florent37.materialviewpager.sample.R;
import com.github.florent37.materialviewpager.sample.adapter.TestRecyclerViewAdapter;
import com.github.florent37.materialviewpager.sample.service.APIClient;
import com.github.florent37.materialviewpager.sample.service.APIInterface;
import com.github.florent37.materialviewpager.sample.utility.Constants;
import com.github.florent37.materialviewpager.sample.videoslistresponse.VideosListResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewFragment extends Fragment {

    private static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 100;
    private APIInterface apiInterface;
    private int mLimit = 50;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView((R.id.progressbar))
    ProgressBar mProgressBar;
    private TestRecyclerViewAdapter testRecyclerViewAdapter;
    private List<com.github.florent37.materialviewpager.sample.videoslistresponse.Item> mItemList;
    private static final int TYPE_CELL = 1;
    static final int TYPE_HEADER = 0;
    private boolean mNoMoreLoad = true;
    private LinearLayoutManager mLayoutManager;
    private String mNextPageToken="";
    public static RecyclerViewFragment newInstance(String playlistid,String url) {
        RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PLAYLISTID, playlistid);
        args.putString(Constants.URL,url);
        recyclerViewFragment.setArguments(args);
        return recyclerViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mItemList = new ArrayList<>();
        ButterKnife.bind(this, view);


        //setup materialviewpager

        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mLayoutManager=new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        //  mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        testRecyclerViewAdapter = new TestRecyclerViewAdapter(mItemList, getActivity());
        mRecyclerView.setAdapter(testRecyclerViewAdapter);

      setListener();

    }

    public void loadData()
    {   String playlistid = getArguments().getString(Constants.PLAYLISTID);
        Log.e("id", playlistid + "");
//        Log.e("size",mItemList.size()+"");
        if(mItemList.size()==0) {
           mProgressBar.setVisibility(View.VISIBLE);
            getplayList(playlistid);

        }
    }

    private void setListener()
    {
        /**
         ************************** to load more data if recycler view reached bottom **********************
         */
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //recycler view reached top restart the timer



                if (dy > 0) //check for scroll down
                {



                    if (!mNoMoreLoad) {

                        if ((mLayoutManager.getChildCount() + mLayoutManager.findFirstVisibleItemPosition()) >= mLayoutManager.getItemCount()) {
                            String playlistid = getArguments().getString(Constants.PLAYLISTID);
                            mNoMoreLoad = true;
                            mProgressBar.setVisibility(View.VISIBLE);
                            getplayListMore(playlistid);
                           Log.e("status","reached bottom");
                        }
                    }

                }
            }

        };


        mRecyclerView.addOnScrollListener(onScrollListener);
    }

    private void getplayList(String playlistid) {
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetVideos(playlistid, Constants.KEY, Constants.PART, mLimit);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.body() != null && response.isSuccessful()) {
                    VideosListResponse videosListResponse = (VideosListResponse) response.body();
                    mNextPageToken=videosListResponse.getNextPageToken();
                    com.github.florent37.materialviewpager.sample.videoslistresponse.Item headeritem = new com.github.florent37.materialviewpager.sample.videoslistresponse.Item();
                    headeritem.setItemtype(TYPE_HEADER);
                    headeritem.setUrl(getArguments().getString(Constants.URL));
                  //  mItemList.add(headeritem);
                    for (com.github.florent37.materialviewpager.sample.videoslistresponse.Item item : videosListResponse.getItems()) {
                        item.setItemtype(TYPE_CELL);
                        mItemList.add(item);
                    }
                    testRecyclerViewAdapter.notifyDataSetChanged();
                    //to check load more enable or not
                    if(mItemList.size()<videosListResponse.getPageInfo().getTotalResults())
                    {
                        mNoMoreLoad=false;
                    }
                    else
                    {
                        mNoMoreLoad=true;
                    }
                    Log.e("vsize", videosListResponse.getItems().size() + "");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e("error", t.getLocalizedMessage());
            }
        });
    }

    private void getplayListMore(String playlistid) {
        /**
         GET List Resources
         **/
        Call call = apiInterface.doGetVideosMore(playlistid, Constants.KEY, Constants.PART, mLimit,mNextPageToken);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.body() != null && response.isSuccessful()) {
                    VideosListResponse videosListResponse = (VideosListResponse) response.body();
                    mNextPageToken=videosListResponse.getNextPageToken();
                    com.github.florent37.materialviewpager.sample.videoslistresponse.Item headeritem = new com.github.florent37.materialviewpager.sample.videoslistresponse.Item();
                    headeritem.setItemtype(TYPE_HEADER);
                    headeritem.setUrl(getArguments().getString(Constants.URL));
                    //  mItemList.add(headeritem);
                    for (com.github.florent37.materialviewpager.sample.videoslistresponse.Item item : videosListResponse.getItems()) {
                        item.setItemtype(TYPE_CELL);
                        mItemList.add(item);
                    }
                    testRecyclerViewAdapter.notifyDataSetChanged();
                    //to check load more enable or not
                    if(mItemList.size()<videosListResponse.getPageInfo().getTotalResults())
                    {
                        mNoMoreLoad=false;
                    }
                    else
                    {
                        mNoMoreLoad=true;
                    }
                    Log.e("vsize", videosListResponse.getItems().size() + "");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e("error", t.getLocalizedMessage());
            }
        });
    }

}
