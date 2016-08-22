package com.example.user.spaceistheplace;

import android.graphics.RectF;

public class Asteroid {

    protected RectF rect;

    protected float length;
    protected float height;

    protected float x;
    protected float y;

    public final int STOPPED = 0;
    public final int UP = 1;
    public final int DOWN = 2;

    protected float asteroidSpeed;
    protected int asteroidMoving = STOPPED;

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

    public void setMovementState(int state) {
        asteroidMoving = state;
    }

    public void update(long fps) {
        if (asteroidMoving == UP) {
            y = y - asteroidSpeed / fps;
        }

        if (asteroidMoving == DOWN) {
            y = y + asteroidSpeed / fps;
        }

        rect.top = y;
        rect.bottom = y + height;
    }

}



//        Random r = new Random();
//        int i1 = r.nextInt(screenX - 50) + 50;
//        x = screenX - i1;
