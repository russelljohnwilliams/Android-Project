package com.example.user.spaceistheplace;

import android.graphics.RectF;

public class Asteroid {

    protected RectF rect;

    protected float length;
    protected float height;

    protected float x;
    protected float y;

    protected float asteroidSpeed;
    protected int asteroidDrift;
    protected int asteroidDirection;


    public Asteroid(int length, int height, int x, int y, int asteroidSpeed, int asteroidDrift, int asteroidDirection ){

        this.length = length;
        this.height = height;

        this.x = x;
        this.y = y;

        this.asteroidSpeed = asteroidSpeed;
        this.asteroidDrift = asteroidDrift;
        this.asteroidDirection = asteroidDirection;

        rect = new RectF(x, y, x + length, y + height);
    }

    public RectF getRect() {
        return rect;
    }

    public void update(long fps) {
        y = y + asteroidSpeed / fps;
//        if (asteroidDirection > 10){
//            x = x + asteroidDrift / fps;
//        }
//        else{
//            x = x - asteroidDrift / fps;
//        }
        rect.top = y;
        rect.bottom = y + height;
//        rect.left = x;
//        rect.right = x + length;
    }

}

