package com.example.glidedemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by 84625 on 2018/3/20.
 */

public class OptimalizeImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Cursor mCursor;

    public OptimalizeImageAdapter(Context context, Cursor c) {
        mCursor = c;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mCursor.moveToPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        mCursor.moveToPosition(position);
//        View view = mInflater.inflate(R.layout.optimalize_image_list_item, null);
//        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
//        Bitmap bitmap = BitmapFactory.decodeFile(path);
//        ((ImageView) view.findViewById(R.id.image)).setImageBitmap(bitmap);
//        return view;
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.optimalize_image_list_item, null);
            holder.imageView = convertView
                    .findViewById(R.id.image);
            holder.txt_image_info = convertView.findViewById(R.id.txt_image_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mCursor.moveToPosition(position);
        //图片路径
        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        //对图片进行优化，方法中传递的是我们想要的图片的最大宽高，
        // 由于图片的宽高比不一定会一致，所以说方法内部会按照图片原本的宽高比来计算最后的宽高
        Bitmap bitmap = samplingRateCompress(path, 400, 400);
        holder.imageView.setImageBitmap(bitmap);

        //获取图片信息
        StringBuilder sb = new StringBuilder();
        sb.append("AllocationByteCount = " + bitmap.getAllocationByteCount());
        holder.txt_image_info.setText(sb.toString());
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView txt_image_info;
    }

    /**
     * 采样率压缩（设置图片的采样率，降低图片像素）
     */
    public Bitmap samplingRateCompress(String filePath, int destWidt, int destHeight) {
        // 数值越高，图片像素越低
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //采样率
        options.inSampleSize = calculateInSampleSize(options, destWidt, destHeight);
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
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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
}
