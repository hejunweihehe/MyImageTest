package com.example.glidedemo;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ConfigActivity extends AppCompatActivity {
    private ImageView image;
    private TextView txt_bitmap_info;
    private TextView txt_pre_config;
    private CheckBox chx_is_store;
    private Bitmap bitmap;
    private String path;
    private String name;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        image = findViewById(R.id.image);
        txt_bitmap_info = findViewById(R.id.txt_bitmap_info);
        txt_pre_config = findViewById(R.id.txt_pre_config);
        chx_is_store = findViewById(R.id.chx_is_store);
        spinner = findViewById(R.id.spinner);
        //加载Spinner
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Bitmap.Config.values());
        spinner.setAdapter(adapter);

        ContentResolver cp = getContentResolver();
        Cursor cursor = cp.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        cursor.moveToPosition(0);
        //图片路径
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        //图片名字
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));

        //加载图片的格式
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        StringBuilder sb = new StringBuilder();
        sb.append("inPreferredConfig:" + options.inPreferredConfig);
        txt_pre_config.setText(sb.toString());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_compress: {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap.Config newConfig = (Bitmap.Config) spinner.getSelectedItem();
                options.inPreferredConfig = newConfig;
                bitmap = BitmapFactory.decodeFile(path, options);
                image.setImageBitmap(bitmap);
                refreshUi();
                //是否保存到本地
                if (chx_is_store.isChecked()) {
                    try {
                        File imageFile = new File(Environment.getExternalStorageDirectory() + "/config_" + spinner.getSelectedItem() + System.currentTimeMillis() + name);
                        FileOutputStream fos = new FileOutputStream(imageFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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
}
