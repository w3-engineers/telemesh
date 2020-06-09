package com.w3engineers.unicef.telemesh.data.helper;

import android.support.annotation.NonNull;

public class GroupDataHelper extends RmDataHelper {

    private static GroupDataHelper groupDataHelper = new GroupDataHelper();

    private GroupDataHelper() {

    }

    @NonNull
    public static GroupDataHelper getInstance() {
        return groupDataHelper;
    }

    public void groupDataObserver() {

    }



}
