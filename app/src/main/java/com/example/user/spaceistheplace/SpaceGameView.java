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

    private Canvas canvas;
    private Paint paint;
    private Paint paintOutline;
    private Paint hidden;
    private Paint grey;

    public SpaceGameView(Context context, int x, int y) {

        super(context);

        this.context = context;

        ourHolder = getHolder();

        paintOutline = new Paint();
        paint = new Paint();
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
        player = new Player (screenX / 20, screenY / 25, screenX / 2, screenY - 50, 1000);
        leftWall = new Wall(screenY + 20, 5,  0,  0);
        rightWall = new Wall(screenY + 20, 5,  screenX, 0);
        floor = new Wall(100, screenX * 3, -screenX, screenY + screenY);
        createToken();
        createAsteroid();
        createAsteroidBig();
        loopTheStars();
        loopSmallAsteroids();
    }

    private int randomNumber(int a, int b){
        Random r = new Random();
        return r.nextInt(a - b) + b;
    }

    private void loopTheStars() {
        for (int i = 0; i < stars.length ; i++) {
            createTheStars(i);
        }
    }

    private void createTheStars(int i){
        int x = randomNumber(screenX, 1);
        int y = randomNumber(screenY, screenY * -1);
        int s = randomNumber(600, 200);
        stars[i] = new Token(screenY / 160, screenY / 160, x, y, s, 0 );
    }

    private void createToken(){

        int z = randomNumber(screenX, 100);
        token = new Token (screenY / 20, screenY / 20, z, 0 - 55, 300, 1);
    }

    private void createAsteroid(){
        int z = randomNumber(screenX, 100 * -2);
        int a = randomNumber(screenX, 1 - 20);
        int y = 0 - screenY / 2;
        asteroid = new Asteroid(screenX / 4, screenX / 4, z, y, 350, 50, a);
    }

    private void loopSmallAsteroids(){
        for (int i = 0; i < asteroidsSmall.length; i++) {
            createSmallAsteroids(i);
        }
    }

    private void createSmallAsteroids (int i){
        int x = randomNumber(screenX, 1);
        int y = randomNumber(0 - 20, 0 - 1000);
        int s = randomNumber(550, 350);
        int a = randomNumber(screenX, 1 - 20);
        asteroidsSmall[i] = new Asteroid(screenY / 25, screenY / 25, x, y, s, 100, a);
    }

    private void createAsteroidBig(){
            int a = randomNumber(screenX, 1 - 20);
            int z = randomNumber(screenX-800, 1);
            int y = 0 - screenY - 20;
            asteroidBig = new Asteroid(screenX / 2, screenX / 2, z, y, 200, 75, a);
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
        for(int i = 0; i < stars.length; i++){
            stars[i].update(fps);
        }

        for (Asteroid anAsteroidsSmall1 : asteroidsSmall) {
            anAsteroidsSmall1.update(fps);
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
        }

        if (RectF.intersects(asteroid.getRect(), player.getRect())) {
            hitByAsteroid();
        }

        if (RectF.intersects(player.getRect(), asteroidBig.getRect())) {
            hitByAsteroid();
        }

        for (Asteroid anAsteroidsSmall : asteroidsSmall) {
            if (RectF.intersects(player.getRect(), anAsteroidsSmall.getRect())) {
                hitByAsteroid();
            }
        }

        if (RectF.intersects(leftWall.getRect(), player.getRect())) {
            player.moveShip(50);
        }

        if (RectF.intersects(rightWall.getRect(), player.getRect())) {
            player.moveShip(screenX - 200);
        }

        if (RectF.intersects(floor.getRect(), token.getRect())) {
            createToken();
        }

        if (RectF.intersects(floor.getRect(), asteroid.getRect())) {
            createAsteroid();
        }

        if (RectF.intersects(floor.getRect(), asteroidBig.getRect())) {
            createAsteroidBig();
        }

        for(int i = 0; i < stars.length; i++) {
            if (RectF.intersects(stars[i].getRect(), floor.getRect())) {
                createTheStars(i);
            }
        }

        for(int i = 0; i < asteroidsSmall.length; i++) {
            if (RectF.intersects(asteroidsSmall[i].getRect(), floor.getRect())) {
                createSmallAsteroids(i);
            }
        }

            if (lives <= 0) {
                player.moveShip(2500);
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

            grey.setColor(Color.argb(175, 255, 255, 255));

            paintOutline.setStrokeWidth(2);
            paintOutline.setColor(Color.WHITE);
            paintOutline.setStyle(Paint.Style.STROKE);
            paintOutline.setTextAlign(Paint.Align.CENTER);

            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(screenY / 20);

            canvas.drawColor(Color.BLACK);
            canvas.drawRect(player.getRect(), paint);
            canvas.drawRect(token.getRect(), paint);
            canvas.drawRect(asteroid.getRect(), paintOutline);
            canvas.drawRect(asteroidBig.getRect(), paintOutline);
            canvas.drawRect(leftWall.getRect(), hidden);
            canvas.drawRect(rightWall.getRect(), hidden);
            canvas.drawRect(floor.getRect(), hidden);

            canvas.drawText("Score: " + score, screenX / 12, screenY / 12 , paint);
            canvas.drawText("Lives: " + lives, screenX / 15 * 14, screenY / 12, paint);

            for (int i = 0; i < stars.length; i++) {
                canvas.drawRect(stars[i].getRect(), grey);
            }

            for (int i = 0; i < asteroidsSmall.length; i++) {
                canvas.drawRect(asteroidsSmall[i].getRect(), paintOutline);
            }

            if (lives <= 0){
                paintOutline.setTextSize(screenX / 6);
                canvas.drawText("GAME", screenX / 3, screenY / 3, paintOutline);
                canvas.drawText("OVER", screenX / 3 * 2, screenY / 5 * 3, paintOutline);
//                paint.setTextSize(screenY / 16);
                canvas.drawText(" your final Score was: " + score, screenX / 2, screenY / 7 * 5, paint);
                canvas.drawText("click anywhere the start again",screenX / 2, screenY / 7 * 6, paint);

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

