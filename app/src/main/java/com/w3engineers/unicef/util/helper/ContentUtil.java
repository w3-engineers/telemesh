package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.android.gms.common.util.IOUtils;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ContentUtil {

    private static ContentUtil contentUtil = null;
    private int ThumbHeight = 160, ThumbWidth = 200;

    static {
        contentUtil = new ContentUtil();
    }

    public static ContentUtil getInstance() {
        return contentUtil;
    }


    public String getRealPathFromURI(Uri contentURI) {

        if (contentURI == null) {
            return null;
        }
        Cursor cursor = null;

        try {
            Context context = TeleMeshApplication.getContext();

            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentURI,
                    projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if(column_index != -1){
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    cursor.close();
                    return path;
                }
                else{
                    cursor.close();
                    File tempImageFile = prepareFile();
                    if (tempImageFile == null)
                        return null;
                    tempImageFile.deleteOnExit();
                    try(OutputStream outputStream = new FileOutputStream(tempImageFile)){
                        InputStream imageInputStream = context.getContentResolver().openInputStream(contentURI);
                        if(imageInputStream != null){
                            IOUtils.copyStream(imageInputStream, outputStream);
                            imageInputStream.close();
                            outputStream.close();
                            return tempImageFile.getAbsolutePath();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return contentURI.getPath();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getThumbnailFromImagePath(String imagePath) {
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(imagePath), ThumbWidth, ThumbHeight);

        File file = prepareThumbFile();
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            ThumbImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    private File prepareFile() {
        try {
            Context context = TeleMeshApplication.getContext();
            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    context.getString(R.string.app_name));
            if (!file.exists()) {
                file.mkdirs();
            }

            File contentFolder = new File(file.getAbsolutePath() + "/" +
                    Constants.DirectoryName.ContentFolder);
            if (!contentFolder.exists()) {
                contentFolder.mkdirs();
            }

            String fileName = "IMG_" + System.currentTimeMillis();
            return File.createTempFile(fileName, ".jpg", contentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File prepareThumbFile() {
        try {
            Context context = TeleMeshApplication.getContext();
            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    context.getString(R.string.app_name));
            if (!file.exists()) {
                file.mkdirs();
            }

            File contentFolder = new File(file.getAbsolutePath() + "/" +
                    Constants.DirectoryName.ContentThumbFolder);
            if (!contentFolder.exists()) {
                contentFolder.mkdirs();
            }

            String fileName = "IMG_THUMB_" + System.currentTimeMillis();
            return File.createTempFile(fileName, ".jpg", contentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
