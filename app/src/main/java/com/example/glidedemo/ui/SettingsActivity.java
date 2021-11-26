package com.example.glidedemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.glidedemo.ImageInfo;
import com.example.glidedemo.R;

public class SettingsActivity extends AppCompatActivity {
    private int requestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_choose_icon: {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, requestCode);
                } else {
                    Toast.makeText(this, "没有app可以响应", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    ImageInfo.getInstance().setUri(uri);
                    return;
                }
            }

            Toast.makeText(this, "获取图片失败，或者是相册中没有图片", Toast.LENGTH_SHORT).show();
        }
    }
}
