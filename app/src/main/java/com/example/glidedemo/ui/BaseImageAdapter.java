package com.example.glidedemo.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.glidedemo.R;

import java.util.LinkedList;

/**
 * 图片展示列表的基类，该类使用模板方法模式，bindView方法供给子类自己去实现对图片的优化
 * 该类也可以直接使用，bindView默认是已经实现好了的，提供了最基本的图片展示功能，但是没有做任何的优化
 * <p>
 * 没有任何优化的图片加载会卡，但是不会出现错位这些情况
 * Created by 84625 on 2018/4/10.
 */

public class BaseImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    protected Cursor mCursor;//访问控制符是protected，好让子类能够访问到
    protected Context mContext;

    public BaseImageAdapter(Context context, Cursor c) {
        mCursor = c;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = mInflater.inflate(R.layout.image_list_item, null);
            holder.imageView = convertView
                    .findViewById(R.id.image);
            holder.txt_image_info = convertView.findViewById(R.id.txt_image_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bindView(holder, position, mCursor);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView txt_image_info;
    }

    /**
     * 需要修改初始化方法的时候就重写这个方法就行了
     *
     * @param holder
     * @param position
     * @param cursor
     */
    protected void bindView(ViewHolder holder, int position, Cursor cursor) {
        cursor.moveToPosition(position);
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        holder.imageView.setImageBitmap(bitmap);
        StringBuilder sb = new StringBuilder();
        sb.append("name = " + name);
        sb.append("\nAllocationByteCount = " + bitmap.getAllocationByteCount());
        holder.txt_image_info.setText(sb.toString());
    }
}
