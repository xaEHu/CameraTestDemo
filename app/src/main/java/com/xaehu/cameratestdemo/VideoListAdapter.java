package com.xaehu.cameratestdemo;

import android.media.MediaMetadataRetriever;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class VideoListAdapter extends BaseQuickAdapter<VideoInfoBean, BaseViewHolder> {

    MediaMetadataRetriever media = new MediaMetadataRetriever();
    public VideoListAdapter(@Nullable List<VideoInfoBean> data) {
        super(R.layout.adapter_video_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfoBean item) {
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.time,item.getTime());

        media.setDataSource(item.getPath());
        helper.setImageBitmap(R.id.img,media.getFrameAtTime());
    }
}
