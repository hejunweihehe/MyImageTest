package com.example.glidedemo.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
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

import com.example.glidedemo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class QualityCompressActivity extends ImageBaseActivity {
    private ImageView image;
    private TextView txt_bitmap_info;
    private CheckBox chx_is_store;
    EditText edit_quality;
    Bitmap bitmap;
    String path;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_compress);
        image = findViewById(R.id.image);
        txt_bitmap_info = findViewById(R.id.txt_bitmap_info);
        edit_quality = findViewById(R.id.edit_quality);
        chx_is_store = findViewById(R.id.chx_is_store);
        ContentResolver cp = getContentResolver();
        Cursor cursor = cp.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToPosition(0);
        //图片路径
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        //图片名字
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        bitmap = BitmapFactory.decodeFile(path);
        image.setImageBitmap(bitmap);
        refreshUi();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_compress: {
                int quality = Integer.valueOf(edit_quality.getText().toString());
                bitmap = qualityCompress(bitmap, quality);
                image.setImageBitmap(bitmap);
                refreshUi();
            }
            break;
        }
    }

    private void refreshUi() {
        StringBuilder sb = new StringBuilder();
        sb.append("path:" + path);
        sb.append("\nname:" + name);
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
     * 质量压缩
     * quality取值为0到100
     */
    public Bitmap qualityCompress(Bitmap bmp, int quality) {
        // 0-100 100为不压缩

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        //是否保存到本地
        if (chx_is_store.isChecked()) {
            try {
                File imageFile = new File(Environment.getExternalStorageDirectory() + "/" + name);
                StringBuilder imageName = new StringBuilder();
                for (int i = 0, last = 0; imageFile.exists(); i++) {
                    imageName.delete(0, imageName.length());
                    last = name.lastIndexOf(".");
                    imageName.append(name.substring(0, last));
                    imageName.append("(" + i + ")");
                    imageName.append(name.substring(last, name.length()));
                    imageFile = new File(Environment.getExternalStorageDirectory() + "/" + imageName.toString());
                    name = imageName.toString();
                    Log.d("hjw", imageFile.getAbsolutePath());
                }
                Log.d("hjw", "" + imageFile.getAbsolutePath());
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
        try {
            bmp = BitmapFactory.decodeStream(isBm);//把ByteArrayInputStream数据生成图片
            isBm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp;
    }
}
