package com.example.glidedemo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by 84625 on 2018/3/20.
 */

public class OptimalizeImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Cursor mCursor;
    private boolean isIdle = true;//该变量判断当前ListVIew是否是静止的，false表示在滑动/拖动，true表示静止，此时就可以去下载图片了

    public OptimalizeImageAdapter(Context context, Cursor c) {
        mCursor = c;
        mInflater = LayoutInflater.from(context);
    }

    public void setIdle(boolean idle) {
        isIdle = idle;
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
        if (isIdle) {
            //图片路径
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            // 设置Tag
            holder.imageView.setTag(path);
            //开启异步任务
            BitmapCompressTask task = new BitmapCompressTask(holder.imageView, holder.txt_image_info, mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));
            task.execute(path);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView txt_image_info;
    }

    private class BitmapCompressTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView image;
        private TextView imageInfo;
        private String tag;
        private String name;

        public BitmapCompressTask(ImageView image, TextView imageInfo, String name) {
            this.image = image;
            this.imageInfo = imageInfo;
            this.name = name;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            tag = strings[0];
            //对图片进行优化，方法中传递的是我们想要的图片的最大宽高，
            // 由于图片的宽高比不一定会一致，所以说方法内部会按照图片原本的宽高比来计算最后的宽高
            Log.d("hjw", "path = " + strings[0]);
            Bitmap bitmap = samplingRateCompress(strings[0], 400, 400);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                //通过tag来判断是否是当前位置
                if (image.getTag() != null && image.getTag().equals(tag)) {
                    image.setImageBitmap(bitmap); //获取图片信息
                    StringBuilder sb = new StringBuilder();
                    sb.append("name = " + name);
                    sb.append("\nAllocationByteCount = " + bitmap.getAllocationByteCount());
                    imageInfo.setText(sb.toString());
                }
            }
        }
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

        // 把压缩后的数据存放到baos中
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //这里的图片是有可能为null的，特别是在网络加载图片的时候就经常会遇到，
        // 可能是由于图片没加载成功或者是这张图是缓存图，然后被清空了但是数据库的记录还在
        if (bitmap == null) {
            return null;
        }
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
