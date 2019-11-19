package com.w3engineers.unicef.util.helper;

public interface WalletPrepareListener {

    void onGetWalletInformation(String address, String publickKey);
    void onWalletLoadError(String errorMessage);
}
