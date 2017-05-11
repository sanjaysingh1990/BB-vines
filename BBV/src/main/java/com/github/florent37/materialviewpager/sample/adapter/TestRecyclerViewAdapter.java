package com.github.florent37.materialviewpager.sample.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.materialviewpager.sample.R;
import com.github.florent37.materialviewpager.sample.activity.PlayerActivity;
import com.github.florent37.materialviewpager.sample.other.Config;
import com.github.florent37.materialviewpager.sample.utility.Util;
import com.github.florent37.materialviewpager.sample.videoslistresponse.Item;
import com.github.florent37.materialviewpager.sample.videoslistresponse.Snippet;
import com.github.florent37.materialviewpager.sample.videoslistresponse.VideosListResponse;

import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class TestRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private List<Item> contents;
private Context mContext;
    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    public TestRecyclerViewAdapter(List<Item> contents,Context mContext) {
        this.contents = contents;
        this.mContext=mContext;
    }

    @Override
    public int getItemViewType(int position) {
        switch (contents.get(position).getItemtype()) {
            case 0:
                return TYPE_HEADER;
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
                        .placeholder(R.mipmap.ic_launcher)
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
                Log.e("timer",Util.getInstance().getDateTime(snippet.getPublishedAt()));
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
            com.github.florent37.materialviewpager.sample.other.Item data=new com.github.florent37.materialviewpager.sample.other.Item();
            data.setVideoId(snippet.getResourceId().getVideoId());
            data.setTitle(snippet.getTitle());
            data.setSubtitle(snippet.getDescription());
            Intent intent=new Intent(mContext,PlayerActivity.class);
            intent.putExtra(Config.ITEM,data);
            mContext.startActivity(intent);
        }
    }
    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        private ImageView mImgHeaderThumbnail;
        private ImageView mImgPlayAll;

        private ViewHolderHeader(View v) {
            super(v);
            mImgHeaderThumbnail = (ImageView) v.findViewById(R.id.image_header);
            mImgPlayAll = (ImageView) v.findViewById(R.id.image_play);
        }


    }

}