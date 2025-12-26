package com.example.clothshop.Intro;

public class IntroItem {

    private int image;
    private String title;
    private String desc;

    public IntroItem(int image, String title, String desc) {
        this.image = image;
        this.title = title;
        this.desc = desc;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }
}
