package com.example.glidedemo.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MatrixSizeActivity extends ImageBaseActivity {
    private ImageView image;
    private TextView txt_bitmap_info;
    private CheckBox chx_is_store;
    private Bitmap bitmap;
    private String path;
    private String name;
    private EditText edit_scaley;
    private EditText edit_scalex;
    private int height;//原始高度
    private int width;//原始宽度

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix_size);
        image = findViewById(R.id.image);
        txt_bitmap_info = findViewById(R.id.txt_bitmap_info);
        chx_is_store = findViewById(R.id.chx_is_store);
        edit_scalex = findViewById(R.id.edit_scalex);
        edit_scaley = findViewById(R.id.edit_scaley);

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
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        refreshUi();
    }

    public void onClick(View view) {
        float scalex = Float.parseFloat(edit_scalex.getText().toString());
        float scaley = Float.parseFloat(edit_scaley.getText().toString());
        Matrix matrix = new Matrix();
        matrix.setScale(scalex, scaley);
        Bitmap tmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        image.setImageBitmap(tmp);
        bitmap = tmp;
        refreshUi();
        //判断是否需要存在本地
        if (chx_is_store.isChecked()) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                File imageFile = new File(Environment.getExternalStorageDirectory() + "/matrix_" + System.currentTimeMillis() + name);
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
    }

    /**
     * 刷新图片的信息数据
     */

    private void refreshUi() {
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
}
