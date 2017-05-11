package com.raj.moh.materialviewpager.sample.VideoInfoResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Videoinforesponse {

@SerializedName("items")
@Expose
private List<Item> items = null;

public List<Item> getItems() {
return items;
}

public void setItems(List<Item> items) {
this.items = items;
}

}