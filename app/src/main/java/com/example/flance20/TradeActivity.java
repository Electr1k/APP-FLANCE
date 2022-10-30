package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_activity);
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
    @SuppressLint("InflateParams")
    public void sendMessage(View view){
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        view = inflater.inflate(R.layout.success_register, null);
        lp.gravity = Gravity.CENTER;
        view.setLayoutParams(lp);
        TextView text = view.findViewById(R.id.errorwindow);
        text.setText("Спасибо за отзыв!\nМы учтем все Ваши предложения и пожелания!");
        ConstraintLayout main = findViewById(R.id.main);
        View finalView = view;
        TextView btn = view.findViewById(R.id.btn_auth);
        btn.setText("Отлично!");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View views) {
                main.removeView(finalView);
            }
        });
        view.findViewById(R.id.closest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        main.addView(view);
    } // Устанавливаем окно успеха
    public void openMain(View view){ // на главную
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openMap(View view){ // на главную
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    }
    public void openReservation(View view){ // мои бронирования
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
    public void openProfile(View view){ // в профиль
        onBackPressed();
    }
}