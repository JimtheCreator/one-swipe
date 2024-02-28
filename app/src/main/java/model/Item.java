package model;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String link;

    @SerializedName("snippet")
    private String snippet;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getSnippet() {
        return snippet;
    }
}
