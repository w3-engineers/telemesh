package com.w3engineers.unicef.telemesh.data.local.db;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseEntity;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

@SuppressLint("ParcelCreator")
public class DbBaseEntity extends BaseEntity {

    protected DbBaseEntity() {

    }

    protected DbBaseEntity(@NonNull Parcel in) {
        super(in);
    }

}
