package com.example.glidedemo.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.glidedemo.ImageInfo;
import com.example.glidedemo.R;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SingleImageActivity extends ImageBaseActivity {
    private static final String TAG = "SingleImageActivity";
    private ImageView image;
    private TextView txt_bitmap_info;
    private int MY_REQUESTCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        image = findViewById(R.id.image);
        txt_bitmap_info = findViewById(R.id.txt_bitmap_info);

        Uri uri = ImageInfo.getInstance().getUri();
        if (uri == null) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, MY_REQUESTCODE);
        } else {
            updateView();
        }

    }

    private void updateView() {
        Uri uri = ImageInfo.getInstance().getUri();
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "" + Log.getStackTraceString(e));
            e.printStackTrace();
        }
        image.setImageBitmap(bitmap);
        StringBuilder sb = new StringBuilder();
        sb.append("uri:" + uri.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUESTCODE) {
            updateView();
        }
    }
}