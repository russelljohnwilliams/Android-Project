package com.example.user.spaceistheplace;

import android.graphics.RectF;

public class Star extends GameObjects{

    protected int starSpeed;


    public Star(int length, int width, int x, int y, int starSpeed){

        this.length = length;
        this.width = width;

        this.x = x;
        this.y = y;

        this.starSpeed = starSpeed;

        rect = new RectF(x, y, x + length, y + width);
    }

    public void update(float fps) {
        y = y + starSpeed / fps;
        rect.top = y;
        rect.bottom = y + width;
    }

}








