package com.xaehu.cameratestdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    private File cameraSavePath;//拍照照片路径
    private Uri uri;//照片uri
    private int btnId;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image);
    }

    public void onBtnClick(View view){
        btnId = view.getId();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permission, 10000);
                return;
            }
        }
        openCamera(btnId);
    }

    private void openCamera(int btnId) {
        if (btnId == R.id.picture_system) {
            selectPicture();
            return;
        }
        if (btnId == R.id.video_system) {
            openVideo();
            return;
        }
        if (btnId == R.id.camera_diy) {
            startActivity(new Intent(this, CameraActivity.class));
            return;
        }
        if (btnId == R.id.camera_system) {
            openCamera();
        }
        if (btnId == R.id.video_select) {
            selectVideo();
        }
    }

    private void selectVideo() {
        Intent intent = new Intent();
        if("Meizu".equalsIgnoreCase(android.os.Build.MANUFACTURER)){  // 判断用户手机是否是“魅族”。忽略大小写的比较
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
        }else {
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, 10005);
    }

    private void selectPicture(){
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 10002);
    }

    private void openCamera() {
        cameraSavePath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //第二个参数为 包名.fileprovider
            uri = FileProvider.getUriForFile(MainActivity.this, ".pictest.fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 10001);
    }

    private void openVideo(){
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".mp4";   // 保存路径
        cameraSavePath = new File(filePath);   // 将路径转换为Uri对象
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);  // 表示跳转至相机的录视频界面
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);    // MediaStore.EXTRA_VIDEO_QUALITY 表示录制视频的质量，从 0-1，越大表示质量越好，同时视频也越大
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);   // 设置视频录制的最长时间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(MainActivity.this, ".pictest.fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);// 表示录制完后保存的录制，如果不写，则会保存到默认的路径，在onActivityResult()的回调，通过intent.getData中返回保存的路径
        startActivityForResult(intent, 10003);  // 跳转
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10000) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 所有的权限都授予了
                openCamera(btnId);
            } else {
                Toast.makeText(this, "请打开授权", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String photoPath;
        if (requestCode == 10001 && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = uri.getEncodedPath();
            }
            Toast.makeText(this, "拍照返回图片路径:"+ photoPath, Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));
        } else if (requestCode == 10002 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            //查询我们需要的数据
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            photoPath = cursor.getString(columnIndex);
            cursor.close();
            Toast.makeText(this, "选择的图片路径:"+ photoPath, Toast.LENGTH_SHORT).show();
            imageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));
        } else if (requestCode == 10003 && resultCode == RESULT_OK && null != data) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = uri.getEncodedPath();
            }
            Toast.makeText(this, "录制的视频的路径:"+ photoPath, Toast.LENGTH_SHORT).show();
        } else if (requestCode == 10005 && resultCode == RESULT_OK && null != data){
            Cursor cursor = null;
            try {
                Uri selectedVideo = data.getData();      // 获取视频的Uri
                String[] filePathColumn = {MediaStore.Video.Media.DATA};
                cursor = getContentResolver().query(selectedVideo,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                photoPath = cursor.getString(columnIndex);          // 视频路径
                Toast.makeText(this, "选择的视频的路径:"+ photoPath, Toast.LENGTH_SHORT).show();
            } finally {
                if (cursor != null) cursor.close();                   // 关闭cursor
            }
        }
    }
}
