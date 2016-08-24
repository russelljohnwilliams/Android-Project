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

    // gets the value to add to the score
    public int getValue(){
        return value;
    }

    // works with the update to move the object, fps is determined
    // when object is created in SpaceGameView.
    // it does so by calculating how many pixes it should move in the
    // direction of the given coordinates.

    public void update(float fps) {
        y = y + tokenSpeed / fps;
        rect.top = y;
        rect.bottom = y + width;
    }

}








