package com.example.user.spaceistheplace;

import android.graphics.RectF;


public class Button extends GameObjects {


    public Button(int length, int height, int x, int y) {

        this.length = length;
        this.width = height;

        this.x = x;
        this.y = y;


        rect = new RectF(x, y, x + length, y + height);

    }
}
