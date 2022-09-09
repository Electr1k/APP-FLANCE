package com.example.flance20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.example.flance20.model.EstablishmentsMain;
import com.example.flance20.model.Settings;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;
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

public class MapsPage extends AppCompatActivity {
    private MapView mapView;
    String ngrok = null;
    String url_main_page = null;
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.initialize(this);
        // Создание MapView.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_page);
        Settings settings = new Settings(MapsPage.this);
        ngrok = settings.getNgrokUrl(); // получем ngrok url
        url_main_page = ngrok + "/api/";
        mapView = (MapView)findViewById(R.id.mapview);
        // Перемещение камеры в центр Таганрога.
        mapView.getMap().move(
                new CameraPosition(new Point(47.211859, 38.924804), 13.0f, 0.0f, 0.0f));

        new GetURLData().execute(url_main_page); // получем заведения
    }

    // Get запрос для получения заведений
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
                    buffer.append(line).append("\n"); // собираем ответ
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
            if (result != null) { // если получили ответ
                try {
                    JSONObject obj = new JSONObject(result);
                    ArrayList<EstablishmentsMain> establishmentsList = new ArrayList<>();
                    JSONArray establishments = obj.getJSONArray("establishments");
                    for (int i = 0; i < establishments.length(); i++) { // добавляем в лист заведения
                        JSONObject establishment = establishments.getJSONObject(i);
                        int id = establishment.getInt("id");
                        String name = establishment.getString("name");
                        String address = establishment.getString("address");
                        String url_preview_img = establishment.getString("url_preview_img");
                        double lat_for_map = establishment.getDouble("lat_for_map");
                        double lng_for_map = establishment.getDouble("lng_for_map");
                        establishmentsList.add(new EstablishmentsMain(id, name, address, url_preview_img, lat_for_map, lng_for_map));
                    }
                    setEstablishmentsOnMap(establishmentsList); // из листа на карту
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                View view = inflater.inflate(R.layout.error_window, null);
                view.setLayoutParams(lp);
                ConstraintLayout main = findViewById(R.id.main);
                main.addView(view);
            }
        }
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void setEstablishmentsOnMap(ArrayList<EstablishmentsMain> establishmentsList) {
        for (int i=0;i<establishmentsList.size();i++){
            // создаем точку на карте
            int finalI = i;
            PlacemarkMapObject point = mapView.getMap().getMapObjects().addPlacemark(new Point(establishmentsList.get(finalI).getLat_for_map(), establishmentsList.get(finalI).getLng_for_map()), ImageProvider.fromResource(this, R.drawable.ic_point));
            point.addTapListener(new MapObjectTapListener() {
                @Override
                public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                    Intent intent = new Intent(MapsPage.this, BookingPage.class);
                    intent.putExtra("id", establishmentsList.get(finalI).getId());
                    intent.putExtra("name", establishmentsList.get(finalI).getName());
                    intent.putExtra("context", "Map");
                    startActivity(intent);
                    return true;
                }
            });
            point.setText(establishmentsList.get(finalI).getName());
        }
    }

    @Override
    protected void onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }
    @Override
    protected void onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }
    public void openMain(View view){ // на главную
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openReservation(View view){ // мои бронирования
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
    public void openProfile(View view){ // в профиль
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }
    public void search(View view){
        System.out.println("Поиск");
    } // поиск по карте
}