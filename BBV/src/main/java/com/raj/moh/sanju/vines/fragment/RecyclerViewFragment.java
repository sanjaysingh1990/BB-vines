package com.raj.moh.sanju.vines.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import com.raj.moh.sanju.vines.adapter.RecyclerViewAdapter;
import com.raj.moh.sanju.vines.callbacks.SnackBarEvent;
import com.raj.moh.sanju.vines.service.APIClient;
import com.raj.moh.sanju.vines.service.APIInterface;
import com.raj.moh.sanju.vines.utility.Constants;
import com.raj.moh.sanju.vines.utility.Util;
import com.raj.moh.sanju.vines.videoslistresponse.Item;
import com.raj.moh.sanju.vines.videoslistresponse.VideosListResponse;
import com.rajmoh.allvines.R;

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
    @BindView(R.id.constraintlayout)
    ConstraintLayout constraintLayout;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Item> mItemList;
    private static final int TYPE_CELL = 1;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ADS = 2;
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
        recyclerViewAdapter = new RecyclerViewAdapter(mItemList, getActivity());
        mRecyclerView.setAdapter(recyclerViewAdapter);

      setListener();

    }

    public void loadData()
    {   final String playlistid = getArguments().getString(Constants.PLAYLISTID);
      //  Log.e("id", playlistid + "");
//        Log.e("size",mItemList.size()+"");
        if(mItemList.size()==0) {
             if(Util.getInstance().isOnline(getActivity())) {
                 mProgressBar.setVisibility(View.VISIBLE);
                 getplayList(playlistid);
            }
            else
            {
                Util.getInstance().showSnackBar(constraintLayout, getResources().getString(R.string.no_internet_connecton), "RETRY",true, new SnackBarEvent() {
                    @Override
                    public void retry() {
                       loadData();
                    }
                });
            }
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
                         //  Log.e("status","reached bottom");
                        }
                    }

                }
            }

        };


        mRecyclerView.addOnScrollListener(onScrollListener);
    }

    private void getplayList(final String playlistid) {
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
                    Item headeritem = new Item();
                    headeritem.setItemtype(TYPE_HEADER);
                    headeritem.setUrl(getArguments().getString(Constants.URL));
                    headeritem.setId(playlistid);
                   // mItemList.add(headeritem);
                    for (Item item : videosListResponse.getItems()) {
                          if(item.getSnippet().getTitle().compareToIgnoreCase("Private video")!=0) {
                           item.setItemtype(TYPE_CELL);
                           mItemList.add(item);
                       }
                           if(mItemList.size()%10==0&&mItemList.size()>10)
                        {
                            Item adsItem = new Item();
                            adsItem.setItemtype(TYPE_ADS);
                            mItemList.add(adsItem);

                        }
                    }



                    Item adsItem = new Item();
                    adsItem.setItemtype(TYPE_ADS);
                    mItemList.add(adsItem);


                    recyclerViewAdapter.notifyDataSetChanged();
                    //to check load more enable or not
                    if(mItemList.size()<videosListResponse.getPageInfo().getTotalResults())
                    {
                        mNoMoreLoad=false;
                    }
                    else
                    {
                        mNoMoreLoad=true;
                    }
                  //  Log.e("vsize", videosListResponse.getItems().size() + "");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
              //  Log.e("error", t.getLocalizedMessage());
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
                       for (Item item : videosListResponse.getItems()) {
                           if(item.getSnippet().getTitle().compareToIgnoreCase("Private video")!=0) {
                               item.setItemtype(TYPE_CELL);
                               mItemList.add(item);
                           }
                           if(mItemList.size()%10==0) // add ads at every 10 multiple position
                           {
                               Item adsItem = new Item();
                               adsItem.setItemtype(TYPE_ADS);
                               mItemList.add(adsItem);

                           }
                    }
                    Item adsItem = new Item();
                    adsItem.setItemtype(TYPE_ADS);
                    mItemList.add(adsItem);

                    recyclerViewAdapter.notifyDataSetChanged();
                    //to check load more enable or not
                    if(mItemList.size()<videosListResponse.getPageInfo().getTotalResults())
                    {
                        mNoMoreLoad=false;
                    }
                    else
                    {
                        mNoMoreLoad=true;
                    }
                 //   Log.e("vsize", videosListResponse.getItems().size() + "");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
               // Log.e("error", t.getLocalizedMessage());
            }
        });
    }

}
