package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {
    @SerializedName("items")
    private List<Item> items;

    @SerializedName("searchInformation")
    private SearchInformation searchInformation;

    public List<Item> getItems() {
        return items;
    }

    public SearchInformation getSearchInformation() {
        return searchInformation;
    }
}

