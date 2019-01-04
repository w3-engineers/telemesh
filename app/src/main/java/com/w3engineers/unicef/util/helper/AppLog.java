package com.w3engineers.unicef.util.helper;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/11/2018 at 1:01 PM.
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/11/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import android.util.Log;

public class AppLog {
    public static final String TAG = AppLog.class.getName();

    public static void v(String msg){
        v(null, msg);
    }
    public static void e(String msg){
        e(null, msg);
    }
    public static void d(String msg){
        d(null, msg);
    }


    public static void v(String tag, String msg){
        if(tag == null) tag = TAG;
        Log.v(tag, msg);
    }
    public static void e(String tag, String msg){
        if(tag == null) tag = TAG;
        Log.e(tag, msg);
    }
    public static void d(String tag, String msg){
        if(tag == null) tag = TAG;
        Log.d(tag, msg);
    }

}
