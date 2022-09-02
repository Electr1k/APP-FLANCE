package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.flance20.adapter.EstablishmentAdapter;
import com.example.flance20.model.EstablishmentsMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView establishmentRecycler;
    EstablishmentAdapter establishmentAdapter;
    String ngrok = "https://0131-95-174-108-193.eu.ngrok.io";
    String url_main_page = ngrok + "/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetURLData().execute(url_main_page);

    }

    class GetURLData extends AsyncTask<String, String, String> {

        protected void onPriExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    List<EstablishmentsMain> establishmentsList = new ArrayList<>();
                    JSONArray establishments = obj.getJSONArray("establishments");
                    for (int i = 0; i < establishments.length(); i++) {
                        JSONObject establishment = establishments.getJSONObject(i);
                        int id = establishment.getInt("id");
                        String name = establishment.getString("name");
                        String address = establishment.getString("address");
                        String url_preview_img = establishment.getString("url_preview_img");
                        double lat_for_map = establishment.getDouble("lat_for_map");
                        double lng_for_map = establishment.getDouble("lng_for_map");
                        establishmentsList.add(new EstablishmentsMain(id, name, address, url_preview_img, lat_for_map, lng_for_map));
                    }
                    setEstablishmentsRecycler(establishmentsList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                RecyclerView recyclerView = findViewById(R.id.establishmentRecycler);
                recyclerView.removeAllViews();
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                View view = inflater.inflate(R.layout.error_window, null);
                view.setLayoutParams(lp);
                ConstraintLayout main = findViewById(R.id.main);
                main.addView(view);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float sensitvity = 250;
            if (e2.getY() - e1.getY() > sensitvity) {
                SwipeUp();
            }
            return true;
        }
    };

    GestureDetector gestureDetector = new GestureDetector(getBaseContext(), simpleOnGestureListener);

    private void SwipeUp() {
        System.out.println("It's swipe");
        setContentView(R.layout.activity_main);
        new GetURLData().execute(url_main_page);
    }

    private void setEstablishmentsRecycler(List<EstablishmentsMain> establishmentsList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        establishmentRecycler = findViewById(R.id.establishmentRecycler);
        establishmentRecycler.setLayoutManager(layoutManager);
        establishmentAdapter = new EstablishmentAdapter(MainActivity.this, establishmentsList);
        establishmentRecycler.setAdapter(establishmentAdapter);
    }

    public void openMap(View view) {
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    }
    public void openReservation(View view) {
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
    public void openProfile(View view) {
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }
}