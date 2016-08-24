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
    private Paint grey;
    private long fps;

    private long timeThisFrame;

    private int screenX;
    private int screenY;

    private int score = 0;
    private int lives = 3;
    private Player player;
    protected Token token;
    private Asteroid asteroid;
    private Asteroid asteroidBig;
    private Wall leftWall;
    private Wall rightWall;
    private Wall floor;
    private Token[] stars;
    private Asteroid[] asteroidsSmall;
    int numStars = 0;
    int numAsteroids = 0;


    public SpaceGameView(Context context, int x, int y) {

        super(context);

        this.context = context;

        ourHolder = getHolder();

        paint = new Paint();
        paintOutline = new Paint();
        hidden = new Paint();
        grey = new Paint();
        screenX = x;
        screenY = y;
        prepareLevel();

    }
    private void restartGame(){
        lives = 3;
        score = 0;
        prepareLevel();
    }
//
    private void prepareLevel(){
        stars = new Token[70];
        asteroidsSmall = new Asteroid[2];
        player = new Player (screenX / 20, screenY / 25, screenX / 2, screenY - 50, 3000);

        leftWall = new Wall(screenY, 5,  0,  0);
        rightWall = new Wall(screenY, 5,  screenX, 0);
        floor = new Wall(100, screenX * 3, -screenX, screenY + screenY);
        createToken();
        createAsteroid();
        createAsteroidBig();
        createTheStars();
        createSmallAsteroids();
    }

    private int randomNumber(int a, int b){
        Random r = new Random();
        return r.nextInt(a - b) + b;
    }

    private void createTheStars() {
        numStars = 0;
        for (int i = 0; i < stars.length ; i++) {

            int x = randomNumber(screenX, 1);
            int y = randomNumber(screenY, screenY * -1);
            int s = randomNumber(1200, 600);

            stars[i] = new Token(screenY / 160, screenY / 160, x, y, s, 0 );
            numStars ++;
            Log.d("CONSOLE LOG :", "WE DID IT, WE MADE THE STARS");
        }
    }

    private void createToken(){

        int z = randomNumber(screenX, 100);
        token = new Token (screenY / 20, screenY / 20, z, 0 - 55, 1000, 1);
        Log.d("CONSOLE LOG :", "TOKEN CREATED");
    }

    private void createAsteroid(){
        int z = randomNumber(screenX, 100 * -2);
        int a = randomNumber(screenX, 1 - 20);
        int y = 0 - screenY / 2;
        asteroid = new Asteroid(screenX / 4, screenX / 4, z, y, 800, 150, a);
        Log.d("CONSOLE LOG :", "ASTEROIDS CREATED");
    }

    private void createSmallAsteroids(){
        numAsteroids = 0;
        for (int i = 0; i < asteroidsSmall.length; i++) {

            int x = randomNumber(screenX, 1);
            int y = randomNumber(0 - 20, 0 - 1000);
            int s = randomNumber(1200, 900);
            int a = randomNumber(screenX, 1 - 20);
            asteroidsSmall[i] = new Asteroid(screenY / 25, screenY / 25, x, y, s, 200, a);
            numAsteroids ++;
            Log.d("CONSOLE LOG :", "SMALL ASTEROIDS CREATED");

        }
    }

    private void createAsteroidBig(){
            int a = randomNumber(screenX, 1 - 20);
            int z = randomNumber(screenX-800, 1);
            int y = 0 - screenY - 20;
            asteroidBig = new Asteroid(screenX / 2, screenX / 2, z, y, 500, 150, a);
        Log.d("CONSOLE LOG :", "BIG ASTEROIDS CREATED");

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
                fps = 2000 / timeThisFrame;
            }
        }
    }

    private void update() {

        player.update(fps);
        token.update(fps);
        for(int i = 0; i < numStars; i++){
            stars[i].update(fps);
        }

        for(int i = 0; i < numAsteroids; i++){
            asteroidsSmall[i].update(fps);
        }

        if (score > 1) {
            asteroid.update(fps);
        }

        if (score > 2) {
            asteroidBig.update(fps);
        }

         if (RectF.intersects(token.getRect(), player.getRect())) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            ourHolder.unlockCanvasAndPost(canvas);
            createToken();
            score += token.getValue();
            Log.d("CONSOLE LOG", "POINTS SCORED FOR CATCHING THE TOKEN!!!");
        }

        if (RectF.intersects(asteroid.getRect(), player.getRect())) {
            hitByAsteroid();
            Log.d("CONSOLE LOG", "YOU LOST A LIFE!!!");
        }

        if (RectF.intersects(player.getRect(), asteroidBig.getRect())) {
            hitByAsteroid();
            Log.d("CONSOLE LOG", "HIT BY ASTEROID!!!");
        }
        for (Asteroid anAsteroidsSmall : asteroidsSmall) {
            if (RectF.intersects(player.getRect(), anAsteroidsSmall.getRect())) {
                hitByAsteroid();
                Log.d("CONSOLE LOG", "HIT BY ASTEROID!!!");
            }
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
            Log.d("CONSOLE LOG", "BIG ASTEROID HIT THE FLOOR!!!");
        }

        for(int i = 0; i < 70; i++) {
            if (RectF.intersects(stars[i].getRect(), floor.getRect())) {

                int x = randomNumber(screenX, 1);
                int y = randomNumber(screenY, 1);
                int s = randomNumber(600, 300);

                stars[i] = new Token(5, 5, x, y, s, 0 );
                Log.d("CONSOLE LOG", "ASTEROID HIT THE FLOOR!!!");
            }
        }

        for(int i = 0; i < asteroidsSmall.length; i++) {
            if (RectF.intersects(asteroidsSmall[i].getRect(), floor.getRect())) {

                int x = randomNumber(screenX, 1);
                int y = randomNumber(0 - 20, 0 - 1000);
                int s = randomNumber(1000, 700);
                int a = randomNumber(screenX, 1 - 20);

                asteroidsSmall[i] = new Asteroid(screenY / 18, screenY / 18, x, y, s, 200, a);
                Log.d("CONSOLE LOG", "SMALL ASTEROID HIT THE FLOOR!!!");
            }
        }

            if (lives <= 0) {
                player.moveShip(2500);
                Log.d("CONSOLE LOG", "GAME OVER!!!");
                paused = false;
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

            hidden.setColor(Color.argb(0, 0, 0, 0));
            grey.setColor(Color.argb(100, 255, 255, 255));
            paintOutline.setStrokeWidth(2);
            paintOutline.setColor(Color.WHITE);
            paintOutline.setStyle(Paint.Style.STROKE);

            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(50);

            canvas.drawColor(Color.BLACK);
            canvas.drawRect(player.getRect(), paint);
            canvas.drawRect(token.getRect(), paint);
            canvas.drawRect(asteroid.getRect(), paintOutline);
            canvas.drawRect(asteroidBig.getRect(), paintOutline);
            canvas.drawRect(leftWall.getRect(), hidden);
            canvas.drawRect(rightWall.getRect(), hidden);
            canvas.drawRect(floor.getRect(), hidden);
            canvas.drawText("Score: " + score, 150, 100, paint);
            canvas.drawText("Lives: " + lives, screenX - 200, 100, paint);

            for (int i = 0; i < numStars; i++) {
                canvas.drawRect(stars[i].getRect(), grey);
            }

            for (int i = 0; i < numAsteroids; i++) {
                canvas.drawRect(asteroidsSmall[i].getRect(), paintOutline);
            }

            if (lives <= 0){
                paint.setTextSize(200);
                canvas.drawText("GAME OVER", screenX / 2, screenY / 3, paint);
                paint.setTextSize(90);
                canvas.drawText(" your final Score was: " + score, screenX / 2, screenY / 3 * 2, paint);
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

                    if (lives <= 0) {
                        restartGame();
                    }
                    else if (motionEvent.getX() > screenX / 2) {

                        player.setMovementState(player.RIGHT);
                    } else {
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

