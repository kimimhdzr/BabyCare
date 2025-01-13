package com.example.babycare.DataBinding.Cache;

import com.example.babycare.DataBinding.Model.PostModel;

import java.util.ArrayList;
import java.util.List;

public class PostCache {
    private static PostCache instance;
    private List<PostModel> postsList;

    private PostCache() {
        postsList = new ArrayList<>();
    }

    public static synchronized PostCache getInstance() {
        if (instance == null) {
            instance = new PostCache();
        }
        return instance;
    }

    public List<PostModel> getPostsList() {
        return postsList;
    }

    public void setPostsList(List<PostModel> newpostsList) {
        this.postsList.clear();
        this.postsList.addAll(newpostsList);
    }

    public void addPost(PostModel newpost) {
        postsList.add(0, newpost); //new post to first index
    }

    public boolean isEmpty() {
        return postsList.isEmpty();
    }

    public void clearCache() {
        postsList.clear();
    }
}
