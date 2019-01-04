package com.w3engineers.unicef.telemesh.data.remote.model;

import com.w3engineers.ext.strom.application.data.BaseResponse;
import com.w3engineers.unicef.telemesh.TeleMeshUser.*;

/**
 * * ============================================================================
 * * Copyright (C) 2018 W3 Engineers Ltd - All Rights Reserved.
 * * Unauthorized copying of this file, via any medium is strictly prohibited
 * * Proprietary and confidential
 * * ----------------------------------------------------------------------------
 * * Created by: Mimo Saha on [10-Sep-2018 at 3:55 PM].
 * * Email: mimosaha@w3engineers.com
 * * ----------------------------------------------------------------------------
 * * Project: TeleMesh.
 * * Code Responsibility: <Purpose of code>
 * * ----------------------------------------------------------------------------
 * * Edited by :
 * * --> <First Editor> on [10-Sep-2018 at 3:55 PM].
 * * --> <Second Editor> on [10-Sep-2018 at 3:55 PM].
 * * ----------------------------------------------------------------------------
 * * Reviewed by :
 * * --> <First Reviewer> on [10-Sep-2018 at 3:55 PM].
 * * --> <Second Reviewer> on [10-Sep-2018 at 3:55 PM].
 * * ============================================================================
 **/
public class UserDiscoveryResponse extends BaseResponse {

    public static final int ADDED = 1;
    public static final int GONE = 2;

    public RMUserModel rmUserModel;
    public int state;

    public UserDiscoveryResponse(RMUserModel rmUserModel) {
        this.rmUserModel = rmUserModel;
    }

}
