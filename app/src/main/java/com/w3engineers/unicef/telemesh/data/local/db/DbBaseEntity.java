package com.w3engineers.unicef.telemesh.data.local.db;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.w3engineers.ext.strom.application.data.helper.local.base.BaseEntity;


/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [22-Oct-2018 at 6:36 PM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: telemesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [22-Oct-2018 at 6:36 PM].
 * * --> <Second Editor> on [22-Oct-2018 at 6:36 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [22-Oct-2018 at 6:36 PM].
 * * --> <Second Reviewer> on [22-Oct-2018 at 6:36 PM].
 * * ============================================================================
 **/

@SuppressLint("ParcelCreator")
public class DbBaseEntity extends BaseEntity {

    protected DbBaseEntity() {

    }

    protected DbBaseEntity(Parcel in) {
        super(in);
    }

}
