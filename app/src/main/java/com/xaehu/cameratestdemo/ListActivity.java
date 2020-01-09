package com.xaehu.cameratestdemo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *  获取某个目录下所有视频列表
 */
public class ListActivity extends AppCompatActivity {

    private String TAG = "ListActivity";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycler);
        final List<VideoInfoBean> list = getVideoList();
        Log.i(TAG,"视频的数量："+list.size());
        VideoListAdapter adapter = new VideoListAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(ListActivity.this, list.get(position).getPath(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<VideoInfoBean> getVideoList() {
        List<VideoInfoBean> list = new ArrayList<>();

//        File videoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "MyTestVideo");
        File videoDir = new File(Environment.getExternalStorageDirectory().getPath() + "/MyTestVideo/");
        String path = videoDir.getAbsolutePath();
        Log.i(TAG, "视频的路径：" + path);

        if (videoDir.isDirectory()) {
            File[] files = videoDir.listFiles();
            Log.i(TAG, "文件数量" + files.length);

            VideoInfoBean bean;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.exists()) {
                    //注意：此处未判断文件是否是视频格式 // TODO: 2020/1/9  
                    bean = new VideoInfoBean();
                    bean.setName(file.getName());
                    bean.setPath(file.getAbsolutePath());
//                    Uri uri;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        uri = FileProvider.getUriForFile(this, ".pictest.fileprovider", file);
//                    } else {
//                        uri = Uri.fromFile(file);
//                    }
//                    bean.setTime(getTime(uri));
                    bean.setTime(getTime(file.getAbsolutePath()));
                    list.add(bean);
                }
            }
        }
        return list;
    }

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss",Locale.getDefault());
    private Date date = new Date();
    private String getTime(Uri uri){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this,uri);
            mediaPlayer.prepare();
            date.setTime(mediaPlayer.getDuration());
            return sdf.format(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "00:00";
    }
    private String getTime(String url){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            date.setTime(mediaPlayer.getDuration());
            return sdf.format(date);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "00:00";
    }
}
