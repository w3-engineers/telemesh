package com.w3engineers.unicef.telemesh.data.analytics;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class CredentialHolder {
    private static String sParseAppId;
    private static String sParseClientKey;
    private static String sParseServerUrl;
    private static CredentialHolder sInstance;

    /**
     * Constructor
     */

    public static CredentialHolder getInStance() {
        if (sInstance == null) {
            sInstance = new CredentialHolder();
        }
        return sInstance;
    }

    private CredentialHolder() {
    }

    /**
     * This method is responsible for initializing all the secret key
     *
     * @param parseAppId     Parse app Id
     * @param parseClientKey Parse Client key
     * @param parseServerUrl parse server url
     */
    public void init(String parseAppId, String parseClientKey, String parseServerUrl) {
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
