package com.example.user.spaceistheplace;

import android.graphics.RectF;


public abstract class GameObjects {

    protected RectF rect;

    protected int length;
    protected int width;

    protected float x;
    protected float y;

    public RectF getRect(){
        return rect;
    }

}

