package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.zhihu.matisse.engine.ImageEngine;

/**
 * Created by Faisal Ahmed on 2/25/2019.
 * Senior Software Engineer @ InterSpeed
 */
public class MyGlideEngineUtil implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        RequestOptions requestOptions = new RequestOptions()
                .override(resize, resize)
                .centerCrop()
                .placeholder(placeholder);
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        RequestOptions requestOptions = new RequestOptions()
                .override(resize, resize)
                .centerCrop()
                .placeholder(placeholder);
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }


    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        imageView.setAdjustViewBounds(true);
        RequestOptions requestOptions = new RequestOptions()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH);
        Glide.with(context)
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        RequestOptions requestOptions = new RequestOptions()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .centerCrop();
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }


    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}