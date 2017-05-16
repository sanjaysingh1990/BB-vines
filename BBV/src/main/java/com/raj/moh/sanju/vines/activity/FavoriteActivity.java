package com.raj.moh.sanju.vines.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.raj.moh.sanju.vines.adapter.RecyclerViewAdapter;
import com.raj.moh.sanju.vines.callbacks.SnackBarEvent;
import com.raj.moh.sanju.vines.other.Item;
import com.raj.moh.sanju.vines.utility.Util;
import com.raj.moh.sanju.vines.videoslistresponse.Default;
import com.raj.moh.sanju.vines.videoslistresponse.ResourceId;
import com.raj.moh.sanju.vines.videoslistresponse.Snippet;
import com.raj.moh.sanju.vines.videoslistresponse.Thumbnails;
import com.rajmoh.allvines.R;
import com.rajmoh.allvines.databinding.ActivityFavoriteBinding;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
public class FavoriteActivity extends Activity {
    private static final int TYPE_CELL = 1;
    private static final int TYPE_ADS = 2;
    private RecyclerViewAdapter recyclerViewAdapter;
    ActivityFavoriteBinding activityFavoriteBinding;
    private List<com.raj.moh.sanju.vines.videoslistresponse.Item> mItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFavoriteBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorite);
        activityFavoriteBinding.setActivityActivity(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        activityFavoriteBinding.progressbar.setVisibility(View.VISIBLE);

        mItemList = new ArrayList<>();
        RealmResults<Item> results =
                Util.getInstance().getRelam(this).where(Item.class).findAll();

        for (Item item : results) {
            Thumbnails thumbnails = new Thumbnails();
            Default def = new Default();
            def.setUrl(item.getUrl());
            thumbnails.setDefault(def);
            Snippet snippet = new Snippet();
            snippet.setThumbnails(thumbnails);
            snippet.setDescription(item.getSubtitle());
            snippet.setTitle(item.getTitle());
            snippet.setPublishedAt(item.getCreateAt());
            ResourceId resourceId = new ResourceId();
            resourceId.setVideoId(item.getVideoId());
            snippet.setResourceId(resourceId);
           // Log.e("results1", item.getTitle());
            com.raj.moh.sanju.vines.videoslistresponse.Item mItem = new com.raj.moh.sanju.vines.videoslistresponse.Item();
            mItem.setItemtype(TYPE_CELL);
            mItem.setSnippet(snippet);
            mItemList.add(mItem);
            if (mItemList.size() % 10 == 0 && mItemList.size() >= 10) {
                com.raj.moh.sanju.vines.videoslistresponse.Item adsItem = new com.raj.moh.sanju.vines.videoslistresponse.Item();
                adsItem.setItemtype(TYPE_ADS);
                mItemList.add(adsItem);

            }


        }
        if (mItemList.size() > 0) {
            //show banner ads at the end of the list
            com.raj.moh.sanju.vines.videoslistresponse.Item adsItem = new com.raj.moh.sanju.vines.videoslistresponse.Item();
            adsItem.setItemtype(TYPE_ADS);
            mItemList.add(adsItem);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        activityFavoriteBinding.recyclerView.setLayoutManager(mLayoutManager);

        //Use this now
        activityFavoriteBinding.recyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        recyclerViewAdapter = new RecyclerViewAdapter(mItemList, this);
        activityFavoriteBinding.recyclerView.setAdapter(recyclerViewAdapter);

        if (mItemList.size() == 0) {

            loadFromServer();
        }
        else
        {
            activityFavoriteBinding.progressbar.setVisibility(View.INVISIBLE);

        }
    }

    private void loadFromServer() {
        if (Util.getInstance().isOnline(this)) {
            activityFavoriteBinding.progressbar.setVisibility(View.VISIBLE);

            //get device id
            String android_id = Util.getInstance().getDeviceId(FavoriteActivity.this);
            String favoritebranch="UserFavorites";
            if(getApplicationContext().getPackageName().compareToIgnoreCase("com.rajmoh.mysteriousworld")==0)
            {
                favoritebranch="UserFavoritesMysetriousWorld";
            }
            Util.getInstance().getDatabaseReference().child(favoritebranch).child(android_id + "Favorites").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    activityFavoriteBinding.progressbar.setVisibility(View.INVISIBLE);

                    for (DataSnapshot noteDataSnapshot : dataSnapshot.getChildren()) {
                        Item item = noteDataSnapshot.getValue(Item.class);
                       // Log.e("item", item.getTitle());
                        Thumbnails thumbnails = new Thumbnails();
                        Default def = new Default();
                        def.setUrl(item.getUrl());
                        thumbnails.setDefault(def);
                        Snippet snippet = new Snippet();
                        snippet.setThumbnails(thumbnails);
                        snippet.setDescription(item.getSubtitle());
                        snippet.setTitle(item.getTitle());
                        snippet.setPublishedAt(item.getCreateAt());
                        ResourceId resourceId = new ResourceId();
                        resourceId.setVideoId(item.getVideoId());
                        snippet.setResourceId(resourceId);
                       // Log.e("results1", item.getTitle());
                        com.raj.moh.sanju.vines.videoslistresponse.Item mItem = new com.raj.moh.sanju.vines.videoslistresponse.Item();
                        mItem.setItemtype(TYPE_CELL);
                        mItem.setSnippet(snippet);
                        mItemList.add(mItem);
                        if (mItemList.size() % 10 == 0 && mItemList.size() >= 10) {
                            com.raj.moh.sanju.vines.videoslistresponse.Item adsItem = new com.raj.moh.sanju.vines.videoslistresponse.Item();
                            adsItem.setItemtype(TYPE_ADS);
                            mItemList.add(adsItem);

                        }

                        //save to favorite to database
                        Realm myRealm = Util.getInstance().getRelam(FavoriteActivity.this);
                        myRealm.beginTransaction();
                        // Create an object
                        Item itemsave = myRealm.createObject(Item.class);
                        itemsave.setSubtitle(item.getSubtitle());
                        itemsave.setTitle(item.getTitle());
                        itemsave.setUrl(item.getUrl());
                        itemsave.setVideoId(item.getVideoId());
                        itemsave.setCreateAt(item.getCreateAt());
                        myRealm.commitTransaction();


                    }
                    if (mItemList.size() > 0) {
                        //show banner ads at the end of the list
                        com.raj.moh.sanju.vines.videoslistresponse.Item adsItem = new com.raj.moh.sanju.vines.videoslistresponse.Item();
                        adsItem.setItemtype(TYPE_ADS);
                        mItemList.add(adsItem);
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                    if (mItemList.size() == 0) {
                        Util.getInstance().showSnackBar(activityFavoriteBinding.constraintlayout, getResources().getString(R.string.no_favorite), "", false, null);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.e("error", databaseError.getMessage());
                    Util.getInstance().showSnackBar(activityFavoriteBinding.constraintlayout,databaseError.getMessage(), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                        @Override
                        public void retry() {
                            loadFromServer();
                        }
                    });
                    activityFavoriteBinding.progressbar.setVisibility(View.INVISIBLE);

                }
            });
        }
        else
        {
            Util.getInstance().showSnackBar(activityFavoriteBinding.constraintlayout, getResources().getString(R.string.no_internet_connecton), getResources().getString(R.string.retry), true, new SnackBarEvent() {
                @Override
                public void retry() {
                   loadFromServer();
                }
            });
            activityFavoriteBinding.progressbar.setVisibility(View.INVISIBLE);

        }
    }

}
