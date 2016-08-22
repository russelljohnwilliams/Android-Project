package com.example.user.spaceistheplace;

import android.graphics.RectF;


public class Wall {

    protected RectF rect;

    protected int length;
    protected int height;

    protected int x;
    protected int y;


    public Wall(int length, int height, int x, int y){

        this.length = length;
        this.height = height;

        this.x = x;
        this.y = y;

        rect = new RectF(x, y, x + length, y + height);
    }

    public RectF getRect(){
        return rect;
    }

}

