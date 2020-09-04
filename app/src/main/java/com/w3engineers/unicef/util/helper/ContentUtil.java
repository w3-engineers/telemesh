package com.w3engineers.unicef.util.helper;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.util.IOUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
                    File tempImageFile = prepareFile("");
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

    public String getFilePathFromUri(Uri uri) {
        String selection = null;
        String[] selectionArgs = null;
        Context context = TeleMeshApplication.getContext();
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                if(cursor != null){
                    int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    if(column_index != -1){
                        if (cursor.moveToFirst()) {
                            String path = cursor.getString(column_index);
                            cursor.close();
                            return path;
                        }
                    }
                    else{
                        cursor.close();
                        File tempImageFile = prepareFile("");
                        tempImageFile.deleteOnExit();
                        try(OutputStream outputStream = new FileOutputStream(tempImageFile)){
                            InputStream imageInputStream = context.getContentResolver().openInputStream(uri);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public String compressImage(String filePath) {
        try {
            Context context = TeleMeshApplication.getContext();
            File contentFolder = getFileDirectory(Constants.DirectoryName.ContentFolder);

            return SiliCompressor.with(context).compress(filePath, contentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public String compressVideo(String filePath) {
        try {
            Context context = TeleMeshApplication.getContext();
            File contentFolder = getFileDirectory(Constants.DirectoryName.ContentFolder);

            return SiliCompressor.with(context).compressVideo(filePath, contentFolder.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public String getThumbnailFromVideoPath(String videoPath) {
        Bitmap thumbImage = ThumbnailUtils.createVideoThumbnail(videoPath,
                MediaStore.Images.Thumbnails.MINI_KIND);

        File file = prepareThumbFile();
        if (file.exists ()) file.delete ();
        try {

            FileOutputStream out = new FileOutputStream(file);
            thumbImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
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

    public String getContentFromUrl(String fileURL) {
        OkHttpClient client = new OkHttpClient();

        String filePath = null;

        Request request = new Request.Builder().url(fileURL).build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Failed to download file: " + response);
            }

            fileURL = java.net.URLDecoder.decode(fileURL, StandardCharsets.UTF_8.name());

            String fileName = generateFileName(fileURL);
            File file = getFileFromName(fileName);

            if (file != null) {

                FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
                fos.write(response.body().bytes());
                fos.close();
            }

            if (file != null) {
                filePath = file.getAbsolutePath();

                if (isTypeImage(filePath)) {
                    filePath = compressImage(filePath);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            filePath = null;
        }

        return filePath;
    }

    public String getCopiedFilePath(String originalFilePath, boolean isThumb) {

        if (TextUtils.isEmpty(originalFilePath))
            return null;

        File originalFile = new File(originalFilePath);
        if (originalFile.exists()) {

            File copyFile;

            String extension = originalFilePath.substring(originalFilePath.lastIndexOf("."));

            if (isThumb) {
                copyFile = prepareThumbFile();
            } else {
                copyFile = prepareFile(extension);
            }

            if (copyFile != null) {
                originalFile.renameTo(copyFile);
                return copyFile.getAbsolutePath();
            }
        }
        return null;
    }

    private File prepareFile(String extension) {
        try {
            if (TextUtils.isEmpty(extension)) {
                extension = ".jpg";
            }
            File contentFolder = getFileDirectory(Constants.DirectoryName.ContentFolder);

            String fileName = "CONTENT_" + System.currentTimeMillis();
            return File.createTempFile(fileName, extension, contentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private File getFileFromName(String fileName) {
        try {
            File contentFolder = getFileDirectory(Constants.DirectoryName.ContentFolder);
            String extension = fileName.substring(fileName.lastIndexOf("."));
            return File.createTempFile(fileName, extension, contentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateFileName(String fileUrl) {
        try {
            String extension = fileUrl.substring(fileUrl.lastIndexOf("."));
            return UUID.randomUUID().toString() + extension;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFileNameFromURL(String url) {
        if (url == null) {
            return "";
        }
        try {
            URL resource = new URL(url);
            String host = resource.getHost();
            if (host.length() > 0 && url.endsWith(host)) {
                // handle ...example.com
                return "";
            }
        } catch (MalformedURLException e) {
            return "";
        }

        int startIndex = url.lastIndexOf('/') + 1;
        int length = url.length();

        // find end index for ?
        int lastQMPos = url.lastIndexOf('?');
        if (lastQMPos == -1) {
            lastQMPos = length;
        }

        // find end index for #
        int lastHashPos = url.lastIndexOf('#');
        if (lastHashPos == -1) {
            lastHashPos = length;
        }

        // calculate the end index
        int endIndex = Math.min(lastQMPos, lastHashPos);
        return url.substring(startIndex, endIndex);
    }

    private File getFileDirectory(String folderName) {
        Context context = TeleMeshApplication.getContext();
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                context.getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }

        File contentFolder = new File(file.getAbsolutePath() + "/" +
                folderName);
        if (!contentFolder.exists()) {
            contentFolder.mkdirs();
        }
        return contentFolder;
    }

    private File prepareThumbFile() {
        try {
            File contentFolder = getFileDirectory(Constants.DirectoryName.ContentThumbFolder);

            String fileName = "IMG_THUMB_" + System.currentTimeMillis();
            return File.createTempFile(fileName, ".jpg", contentFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getMediaDuration(String path) {
        MediaPlayer mediaPlayer = MediaPlayer.create(TeleMeshApplication.getContext(), Uri.parse(path));
        int duration = mediaPlayer != null ? mediaPlayer.getDuration() : 0;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (duration == 0) {
            return 0;
        }
        return duration;
    }

    public static String getMediaTime(long milliseconds) {
        String TimerString = "";
        String secondsString;

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            TimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        TimerString = TimerString + minutes + ":" + secondsString;

        return TimerString;
    }

    public String getContentMessageBody(String path) {
        if (TextUtils.isEmpty(path))
            return "";

        if (isTypeImage(path)) {
            return Constants.ContentMessageBody.IMAGE_MESSAGE;
        } else if (isTypeVideo(path)) {
            return Constants.ContentMessageBody.VIDEO_MESSAGE;
        } else if (isTypeAudio(path)) {
            return Constants.ContentMessageBody.AUDIO_MESSAGE;
        } else if (isTypeMisc(path)) {
            return Constants.ContentMessageBody.MISC_MESSAGE;
        } else if (isTypeCompress(path)) {
            return Constants.ContentMessageBody.COMPRESS_MESSAGE;
        } else if (isTypeApp(path)) {
            return Constants.ContentMessageBody.APK_MESSAGE;
        }

        return "";
    }

    public int getContentMessageType(String path) {
        if (TextUtils.isEmpty(path))
            return Constants.MessageType.TYPE_DEFAULT;

        if (isTypeImage(path)) {
            return Constants.MessageType.IMAGE_MESSAGE;
        } else if (isTypeVideo(path)) {
            return Constants.MessageType.VIDEO_MESSAGE;
        } else if (isTypeAudio(path)) {
            return Constants.MessageType.AUDIO_MESSAGE;
        } else if (isTypeMisc(path)) {
            return Constants.MessageType.MISC_MESSAGE;
        } else if (isTypeCompress(path)) {
            return Constants.MessageType.COMPRESS_MESSAGE;
        } else if (isTypeApp(path)) {
            return Constants.MessageType.APK_MESSAGE;
        }

        return Constants.MessageType.TYPE_DEFAULT;
    }

    public boolean isTypeImage(String path) {
        return path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")
                || path.endsWith(".pjpeg") || path.endsWith(".rgb") || path.endsWith(".webp")
                || path.endsWith(".gif") || path.endsWith(".bmp") || path.endsWith(".ico");
    }

    public boolean isTypeVideo(String path) {
        return path.endsWith(".3gp") || path.endsWith(".mpg") || path.endsWith(".mpeg")
                || path.endsWith(".mpe") || path.endsWith(".mp4") || path.endsWith(".avi")
                || path.endsWith(".wmv") || path.endsWith(".ogv") || path.endsWith(".flv")
                || path.endsWith(".3g2") || path.endsWith(".uvh") || path.endsWith(".uvm")
                || path.endsWith(".uvu") || path.endsWith(".uvp") || path.endsWith(".uvs")
                || path.endsWith(".uvv") || path.endsWith(".fvt") || path.endsWith(".f4v")
                || path.endsWith(".fli") || path.endsWith(".h261") || path.endsWith(".h263")
                || path.endsWith(".h264") || path.endsWith(".jpm") || path.endsWith(".jpgv")
                || path.endsWith(".m4v") || path.endsWith(".asf") || path.endsWith(".pyv")
                || path.endsWith(".wm") || path.endsWith(".wmx") || path.endsWith(".wvx")
                || path.endsWith(".mj2") || path.endsWith(".mxu") || path.endsWith(".movie")
                || path.endsWith(".mov") || path.endsWith(".mkv") || path.endsWith(".webm")
                || path.endsWith(".qt") || path.endsWith(".viv");
    }

    public boolean isTypeAudio(String path) {
        return path.endsWith(".m4p") || path.endsWith(".3gpp") || path.endsWith(".mp3")
                || path.endsWith(".wma") || path.endsWith(".wav") || path.endsWith(".ogg")
                || path.endsWith(".m4a") || path.endsWith(".aac") || path.endsWith(".ota")
                || path.endsWith(".imy") || path.endsWith(".rtx") || path.endsWith(".rtttl")
                || path.endsWith(".xmf") || path.endsWith(".mid") || path.endsWith(".mxmf")
                || path.endsWith(".amr") || path.endsWith(".flac");
    }

    public boolean isTypeMisc(String path) {
        return path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".rtf")
                || path.endsWith(".pdf") || path.endsWith(".ppt") || path.endsWith(".pptx")
                || path.endsWith(".xls") || path.endsWith(".xlsx") || path.endsWith(".txt");
    }

    public boolean isTypeCompress(String path) {
        return path.endsWith(".zip") || path.endsWith(".rar");
    }

    public boolean isTypeApp(String url) {
        return url.endsWith(".apk");
    }
}
