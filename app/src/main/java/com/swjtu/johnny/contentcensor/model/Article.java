package com.swjtu.johnny.contentcensor.model;

import android.graphics.Bitmap;

/**
 * Created by Johnny on 2017/4/5.
 */

public class Article {
    private int id;
    private Bitmap icon;
    private String title;
    private String state;
    private String time;

    public Article(int id,Bitmap icon,String title,String state,String time){
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.state = state;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getState() {
        return state;
    }

    public String getTime() {
        return time;
    }
}
