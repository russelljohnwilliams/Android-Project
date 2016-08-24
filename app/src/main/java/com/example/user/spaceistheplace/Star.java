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

    // works with the update to move the object, fps is determined
    // when object is created in SpaceGameView.
    // it does so by calculating how many pixes it should move in the
    // direction of the given coordinates.

    public void update(float fps) {
        y = y + starSpeed / fps;
        rect.top = y;
        rect.bottom = y + width;
    }

}








