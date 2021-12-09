package com.example.glidedemo.ui

import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.blankj.utilcode.util.UriUtils
import com.example.glidedemo.ImageInfo
import com.example.glidedemo.ImageUtils
import com.example.glidedemo.R
import com.example.glidedemo.databinding.ActivityCompressorBinding
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileNotFoundException
import java.lang.StringBuilder

class CompressorActivity : AppCompatActivity() {
    lateinit var binding: ActivityCompressorBinding
    private val requestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compressor)
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "*/*"
            startActivityForResult(intent)
        }
    }

    private fun startActivityForResult(intent: Intent) {
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestCode)
        } else {
            Toast.makeText(this, "没有app可以响应", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            data?.run {
                data.data?.also {
                    ImageInfo.getInstance().uri = it
                    try {
                        val ins = contentResolver.openInputStream(it)
                        val bitmap = BitmapFactory.decodeStream(ins)
                        val info = ImageUtils.parseUriDetails(this@CompressorActivity, it)
                        binding.tvOriginalDetails.text = info + getBitmapInfo(bitmap)
                        binding.ivOriginalImage.setImageBitmap(bitmap)
                        var f: File = UriUtils.uri2File(it)
                        compress(f, bitmap)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }
                    return
                }
            }
        }
    }

    fun compress(actualImageFile: File, bitmap: Bitmap) {
        runBlocking {
            val compressedImageFile = Compressor.compress(this@CompressorActivity, actualImageFile) {
                resolution(width = 360, height = (bitmap.height * ((360.0f) / bitmap.width)).toInt())
                quality(80)
                format(Bitmap.CompressFormat.WEBP)
            }
            var bitmap = BitmapFactory.decodeFile(compressedImageFile.absolutePath)
            binding.tvCompressedDetails.text = getBitmapInfo(compressedImageFile)
            binding.ivCompressedImage.setImageBitmap(bitmap)
            ImageUtils.saveBitmap(this@CompressorActivity, bitmap, getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath, compressedImageFile.name)
        }
    }

    private fun getBitmapInfo(compressedImageFile: File): String {
        val bitmap = BitmapFactory.decodeFile(compressedImageFile.absolutePath)
        val sb = StringBuilder()
        sb.append("path:${compressedImageFile.absolutePath}")
        sb.append("\ngetByteCount:${bitmap.byteCount}")
        sb.append("\ngetHeight:${bitmap.height}")
        sb.append("\ngetWidth:${bitmap.width}")
        sb.append("\ngetDensity:${bitmap.density}")
        sb.append("\ngetAllocationByteCount:${bitmap.allocationByteCount}")
        sb.append("\ngetGenerationId:${bitmap.generationId}")
        sb.append("\ngetRowBytes:${bitmap.rowBytes}")
        return sb.toString()
    }

    private fun getBitmapInfo(bitmap: Bitmap): String {
        val sb = StringBuilder()
        sb.append("\ngetByteCount:${bitmap.byteCount}")
        sb.append("\ngetHeight:${bitmap.height}")
        sb.append("\ngetWidth:${bitmap.width}")
        sb.append("\ngetDensity:${bitmap.density}")
        sb.append("\ngetAllocationByteCount:${bitmap.allocationByteCount}")
        sb.append("\ngetGenerationId:${bitmap.generationId}")
        sb.append("\ngetRowBytes:${bitmap.rowBytes}")
        return sb.toString()
    }

    companion object {
        const val TAG = "CompressorActivity"
    }
}