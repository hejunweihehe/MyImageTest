package com.example.glidedemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.glidedemo.ui.BaseImageAdapter;

public class ImageActivity extends AppCompatActivity {
    private ListView lst;
    private BaseImageAdapter imageAdapter;
    public static final int EXTRA_OPTIMIZE_THREAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        lst = findViewById(R.id.lst);
        ContentResolver cp = getContentResolver();
        Cursor cursor = cp.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        imageAdapter = new BaseImageAdapter(this, cursor);
        lst.setAdapter(imageAdapter);
    }
}
