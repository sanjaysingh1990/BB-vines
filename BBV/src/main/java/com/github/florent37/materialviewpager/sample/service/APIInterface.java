package com.github.florent37.materialviewpager.sample.service;

import com.github.florent37.materialviewpager.sample.VideoInfoResponse.Videoinforesponse;
import com.github.florent37.materialviewpager.sample.pojo.channellistresponse.ChannelListResponse;
import com.github.florent37.materialviewpager.sample.videoslistresponse.VideosListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {



    @GET("/youtube/v3/playlists")
    Call<ChannelListResponse> doGetPlayList(@Query("part") String part, @Query("channelId") String channelId, @Query("key") String key);
    @GET("/youtube/v3/playlistItems")
    Call<VideosListResponse> doGetVideos(@Query("playlistId") String playlistid, @Query("key") String key, @Query("part") String part, @Query("maxResults") Integer limit);
    @GET("/youtube/v3/videos")
    Call<Videoinforesponse> doGetVideoInfo(@Query("id") String videoid, @Query("key") String key, @Query("fields") String fields, @Query("part") String  part);


}