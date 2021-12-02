package com.example.glidedemo.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glidedemo.ImageInfo;
import com.example.glidedemo.ImageUtils;
import com.example.glidedemo.R;

import java.io.FileNotFoundException;

public class ImageInfoActivity extends ImageBaseActivity {
    private ImageView image;
    private Bitmap bitmap;
    private TextView tvt_image_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);
        image = findViewById(R.id.image);
        tvt_image_info = findViewById(R.id.tvt_image_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUi();
    }

    /**
     * 刷新图片的信息数据
     */
    private void refreshUi() {
        String path = null;
        String name;
        StringBuilder sb = new StringBuilder();

        Uri uri = ImageInfo.getInstance().getUri();
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String fileInfo;
//        try {
        //默认uri：content://media/external/images/media
        bitmap = ImageUtils.getBitmap(this, uri);
        if (bitmap == null) {
            bitmap = ImageUtils.getMediaBitmap(this, uri);
            if (bitmap != null) {
                ContentResolver cp = getContentResolver();
                Cursor cursor = cp.query(uri, null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
                cursor.moveToPosition(0);
                //图片路径
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                //图片名字
                name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                fileInfo = "文件信息:\n" + "path:" + path + "\nname:" + name + "\nsize:" + cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)) + "\n";
            } else {
                Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            fileInfo = "文件信息:\n" + ImageUtils.parseUriDetails(this, uri);
        }
        image.setImageBitmap(bitmap);


        if (fileInfo != null) {
            sb.append(fileInfo).append("\n");
        }

        sb.append("Bitmap信息:");
        sb.append("\ngetConfig:" + bitmap.getConfig());
        sb.append("\ngetByteCount:" + bitmap.getByteCount());
        sb.append("\ngetHeight:" + bitmap.getHeight());
        sb.append("\ngetWidth:" + bitmap.getWidth());
        sb.append("\ngetDensity:" + bitmap.getDensity());
        sb.append("\ngetAllocationByteCount:" + bitmap.getAllocationByteCount());
        sb.append("\ngetGenerationId:" + bitmap.getGenerationId());
        sb.append("\ngetRowBytes:" + bitmap.getRowBytes());
        tvt_image_info.setText(sb.toString());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }
}
