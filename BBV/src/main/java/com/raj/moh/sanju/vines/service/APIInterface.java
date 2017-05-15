package com.raj.moh.sanju.vines.service;

import com.raj.moh.sanju.vines.pojo.channellistresponse.ChannelListResponse;
import com.raj.moh.sanju.vines.singlevideoinforesponse.SingleVideoInfoResponse;
import com.raj.moh.sanju.vines.videoslistresponse.VideosListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {



    @GET("/youtube/v3/playlists")
    Call<ChannelListResponse> doGetPlayList(@Query("part") String part, @Query("channelId") String channelId, @Query("key") String key);
    @GET("/youtube/v3/playlistItems")
    Call<VideosListResponse> doGetVideos(@Query("playlistId") String playlistid, @Query("key") String key, @Query("part") String part, @Query("maxResults") Integer limit);
    @GET("/youtube/v3/videos")
    Call<SingleVideoInfoResponse> doGetVideoInfo(@Query("id") String videoid, @Query("key") String key, @Query("fields") String fields, @Query("part") String  part);
    @GET("/youtube/v3/playlistItems")
    Call<VideosListResponse> doGetVideosMore(@Query("playlistId") String playlistid, @Query("key") String key, @Query("part") String part, @Query("maxResults") Integer limit,@Query("pageToken") String pageToken);


}