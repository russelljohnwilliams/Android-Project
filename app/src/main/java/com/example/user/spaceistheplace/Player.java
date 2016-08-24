package com.example.user.spaceistheplace;

import android.graphics.RectF;



public class Player {

    private RectF rect;
    float length;
    float height;
    float x;
    float y;

    private float paddleSpeed;

    public final int STOPPED = 0;
    public final int RIGHT = 1;
    public final int LEFT = 2;

    private int paddleMoving = STOPPED;

    public Player(int screenX, int screenY) {
        length = 130;
        height = 40;


        x = screenX / 2;
        y = screenY - 150;

        rect = new RectF(x, y, x + length, y + height);

        paddleSpeed = 3000;
    }

    public RectF getRect() {
        return rect;
    }

    public void setMovementState(int state) {
        paddleMoving = state;
    }

    public void update(long fps) {
        if (paddleMoving == LEFT) {
            x = x - paddleSpeed / fps;
        }

        if (paddleMoving == RIGHT) {
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

    public void moveShip(int n){
        x = n;
    }


}
