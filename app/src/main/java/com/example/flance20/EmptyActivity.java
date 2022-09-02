package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_activity);
        TextView textHeaders = findViewById(R.id.pagename);
        TextView textBody = findViewById(R.id.textBody);
        String headers = getIntent().getStringExtra("headers");
        String body = getIntent().getStringExtra("body");
        textHeaders.setText(headers);
        textBody.setText(body);
    }

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