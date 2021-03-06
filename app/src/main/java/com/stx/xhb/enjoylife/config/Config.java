package com.stx.xhb.enjoylife.config;

import com.stx.xhb.enjoylife.R;

/**
 * Created by Mr.xiao on 16/7/11.
 */
public class Config {

    public static final String FEED = "IMAGE";
    public static final String WALLPAPER = "WALLPAPER";
    public static final String ZHIHU = "ZHIHU";
    public static final String VIDEO = "VIDEO";

    public enum Channel {
        IMAGE(R.string.fragment_image_title, R.drawable.ic_photo_black_24dp),
        WALLPAPER(R.string.fragment_wallpaper_title, R.drawable.icon_picture_black_24px),
        VIDEO(R.string.fragment_video_title, R.drawable.ic_video_black_24px),
        ZHIHU(R.string.fragment_zhihu_title, R.drawable.icon_zhihu_black_24px);

        private int title;
        private int icon;

        Channel(int title, int icon) {
            this.title = title;
            this.icon = icon;
        }

        public int getTitle() {
            return title;
        }

        public void setTitle(int title) {
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

    }
}
