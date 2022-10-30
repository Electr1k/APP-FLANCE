package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class QuestionnaireActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questionnaire_activity);
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
    public void openWeb(View view){ //  Открываем браузер
        String url_s = "https://docs.google.com/forms/d/e/1FAIpQLSfbf4y5WfBol0vt0IvH1AtsZBcZT-jpasWrkbYdzx-1wKL7Qg/viewform";
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url_s)));
    }
}