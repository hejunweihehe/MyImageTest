package com.example.glidedemo.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.glidedemo.ImageUtils.samplingRateCompress;

/**
 * 该Adapter是一个图片的质量和尺寸优化框架
 * Created by 84625 on 2018/4/10.
 */

public class QualityAndSizeOptimizeAdapter extends BaseImageAdapter {
    public QualityAndSizeOptimizeAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    protected void bindView(ViewHolder holder, int position, Cursor cursor) {
        mCursor.moveToPosition(position);
        //图片路径
        final String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        //此时可以加载图片了
        // 设置Tag
        holder.imageView.setTag(path);

        String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        //使用线程的方式来加载图片
        loadBitmap(path, holder.imageView, holder.txt_image_info, name, new ImageCallBack() {
            @Override
            public void onImageLoaded(ImageView imageView, final TextView imageInfo, Bitmap bitmap, String name) {
                if (bitmap != null) {
                    //通过tag来判断是否是当前位置
                    if (imageView.getTag() != null && imageView.getTag().equals(path)) {
                        imageView.setImageBitmap(bitmap); //获取图片信息
                        StringBuilder sb = new StringBuilder();
                        sb.append("name = " + name);
                        sb.append("\nAllocationByteCount = " + bitmap.getAllocationByteCount());
                        imageInfo.setText(sb.toString());
                        Log.d("hjw", "complete path = " + path);
                    }
                }
            }
        });
    }

    private void loadBitmap(final String path, final ImageView imageView, final TextView imageInfo, final String name, final ImageCallBack callBack) {
        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = samplingRateCompress(path, 400, 400);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onImageLoaded(imageView, imageInfo, bitmap, name);
                    }
                });
            }
        });
        thread.start();
    }

    private interface ImageCallBack {
        public void onImageLoaded(ImageView imageView, final TextView imageInfo, Bitmap bitmap, String name);
    }
}
