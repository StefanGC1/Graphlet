package com.example.graphlet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private View[] circles;
    private TextView title;
    private LineView lineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        title = findViewById(R.id.title);
        circles = new View[]{
                findViewById(R.id.circle3),
                findViewById(R.id.circle4),
                findViewById(R.id.circle2),
                findViewById(R.id.circle1)
        };

        lineView = findViewById(R.id.line_view);
        lineView.setTitle(title);

        startAnimations();
    }

    private void startAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        title.startAnimation(fadeIn);

        for (View circle : circles) {
            circle.startAnimation(fadeIn);
        }

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                for (View circle : circles) {
                    circle.bringToFront();
                }
                title.bringToFront();
                startLineAnimations();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void startLineAnimations() {
        Handler handler = new Handler();
        float[] lineCoordinates = new float[24]; // 4 lines * 4 coordinates (startX, startY, stopX, stopY)
        int index = 0;

        for (int i = 0; i < circles.length - 1; i++) {
            for (int j = i + 1; j < circles.length; j++) {
                float startX = circles[i].getX() + circles[i].getWidth() / 2.0f;
                float startY = circles[i].getY() + circles[i].getHeight() / 2.0f;
                float stopX = circles[j].getX() + circles[j].getWidth() / 2.0f;
                float stopY = circles[j].getY() + circles[j].getHeight() / 2.0f;
                lineCoordinates[index] = startX;
                lineCoordinates[index + 1] = startY;
                lineCoordinates[index + 2] = stopX;
                lineCoordinates[index + 3] = stopY;
                index += 4;
            }
        }
        lineView.setLineCoordinates(lineCoordinates);
        lineView.setTitle(title);
        lineView.animateLineDrawing();

        handler.postDelayed(this::startMainActivity, 400 * (lineCoordinates.length / 4 + 1));
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
