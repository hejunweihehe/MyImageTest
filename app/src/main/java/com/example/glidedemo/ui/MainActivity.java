package com.example.glidedemo.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.glidedemo.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ImageBaseActivity {
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> unRequestedPermissions = new ArrayList<>();
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }

    /**
     * 获取权限
     */
    private void requestPermissions() {
        //首先筛选出没有授权的权限
        for (String p : permissions) {
            if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                unRequestedPermissions.add(p);
            }
        }
        //list避免为空，会报异常
        if (!unRequestedPermissions.isEmpty()) {
            //统一获取权限
            String[] sArray = new String[unRequestedPermissions.size()];
            sArray = unRequestedPermissions.toArray(sArray);
            requestPermissions(sArray, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_show_image: {
                Intent intent = new Intent(this, ImageInfoActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                break;
            }
            case R.id.btn_unoptimize: {
                Intent intent = new Intent(this, ImageActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_optimize_thread: {
                Intent intent = new Intent(this, OptimalizeImageActivity.class);
                intent.putExtra("adapter", ImageActivity.EXTRA_OPTIMIZE_THREAD);
                startActivity(intent);
            }
            case R.id.btn_optimize_async_task: {
                Intent intent = new Intent(this, OptimalizeImageActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_glide_optimize: {
                Intent intent = new Intent(this, GlideOptimalizeImageActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_quality_compress_image: {
                Intent intent = new Intent(this, QualityCompressActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_size_compress_image: {
                //尺寸压缩
                Intent intent = new Intent(this, SizeCompressActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_size_compress_image_by_glide: {
                //使用glide框架进行尺寸压缩
                Intent intent = new Intent(this, SizeCompressByGlideActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_in_sample_size_image: {
                Intent intent = new Intent(this, InSampleSizeActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_matrix_image: {
                Intent intent = new Intent(this, MatrixSizeActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_config_image: {
                Intent intent = new Intent(this, ConfigActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_multi_compress_image: {
            }
            break;
        }
    }
}