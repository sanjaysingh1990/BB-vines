package com.raj.moh.materialviewpager.sample.other;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NEERAJ on 5/7/2017.
 */

public class Data implements Parcelable {
    public String playListName;
    public String playListUrl;
    public String playListId;

    public Data(String playlist,String url,String playListId)
    {
        this.playListName=playlist;
        this.playListUrl=url;
        this.playListId=playListId;
    }


    protected Data(Parcel in) {
        playListName = in.readString();
        playListUrl = in.readString();
        playListId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playListName);
        dest.writeString(playListUrl);
        dest.writeString(playListId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}
