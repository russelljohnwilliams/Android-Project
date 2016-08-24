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

// surfaceView is an android class and Provides a dedicated drawing surface.
// Runnable is a Java interface which allows the run method to work.
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
    private int speed = 1000;
    private Player player;

    protected Token token;

    private Asteroid asteroid;
    private Asteroid asteroidBig;

    private Wall leftWall;
    private Wall rightWall;
    private Wall floor;

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
        prepareGame();
    }

  //  method called to restart the game, resets lives and score and then prepares the game again
    private void restartGame(){
        lives = 3;
        score = 0;
        prepareGame();
    }

    // prepares the game, creates new star and asteroids arrays, can change the numbers dynamically,
    // new player, and the boundaries the game are created,
    // and all the objects which are generated inside their own methods, are all called here.
    private void prepareGame(){
        stars = new Star[100];
        asteroidsSmall = new Asteroid[3];
        player = new Player (screenX / 20, screenY / 25, screenX / 2, screenY - 50, 1000);
        leftWall = new Wall(screenY + 20, 5,  0,  0);
        rightWall = new Wall(screenY + 20, 5,  screenX, 0);
        floor = new Wall(100, screenX * 3, -screenX, screenY + screenY);

        // calls the methods to generate the tokens, asteroids and stars.
        createToken();
        createAsteroid();
        createAsteroidBig();
        loopTheStars();
        loopTheStars();
        loopSmallAsteroids();
    }

    // random number generator, wee used in the creation of most of the game objects.
    // pass in two numbers to generate a single int.
    private int randomNumber(int a, int b){
        Random r = new Random();
        return r.nextInt(a - b) + b;
    }

    // first part of a loop function to generate the stars as the background,
    // split in two as the second half is used again later on.
    private void loopTheStars() {
        for (int i = 0; i < stars.length ; i++) {
            createTheStars(i);
        }
    }

    // depending on the number of stars in the array,
    // this will generate the chosen amount, using the random number method above,
    // it will create the random numbers for the X & Y axis, and for the speed of the stars.
    private void createTheStars(int i){
        int x = randomNumber(screenX, 1);
        int y = randomNumber(screenY, screenY * -1);
        int s = randomNumber(600, 200);
        stars[i] = new Star(screenY / 160, screenY / 160, x, y, s );
    }

    // this is the same as the stars loop. again split in two
    // due to the second half being used again later.
    private void loopSmallAsteroids(){
        for (int i = 0; i < asteroidsSmall.length; i++) {
            createSmallAsteroids(i);
        }
    }
 // second half of asteroid maker.
    private void createSmallAsteroids (int i){
        int x = randomNumber(screenX, 1);
        int y = randomNumber(0 - 20, 0 - 1000);
        int s = randomNumber(550, 350);
        int a = randomNumber(screenX, 1 - 20);
        asteroidsSmall[i] = new Asteroid(screenY / 25, screenY / 25, x, y, s, 100, a);
    }

    // creates the tokens, one at a time, this has one random number to determine the X axis
    // other attributes are declared here manually.
    private void createToken(){
        int x= randomNumber(screenX, 100);
        token = new Token (screenY / 20, screenY / 20, x, 0 - 55, 300, 1);
    }

    // like the token, this creates one medium size asteroid, with random X and Y axis positions.
    private void createAsteroid(){
        int z = randomNumber(screenX, 100 * -2);
        int a = randomNumber(screenX, 1 - 20);
        int y = 0 - screenY / 2;
        asteroid = new Asteroid(screenX / 4, screenX / 4, z, y, 350, 50, a);
    }

    // same as medium, this is for big asteroids.
    private void createAsteroidBig(){
            int a = randomNumber(screenX, 1 - 20);
            int z = randomNumber(screenX-800, 1);
            int y = 0 - screenY - 20;
            asteroidBig = new Asteroid(screenX / 2, screenX / 2, z, y, 200, 75, a);
    }

    // the loop.
    // takes the current time and stores it in currentFrameTime
    // if the game is NOT paused it will call the UPDATE() method
    // Then it will draw all the items again in their new locations (if they have moved)

    // timeThisFrame, this bit get information we can use on the objects that need to be animated,
    // so that we can determine how fast or slow they move.
    // "speed" is declared at the top, and set at 1000, (milliseconds),
    // this is changed later on, to speed up the game.
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


    //           This is the update method        //
    private void update() {

        player.update(fps);
        token.update(fps);

        // to update  all the stars, we need to loop through them as they move.
        for (Star star : stars) {
            star.update(fps);
        }

        // same as the stars
        for (Asteroid anAsteroidsSmall1 : asteroidsSmall) {
            anAsteroidsSmall1.update(fps);
        }

        // this allows the regenerated (but currently static and off screen)
        // asteroids to be released, now that the score is high enough to add a layer of
        // difficulty to the game

        if (score > 5) {
            asteroid.update(fps);
        }

        // now the BIG asteroids are created.

        if (score > 10) {
            asteroidBig.update(fps);
        }

        // speeds up the game

        if (score > 15) {
            speed = 900;
        }

        // speeds up further
        if (score > 20) {
            speed = 750;
        }

        // speeds up again
        if (score > 30) {
            speed = 600;
        }

        // these are the intersects, which detect collisions of objects.
        // then perform actions

        // this one gets points from a token, gives a quick burst of white , covering the screen
        // and regenerates a new token at the top of the screen, (could have other tokens that
        // slow down, speed up, reverse controls, etc.

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

        //this has to loop through all the asteroids to find out
        // which one collided with the player.
        for (Asteroid anAsteroidsSmall : asteroidsSmall) {
            if (RectF.intersects(player.getRect(), anAsteroidsSmall.getRect())) {
                hitByAsteroid();
            }
        }
        // stops the ship from leaving the game zone, it moves the ship away from the wall.
        // it judders a bit, but if it was any closer the ship can still be forced through the wall
        // a better way of doing this would be preferable.
        if (RectF.intersects(leftWall.getRect(), player.getRect())) {
            player.moveShip(50);
        }
        // as above
        if (RectF.intersects(rightWall.getRect(), player.getRect())) {
            player.moveShip(screenX - 200);
        }

        // if a token was not collected, it will be regenerated when it
        // hits a 'floor' created about 200 px below the game screen
        if (RectF.intersects(floor.getRect(), token.getRect())) {
            createToken();
        }

        // as above
        if (RectF.intersects(floor.getRect(), asteroid.getRect())) {
            createAsteroid();
        }
        // as above
        if (RectF.intersects(floor.getRect(), asteroidBig.getRect())) {
            createAsteroidBig();
        }


        // when the stars collide with the 'floor' new versions are generated,
        // we need to loop through all the stars to see which ones have collided.
        for (int i = 0; i < stars.length; i++) {
            if (RectF.intersects(stars[i].getRect(), floor.getRect())) {
                createTheStars(i);
            }
        }

        // similar to the stars loop and regenerate.
        for (int i = 0; i < asteroidsSmall.length; i++) {
            if (RectF.intersects(asteroidsSmall[i].getRect(), floor.getRect())) {
                createSmallAsteroids(i);
            }
        }

        //  end of game, hides the ship and keeps the game scrolling       //
        if (lives <= 0) {
            player.moveShip(2500);
            paused = false;
            speed = 1000;
        }
    }

    // when the ship intersects with an asteroid, this is called
    // it colours the screen red for one frame, removes a life,
    // and resets the asteroids back to a newly created state.
    // this prevents the ship from respawning right in the middle
    // of an asteroid and losing a life immediately
    private void hitByAsteroid(){
        canvas = ourHolder.lockCanvas();
        canvas.drawColor(Color.RED);
        ourHolder.unlockCanvasAndPost(canvas);
        prepareGame();
        lives -= 1;
    }


    // draws all the objects to the canvas and sets up styles
    // (outlines, solid, colour, hidden)
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

            // we need to loop again all those stars so we can draw each one to the screen.
            for (int i = 0; i < stars.length; i++) {
                canvas.drawRect(stars[i].getRect(), grey);
            }

            // same as the stars, loop and draw
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

    // pause the game, works in conjunction with the
    // onResume / onPause methods in SpaceGame class
    public void pause() {
        playing = false;

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    // same as pause, but resumes the game on initial screen touch.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override

    // user interface to control the game, if statement determines if the game is over
    // (lives <= 0), this means that if you touch anywhere, the game reloads.
    // if the game is in play, then it is a simple case of left side of the screen
    // moves the ship left, and right side of the screen moves right.
    // If there is no button press, then the ship doesn't move.
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

