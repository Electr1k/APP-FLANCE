package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import javax.net.ssl.HttpsURLConnection;

public class BookingForm extends AppCompatActivity {
    int count = 1; // Кол-во гостей 1<=count<=5
    String ngrok = null, id, name_f, selectData = null; // ngrok url, id, нейм заведения для запроса, выбранная дата из карусели
    final int[] selectTime = {-1}; // выбранное время из карусели
    String contextParent = null; // Из какого активити был запущен

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);
        Settings settings = new Settings(this);
        ngrok = settings.getNgrokUrl(); // установка ngrok url
        setForm(); // установка формы логин/юзеринфо
    }

    // Установщик формы
    void setForm(){
        ImageButton btn_close = findViewById(R.id.closest);
        String name = getIntent().getStringExtra("name");
        String address_s = getIntent().getStringExtra("address");
        id = getIntent().getStringExtra("id");
        contextParent = getIntent().getStringExtra("context");
        if (contextParent.equals("Main")){ // если запустили из мейнактивити
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // при нажатии на крестик выйти в мейнактивити
                        Intent intent = new Intent(BookingForm.this, MainActivity.class);
                        startActivity(intent);
                    }
            });
        }
        else{ // если открыли с мапы
            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { // при нажатии на крестик выйти в мапактивити
                    Intent intent = new Intent(BookingForm.this, MapsPage.class);
                    startActivity(intent);
                }
            });
        }
        name_f = name; // глобал для запроса
        TextView nameEst = findViewById(R.id.nameEst), address = findViewById(R.id.addressBooking);
        nameEst.setText(name);
        address.setText(address_s);
        JSONObject booking = null;
        JSONArray data_s = null;
        final TextView[] actiondata = {null};
        final TextView[] actiontime = {null};

        if(getIntent().hasExtra("booking")) {
            try {
                booking = new JSONObject(getIntent().getStringExtra("booking"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (!(booking == null)){ // booking из Json получен корректно
            try {
                data_s = booking.getJSONArray("data"); // даты из Json
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i=0;i< data_s.length();i++){ // Заполнение ленты дат
                try {
                    String data = data_s.getString(i);
                    TextView textdata = new TextView(BookingForm.this);
                    textdata.setText(data);
                    textdata.setTextSize(30);
                    LinearLayout lineardata = findViewById(R.id.lineardata);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(20,5,20,5);
                    textdata.setTextColor(Color.BLACK);
                    textdata.setGravity(Gravity.CENTER);
                    textdata.setLayoutParams(params);
                    JSONObject finalBooking = booking;
                    int finalI = i;
                    // При нажатии на дату с индексом i
                    textdata.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onClick(View view) {
                            if (actiondata[0] == null) actiondata[0] = textdata; // выбронная дата
                            else{
                                actiondata[0].setBackground(null); // если до этого дата была выбрана, убираем ей бэк
                                actiondata[0] = textdata;
                                selectTime[0] = -1;
                                Button btn_booking = findViewById(R.id.booking_button); // устанавливаем новой дате бэк
                                btn_booking.setBackground(getDrawable(R.drawable.booking_button_noactivity));
                            }
                            LinearLayout linearTime = findViewById(R.id.lineartime);
                            linearTime.setBackground(getDrawable(R.drawable.countback));
                            linearTime.removeAllViewsInLayout();
                            System.out.println(data);
                            textdata.setBackground(getDrawable(R.drawable.booking_button));
                            try {
                                JSONArray timeArr = finalBooking.getJSONArray(data);
                                HashSet<Integer> timeSet = new HashSet<Integer>();
                                boolean k =false; // Карусель не пустая
                                // Заполняем сет (множество) занятого времени
                                for (int i=0;i<timeArr.length();i++) timeSet.add(timeArr.getInt(i));
                                // Создаем карусель часов
                                for (int j=0;j<23;j++){
                                    if (!timeSet.contains(j)){ // час не занят
                                        k=true;
                                        TextView textTime = new TextView(BookingForm.this);
                                        textTime.setText(Integer.toString(j)+":00");
                                        textTime.setTextSize(30);
                                        textTime.setGravity(Gravity.CENTER);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                        params.setMargins(20,5,20,5);
                                        textTime.setTextColor(Color.BLACK);
                                        textTime.setLayoutParams(params);
                                        int finalJ = j;
                                        textTime.setOnClickListener(new View.OnClickListener() {
                                            @SuppressLint("ResourceType")
                                            @Override
                                            public void onClick(View view) { // выбран час
                                                if (actiontime[0] == null) actiontime[0] = textTime; // час выбран впервые
                                                else{ // час переизбран
                                                    actiontime[0].setBackground(null);
                                                    actiontime[0] = textTime;
                                                }
                                                selectData = data; // выбранная дата
                                                selectTime[0] = finalJ; // выбранное время(час)
                                                textTime.setBackground(getDrawable(R.drawable.booking_button));
                                                Button button_booking = findViewById(R.id.booking_button);
                                                button_booking.setBackground(getDrawable(R.drawable.booking_button));
                                            }
                                        });
                                        linearTime.addView(textTime);
                                    }
                                }
                                if (!k){ // Все часы заняты
                                    TextView textTime = new TextView(BookingForm.this);
                                    textTime.setText("На эту дату нет свободных часов");
                                    textTime.setTextSize(22);
                                    textTime.setGravity(Gravity.CENTER);
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    params.setMargins(20,5,20,5);
                                    textTime.setTextColor(Color.BLACK);
                                    textTime.setLayoutParams(params);
                                    linearTime.addView(textTime);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    lineardata.addView(textdata);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        System.out.println(booking);
    }


    // Post запрос для бронирования
    class Booking_req extends AsyncTask<String, String, String> {

        Settings session = new Settings(BookingForm.this);
        protected void onPriExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Button btn = findViewById(R.id.booking_button);
            btn.setOnClickListener(null);
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String id = strings[1], name = strings[2], person = strings[3], data = strings[4], time = strings[5];
                String jsonInputString = "{\"id\": \""+id+"\", \"name\": \""+name+"\", \"person\": \""+person+"\", \"data\": \""+data+"\", \"time\": \""+time+"\"}";
                byte[] out = jsonInputString.getBytes("utf-8"); // тело запроса Json
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                String cookie = session.getSession(); // куки
                connection.addRequestProperty("Cookie", cookie); // устанавливаем куки
                connection.setRequestMethod("POST");
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
            System.out.println(result);
            // обработка нет инета или сервер упал
            if (result!=null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getBoolean("success")){ // успешно, установка уведа
                        LayoutInflater inflater = getLayoutInflater();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        View view = inflater.inflate(R.layout.success_register, null);
                        TextView textView = view.findViewById(R.id.errorwindow);
                        textView.setText("Вы успешно забронировали время!");
                        TextView button = view.findViewById(R.id.btn_auth);
                        button.setText("Мои бронирования");
                        lp.gravity = Gravity.CENTER;
                        view.setLayoutParams(lp);
                        ConstraintLayout main = findViewById(R.id.main);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) { // запуск активити бронирований юзера
                                Intent intent = new Intent(BookingForm.this, ReservationPage.class);
                                startActivity(intent);
                            }
                        });
                        view.findViewById(R.id.closest).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) { // запуск мейнактивити
                                Intent intent = new Intent(BookingForm.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        main.addView(view);
                    }
                    else{
                        if (obj.getInt("Error")==0){ // час заняли, установка уведа
                            ScrollView scrollView = findViewById(R.id.scroldelete);
                            scrollView.removeAllViews();
                            LayoutInflater inflater = getLayoutInflater();
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            View view = inflater.inflate(R.layout.error_window, null);
                            view.setLayoutParams(lp);
                            TextView text = view.findViewById(R.id.errorwindow);
                            text.setText("Похоже этот час кто-то занял :(\nДля обновления свайпните");
                            text.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SwipeUp();
                                }
                            });
                            ConstraintLayout main = findViewById(R.id.main);
                            main.addView(view);
                        }
                        else{ // не авторизован, установка уведа
                            ScrollView scrollView = findViewById(R.id.scroldelete);
                            scrollView.removeAllViews();
                            LayoutInflater inflater = getLayoutInflater();
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            View view = inflater.inflate(R.layout.success_register, null);
                            view.findViewById(R.id.flipper).setBackground(getDrawable(R.drawable.errorwindow));
                            Button btn = view.findViewById(R.id.btn_auth);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(BookingForm.this, ProfilePage.class);
                                    startActivity(intent);
                                }
                            });
                            TextView text = view.findViewById(R.id.errorwindow);
                            text.setText("Вы не авторизованы");
                            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            text.setGravity(Gravity.CENTER_HORIZONTAL);
                            view.setLayoutParams(lp);
                            ConstraintLayout main = findViewById(R.id.main);
                            main.addView(view);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else {
                // нет инета, сетим увед
                ScrollView scrollView = findViewById(R.id.scroldelete);
                scrollView.removeAllViews();
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                View view = inflater.inflate(R.layout.error_window, null);
                view.setLayoutParams(lp);
                TextView text = view.findViewById(R.id.errorwindow);
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SwipeUp();
                    }
                });
                ConstraintLayout main = findViewById(R.id.main);
                main.addView(view);
            }
        }
    }

    // ловим свайп
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float sensitvity = 250;
            if (e2.getY() - e1.getY() > sensitvity) {
                SwipeUp();
            }
            return true;
        }
    };

    GestureDetector gestureDetector = new GestureDetector(getBaseContext(), simpleOnGestureListener);

    // нашли свайп рулим на главную
    private void SwipeUp() {
        System.out.println("It's swipe");
        Intent intent = new Intent(this, BookingPage.class);
        intent.putExtra("id", Integer.parseInt(id));
        intent.putExtra("name", name_f);
        startActivity(intent);
    }

    // минусуем гостей
    @SuppressLint("SetTextI18n")
    public void countMinus(View view){
        if (count > 1){
            count--;
            TextView textView = findViewById(R.id.count);
            textView.setText( Integer.toString(count));
        }
    }
    // плодим гостей
    @SuppressLint("SetTextI18n")
    public void countPlus(View view){
        if (count < 5){
            count++;
            TextView textView = findViewById(R.id.count);
            textView.setText( Integer.toString(count));
        }
    }
    public void openMain(View view){ // если нажали на главную
        if (contextParent.equals("Main")) { // если пришли с главной то назад
            onBackPressed();
        }
        else{ // если пришли с карты, запускем карту
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    public void booking(View view){
        if (selectData!=null && selectTime[0]!=-1){ // выбраны и дата и время
            // запрос на сервер для букинга
            new Booking_req().execute(ngrok+"/new_booking", id, name_f, Integer.toString(count), selectData, Integer.toString(selectTime[0]));
        }
    }
    public void openReservation(View view){ // мои брони
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
    public void openMap(View view){ // карта
        if (contextParent.equals("Main")) { // если пришли с главной, то на карту
            Intent intent = new Intent(this, MapsPage.class);
            startActivity(intent);
        }
        else{ // если пришли с карты, то назад
            onBackPressed();
        }
    }
    public void openProfile(View view){ // профиль
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }
}