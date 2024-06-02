package com.example.graphlet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

public class LineView extends View {

    private Paint paint;
    private float[] lineCoordinates;
    private float[] animatedCoordinates;
    private int currentLineIndex;
    private float animationProgress;
    private TextView title;

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        paint.setStrokeWidth(5f);
        lineCoordinates = new float[0];
        animatedCoordinates = new float[0];
        currentLineIndex = 0;
        animationProgress = 0;
    }

    public void setLineCoordinates(float[] lineCoordinates) {
        this.lineCoordinates = lineCoordinates;
        animatedCoordinates = new float[lineCoordinates.length];
        invalidate(); // This will call the onDraw method
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public void setAnimationProgress(float progress) {
        animationProgress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lineCoordinates != null && lineCoordinates.length > 0) {
            for (int i = 0; i < animatedCoordinates.length; i += 4) {
                canvas.drawLine(animatedCoordinates[i], animatedCoordinates[i + 1], animatedCoordinates[i + 2], animatedCoordinates[i + 3], paint);
            }
            if (currentLineIndex < lineCoordinates.length) {
                float startX = lineCoordinates[currentLineIndex];
                float startY = lineCoordinates[currentLineIndex + 1];
                float stopX = lineCoordinates[currentLineIndex + 2];
                float stopY = lineCoordinates[currentLineIndex + 3];
                float animatedStopX = startX + (stopX - startX) * animationProgress;
                float animatedStopY = startY + (stopY - startY) * animationProgress;
                canvas.drawLine(startX, startY, animatedStopX, animatedStopY, paint);
                title.invalidate();
            }
        }
    }

    public void animateLineDrawing() {
        if (currentLineIndex < lineCoordinates.length) {
            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration(400);
            animator.addUpdateListener(animation -> {
                setAnimationProgress((float) animation.getAnimatedValue());
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animatedCoordinates[currentLineIndex] = lineCoordinates[currentLineIndex];
                    animatedCoordinates[currentLineIndex + 1] = lineCoordinates[currentLineIndex + 1];
                    animatedCoordinates[currentLineIndex + 2] = lineCoordinates[currentLineIndex + 2];
                    animatedCoordinates[currentLineIndex + 3] = lineCoordinates[currentLineIndex + 3];
                    currentLineIndex += 4;
                    animateLineDrawing();
                }
            });
            animator.start();
        }
    }
}
