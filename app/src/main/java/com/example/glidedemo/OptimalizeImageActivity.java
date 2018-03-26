package com.example.glidedemo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class OptimalizeImageActivity extends AppCompatActivity {
    private ListView lst;
    private OptimalizeImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optimalize_image);
        lst = findViewById(R.id.lst);
        ContentResolver cp = getContentResolver();
        Cursor cursor = cp.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Images.ImageColumns.DATA + " DESC");
        adapter = new OptimalizeImageAdapter(this, cursor);
        lst.setAdapter(adapter);
    }
}
