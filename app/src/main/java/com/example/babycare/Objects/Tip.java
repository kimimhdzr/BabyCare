package com.example.babycare.Objects;

import android.graphics.drawable.PictureDrawable;

public class Tip {
    String title,desc;
    PictureDrawable image;

    public Tip(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public PictureDrawable getImage() {
        return image;
    }

    public void setImage(PictureDrawable image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
