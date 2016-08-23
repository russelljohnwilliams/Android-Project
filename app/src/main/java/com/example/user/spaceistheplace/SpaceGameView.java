package com.example.user.spaceistheplace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;


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

    private int score = 0;
    private int lives = 3;

    private Player player;
    private Token token;

    private Asteroid asteroid;
    private Asteroid asteroidBig;
    private Wall leftWall;
    private Paint paintOutline;

    public SpaceGameView(Context context, int x, int y) {

        super(context);

        paintOutline = new Paint();

        this.context = context;

        ourHolder = getHolder();
        paint = new Paint();

        screenX = x;
        screenY = y;


        prepareLevel();

    }

    private void prepareLevel(){
        player = new Player (screenX, screenY);
        leftWall = new Wall(screenX, 5,  0,  0);
        createToken();
        createAsteroid();

//        createAsteroidBig();
    }

//    private int randomNumber(){
//        Random r = new Random();
//        int z = r.nextInt(screenX - 50) + 50;
//        return z;
//    }

    private void createToken(){
        Random r = new Random();
        int z = r.nextInt(screenX - 50) + 50;
        token = new Token (50, 50, z, 100, 500, 5);
        token.setMovementState(token.DOWN);
    }

    private void createAsteroid(){
        Random r = new Random();
        int z = r.nextInt(screenX - 50) + 50;
        int y = screenY - 2500;
        asteroid = new Asteroid(500, 500, z, y, 400);
        asteroid.setMovementState(token.DOWN);
    }

//    private void createAsteroidBig(){
//            Random r = new Random();
//            int z = r.nextInt(screenX - 50) + 50;
//            int y = screenY - 3500;
//            asteroidBig = new Asteroid(900, 900, z, y, 100);
//            asteroidBig.setMovementState(token.DOWN);//
//        }



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
        token.update(fps);
        asteroid.update(fps);
//        asteroidBig.update(fps);

             if (RectF.intersects(token.getRect(), player.getRect())) {
//            canvas = ourHolder.lockCanvas();
//            canvas.drawColor(Color.WHITE);
//            ourHolder.unlockCanvasAndPost(canvas);
//            token = null;
            createToken();
            score += token.getValue();
            Log.d("CONSOLE LOG", "POINTS SCORED FOR CATCHING THE BALL!!!");
        }

        if (RectF.intersects(asteroid.getRect(), player.getRect())) {
//            canvas = ourHolder.lockCanvas();
//            canvas.drawColor(Color.RED);
//            ourHolder.unlockCanvasAndPost(canvas);
            createAsteroid();
            lives -= 1;
            Log.d("CONSOLE LOG", "YOU LOST A LIFE!!!");

        }

        if (RectF.intersects(leftWall.getRect(), player.getRect())) {
            player.leftWallBlock();
            Log.d("CONSOLE LOG", "HIT THE LEFT WALL!!!");

        }

//      if (lives <= 0){
//
//            pause();
//            canvas = ourHolder.lockCanvas();
//            canvas.drawText("GAME OVER", 70, 150, paint);
//            canvas.drawText("Score: " + score, 50, 100, paint);
//            ourHolder.unlockCanvasAndPost(canvas);
//
//        }

//
    }

    private void draw(){

        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 26, 128, 182));

            paint.setColor(Color.argb(255, 255, 255, 255));

            canvas.drawRect(player.getRect(), paint);

            paintOutline.setStrokeWidth(2);
            paintOutline.setColor(Color.WHITE);
            paintOutline.setStyle(Paint.Style.STROKE);


            canvas.drawRect(token.getRect(), paintOutline);
            canvas.drawRect(asteroid.getRect(), paintOutline);
//            canvas.drawRect(asteroidBig.getRect(), paintOutline);

            canvas.drawRect(leftWall.getRect(), paint);
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

