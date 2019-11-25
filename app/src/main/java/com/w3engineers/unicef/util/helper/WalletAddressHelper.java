package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.w3engineers.mesh.application.data.local.wallet.Web3jWalletHelper;
import com.w3engineers.unicef.telemesh.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class WalletAddressHelper {
    private static String walletSuffixDir;

    private static String readWalletAddress(Context context) {
        walletSuffixDir = "wallet/" + context.getResources().getString(com.w3engineers.mesh.R.string.app_name);

        String filePath = Web3jWalletHelper.onInstance(context).getWalletDir(walletSuffixDir);
        File directory = new File(filePath);
        StringBuilder result = new StringBuilder();
        File[] list = directory.listFiles();
        if (list != null) {
            File walletFile = list[0];
            if (walletFile.exists()) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(walletFile);
                    char current;
                    while (fis.available() > 0) {
                        current = (char) fis.read();
                        result.append(current);
                    }
                } catch (Exception e) {
                    Log.e("WalletRead", "Error: " + e.getMessage());
                } finally {
                    if (fis != null)
                        try {
                            fis.close();
                        } catch (IOException ignored) {
                        }
                }
            }
        }

        if (TextUtils.isEmpty(result.toString())) {
            return result.toString();
        }

        try {
            JSONObject walletObj = new JSONObject(result.toString());
            result = new StringBuilder("0x" + walletObj.optString("address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public static SpannableString getWalletSpannableString(Context context) {
        String walletText = context.getResources().getString(R.string.file_exit);
        String address = WalletAddressHelper.readWalletAddress(context);

        String walletExistMessage = String.format(walletText, address);
        SpannableString spannableString = new SpannableString(walletExistMessage);


        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.wallet_highlight_color)),
                1, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int startIndex = walletExistMessage.length() - address.length();

        spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.wallet_highlight_color)),
                startIndex, walletExistMessage.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }
}
