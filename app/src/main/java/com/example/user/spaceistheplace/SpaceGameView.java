package com.example.user.spaceistheplace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class SpaceGameView extends SurfaceView implements Runnable{

    Context context;

    private Thread gameThread = null;

    private SurfaceHolder ourHolder;

    private volatile boolean playing;

    private boolean paused = true;

    private Canvas canvas;
    private Paint paint;

    private long fps;

    private long timeThisFrame;

    private int screenX;
    private int screenY;

    private Player player;

    private int score = 0;

    private int lives = 3;

    public SpaceGameView(Context context, int x, int y) {

        super(context);

        this.context = context;

        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;


        prepareLevel();
    }

    private void prepareLevel(){

        player = new Player (screenX, screenY);

    }

    @Override
    public void run() {
        while (playing) {
            long startFrameTime = System.currentTimeMillis();

            if(!paused){
                update();
            }

            draw();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;

            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }
    }

    private void update() {


        player.update(fps);

    }

    private void draw(){

        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255,  255, 255, 255));

            canvas.drawRect(player.getRect(), paint);

            paint.setColor(Color.argb(255,  249, 129, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }
    public void pause() {
        playing = false;

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;
                if (motionEvent.getX() > screenX / 2) {

                    player.setMovementState(player.RIGHT);
                } else

                {
                    player.setMovementState(player.LEFT);
                }

                break;

            case MotionEvent.ACTION_UP:
                player.setMovementState(player.STOPPED);
                break;

        }
        return true;
    }
}

