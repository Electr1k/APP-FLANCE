package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.flance20.model.Settings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class ReservationPage extends AppCompatActivity {
    String ngrok = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_page);
        Settings settings = new Settings(this);
        ngrok = settings.getNgrokUrl(); // получаем ngrok url
        new GetBooking().execute(ngrok+ "/my_booking"); // получаем мои бронирования
    }

    // Post запрос для удаления букинга
    class DeleteBooking extends AsyncTask<String, String, String> {
        Settings session = new Settings(ReservationPage.this);
        protected void onPriExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String id = strings[1],data=strings[2],time=strings[3], index=strings[4];
                String jsonInputString = "{\"id\": \""+id+"\", \"data\": \""+data+"\", \"time\": \""+time+"\", \"index\": \""+index+"\"}";
                byte[] out = jsonInputString.getBytes("utf-8"); // тело запроса
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                String cookie = session.getSession();
                System.out.println(jsonInputString);
                connection.addRequestProperty("Cookie", cookie); // устанавливаем куки
                connection.setRequestMethod("POST"); // устанавливаем метод
                connection.setRequestProperty("Content-Type", "application/json");
                connection.connect();
                OutputStream os = connection.getOutputStream();
                os.write(out, 0, out.length);
                os.close();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n"); // собираем ответ
                }

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
            if (result != null){
                try {
                    if (new JSONObject(result).getBoolean("success")) { // удачно удалили
                        setContentView(R.layout.activity_reservation_page); // перезагурзка страницы
                        new GetBooking().execute(ngrok+ "/my_booking"); // загрузка букингов
                    }
                    else{ // неудача (не авторизирован)
                        LinearLayout recyclerView = findViewById(R.id.reservationRecycler);
                        recyclerView.removeAllViews();
                        LayoutInflater inflater = getLayoutInflater();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        View view = inflater.inflate(R.layout.success_register, null);
                        TextView textView = view.findViewById(R.id.errorwindow);
                        textView.setText("Вы не авторизованы!");
                        TextView button = view.findViewById(R.id.btn_auth);
                        button.setText("Войти");
                        lp.gravity = Gravity.CENTER;
                        view.setLayoutParams(lp);
                        ConstraintLayout main = findViewById(R.id.main);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ReservationPage.this, ProfilePage.class);
                                startActivity(intent);
                            }
                        });
                        view.findViewById(R.id.closest).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ReservationPage.this, ProfilePage.class);
                                startActivity(intent);
                            }
                        });
                        main.addView(view);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{ // нет инета
                LinearLayout recyclerView = findViewById(R.id.reservationRecycler);
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


    // Get запрос для получения моих бронирований
    class GetBooking extends AsyncTask<String, String, String> {
        Settings session = new Settings(ReservationPage.this);
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
                String cookie = session.getSession();
                connection.setRequestProperty("Cookie", cookie); // сеттим куки
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n"); // получаем ответ
                }
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
            if (result != null){
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getBoolean("success")){ // удачно получили
                        obj = obj.getJSONObject("booking");
                        int count = obj.getInt("count");
                        JSONArray reservations = obj.getJSONArray("list");
                        JSONObject item;
                        for (int i=0;i<count;i++){ // через цикл добавляем брони
                            item = reservations.getJSONObject(i);
                            String name = item.getString("name");
                            String time = item.getString("time");
                            String id = item.getString("id");
                            String data = item.getString("data");
                            String timeWork = item.getString("timeWork");
                            LinearLayout linearLayout = findViewById(R.id.reservationRecycler);
                            LayoutInflater inflater = getLayoutInflater();
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            View view = inflater.inflate(R.layout.reservation_item, null);
                            TextView dataText = view.findViewById(R.id.data);
                            String[] months = { // для конвертаии из численной даты в строку
                                    "Январь",
                                    "Февраль",
                                    "Март",
                                    "Апрель",
                                    "Май",
                                    "Июнь",
                                    "Июль",
                                    "Август",
                                    "Сентябрь",
                                    "Октябрь",
                                    "Ноябрь",
                                    "Декабрь"
                            };
                            Integer month = Integer.parseInt(data.substring(data.indexOf('.')+1, data.length()));
                            String dats = data.substring(0, data.indexOf('.')) + ' ' + months[month-1];
                            dataText.setText(dats);
                            TextView timeText = view.findViewById(R.id.time);
                            timeText.setText(time+":00");
                            TextView nameText = view.findViewById(R.id.name);
                            nameText.setText(name);
                            TextView timeWorkText = view.findViewById(R.id.timeWork);
                            timeWorkText.setText(timeWork);
                            ImageButton btn = view.findViewById(R.id.closest);
                            int finalI = i;
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) { // кнопка удаления
                                    LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    View accept = inflater.inflate(R.layout.accept_delete, null);
                                    lpa.gravity = Gravity.CENTER;
                                    accept.setLayoutParams(lpa);
                                    TextView btn_yes = accept.findViewById(R.id.yes);
                                    btn_yes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) { // подтверждение
                                            ConstraintLayout main = findViewById(R.id.main);
                                            main.removeView(findViewById(R.id.accept_delete));
                                            new DeleteBooking().execute(ngrok+"/delete_booking", id, data,time, Integer.toString(finalI));
                                        }
                                    });
                                    TextView btn_no = accept.findViewById(R.id.no);
                                    ImageButton btn_back = accept.findViewById(R.id.closest);
                                    btn_back.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) { // назад
                                            ConstraintLayout main = findViewById(R.id.main);
                                            main.removeView(findViewById(R.id.accept_delete));
                                        }
                                    });
                                    btn_no.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) { // не подтвердил
                                            ConstraintLayout main = findViewById(R.id.main);
                                            main.removeView(findViewById(R.id.accept_delete));
                                        }
                                    });
                                    ConstraintLayout main = findViewById(R.id.main);
                                    main.addView(accept);
                                }
                            });
                            view.setLayoutParams(lp);
                            linearLayout.addView(view);
                        }
                        if (count == 0){
                            LinearLayout recyclerView = findViewById(R.id.reservationRecycler);
                            recyclerView.removeAllViews();
                            LayoutInflater inflater = getLayoutInflater();
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            View view = inflater.inflate(R.layout.success_register, null);
                            TextView textView = view.findViewById(R.id.errorwindow);
                            textView.setText("Вы еще ничего не забронировали!\nНастало время это исправить");
                            TextView button = view.findViewById(R.id.btn_auth);
                            button.setText("Go");
                            lp.gravity = Gravity.CENTER;
                            view.setLayoutParams(lp);
                            ConstraintLayout main = findViewById(R.id.main);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ReservationPage.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                            view.findViewById(R.id.closest).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    main.removeView(findViewById(R.id.success));
                                }
                            });
                            main.addView(view);
                        }
                    }
                    else{ // не аввторизирован
                        LinearLayout recyclerView = findViewById(R.id.reservationRecycler);
                        recyclerView.removeAllViews();
                        LayoutInflater inflater = getLayoutInflater();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        View view = inflater.inflate(R.layout.success_register, null);
                        TextView textView = view.findViewById(R.id.errorwindow);
                        textView.setText("Вы не авторизованы!");
                        TextView button = view.findViewById(R.id.btn_auth);
                        button.setText("Войти");
                        lp.gravity = Gravity.CENTER;
                        view.setLayoutParams(lp);
                        ConstraintLayout main = findViewById(R.id.main);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ReservationPage.this, ProfilePage.class);
                                startActivity(intent);
                            }
                        });
                        view.findViewById(R.id.closest).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ReservationPage.this, ProfilePage.class);
                                startActivity(intent);
                            }
                        });
                        main.addView(view);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else{ // нет инета
                LinearLayout recyclerView = findViewById(R.id.reservationRecycler);
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
    // собираем свайпы
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

    private void SwipeUp() { // перезагрузка
        setContentView(R.layout.activity_reservation_page);
        new GetBooking().execute(ngrok+ "/my_booking");
    }
    public void openMain(View view){ // на главную
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openMap(View view){ // на карту
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    }
    public void openProfile(View view){ // в профиль
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }
}