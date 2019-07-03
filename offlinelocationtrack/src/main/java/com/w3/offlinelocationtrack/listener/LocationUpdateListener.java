package com.w3.offlinelocationtrack.listener;

import android.location.Location;

/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/3/2019 at 11:28 AM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/3/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */


public interface LocationUpdateListener {
    void onGetLocation(Location location);
}
