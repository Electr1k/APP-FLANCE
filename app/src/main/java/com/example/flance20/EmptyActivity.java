package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.Console;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity);
        // установка заголовка и текст внутри страницы
        TextView textHeaders = findViewById(R.id.pagename);
        String headers = getIntent().getStringExtra("headers");
        textHeaders.setText(headers);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    // ищем свайпы
    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float sensitvity = 250;
            if (Math.abs(e2.getX() - e1.getX()) > sensitvity) {
                onBackPressed();
            }
            return true;
        }
    };
    GestureDetector gestureDetector = new GestureDetector(getBaseContext(), simpleOnGestureListener);

    public void openMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openMap(View view){
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    }
    public void openProfile(View view){
        onBackPressed();
    }
    public void openReservation(View view){
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }

}