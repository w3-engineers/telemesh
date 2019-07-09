package com.w3engineers.unicef.telemesh.data.remote;

/*
 *  ****************************************************************************
 *  * Created by : Md Tariqul Islam on 7/8/2019 at 6:40 PM.
 *  * Email : tariqul@w3engineers.com
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md Tariqul Islam on 7/8/2019.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

public class CredentialHolder {
    private static String sParseAppId;
    private static String sParseClientKey;
    private static String sParseServerUrl;

    /**
     * Constructor
     */
    private CredentialHolder() {
    }

    /**
     * This method is responsible for initializing all the secret key
     *
     * @param parseAppId     Parse app Id
     * @param parseClientKey Parse Client key
     * @param parseServerUrl parse server url
     */
    public static void init(String parseAppId, String parseClientKey, String parseServerUrl) {
        sParseAppId = parseAppId;
        sParseClientKey = parseClientKey;
        sParseServerUrl = parseServerUrl;
    }

    /**
     * This method will return parse app id
     *
     * @return parse app id
     */
    public static String getParseAppId() {
        return sParseAppId;
    }

    /**
     * This method will return parse client key
     *
     * @return parse app client key
     */
    public static String getParseClientKey() {
        return sParseClientKey;
    }


    /**
     * This method will return parse server url
     *
     * @return parse server url
     */
    public static String getParseServerUrl() {
        return sParseServerUrl;
    }
}
