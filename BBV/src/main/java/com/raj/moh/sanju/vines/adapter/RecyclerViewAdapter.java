package com.raj.moh.sanju.vines.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.raj.moh.sanju.vines.activity.PlayerActivity;
import com.raj.moh.sanju.vines.other.Config;
import com.raj.moh.sanju.vines.utility.Util;
import com.raj.moh.sanju.vines.videoslistresponse.Item;
import com.raj.moh.sanju.vines.videoslistresponse.Snippet;
import com.rajmoh.allvines.R;

import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private List<Item> contents;
private Context mContext;
    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;
    static final int TYPE_ADS = 2;


    public RecyclerViewAdapter(List<Item> contents, Context mContext) {
        this.contents = contents;
        this.mContext=mContext;
    }

    @Override
    public int getItemViewType(int position) {
        switch (contents.get(position).getItemtype()) {
            case 0:
                return TYPE_HEADER;

            case 2:
                return TYPE_ADS;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case TYPE_HEADER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_big, parent, false);
                return new ViewHolderHeader(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new ViewHolderNormal(view) {
                };
            }
            case TYPE_ADS: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_add_view, parent, false);
                return new ViewHolderAds(view) {
                };
            }

        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ViewHolderHeader viewHolderHeader= (ViewHolderHeader) holder;
                Glide.with(mContext).load(contents.get(position).getUrl())
                        .thumbnail(0.5f)
                        .placeholder(R.drawable.place_holder)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolderHeader.mImgHeaderThumbnail);


                break;
            case TYPE_CELL:
           ViewHolderNormal viewHolderNormal= (ViewHolderNormal) holder;
                Snippet snippet=contents.get(position).getSnippet();

                //load image from url
                Glide.with(mContext).load(snippet.getThumbnails().getDefault().getUrl())
                        .thumbnail(0.5f)
                        .placeholder(R.drawable.place_holder)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolderNormal.mImgThumbnail);
                viewHolderNormal.mTxtTitle.setText(snippet.getTitle());
                viewHolderNormal.mTxtDescription.setText(snippet.getDescription());
              //  Log.e("timer", Util.getInstance().getDateTime(snippet.getPublishedAt()));
                viewHolderNormal.mTxtPublishedAt.setText(Util.getInstance().getDateTime(snippet.getPublishedAt()));
                break;
        }
    }

    public class ViewHolderNormal extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mImgThumbnail;
        private TextView mTxtTitle;
        private TextView mTxtDescription;
        private TextView mTxtPublishedAt;

        private ViewHolderNormal(View v) {
            super(v);
            mImgThumbnail = (ImageView) v.findViewById(R.id.image_thumbnail);
            mTxtTitle = (TextView) v.findViewById(R.id.text_title);
            mTxtDescription = (TextView) v.findViewById(R.id.text_description);
            mTxtPublishedAt = (TextView) v.findViewById(R.id.text_timeat);
            mImgThumbnail.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Snippet snippet=contents.get(getAdapterPosition()).getSnippet();
            Intent intent=new Intent(mContext,PlayerActivity.class);
            intent.putExtra(Config.VIDEO_ID,snippet.getResourceId().getVideoId());
            mContext.startActivity(intent);
        }
    }
    public class ViewHolderHeader extends RecyclerView.ViewHolder  {
        private ImageView mImgHeaderThumbnail;

        private ViewHolderHeader(View v) {
            super(v);
            mImgHeaderThumbnail = (ImageView) v.findViewById(R.id.image_header);
           }



    }


    public class ViewHolderAds extends RecyclerView.ViewHolder {
        private AdView mAdView;


        private ViewHolderAds(View v) {
            super(v);
            mAdView = (AdView)v. findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
                   // .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                  //  .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                    .build();
            mAdView.loadAd(adRequest);
            /*mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    Log.e("adderror",i+"");
                }

                @Override
                public void onAdOpened() {
                    Log.e("add","opened");
                }

                @Override
                public void onAdLoaded() {
                    Log.e("add","loaded");
                }
            });*/
        }


    }

}