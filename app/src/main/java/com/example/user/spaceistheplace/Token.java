package com.example.user.spaceistheplace;


import android.graphics.RectF;

import java.util.Random;

public class Token {

    protected RectF rect;

    protected int length;
    protected int height;

    protected float x;
    protected float y;

    public final int STOPPED = 0;
    public final int UP = 1;
    public final int DOWN = 2;

    protected int tokenSpeed;
    protected int tokenMoving = STOPPED;

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

    public void setMovementState(int state) {
        tokenMoving = state;
    }

    public void update(float fps) {
        if (tokenMoving == UP) {
            y = y - tokenSpeed / fps;
        }

        if (tokenMoving == DOWN) {
            y = y + tokenSpeed / fps;
        }

        rect.top = y;
        rect.bottom = y + height;
    }


}








