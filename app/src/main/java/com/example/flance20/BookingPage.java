package com.example.flance20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.flance20.model.Sessions;

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

public class BookingPage extends AppCompatActivity {

    String url_s, url_main_page, ngrok = "https://0131-95-174-108-193.eu.ngrok.io", address_s, nameInPage_s;
    int id;
    JSONObject booking_s;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);
        TextView pageName = findViewById(R.id.pagename);
        pageName.setText(getIntent().getStringExtra("name"));
        id = getIntent().getIntExtra("id", -1);
        url_main_page = ngrok + "/api/" + id;
        new GetURLData().execute(url_main_page);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }


    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float sensitvity = 250;
            if (Math.abs(e2.getX() - e1.getX()) > 150) {
                SwipeRight();
            }
            if (e2.getY() - e1.getY() > sensitvity) {
                SwipeUp();
            }
            return true;
        }
    };

    GestureDetector gestureDetector = new GestureDetector(getBaseContext(), simpleOnGestureListener);

    private void SwipeRight() {
        System.out.println("It's swipe on right");
        onBackPressed();
    }
    private void SwipeUp() {
        System.out.println("It's swipe");
        setContentView(R.layout.activity_booking_page);
        TextView pageName = findViewById(R.id.pagename);
        TextView inPageName = findViewById(R.id.nameInPage);
        inPageName.setText(getIntent().getStringExtra("name"));
        pageName.setText(getIntent().getStringExtra("name"));
        new BookingPage.GetURLData().execute(url_main_page);
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    class GetURLData extends AsyncTask<String, String, String> {
        protected void onPriExecute(){

            super.onPreExecute();

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine())!= null)
                    buffer.append(line).append("\n");
                result = buffer.toString();
                if (connection != null)
                    connection.disconnect();
                return  result;

            } catch (MalformedURLException e){
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
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            // удалить загрузочный слой
            TextView address = findViewById(R.id.addressInPage);
            TextView description = findViewById(R.id.description);
            TextView time_work = findViewById(R.id.timeWork);
            TextView addressDown = findViewById(R.id.addressDown);
            ImageView preview_img = findViewById(R.id.preview_img);
            TextView nameInpage = findViewById(R.id.nameInPage);
            LinearLayout prices = findViewById(R.id.price);
            if (result != null) {
                try {
                    JSONObject obj = new JSONObject(result);
                    address_s = obj.getString("address");
                    String url_preview_img_s = obj.getString("url_preview_img");
                    url_s = obj.getString("url");
                    String descriptions_s = obj.getString("description");
                    JSONObject prices_s = obj.getJSONObject("prices");
                    Boolean wifi_s = obj.getBoolean("wifi");
                    Boolean battery_s = obj.getBoolean("battery");
                    Boolean silence_s = obj.getBoolean("silence");
                    Boolean cashless_payment_s = obj.getBoolean("cashless_payment");
                    String time_work_s = obj.getString("time_work");
                    nameInPage_s = obj.getString("name");
                    booking_s = obj.getJSONObject("booking");
                    new DownloadImageTask(preview_img).execute(url_preview_img_s);
                    address.setText(address_s);
                    nameInpage.setText(nameInPage_s);
                    description.setText(descriptions_s);
                    time_work.setText(time_work_s);
                    addressDown.setText(address_s);
                    JSONArray item = prices_s.getJSONArray("item");
                    JSONArray price = prices_s.getJSONArray("price");

                    for (int i=0;i<item.length();i++){
                        LinearLayout linearLayout = new LinearLayout(BookingPage.this);
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        TextView textView1 = new TextView(BookingPage.this);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.1f);
                        params.setMargins(75,7,0,0);
                        textView1.setLayoutParams(params);
                        textView1.setTextColor(Color.BLACK);
                        Typeface face = Typeface.createFromAsset(getAssets(), "font/roboto.ttf");
                        textView1.setTypeface(face);
                        textView1.setText(item.getString(i));
                        textView1.setTextSize(20);
                        TextView textView2 = new TextView(BookingPage.this);
                        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins(0,7,75,0);
                        textView2.setLayoutParams(params);
                        textView2.setTextColor(Color.BLACK);
                        textView2.setTypeface(face);
                        textView2.setText(price.getString(i));
                        textView2.setTextSize(20);
                        linearLayout.addView(textView1);
                        linearLayout.addView(textView2);
                        prices.addView(linearLayout);
                    }

                    if (!wifi_s){
                        CardView card = findViewById(R.id.wifi);
                        card.removeAllViews();
                    }
                    if (!battery_s){
                        CardView card = findViewById(R.id.battery);
                        card.removeAllViews();
                    }
                    if (!silence_s){
                        CardView card = findViewById(R.id.silence);
                        card.removeAllViews();
                    }
                    if (!cashless_payment_s){
                        CardView card = findViewById(R.id.cashless_payment);
                        card.removeAllViews();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{

                TextView textError = findViewById(R.id.errorwindow);
                ViewFlipper flipper = findViewById(R.id.viewflipper);
                ScrollView scrollView = findViewById(R.id.scrollable);
                flipper.removeView(scrollView);
                textError.setText("Упс, похоже у Вас проблемы с интернетом...\nДля обновления свайпните вверх");
                textError.setTextSize(20);
                textError.setBackground(getDrawable(R.drawable.errorwindow));
            }
        }
    }


    public void openBookingForm(View view){
        Intent intent = new Intent(this,BookingForm.class);
        intent.putExtra("name", nameInPage_s);
        intent.putExtra("address", address_s);
        intent.putExtra("id", Integer.toString(id));
        intent.putExtra("booking", booking_s.toString());
        startActivity(intent);
    }
    public void openMain(View view){
        onBackPressed();
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
    public void openWebSite(View view){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url_s)));
    }
}