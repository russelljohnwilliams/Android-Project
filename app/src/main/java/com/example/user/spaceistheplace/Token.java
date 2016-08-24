package com.example.user.spaceistheplace;


import android.graphics.RectF;

public class Token {

    protected RectF rect;

    protected int length;
    protected int height;

    protected float x;
    protected float y;

    protected int tokenSpeed;

    protected int value;

    public Token(int length, int height, int x, int y, int tokenSpeed, int value){
        this.length = length;
        this.height = height;

        this.x = x;
        this.y = y;

        this.tokenSpeed = tokenSpeed;

        this.value = value;

        rect = new RectF(x, y, x + length, y + height);
    }

    public RectF getRect() {
        return rect;
    }

    public int getValue(){
        return value;
    }

    public void update(float fps) {
        y = y + tokenSpeed / fps;
        rect.top = y;
        rect.bottom = y + height;
    }

}








