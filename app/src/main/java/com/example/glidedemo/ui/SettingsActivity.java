package com.example.glidedemo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.glidedemo.ImageInfo;
import com.example.glidedemo.ImageUtils;
import com.example.glidedemo.R;
import com.example.glidedemo.databinding.ActivitySettingsBinding;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SettingsActivity extends AppCompatActivity {
    private static ActivitySettingsBinding binding;
    private int requestCode = 1;

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            binding.ivImage.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_choose_icon: {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent);
                break;
            }
            case R.id.btn_open_document: {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent);
                break;
            }
            case R.id.btn_get_document: {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent);
            }
        }
    }

    private void startActivityForResult(Intent intent) {
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, requestCode);
        } else {
            Toast.makeText(this, "没有app可以响应", Toast.LENGTH_SHORT).show();
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
                    try {
                        InputStream in = getContentResolver().openInputStream(uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        Message message = handler.obtainMessage();
                        message.obj = bitmap;
                        handler.sendMessage(message);
                        String info = ImageUtils.parseUriDetails(this, uri);
                        binding.tvDetails.setText(info);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }

            Toast.makeText(this, "请选择一张图片", Toast.LENGTH_SHORT).show();
        }
    }
}
