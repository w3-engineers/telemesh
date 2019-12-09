package com.w3engineers.unicef.util;


/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;

import com.w3engineers.mesh.application.data.local.wallet.WalletManager;
import com.w3engineers.unicef.util.helper.WalletPrepareListener;

public class WalletUtil {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static WalletUtil sInstance;
    private WalletPrepareListener listener;

    public static WalletUtil getInstance(Context context) {
        mContext = context;
        if (sInstance == null) {
            sInstance = new WalletUtil();
        }
        return sInstance;
    }

    private WalletUtil() {
    }

    public void createWallet(String password, WalletPrepareListener walletPrepareListener) {
        this.listener = walletPrepareListener;

        WalletManager.getInstance().createWallet(mContext, password, new WalletManager.WalletCreateListener() {
            @Override
            public void onWalletCreated(String walletAddress, String publicKey) {
                if (listener != null) {
                    listener.onGetWalletInformation(walletAddress, publicKey);
                }
            }

            @Override
            public void onError(String message) {
                if (listener != null) {
                    listener.onWalletLoadError(message);
                }
            }
        });
    }

    public void importWallet(Uri walletUri, String password, WalletPrepareListener walletPrepareListener) {
        this.listener = walletPrepareListener;
        WalletManager.getInstance().importWallet(mContext, password, walletUri, new WalletManager.WalletImportListener() {
            @Override
            public void onWalletImported(String walletAddress, String publicKey) {
                if (listener != null) {
                    listener.onGetWalletInformation(walletAddress, publicKey);
                }
            }

            @Override
            public void onError(String message) {
                if (listener != null) {
                    listener.onWalletLoadError(message);
                }
            }
        });
    }

    public void loadWallet(String password, WalletPrepareListener walletPrepareListener) {
        this.listener = walletPrepareListener;
        WalletManager.getInstance().loadWallet(mContext, password, new WalletManager.WalletLoadListener() {
            @Override
            public void onWalletLoaded(String walletAddress, String publicKey) {
                if (listener != null) {
                    listener.onGetWalletInformation(walletAddress, publicKey);
                }
            }

            @Override
            public void onError(String message) {
                if (listener != null) {
                    listener.onWalletLoadError(message);
                }
            }
        });
    }
}

