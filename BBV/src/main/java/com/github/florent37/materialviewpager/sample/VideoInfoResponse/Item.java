package com.github.florent37.materialviewpager.sample.VideoInfoResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

@SerializedName("id")
@Expose
private String id;
@SerializedName("snippet")
@Expose
private Snippet snippet;
@SerializedName("statistics")
@Expose
private Statistics statistics;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public Snippet getSnippet() {
return snippet;
}

public void setSnippet(Snippet snippet) {
this.snippet = snippet;
}

public Statistics getStatistics() {
return statistics;
}

public void setStatistics(Statistics statistics) {
this.statistics = statistics;
}

}