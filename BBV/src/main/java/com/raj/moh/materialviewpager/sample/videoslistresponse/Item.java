package com.raj.moh.materialviewpager.sample.videoslistresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

@SerializedName("kind")
@Expose
private String kind;
@SerializedName("etag")
@Expose
private String etag;
@SerializedName("id")
@Expose
private String id;
@SerializedName("snippet")
@Expose
private Snippet snippet;

    public int getItemtype() {
        return itemtype;
    }

    public void setItemtype(int itemtype) {
        this.itemtype = itemtype;
    }

    private int itemtype;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

public String getKind() {
return kind;
}

public void setKind(String kind) {
this.kind = kind;
}

public String getEtag() {
return etag;
}

public void setEtag(String etag) {
this.etag = etag;
}

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

}