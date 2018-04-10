package com.example.glidedemo;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by 84625 on 2018/3/20.
 */

public class GlideOptimalizeImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Cursor mCursor;
    private boolean isIdle = true;//该变量判断当前ListVIew是否是静止的，false表示在滑动/拖动，true表示静止，此时就可以去下载图片了
    private Context mContext;

    public GlideOptimalizeImageAdapter(Context context, Cursor c) {
        mCursor = c;
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    public void setIdle(boolean idle) {
        isIdle = idle;
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.optimalize_image_list_item, null);
            holder.imageView = convertView
                    .findViewById(R.id.image);
            holder.txt_image_info = convertView.findViewById(R.id.txt_image_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mCursor.moveToPosition(position);
        //图片路径
        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        Glide.with(mContext)
                .load(path)
                .into(holder.imageView);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView txt_image_info;
    }
}
