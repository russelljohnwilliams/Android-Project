package com.example.user.spaceistheplace;

        import android.app.Activity;
        import android.graphics.Point;
        import android.os.Bundle;
        import android.view.Display;
        import android.view.Window;
        import android.view.WindowManager;


public class SpaceGame extends Activity {

    SpaceGameView spaceGameView;
    Point size;
    Display display;

    int screenY;
    int screenX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;

        spaceGameView = new SpaceGameView(this, size.x, size.y);
        setContentView(spaceGameView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        spaceGameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        spaceGameView.pause();
    }

}
