package com.example.user.spaceistheplace;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

 class SpaceGameView extends SurfaceView implements Runnable{

    Context context;

     private Thread gameThread = null;

     private SurfaceHolder ourHolder;

     private volatile boolean playing;

     private boolean paused = true;
     private boolean fired = false;
     private boolean visible = true;
     private boolean playerActive = true;

     private long fps;
     private long timeThisFrame;

     private int screenX;
     private int screenY;
     private int score = 0;
     private int tokensCollected = 1;
     private int lives = 3;
     private int speed = 1000;

     private Player player;
     protected Token token;
     protected Bullet[] bullet;
     private Asteroid asteroid;
     private Asteroid asteroidBig;
     private Wall leftWall;
     private Wall rightWall;
     private Wall floor;
     private Wall roof;

     private Star[] stars;

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
         bullet = new Bullet[2];
         prepareGame();

    }

    private void restartGame(){
        lives = 3;
        score = 0;
        tokensCollected = 1;
        prepareGame();
        playerActive = true;
    }

    private void prepareGame(){
        stars = new Star[125];
        asteroidsSmall = new Asteroid[50];
        player = new Player (screenX / 25, screenY / 12, screenX / 2, screenY - 50, 700, 700, 700);
        leftWall = new Wall(screenY + 20, 5,  0,  0);
        rightWall = new Wall(screenY + 20, 5,  screenX, 0);
        floor = new Wall(100, screenX * 3, -screenX, screenY + screenY);
        roof = new Wall(70, screenX + 50 , -25, -75);

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
        int speed = randomNumber(600, 200);
        stars[i] = new Star(screenY / 160, screenY / 160, x, y, speed );
    }

    private void loopSmallAsteroids(){
        for (int i = 0; i < asteroidsSmall.length; i++) {
            createSmallAsteroids(i);
        }
    }

    private void createSmallAsteroids (int i){
        int x = randomNumber(screenX, 1);
        int y = randomNumber(0 - 30, 0 - 1000);
        int speed = randomNumber(350, 250);
        int drift = randomNumber(90, 60);
        int direction = randomNumber(20, 1);
        asteroidsSmall[i] = new Asteroid(screenY / 25, screenY / 25, x, y, speed, drift, direction);
    }

     private void unPauseGame (){
         paused = false;
     }

//     private void loopTinyAsteroids(){
//         (int i = 0; i < tinyAsteroids.length; i++) {
//             createTinyAsteroids(i);
//         }
//     }
//
//     private void createTinyAsteroids(){
//         int x = randomNumber(screenX, 1);
//         int y = randomNumber(0 - 20, 0 - 1000);
//         int s = randomNumber(550, 350);
//         int a = randomNumber(20, 1);
//         asteroidsTiny[i] = new Asteroid(screenY / 25, screenY / 25, x, y, s, 100, a);
//

    private void createToken(){
        int x = randomNumber(screenX, 95);
        token = new Token (screenY / 20, screenY / 20, x, 0 - 55, 350, 1);
    }
//
//     private void createBullet() {
//         if (fired == false) {
//             for (int i = 0; i < bullet.length; i++) {
//                 float xPos = player.getX() + player.getLength() / 2;
//                 bullet[i] = new Bullet(10, 40, xPos, screenY - 60, -1600);
//                 fired = true;
//             }
//         }
//     }

     private void createBullet() {
         if (fired == false) {
             float third = player.getLength();
             float xPos = player.getX() + third;
             float xPos2 = player.getX() + player.getLength() - third;
             bullet[0] = new Bullet(10, 40, xPos, screenY - 70, -1600);
             bullet[1] = new Bullet(10, 40, xPos2, screenY -70, -1600);
             fired = true;
         }
     }

    private void createAsteroid(){
        int z = randomNumber(screenX, 100 * -2);
        int a = randomNumber(20, 1);
        int y = 0 - screenY / 2;
        asteroid = new Asteroid(screenX / 4, screenX / 4, z, y, 350, 80, a);
    }

    private void createAsteroidBig(){
            int a = randomNumber(20, 1);
            int z = randomNumber(screenX-400, 1);
            int y = 0 - screenY - 20;
            asteroidBig = new Asteroid(screenX / 2, screenX / 2, z, y, 200, 70, a);
    }


    @Override
    public void run() {
        while (playing) {
            long currentFrameTime = System.currentTimeMillis();
            if(!paused){
                update();
            }

            draw();

            timeThisFrame = System.currentTimeMillis() - currentFrameTime;

            if (timeThisFrame >= 1) {
                fps = speed / timeThisFrame;
            }
        }
    }


    private void update() {

        player.update(fps);
        token.update(fps);

        for (int i = 0; i < bullet.length; i++) {
            if (score >= 5) {
                bullet[i].update(fps);
            }
        }

        for (Star star : stars) {
            star.update(fps);
        }

        for (int i = 0; i < tokensCollected; i++) {
            asteroidsSmall[i].update(fps);
        }

        if (score >= 25) {
            asteroid.update(fps);
        }

        if (score >= 50) {
            asteroidBig.update(fps);
        }

        if (score >= 20) {
            speed = 950;
        }

        if (score >= 30) {
            speed = 900;
        }

        if (score >= 40) {
            speed = 850;
        }

        if (playerActive == true) {
            if (RectF.intersects(token.getRect(), player.getRect())) {
                canvas = ourHolder.lockCanvas();
                canvas.drawColor(Color.WHITE);
                ourHolder.unlockCanvasAndPost(canvas);
                createToken();
                score += token.getValue();
                tokensCollected += 1;
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
        }

        if (RectF.intersects(leftWall.getRect(), player.getRect())) {
            player.playerLeftSpeed = 0;

        }
        if (RectF.intersects(rightWall.getRect(), player.getRect())) {
            player.playerRightSpeed = 0;
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

        for (int i = 0; i < bullet.length; i++) {
        if (RectF.intersects(roof.getRect(), bullet[i].getRect())) {
            fired = false;
            visible = false;
            }
        }

        for (int i = 0; i < stars.length; i++) {
            if (RectF.intersects(stars[i].getRect(), floor.getRect())) {
                createTheStars(i);
            }
        }

        for (int i = 0; i < asteroidsSmall.length; i++) {
            if (RectF.intersects(asteroidsSmall[i].getRect(), floor.getRect())) {
                createSmallAsteroids(i);
            }
            else if (visible == true){
                for (int x = 0; x < bullet.length; x++) {
                    if (RectF.intersects(bullet[x].getRect(), asteroidsSmall[i].getRect())) {
                        score += 1;
                        createSmallAsteroids(i);
                        fired = false;
                    }
                }
            }
        }

        if (lives <= 0) {
            playerActive = false;
//            player.moveShip(2500);
            paused = false;
            speed = 1000;
        }
    }

    private void hitByAsteroid(){
        canvas = ourHolder.lockCanvas();
        canvas.drawColor(Color.RED);
        ourHolder.unlockCanvasAndPost(canvas);
        prepareGame();
        lives -= 1;
    }

    private void draw(){

        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();

            hidden.setColor(Color.argb(0, 0, 0, 0));

            grey.setColor(Color.argb(195, 255, 255, 255));

            paintOutline.setStrokeWidth(2);
            paintOutline.setColor(Color.WHITE);
            paintOutline.setStyle(Paint.Style.STROKE);
            paintOutline.setTextAlign(Paint.Align.CENTER);

            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(screenY / 20);


            canvas.drawColor(Color.BLACK);

            canvas.drawRect(token.getRect(), paint);
            canvas.drawRect(asteroid.getRect(), paintOutline);
            canvas.drawRect(asteroidBig.getRect(), paintOutline);
            canvas.drawRect(leftWall.getRect(), hidden);
            canvas.drawRect(rightWall.getRect(), hidden);
            canvas.drawRect(floor.getRect(), hidden);
            canvas.drawRect(roof.getRect(), paint);

            canvas.drawText("Score: " + score, screenX / 12, screenY / 12 , paint);
            canvas.drawText("Lives: " + lives, screenX / 15 * 14, screenY / 12, paint);

            for (int i = 0; i < stars.length; i++) {
                canvas.drawRect(stars[i].getRect(), grey);
            }

            for (int i = 0; i < asteroidsSmall.length; i++) {
                canvas.drawRect(asteroidsSmall[i].getRect(), paintOutline);
            }

            for (int i = 0; i < bullet.length; i++) {
                if (score >= 5) {
                    canvas.drawRoundRect(bullet[i].getRect(), 100, 100, paint);
                }
            }

            if (playerActive) {
                Bitmap myBitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.spaceship);
                canvas.drawBitmap(myBitmap, null, player.getRect(), null);
//                canvas.drawRoundRect(player.getRect(), 10, 10, paint);
            } else {
                canvas.drawRoundRect(player.getRect(), 10,10, hidden);
            }

            if (lives <= 0){
                paintOutline.setTextSize(screenX / 6);
                canvas.drawText("GAME", screenX / 3, screenY / 3, paintOutline);
                canvas.drawText("OVER", screenX / 3 * 2, screenY / 5 * 3, paintOutline);
                canvas.drawText(" your final Score was: " + score, screenX / 2, screenY / 7 * 5, paint);
                canvas.drawText("click anywhere the start again",screenX / 2, screenY / 7 * 6, paint);
                // make a button here to start a new game
//                canvas.drawRect(button, screenX / 3 * 2, screenY / 5 * 3, paintOutline, paint);
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

                    if (lives <= 0) {
                        restartGame();
                    }
                    else if (motionEvent.getY() > screenY / 1.5) {
                        createBullet();
                        visible =true;
                        fired = true;
                        unPauseGame();
                    }
                    else if (motionEvent.getX() > screenX / 2) {
                        player.setMovementState(player.RIGHT);
                        unPauseGame();
                    } else {
                        player.setMovementState(player.LEFT);
                        unPauseGame();
                    }

                    break;
                    case MotionEvent.ACTION_UP:
                        player.setMovementState(player.STOPPED);
                break;

        }
        return true;
    }
}



// slow down asteroids a bit more
