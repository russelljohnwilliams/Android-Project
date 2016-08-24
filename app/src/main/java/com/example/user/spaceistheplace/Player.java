package com.example.user.spaceistheplace;

import android.graphics.RectF;

public class Player {

    protected RectF rect;
    protected int length;
    protected int height;
    protected float x;
    protected float y;
    protected int playerSpeed;

    protected final int STOPPED = 0;
    protected final int RIGHT = 1;
    protected final int LEFT = 2;

    protected int playerMoving = STOPPED;

    public Player(int length, int height, float x, float y, int playerSpeed) {

        this.length = length;
        this.height = height;

        this.x = x;
        this.y = y;

        this.playerSpeed = playerSpeed;

        rect = new RectF(x, y, x + length, y + height);
    }

    public RectF getRect() {
        return rect;
    }

    public void setMovementState(int state) {
        playerMoving = state;
    }

    public void update(long fps) {
        if (playerMoving == LEFT) {
            x = x - playerSpeed / fps;
        }

        if (playerMoving == RIGHT) {
            x = x + playerSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

    public void moveShip(int n){
        x = n;
    }

}
