package com.raj.moh.sanju.vines.VideoInfoResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Snippet {

@SerializedName("channelId")
@Expose
private String channelId;
@SerializedName("title")
@Expose
private String title;
@SerializedName("categoryId")
@Expose
private String categoryId;

public String getChannelId() {
return channelId;
}

public void setChannelId(String channelId) {
this.channelId = channelId;
}

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public String getCategoryId() {
return categoryId;
}

public void setCategoryId(String categoryId) {
this.categoryId = categoryId;
}

}