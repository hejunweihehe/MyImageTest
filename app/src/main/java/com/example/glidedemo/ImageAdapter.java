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

public class ImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Cursor mCursor;

    public ImageAdapter(Context context, Cursor c) {
        mInflater = LayoutInflater.from(context);
        mCursor = c;
    }


    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mCursor.moveToPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.image_list_item, null);
        ImageView imageView = view.findViewById(R.id.image);
        mCursor.moveToPosition(position);
        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(bitmap);
        return view;
    }
}
