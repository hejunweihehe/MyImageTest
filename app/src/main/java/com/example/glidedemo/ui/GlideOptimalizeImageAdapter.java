package com.example.glidedemo.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.glidedemo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by 84625 on 2018/3/20.
 */

public class GlideOptimalizeImageAdapter extends BaseImageAdapter {

    public GlideOptimalizeImageAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    protected void bindView(ViewHolder holder, int position, Cursor cursor) {
        mCursor.moveToPosition(position);
        //图片路径
        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        Glide.with(mContext)
                .load(path)
                .into(holder.imageView);
    }
}
