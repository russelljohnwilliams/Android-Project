package com.example.user.spaceistheplace;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
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
    private Paint paintOutline;
    private Paint hidden;
    private long fps;

    private long timeThisFrame;

    private int screenX;
    private int screenY;

    private int score = 0;
    private int lives = 3;
    private Player player;
    private Token token;
    private int bitmapYPosition = 0;
    private Asteroid asteroid;
    private Asteroid asteroidBig;
    private Wall leftWall;
    private Wall rightWall;
    private Wall floor;
    private Bitmap spacerace;


    public SpaceGameView(Context context, int x, int y) {

        super(context);

        this.context = context;

        ourHolder = getHolder();

        paint = new Paint();
        paintOutline = new Paint();
        hidden = new Paint();
        spacerace = BitmapFactory.decodeResource(this.getResources(), R.drawable.spacerace);

        screenX = x;
        screenY = y;

        prepareLevel();
    }





private void prepareLevel(){
        player = new Player (screenX, screenY);
        leftWall = new Wall(screenY, 5,  0,  0);
        rightWall = new Wall(screenY, 5,  screenX, 0);
        floor = new Wall(5, screenX, 0, screenY + screenY);
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
        int z = r.nextInt(screenX - 100) + 100;
        token = new Token (50, 50, z, 100, 500, 1);
        token.setMovementState(token.DOWN);
    }

    private void createAsteroid(){
        Random r = new Random();
        int z = r.nextInt(screenX - 100) + 100;
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
        bitmapYPosition +=5;
//        asteroidBig.update(fps);

             if (RectF.intersects(token.getRect(), player.getRect())) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            ourHolder.unlockCanvasAndPost(canvas);
            createToken();
            score += token.getValue();
            Log.d("CONSOLE LOG", "POINTS SCORED FOR CATCHING THE BALL!!!");
        }

        if (RectF.intersects(asteroid.getRect(), player.getRect())) {
            canvas = ourHolder.lockCanvas();
            paint.setTextSize(800);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("HIT", screenX / 2, 800, paint);
            ourHolder.unlockCanvasAndPost(canvas);
            prepareLevel();
            lives -= 1;
            Log.d("CONSOLE LOG", "YOU LOST A LIFE!!!");

        }

        if (RectF.intersects(leftWall.getRect(), player.getRect())) {
            player.leftWallBlock();
            Log.d("CONSOLE LOG", "HIT THE LEFT WALL!!!");

        }

        if (RectF.intersects(rightWall.getRect(), player.getRect())) {
            player.rightWallBlock();
            Log.d("CONSOLE LOG", "HIT THE RIGHT WALL!!!");

        }

        if (RectF.intersects(floor.getRect(), token.getRect())) {
            createToken();
            Log.d("CONSOLE LOG", "TOKEN HIT THE FLOOR!!!");

        }

        if (RectF.intersects(floor.getRect(), asteroid.getRect())) {
            createAsteroid();
            Log.d("CONSOLE LOG", "ASTEROID HIT THE FLOOR!!!");

        }
      if (lives <= 0){


            canvas = ourHolder.lockCanvas();
            paint.setTextSize(200);
            canvas.drawText("GAME OVER", screenX / 2, 500, paint);
            paint.setTextSize(90);
            canvas.drawText(" your final Score was: " + score, screenX / 2, 800, paint);
            ourHolder.unlockCanvasAndPost(canvas);
            player = null;
//          pause();
        }

//
    }

    private void draw(){

        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(spacerace, 0, bitmapYPosition, paint);

            paint.setColor(Color.WHITE);
            hidden.setColor(Color.argb(0, 0, 0, 0));
            canvas.drawRect(player.getRect(), paint);

            paintOutline.setStrokeWidth(2);
            paintOutline.setColor(Color.WHITE);
            paintOutline.setStyle(Paint.Style.STROKE);


            canvas.drawRect(token.getRect(), paintOutline);
            canvas.drawRect(asteroid.getRect(), paintOutline);
//            canvas.drawRect(asteroidBig.getRect(), paintOutline);

            canvas.drawRect(leftWall.getRect(), hidden);
            canvas.drawRect(rightWall.getRect(), hidden);
            canvas.drawRect(floor.getRect(), hidden);
//            canvas.drawBitmap(playerShip.getBitmap(), playerShip.getX(), screenY - 50, paint);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 100, 100, paint);
            canvas.drawText("Lives: " + lives, screenX - 300, 100, paint);

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

