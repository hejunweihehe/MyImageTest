package com.example.glidedemo.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.glidedemo.ImageUtils;
import com.example.glidedemo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 采样率压缩
 */
public class InSampleSizeActivity extends ImageBaseActivity {
    private ImageView image;
    private TextView txt_bitmap_info;
    private CheckBox chx_is_store;
    private EditText edit_sample;
    private EditText edit_width;
    private EditText edit_height;
    private Bitmap bitmap;
    private String path;
    private String name;
    public static final int UNCONSTRAINED = -1;
    private int height;//原始高度
    private int width;//原始宽度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_sample_size);
        image = findViewById(R.id.image);
        txt_bitmap_info = findViewById(R.id.txt_bitmap_info);
        edit_sample = findViewById(R.id.edit_sample);
        edit_width = findViewById(R.id.edit_width);
        edit_height = findViewById(R.id.edit_height);
        chx_is_store = findViewById(R.id.chx_is_store);
        ContentResolver cp = getContentResolver();
        Cursor cursor = cp.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToPosition(0);
        //图片路径
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        //图片名字
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        StringBuilder sb = new StringBuilder();
        sb.append("\noutHeight:" + opts.outHeight);
        sb.append("\noutWidth:" + opts.outWidth);
        height = opts.outHeight;
        width = opts.outWidth;
        txt_bitmap_info.setText(sb.toString());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_compress: {
                //int inSampleSize = Integer.valueOf(edit_sample.getText().toString());
                //bitmap = samplingRateCompress(path, inSampleSize);
                bitmap = samplingRateCompress(path);
                image.setImageBitmap(bitmap);
                refreshUi();
            }
            break;
            case R.id.btn_compress2: {
                bitmap = ImageUtils.samplingRateCompress(path, 400, 400);
                image.setImageBitmap(bitmap);
                //是否保存到本地
                if (chx_is_store.isChecked()) {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // 把压缩后的数据存放到baos中
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        File imageFile = new File(Environment.getExternalStorageDirectory() + "/In_sample_" + System.currentTimeMillis() + name);
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        fos.write(baos.toByteArray());
                        fos.flush();
                        fos.close();
                        //将图片导入到图片库里面
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(imageFile));
                        sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("hjw", "Exception = " + e.toString());
                    }
                }
                refreshUi();
            }
            break;
        }
    }

    /**
     * 刷新图片的信息数据
     */
    private void refreshUi() {
        if (bitmap == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("path:" + path);
        sb.append("\nname:" + name);
        sb.append("\ngetConfig:" + bitmap.getConfig());
        sb.append("\ngetByteCount:" + bitmap.getByteCount());
        sb.append("\ngetHeight:" + bitmap.getHeight());
        sb.append("\ngetWidth:" + bitmap.getWidth());
        sb.append("\ngetDensity:" + bitmap.getDensity());
        sb.append("\ngetAllocationByteCount:" + bitmap.getAllocationByteCount());
        sb.append("\ngetGenerationId:" + bitmap.getGenerationId());
        sb.append("\ngetRowBytes:" + bitmap.getRowBytes());
        txt_bitmap_info.setText(sb.toString());
    }

    /**
     * 采样率压缩（设置图片的采样率，降低图片像素）
     */
    public Bitmap samplingRateCompress(String filePath, int inSampleSize) {
        // 数值越高，图片像素越低
        BitmapFactory.Options options = new BitmapFactory.Options();
        //采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(baos.toByteArray()));
        return bitmap;
    }

    /**
     * 采样率压缩（设置图片的采样率，降低图片像素）
     */
    public Bitmap samplingRateCompress(String filePath) {
        // 数值越高，图片像素越低
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //采样率
        options.inSampleSize = calculateInSampleSize(options, image.getWidth(), image.getHeight());
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

    /**
     * 模仿android源码Gallery2项目中，获取采样率
     *
     * @param width
     * @param height
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public int computeSampleSize(int width, int height,
                                 int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(
                width, height, minSideLength, maxNumOfPixels);

        return initialSize <= 8
                ? nextPowerOf2(initialSize)
                : (initialSize + 7) / 8 * 8;
    }

    private int computeInitialSampleSize(int w, int h,
                                         int minSideLength, int maxNumOfPixels) {
        if (maxNumOfPixels == UNCONSTRAINED
                && minSideLength == UNCONSTRAINED) return 1;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt((double) (w * h) / maxNumOfPixels));

        if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            int sampleSize = Math.min(w / minSideLength, h / minSideLength);
            return Math.max(sampleSize, lowerBound);
        }
    }

    public int nextPowerOf2(int n) {
        if (n <= 0 || n > (1 << 30)) throw new IllegalArgumentException("n is invalid: " + n);
        n -= 1;
        n |= n >> 16;
        n |= n >> 8;
        n |= n >> 4;
        n |= n >> 2;
        n |= n >> 1;
        return n + 1;
    }
}
