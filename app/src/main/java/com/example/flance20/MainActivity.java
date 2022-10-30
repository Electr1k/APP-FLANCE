package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.example.flance20.adapter.EstablishmentAdapter;
import com.example.flance20.model.EstablishmentsMain;
import com.example.flance20.model.Settings;
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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    RecyclerView establishmentRecycler;
    EstablishmentAdapter establishmentAdapter; // адаптер
    String ngrok = null; // ngrok url
    String url_main_page = null; // url
    boolean flag_load_main = false;
    List<EstablishmentsMain> establishmentsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Settings settings = new Settings(this);
        ngrok = settings.getNgrokUrl(); // ngrok url
        System.out.println(ngrok);
        url_main_page = ngrok + "/api/";
        new GetURLData().execute(url_main_page); // загрузка главной ленты
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
                connection.connect(); // открываем коннект
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
            if (result != null) { // успешно получили ответ
                try {
                    JSONObject obj = new JSONObject(result);
                    JSONArray establishments = obj.getJSONArray("establishments");
                    for (int i = 0; i < establishments.length(); i++) { // добавляем в сет заведения
                        JSONObject establishment = establishments.getJSONObject(i);
                        int id = establishment.getInt("id");
                        String name = establishment.getString("name");
                        String address = establishment.getString("address");
                        String url_preview_img = establishment.getString("url_preview_img");
                        double lat_for_map = establishment.getDouble("lat_for_map");
                        double lng_for_map = establishment.getDouble("lng_for_map");
                        establishmentsList.add(new EstablishmentsMain(id, name, address, url_preview_img, lat_for_map, lng_for_map));
                    }
                    flag_load_main = true;
                    setEstablishmentsRecycler(establishmentsList); // запускаем адаптер
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{ // если не получили ответ, ставим увед
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
    // ловим свайпы
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


    public void search(View view){
        EditText name_search = findViewById(R.id.textsearch);
        String name_search_s = name_search.getText().toString().toLowerCase(Locale.ROOT);
        List<EstablishmentsMain> establishments_search = new ArrayList<>();
        for (int i=0;i<establishmentsList.size()&&flag_load_main;i++){
            if (establishmentsList.get(i).getName().toLowerCase().contains(name_search_s)){
                establishments_search.add(establishmentsList.get(i));
            }
        }
        setEstablishmentsRecycler(establishments_search);
    }


    GestureDetector gestureDetector = new GestureDetector(getBaseContext(), simpleOnGestureListener);
    // словили свайп вверх
    private void SwipeUp() {
        System.out.println("It's swipe");
        setContentView(R.layout.activity_main);
        new GetURLData().execute(url_main_page);
    }
    // Запуск адаптера
    private void setEstablishmentsRecycler(List<EstablishmentsMain> establishmentsList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        establishmentRecycler = findViewById(R.id.establishmentRecycler);
        establishmentRecycler.setLayoutManager(layoutManager);
        establishmentAdapter = new EstablishmentAdapter(MainActivity.this, establishmentsList);
        establishmentRecycler.setAdapter(establishmentAdapter);
    }
    public void openMap(View view) { // запуск карты
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    }
    public void openReservation(View view) { // мои бронирования
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
    public void openProfile(View view) { // запуск профиля
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }
}