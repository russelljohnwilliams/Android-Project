package com.example.user.spaceistheplace;


import android.graphics.RectF;

public class Token extends GameObjects {

    protected int tokenSpeed;

    protected int value;

    public Token(int length, int width, int x, int y, int tokenSpeed, int value){
        this.length = length;
        this.width = width;

        this.x = x;
        this.y = y;

        this.tokenSpeed = tokenSpeed;

        this.value = value;

        rect = new RectF(x, y, x + length, y + width);
    }

    public int getValue(){
        return value;
    }

    public void update(float fps) {
        y = y + tokenSpeed / fps;
        rect.top = y;
        rect.bottom = y + width;
    }

}








