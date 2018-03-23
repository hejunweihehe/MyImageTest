package com.example.glidedemo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

/**
 * Created by 84625 on 2018/3/20.
 */

public class ImageAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public ImageAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.image_list_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //图片路径
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ImageView imageView = view.findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);
    }
}
