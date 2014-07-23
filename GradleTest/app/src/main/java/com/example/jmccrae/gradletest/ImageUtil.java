package com.example.jmccrae.gradletest;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.Toast;

import java.io.FileNotFoundException;

/**
 * Created by jmccrae on 9/11/13.
 */
public class ImageUtil {
    ContentResolver content;

    ImageUtil(ContentResolver content) {
        this.content = content;
    }

    public Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight)
            throws FileNotFoundException
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(content.openInputStream(uri), null, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(content.openInputStream(uri), null, options);
    }

    @SuppressWarnings("javadoc")
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        return Bitmap.createScaledBitmap(realImage, width, height, filter);
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

}
