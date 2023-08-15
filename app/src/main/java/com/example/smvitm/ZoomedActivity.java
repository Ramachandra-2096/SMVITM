package com.example.smvitm;

import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ZoomedActivity extends AppCompatActivity {

    private ImageView zoomedImageView;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed);

        zoomedImageView = findViewById(R.id.zoomedImageView);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Get the image URI string from the intent extra
        String imageUriString = getIntent().getStringExtra("imageUriString");

        // Load the image using Glide into the zoomedImageView
        Glide.with(this)
                .load(Uri.parse(imageUriString))
                .into(zoomedImageView);

        zoomedImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f));
            Matrix matrix = new Matrix();
            matrix.setScale(scaleFactor, scaleFactor);
            zoomedImageView.setImageMatrix(matrix);
            return true;
        }
    }
}
