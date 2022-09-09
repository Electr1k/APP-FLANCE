package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.flance20.model.Settings;

public class NgrokActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Load(); // загрузка содержимого страницы
    }
    public void Load(){
        setContentView(R.layout.ngrok_activity);
        TextView url = findViewById(R.id.url);
        url.setText(new Settings(this).getNgrokUrl());
        EditText urlEdit = findViewById(R.id.seturl);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // установка нового ngrok url
                String newUrl = urlEdit.getText().toString();
                if(newUrl.length()!=0){
                    new Settings(NgrokActivity.this).setNgrokUrl(newUrl);
                }
                Load(); // перезагрузка
            }
        });
    }
    public void openMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    } // главная
    public void openMap(View view){
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    } // на карту
    public void openProfile(View view){
        onBackPressed();
    } // в профиль
    public void openReservation(View view){ // мои бронирования
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
}