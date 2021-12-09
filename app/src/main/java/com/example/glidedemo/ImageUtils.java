package com.example.glidedemo;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 84625 on 2018/3/26.
 */

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    /**
     * 根据uri获取文件路径
     */
    public static String getPicturePathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) {
            return getPicturePathFromUriAboveApi19(context, uri);
        } else {
            return getPicturePathFromUriBelowAPI19(context, uri);
        }
    }

    private static String getPicturePathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getPicturePathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

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
        } catch (SecurityException e) {
            e.printStackTrace();
            /**
             * TODO
             * 没有拿到uri访问权限，奇怪的bug，记得修复。BUG出现流程：从图片展示页（ImageInfoActivity）打开设置页，然后选一张图片，按下后退键回到展示页后，这里没报错。然后退到首页，再一次进入图片展示页时，这里就报错了，说是没有uri访问权限
             * 在这篇文章看看能不能找到点线索：https://blog.csdn.net/qq_24125575/article/details/109167437
             * 如果从图库选择一张图片，那么不会报错，但是如果从文件管理器的路径中去找这张图片并选择，那么会报错，所以可能是图库给了权限，而文件管理器没给权限
             */
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

    /**
     * 保存bitmao到指定位置
     */
    public static String saveBitmap(Context context, Bitmap bitmap, String dir, String fileName) {
        Log.d(TAG, "" + dir + " " + fileName);
        FileOutputStream fout = null;
        File file = new File(dir, fileName);
        try {
            fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "生成预览图片失败：" + e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "width is <= 0, or height is <= 0");
            return null;
        } catch (IOException e) {
            Log.e(TAG, "IOException:" + Log.getStackTraceString(e));
            e.printStackTrace();
            return null;
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    Log.e(TAG, "IOException:" + Log.getStackTraceString(ioException));
                }
            }
        }

        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file == null ? null : file.getAbsolutePath();
    }



}
