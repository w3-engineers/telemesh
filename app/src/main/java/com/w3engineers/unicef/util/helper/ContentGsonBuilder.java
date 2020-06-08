package com.w3engineers.unicef.util.helper;

import com.google.gson.Gson;
import com.w3engineers.models.ContentMetaInfo;
import com.w3engineers.unicef.util.helper.model.ContentInfo;

public class ContentGsonBuilder {

    private static Gson gson = new Gson();
    private static ContentGsonBuilder contentGsonBuilder = new ContentGsonBuilder();

    public static ContentGsonBuilder getInstance() {
        return contentGsonBuilder;
    }

    public String getContentInfoJson(ContentInfo contentInfo) {
        return gson.toJson(contentInfo);
    }

    public ContentInfo getContentInfoObj(String contentInfoText) {
        return gson.fromJson(contentInfoText, ContentInfo.class);
    }
}
