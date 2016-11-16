package com.example.user.spaceistheplace;

import android.graphics.RectF;

public class Bullet extends GameObjects {

    protected float bulletSpeed;


    public Bullet(int length, int height,float x, int y, int bulletSpeed){

        this.length = length;
        this.width = height;

        this.x = x;
        this.y = y;

        this.bulletSpeed = bulletSpeed;

        rect = new RectF(x, y, x + length, y + height);
    }

    public void update(float fps) {
        rect.left = x;
        y = y + bulletSpeed / fps;
        rect.top = y;
        rect.bottom = y + width;
    }

}

