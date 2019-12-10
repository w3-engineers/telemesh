package com.w3engineers.unicef.telemesh.data.local.db;

public class BaseMigration {

    public BaseMigration(int targetedVersion, String... queryScript) {
        mTargetedVersion = targetedVersion;
        mQueryScript = queryScript;
    }

    public int getTargetedVersion() {
        return mTargetedVersion;
    }

    public String[] getQueryScript() {
        return mQueryScript;
    }

    private int mTargetedVersion;
    private String[] mQueryScript;

}
