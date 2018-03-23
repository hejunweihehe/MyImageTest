package com.example.glidedemo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_unoptimize: {
                Intent intent = new Intent(this, ImageActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_optimize:
                break;
            case R.id.btn_single_image: {
                Intent intent = new Intent(this, SingleImageActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_quality_compress_image: {
                Intent intent = new Intent(this, QualityCompressActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_size_compress_image: {
                Intent intent = new Intent(this, SizeCompressActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_in_sample_size_image: {
                Intent intent = new Intent(this, InSampleSizeActivity.class);
                startActivity(intent);
            }
            break;
        }
    }
}