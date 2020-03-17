package com.example.glidedemo.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.glidedemo.ImageInfo;
import com.example.glidedemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

public class SizeCompressByGlideActivity extends ImageBaseActivity {
    private ImageView image;
    private TextView txt_bitmap_info;
    private CheckBox chx_is_store;
    private EditText edit_size;
    private String path;
    private String name;
    private Uri uri;
    private int height;//原始高度
    private int width;//原始宽度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_compress);
        image = findViewById(R.id.image);
        txt_bitmap_info = findViewById(R.id.txt_bitmap_info);
        edit_size = findViewById(R.id.edit_size);
        chx_is_store = findViewById(R.id.chx_is_store);
        uri = ImageInfo.getInstance().getUri();
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        ContentResolver cp = getContentResolver();
        Cursor cursor = cp.query(uri, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToPosition(0);
        //图片路径
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        //图片名字
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        uri = Uri.fromFile(new File(path));
        image.setImageURI(uri);
        refreshUi(bitmap);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_compress: {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            int ratio = Integer.valueOf(edit_size.getText().toString());
                            Bitmap bitmap = null;
                            bitmap = sizeCompress(uri, ratio);
                            final Bitmap finalBitmap = bitmap;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    image.setImageBitmap(finalBitmap);
                                    refreshUi(finalBitmap);
                                }
                            });
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            break;
        }
    }

    /**
     * 刷新图片的信息数据
     */
    private void refreshUi(Bitmap bitmap) {
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
     * 尺寸压缩（通过缩放图片像素来减少图片占用内存大小）
     */
    public Bitmap sizeCompress(Uri uri, int ratio) throws ExecutionException, InterruptedException {
        // 尺寸压缩倍数,值越大，图片尺寸越小
        // 压缩Bitmap到对应尺寸
//        Bitmap bitmap = Glide.with(this).asBitmap().load(uri).submit(width / ratio, height / ratio).get();
        Bitmap bitmap = Glide.with(this).asBitmap().load(uri).submit(200, 200).get();

        //判断是否需要存在本地
        if (chx_is_store.isChecked()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                File imageFile = new File(Environment.getExternalStorageDirectory() + "/size_" + System.currentTimeMillis() + name);
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
        return bitmap;
    }
}
