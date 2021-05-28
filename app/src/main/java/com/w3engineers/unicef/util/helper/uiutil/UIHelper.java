package com.w3engineers.unicef.util.helper.uiutil;

import android.app.Activity;
import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.local.grouptable.GroupNameModel;
import com.w3engineers.unicef.telemesh.data.local.messagetable.MessageEntity;
import com.w3engineers.unicef.util.helper.ContentUtil;
import com.w3engineers.unicef.util.helper.GsonBuilder;
import com.w3engineers.unicef.util.helper.TimeUtil;
import com.w3engineers.unicef.util.helper.model.ContentInfo;

import java.util.Calendar;

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

        Context context = TeleMeshApplication.getContext();
        String avatarName = Constants.drawables.AVATAR_IMAGE + resourceId;
        Glide.with(context).load(context.getResources().getIdentifier(avatarName,
                        Constants.drawables.AVATAR_DRAWABLE_DIRECTORY, context.getPackageName()))
                .into(imageView);
    }

    @BindingAdapter("imageStatusResource")
    public static void setImageStatusResource(@NonNull ImageView imageView, int resourceId) {
        //AppLog.v("Image status resource id ="+resourceId);
        int statusId;
        if (resourceId == Constants.MessageStatus.STATUS_SENDING ||
                resourceId == Constants.MessageStatus.STATUS_SENDING_START ||
                resourceId == Constants.MessageStatus.STATUS_RESEND_START) {
            statusId = R.mipmap.ic_dot_grey;
        } else if (resourceId == Constants.MessageStatus.STATUS_SEND) {
            statusId = R.mipmap.ic_sending_grey;
        } else if (resourceId == Constants.MessageStatus.STATUS_DELIVERED) {
            statusId = R.mipmap.ic_deliverd_grey;
        } else if (resourceId == Constants.MessageStatus.STATUS_RECEIVED) {
            statusId = R.mipmap.ic_deliverd;
        } else {
            statusId = R.mipmap.ic_alert;
        }
        Glide.with(TeleMeshApplication.getContext()).load(statusId).into(imageView);
    }

    @BindingAdapter("textDurationResource")
    public static void setDuration(TextView textView, String contentInfoText) {
        if (TextUtils.isEmpty(contentInfoText))
            return;

        ContentInfo contentInfo = GsonBuilder.getInstance().getContentInfoObj(contentInfoText);
        if (contentInfo != null) {
            String duration = ContentUtil.getMediaTime(contentInfo.getDuration()) + "";
            if (!TextUtils.isEmpty(duration)) {
                textView.setText(duration);
            }
        }
    }

    public static void setImageInGlide(ImageView imageView, String imagePath) {
        Glide.with(TeleMeshApplication.getContext()).load(imagePath).into(imageView);
    }

   /* @NonNull
    public static Observable<String> fromSearchView(@NonNull SearchView searchView) {

        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                subject.onNext(s);
                //subject.onComplete();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                subject.onNext(text);
                return true;
            }
        });

        return subject;
    }*/

   /* public static Observable<String> fromSearchEditText(EditText editText) {
        final PublishSubject<String> subject = PublishSubject.create();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                subject.onNext(s.toString());
            }
        });

        return subject;
    }*/

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

    @BindingAdapter("groupName")
    public static void setGroupName(TextView v, String groupName) {
        GroupNameModel groupNameModel = GsonBuilder.getInstance().getGroupNameModelObj(groupName);
        if (groupNameModel == null) {
            v.setText(groupName);
        } else {
            v.setText(groupNameModel.getGroupName());
        }
    }

    @Nullable
    public static String getSeparatorDate(@Nullable MessageEntity messageEntity) {

        if (messageEntity != null) {
            Calendar smsTime = Calendar.getInstance();
            smsTime.setTimeInMillis(messageEntity.time);

            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
                return TeleMeshApplication.getContext().getResources().getString(R.string.today);
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
                return TeleMeshApplication.getContext().getResources().getString(R.string.yesterday);
            } else {
                return TimeUtil.getDateString(messageEntity.time);
            }
        }

        return null;
    }

    public static void hideKeyboardFrom(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
