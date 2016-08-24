package com.example.user.spaceistheplace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class FrontPage extends Activity {

    Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_main);

        mStartButton = (Button) findViewById(R.id.start_button);


        mStartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FrontPage.this, SpaceGame.class);
                startActivity(intent);
            }
        });
    }
}