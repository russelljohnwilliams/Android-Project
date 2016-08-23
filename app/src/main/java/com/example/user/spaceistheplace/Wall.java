package com.example.user.spaceistheplace;

import android.graphics.RectF;


public class Wall {

    protected RectF rect;

    protected int length;
    protected int width;

    protected int x;
    protected int y;


    public Wall(int length, int width, int x, int y){

        this.length = length;
        this.width = width;

        this.x = x;
        this.y = y;

        rect = new RectF(x, y, x + width, y + length);
    }

    public RectF getRect(){
        return rect;
    }

}

