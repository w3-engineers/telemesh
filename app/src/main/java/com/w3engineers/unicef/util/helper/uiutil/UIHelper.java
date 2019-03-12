package com.w3engineers.unicef.util.helper.uiutil;

import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.support.v7.widget.SearchView;
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

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Sikder Faysal Ahmed on [08-Oct-2018 at 1:09 PM].
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.se
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [08-Oct-2018 at 1:09 PM].
 * * --> <Second Editor> on [08-Oct-2018 at 1:09 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [08-Oct-2018 at 1:09 PM].
 * * --> <Second Reviewer> on [08-Oct-2018 at 1:09 PM].
 * * ============================================================================
 **/
public class UIHelper {

    private static String drawable_status_sending = "ic_sending_grey";
    private static String drawable_status_delivered = "ic_deliverd";
    private static String drawable_status_failed = "ic_alert";


    @BindingAdapter("imageResource")
    public static void setImageResource(ImageView imageView, int resourceId) {

        String avatarName = Constants.drawables.AVATER_IMAGE + resourceId;
        Glide.with(App.getContext())
                .load(App.getContext().getResources().getIdentifier(avatarName,
                        Constants.drawables.AVATER_DRAWABLE_DIRECTORY, App.getContext().getPackageName()))
                .into(imageView);
    }

    @BindingAdapter("src")
    public static void setImageFromResource(ImageView imageView, int resourceId) {

        Glide.with(App.getContext())
                .load(resourceId)
                .into(imageView);
    }


    @BindingAdapter("imageStatusResource")
    public static void setImageStatusResource(ImageView imageView, int resourceId){
        //AppLog.v("Image status resource id ="+resourceId);
        String resourceName = "" ;
        if (resourceId == Constants.MessageStatus.STATUS_SENDING) {
            resourceName = drawable_status_sending;
        }else if (resourceId == Constants.MessageStatus.STATUS_DELIVERED) {
            resourceName = drawable_status_delivered;
        }else {
            resourceName = drawable_status_failed;
        }
        Glide.with(App.getContext())
                .load(App.getContext().getResources().getIdentifier(resourceName,
                        Constants.drawables.AVATER_DRAWABLE_DIRECTORY, App.getContext().getPackageName()))
                .into(imageView);
    }


    public static Observable<String> fromSearchView(SearchView searchView) {

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
    public static void setTypeface(TextView v, String style) {
        switch (style) {
            case "bold":
                v.setTypeface(null, Typeface.BOLD);
                break;
            default:
                v.setTypeface(null, Typeface.NORMAL);
                break;
        }
    }

    public static String getSeparatorDate(MessageEntity messageEntity){

        if(messageEntity != null){
            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(messageEntity.time);

            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
                return App.getContext().getResources().getString(R.string.today);
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
                return App.getContext().getResources().getString(R.string.yesterday);
            } else {
                return TimeUtil.getInstance().getDateStirng(messageEntity.time);
                //return messageEntity.message;
            }
        }

        return null;
    }


}
