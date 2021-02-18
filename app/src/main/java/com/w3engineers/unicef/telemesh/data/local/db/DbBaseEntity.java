package com.w3engineers.unicef.telemesh.data.local.db;

import android.annotation.SuppressLint;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    protected DbBaseEntity(@NonNull Parcel in) {
        super(in);
    }

}
