package com.example.glidedemo;

import android.net.Uri;

/**
 * 图片信息实体类，包含了图片的位置等信息。该类给单张图片优化的activity使用
 * 这个类是一个单例模式
 */
public class ImageInfo {
    private static ImageInfo instance = null;
    private String path;//图片路径
    private Uri uri;//图片Uri

    private ImageInfo() {

    }

    public static ImageInfo getInstance() {
        if (instance == null) {
            synchronized (ImageInfo.class) {
                if (instance == null) {
                    instance = new ImageInfo();
                }
            }
        }
        return instance;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }
}
