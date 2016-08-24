package com.example.user.spaceistheplace;

import android.graphics.RectF;

public class Asteroid {

    protected RectF rect;

    protected float length;
    protected float height;

    protected float x;
    protected float y;

    protected float asteroidSpeed;

    public Asteroid(int length, int height, int x, int y, int asteroidSpeed ){

        this.length = length;
        this.height = height;

        this.x = x;
        this.y = y;

        this.asteroidSpeed = asteroidSpeed;

        rect = new RectF(x, y, x + length, y + height);
    }

    public RectF getRect() {
        return rect;
    }

    public void update(long fps) {
        y = y + asteroidSpeed / fps;
        rect.top = y;
        rect.bottom = y + height;
    }

}

