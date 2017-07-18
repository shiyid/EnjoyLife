package com.stx.xhb.enjoylife.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Mr.xiao on 16/8/26.
 * 图片预览Viewpager适配器
 */
public class PhotoViewPagerAdapter extends PagerAdapter{
    private Context context;
    private ArrayList<String> imageList;

    public PhotoViewPagerAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        photoView.enable();
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(context).load(imageList.get(position)).into(photoView);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}