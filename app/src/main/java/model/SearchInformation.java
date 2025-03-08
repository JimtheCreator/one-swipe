package model;

import com.google.gson.annotations.SerializedName;

public class SearchInformation {
    @SerializedName("totalResults")
    private String totalResults;

    public String getTotalResults() {
        return totalResults;
    }
}
