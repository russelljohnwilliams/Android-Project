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
    private Paint paintOutline;
    private Paint hidden;
    private long fps;

    private long timeThisFrame;

    private int screenX;
    private int screenY;

    private int score = 0;
    private int lives = 3;
    private Player player;
    protected Token token;
    private int bitmapYPosition = 0;
    private Asteroid asteroid;
    private Asteroid asteroidBig;
    private Wall leftWall;
    private Wall rightWall;
    private Wall floor;
//    private Bitmap spacerace;

    Token[] stars;
    int numStars;

    public SpaceGameView(Context context, int x, int y) {

        super(context);

        this.context = context;

        ourHolder = getHolder();
        stars = new Token[30];
        paint = new Paint();
        paintOutline = new Paint();
        hidden = new Paint();
//        spacerace = BitmapFactory.decodeResource(this.getResources(), R.drawable.spacerace);

        screenX = x;
        screenY = y;
//        createTheStars();
        prepareLevel();
    }

    private void createTheStars() {

        Random r = new Random();
        int z = r.nextInt(screenX - 1) + 1;

        Random ra = new Random();
        int y = ra.nextInt(0 - 100 * -2) + 100 * -2;

        Random ran = new Random();
        int s = ran.nextInt(500 - 700) + 700;

        int numStars = 0;
        for (int i = 0; i < 20; i++) {
            stars[numStars] = new Token(2, 2, z, y, s, 0 );
            numStars ++;
        }
    }
private void prepareLevel(){
        player = new Player (screenX, screenY);
        leftWall = new Wall(screenY, 5,  0,  0);
        rightWall = new Wall(screenY, 5,  screenX, 0);
        floor = new Wall(5, screenX, 0, screenY + screenY);
        createToken();
        createAsteroid();
        createAsteroidBig();
//        createTheStars();

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
        int z = r.nextInt(screenX - 100 * -2) + 100 * -2;
        int y = screenY - 2500;
        asteroid = new Asteroid(500, 500, z, y, 400);
        asteroid.setMovementState(token.DOWN);
    }

    private void createAsteroidBig(){
            Random r = new Random();
            int z = r.nextInt(screenX-800 - 1) + 1;
            int y = screenY - 2200;
            asteroidBig = new Asteroid(900, 900, z, y, 250);
            asteroidBig.setMovementState(token.DOWN);
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
        token.update(fps);
        asteroid.update(fps);
        //bitmapYPosition +=5;
        for(int i = 0; i < numStars; i++){
            stars[i].update(fps);
        }
        if (score > 5) {
            asteroidBig.update(fps);
        }

         if (RectF.intersects(token.getRect(), player.getRect())) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            ourHolder.unlockCanvasAndPost(canvas);
            createToken();
            score += token.getValue();
            Log.d("CONSOLE LOG", "POINTS SCORED FOR CATCHING THE BALL!!!");
        }

        if (RectF.intersects(asteroid.getRect(), player.getRect())) {
            hitByAsteroid();
            Log.d("CONSOLE LOG", "YOU LOST A LIFE!!!");
        }

        if (RectF.intersects(player.getRect(), asteroidBig.getRect())) {
            hitByAsteroid();
            Log.d("CONSOLE LOG", "HIT BY STEROID!!!");
        }

        if (RectF.intersects(leftWall.getRect(), player.getRect())) {
            player.moveShip(100);
            Log.d("CONSOLE LOG", "HIT THE LEFT WALL!!!");
        }

        if (RectF.intersects(rightWall.getRect(), player.getRect())) {
            player.moveShip(screenX - 200);
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

        if (RectF.intersects(floor.getRect(), asteroidBig.getRect())) {
            createAsteroidBig();
            Log.d("CONSOLE LOG", "ASTEROID HIT THE FLOOR!!!");
        }
            if (lives <= 0) {
                player.moveShip(2500);
                Log.d("CONSOLE LOG", "GAME OVER!!!");
            }
        }

    private void hitByAsteroid(){
        canvas = ourHolder.lockCanvas();
        canvas.drawColor(Color.RED);
        ourHolder.unlockCanvasAndPost(canvas);
        prepareLevel();
        lives -= 1;
    }

    private void draw(){

        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.BLACK);
//            canvas.drawBitmap(spacerace, 0, bitmapYPosition, paint);

            paint.setColor(Color.WHITE);
            hidden.setColor(Color.argb(0, 0, 0, 0));
            canvas.drawRect(player.getRect(), paint);

            paintOutline.setStrokeWidth(2);
            paintOutline.setColor(Color.WHITE);
            paintOutline.setStyle(Paint.Style.STROKE);

            canvas.drawRect(token.getRect(), paintOutline);
            canvas.drawRect(asteroid.getRect(), paintOutline);
            canvas.drawRect(asteroidBig.getRect(), paintOutline);

            canvas.drawRect(leftWall.getRect(), hidden);
            canvas.drawRect(rightWall.getRect(), hidden);
            canvas.drawRect(floor.getRect(), hidden);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(50);
            canvas.drawText("Score: " + score, 150, 100, paint);
            canvas.drawText("Lives: " + lives, screenX - 200, 100, paint);

            for (int i = 0; i < numStars; i++) {
                    canvas.drawRect(stars[i].getRect(), paint);
                }

            if (lives <= 0){
                paint.setTextSize(200);
                canvas.drawText("GAME OVER", screenX / 2, 600, paint);
                paint.setTextSize(90);
                canvas.drawText(" your final Score was: " + score, screenX / 2, 800, paint);
            }
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

