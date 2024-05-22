package edu.byu.cs.tweeter.model.domain;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

public class FeedUpdatePage implements Serializable {
    public List<String> followers;
    public Status status;

    public FeedUpdatePage (List<String> followers, Status status) {
        this.followers = followers;
        this.status = status;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static FeedUpdatePage fromString(String pageString) {
        Gson gson = new Gson();
        return gson.fromJson(pageString, FeedUpdatePage.class);
    }
}
