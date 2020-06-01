package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

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

            try {
                ExifInterface exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                Matrix matrix = null;
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    matrix = new Matrix();
                    matrix.postRotate(270);
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    matrix = new Matrix();
                    matrix.postRotate(180);
                }  else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    matrix = new Matrix();
                    matrix.postRotate(90);
                }

                if (matrix != null) {
                    ThumbImage = Bitmap.createBitmap(ThumbImage, 0, 0, ThumbImage.getWidth(), ThumbImage.getHeight(), matrix, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            FileOutputStream out = new FileOutputStream(file);
            ThumbImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public String getCopiedFilePath(String originalFilePath, boolean isThumb) {

        if (TextUtils.isEmpty(originalFilePath))
            return null;

        File originalFile = new File(originalFilePath);
        if (originalFile.exists()) {

            File copyFile;

            if (isThumb) {
                copyFile = prepareThumbFile();
            } else {
                copyFile = prepareFile();
            }

            if (copyFile != null) {
                originalFile.renameTo(copyFile);
                return copyFile.getAbsolutePath();
            }
        }
        return null;
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
