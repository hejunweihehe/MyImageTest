package com.example.glidedemo;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

/**
 * Created by 84625 on 2018/3/26.
 */

public class ImageUtils {
    /**
     * 采样率压缩（设置图片的采样率，降低图片像素）
     */
    public static Bitmap samplingRateCompress(String filePath, int destWidth, int destHeight) {
        // 数值越高，图片像素越低
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //采样率
        options.inSampleSize = calculateInSampleSize(options, destWidth, destHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        return bitmap;
    }

    /**
     * 简单的采样率获取方式，该方法获取到的采样率正好可以将图片塞到ImageView中
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            //计算图片高度和我们需要高度的最接近比例值
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            //宽度比例值
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //取比例值中的较大值作为inSampleSize
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static String parseUriDetails(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor.getCount() <= 0) {
            Toast.makeText(context, "未检索到数据", Toast.LENGTH_SHORT).show();
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (cursor.moveToNext()) {
            for (String columnName : cursor.getColumnNames()) {
                stringBuilder.append(columnName).append(":").append(cursor.getString(cursor.getColumnIndex(columnName))).append("\n");
            }
        }
        cursor.close();
        return stringBuilder.toString();
    }

    /**
     * 根据uri获取bitmap
     */
    public static Bitmap getBitmap(Context context, Uri uri) {
        try {
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 这是通过media库来获取bitmap
     */
    public static Bitmap getMediaBitmap(Context context, Uri uri) {
        ContentResolver cp = context.getContentResolver();
        Cursor cursor = cp.query(uri, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToPosition(0);
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        return BitmapFactory.decodeFile(path);
    }
}
