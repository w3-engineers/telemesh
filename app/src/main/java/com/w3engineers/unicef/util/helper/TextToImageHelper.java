package com.w3engineers.unicef.util.helper;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;

import com.google.zxing.WriterException;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.io.ByteArrayOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class TextToImageHelper {

    public static void writeWalletAddressToImage(String walletAddress) {

        SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());

        boolean isQrExist = !TextUtils.isEmpty(sharedPref.read(Constants.preferenceKey.MY_WALLET_IMAGE));

        if (!isQrExist) {
            AsyncTask.execute(() -> {
                QRGEncoder qrgEncoder = new QRGEncoder(walletAddress, null, QRGContents.Type.TEXT, 300);
                try {
                    // Getting QR-Code as Bitmap
                    Bitmap bitmap = qrgEncoder.encodeAsBitmap();

                    String bitmapAddress = BitMapToString(bitmap);

                    sharedPref.write(Constants.preferenceKey.MY_WALLET_IMAGE, bitmapAddress);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * @param bitmap Bitmap
     * @return converting bitmap and return a string
     */
    private static String BitMapToString(Bitmap bitmap) {
        if (bitmap == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
