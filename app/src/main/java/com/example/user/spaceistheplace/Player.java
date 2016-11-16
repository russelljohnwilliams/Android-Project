package com.example.user.spaceistheplace;

import android.graphics.RectF;

public class Player extends GameObjects {

    protected int playerRightSpeed;
    protected int playerLeftSpeed;
    protected int defaultSpeed;

    protected final int STOPPED = 0;
    protected final int RIGHT = 1;
    protected final int LEFT = 2;

    protected int playerMoving = STOPPED;

    public Player(int length, int width, float x, float y, int playerLeftSpeed, int playerRightSpeed, int defaultSpeed) {

        this.length = length;
        this.width = width;

        this.x = x;
        this.y = y;

        this.playerLeftSpeed = playerLeftSpeed;
        this.playerRightSpeed = playerRightSpeed;
        this.defaultSpeed = defaultSpeed;

        rect = new RectF(x, y, x + length, y + width);
    }

    public void setMovementState(int state) {
        playerMoving = state;
    }

    //this sets the speed of the controls, if the onTouchEvent detects a
    // left or right press on the screen, then this will work out if
    // the x position should be - or + of the current position.

    public void update(long fps) {
        if (playerMoving == LEFT) {
            x = x - playerLeftSpeed / fps;
            playerRightSpeed = defaultSpeed;
        }

        if (playerMoving == RIGHT) {
            x = x + playerRightSpeed / fps;
            playerLeftSpeed = defaultSpeed;

        }
        
        rect.left = x;
        rect.right = x + length;
    }

    public float getX(){
        return x;
    }

    public float getLength(){
        return length;
    }

//    public float getLength(){
//        return length;
//    }


    // this moves the ship, it is used for the intersect, to stop the ship going through the wall
    // and also at the end of the game, the ship is removed from the playable area.
    public void moveShip(int n){
        x = n;
    }

}
