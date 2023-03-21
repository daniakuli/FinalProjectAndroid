package com.example.finalprojectandroid.Models;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WikiSearchResult {
    @SerializedName("extract")
    CityWiki extract;

    public CityWiki getExtract() {
        return extract;
    }

    public void CityWiki(CityWiki extract) {
        this.extract = extract;
    }
}
