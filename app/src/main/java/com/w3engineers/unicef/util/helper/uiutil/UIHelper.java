package com.w3engineers.unicef.util.helper.uiutil;

import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.w3engineers.ext.strom.App;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.util.helper.TimeUtil;

import java.util.Calendar;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */
public class UIHelper {


    @BindingAdapter("imageResource")
    public static void setImageResource(@NonNull ImageView imageView, int resourceId) {

        String avatarName = Constants.drawables.AVATAR_IMAGE + resourceId;
        Glide.with(App.getContext())
                .load(App.getContext().getResources().getIdentifier(avatarName,
                        Constants.drawables.AVATAR_DRAWABLE_DIRECTORY, App.getContext().getPackageName()))
                .into(imageView);
    }

    /*@BindingAdapter("src")
    public static void setImageFromResource(@NonNull ImageView imageView, int resourceId) {

        Glide.with(App.getContext())
                .load(resourceId)
                .into(imageView);
    }*/


    @BindingAdapter("imageStatusResource")
    public static void setImageStatusResource(@NonNull ImageView imageView, int resourceId){
        //AppLog.v("Image status resource id ="+resourceId);
        int statusId;
        if (resourceId == Constants.MessageStatus.STATUS_SENDING) {
            statusId = R.mipmap.ic_sending_grey;
        }else if (resourceId == Constants.MessageStatus.STATUS_DELIVERED) {
            statusId = R.mipmap.ic_deliverd;
        }else {
            statusId = R.mipmap.ic_alert;
        }
        Glide.with(App.getContext()).load(statusId).into(imageView);
    }

    @NonNull
    public static Observable<String> fromSearchView(@NonNull SearchView searchView) {

        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                subject.onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                subject.onNext(text);
                return true;
            }
        });

        return subject;
    }

    @BindingAdapter("android:typeface")
    public static void setTypeface(@NonNull TextView v, @NonNull String style) {
        switch (style) {
            case "bold":
                v.setTypeface(null, Typeface.BOLD);
                break;
            default:
                v.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    @Nullable
    public static String getSeparatorDate(@Nullable MessageEntity messageEntity){

        if(messageEntity != null){
            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(messageEntity.time);

            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
                return App.getContext().getResources().getString(R.string.today);
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
                return App.getContext().getResources().getString(R.string.yesterday);
            } else {
                return TimeUtil.getDateString(messageEntity.time);
                //return messageEntity.message;
            }
        }

        return null;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
