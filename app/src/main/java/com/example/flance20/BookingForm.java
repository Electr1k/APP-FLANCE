package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.se.omapi.Session;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.flance20.model.Sessions;

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
    int count = 1;
    String ngrok = "https://0131-95-174-108-193.eu.ngrok.io", id, name_f, selectData = null;
    final int[] selectTime = {-1};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);
        setForm();
    }

    void setForm(){
        String name = getIntent().getStringExtra("name");
        String address_s = getIntent().getStringExtra("address");
        id = getIntent().getStringExtra("id");
        name_f = name;
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
        if (!(booking == null)){
            try {
                data_s = booking.getJSONArray("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i=0;i< data_s.length();i++){
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
                    //Typeface face = Typeface.createFromAsset(getAssets(), "font/roboto_bold.xml");
                    //textdata.setTypeface(face);
                    JSONObject finalBooking = booking;
                    int finalI = i;
                    textdata.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onClick(View view) {
                            if (actiondata[0] == null) actiondata[0] = textdata;
                            else{
                                actiondata[0].setBackground(null);
                                actiondata[0] = textdata;
                                selectTime[0] = -1;
                                Button btn_booking = findViewById(R.id.booking_button);
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
                                boolean k =false;
                                for (int i=0;i<timeArr.length();i++) timeSet.add(timeArr.getInt(i));
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
                                            public void onClick(View view) {
                                                if (actiontime[0] == null) actiontime[0] = textTime;
                                                else{
                                                    actiontime[0].setBackground(null);
                                                    actiontime[0] = textTime;
                                                }
                                                selectData = data;
                                                selectTime[0] = finalJ;
                                                textTime.setBackground(getDrawable(R.drawable.booking_button));
                                                Button button_booking = findViewById(R.id.booking_button);
                                                button_booking.setBackground(getDrawable(R.drawable.booking_button));
                                            }
                                        });
                                        linearTime.addView(textTime);
                                    }
                                }
                                if (!k){
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


    class Booking_req extends AsyncTask<String, String, String> {

        Sessions session = new Sessions(BookingForm.this);
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
                byte[] out = jsonInputString.getBytes("utf-8");
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                String cookie = session.getSession();
                System.out.println(jsonInputString);
                connection.addRequestProperty("Cookie", cookie);
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
                    buffer.append(line).append("\n");
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
            // обработка нет инета
            if (result!=null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.getBoolean("success")){
                        LayoutInflater inflater = getLayoutInflater();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        View view = inflater.inflate(R.layout.success_register, null);
                        TextView textView = view.findViewById(R.id.errorwindow);
                        textView.setText("Вы успешно забронировали время!");
                        Button button = view.findViewById(R.id.btn_auth);
                        button.setText("Мои бронирования");
                        lp.gravity = Gravity.CENTER;
                        view.setLayoutParams(lp);
                        ConstraintLayout main = findViewById(R.id.main);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(BookingForm.this, ReservationPage.class);
                                startActivity(intent);
                            }
                        });
                        main.addView(view);
                    }
                    else{
                        if (obj.getInt("Error")==0){
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
                        else{
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


    private void SwipeUp() {
        System.out.println("It's swipe");
        Intent intent = new Intent(this, BookingPage.class);
        intent.putExtra("id", Integer.parseInt(id));
        intent.putExtra("name", name_f);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    public void countMinus(View view){
        if (count > 1){
            count--;
            TextView textView = findViewById(R.id.count);
            textView.setText( Integer.toString(count));
        }
    }
    @SuppressLint("SetTextI18n")
    public void countPlus(View view){
        if (count < 5){
            count++;
            TextView textView = findViewById(R.id.count);
            textView.setText( Integer.toString(count));
        }
    }
    public void openMain(View view){
        onBackPressed();
    }
    public void booking(View view){
        if (selectData!=null && selectTime[0]!=-1){
            new Booking_req().execute(ngrok+"/new_booking", id, name_f, Integer.toString(count), selectData, Integer.toString(selectTime[0]));
        }
    }
    public void openReservation(View view){
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
    public void openMap(View view){
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    }
    public void openProfile(View view){
        Intent intent = new Intent(this, ProfilePage.class);
        startActivity(intent);
    }
}