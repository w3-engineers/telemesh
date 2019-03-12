package com.w3engineers.unicef.util.helper;

/*
 *  ****************************************************************************
 *  * Created by : Md. Azizul Islam on 10/29/2018 at 4:21 PM.
 *  *
 *  * Purpose:
 *  *
 *  * Last edited by : Md. Azizul Islam on 10/29/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ImageUtil {
    private static final String LOCAL_RESOURCE_SCHEME = "res";
    private static Map<Integer, Integer> imageIndexMap;

    static {
        imageIndexMap = new HashMap<>();
        imageIndexMap.put(0, R.mipmap.avatar0);
        imageIndexMap.put(1, R.mipmap.avatar1);
        imageIndexMap.put(2, R.mipmap.avatar2);
        imageIndexMap.put(3, R.mipmap.avatar3);
        imageIndexMap.put(4, R.mipmap.avatar4);
        imageIndexMap.put(5, R.mipmap.avatar5);
        imageIndexMap.put(6, R.mipmap.avatar6);
        imageIndexMap.put(7, R.mipmap.avatar7);
        imageIndexMap.put(8, R.mipmap.avatar8);
        imageIndexMap.put(9, R.mipmap.avatar9);
        imageIndexMap.put(11, R.mipmap.avatar11);
        imageIndexMap.put(12, R.mipmap.avatar12);
        imageIndexMap.put(13, R.mipmap.avatar13);
        imageIndexMap.put(14, R.mipmap.avatar14);
        imageIndexMap.put(15, R.mipmap.avatar15);
        imageIndexMap.put(16, R.mipmap.avatar16);
        imageIndexMap.put(17, R.mipmap.avatar17);
        imageIndexMap.put(18, R.mipmap.avatar18);
        imageIndexMap.put(19, R.mipmap.avatar19);
        imageIndexMap.put(20, R.mipmap.avatar20);
    }

    public static List<Integer> getAllImages() {
        List<Integer> imageIds = new ArrayList<>();
        for (int i = 0; i < imageIndexMap.size(); i++) {
            imageIds.add(i);
        }
        return imageIds;
    }

    private static Uri getUserImageUri(int imageIndex) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(LOCAL_RESOURCE_SCHEME).path(String.valueOf(imageIndexMap.get(imageIndex)));
        return builder.build();
    }

    public static Bitmap getUserImageBitmap(int userImageIndex) {
        Uri uri = getUserImageUri(userImageIndex);
        Bitmap bitmap = getCenterCropBitmap(uri, 400, 400);
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(TeleMeshApplication.getContext().getResources(), imageIndexMap.get(userImageIndex));
        }
        return getCroppedBitmap(bitmap);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {

        if (bitmap == null) return null;

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap getCenterCropBitmap(Uri imageUri, int width, int height) {
        try {
            return Glide.with(TeleMeshApplication.getContext())
                    .asBitmap()
                    .load(imageUri)
                    .apply(getRequestOptions())
                    .submit(width, height)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static RequestOptions getRequestOptions() {
        return RequestOptions.skipMemoryCacheOf(true).diskCacheStrategy(DiskCacheStrategy.NONE);
    }
}
