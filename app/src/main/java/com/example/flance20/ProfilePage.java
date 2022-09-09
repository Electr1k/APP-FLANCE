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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.flance20.model.Settings;
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

public class ProfilePage extends AppCompatActivity {
    String ngrok = null;
    boolean[] flag = {false}; // окно регистрации или авторизации инактив/не добавленно
    int c1 = 0, c2 = 0; // c1 - flag active login form, c2 - register form
    View viewNoAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Settings settings = new Settings(this);
        ngrok = settings.getNgrokUrl(); // получаем ngrok url
        setUserBar(); // установка юезр бара
    }

    // установка формы для авторизации
    void setLoginForm(){
        LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        View login_form = getLayoutInflater().inflate(R.layout.login_form, null);
        lpa.gravity = Gravity.CENTER;
        login_form.setLayoutParams(lpa);
        EditText email = login_form.findViewById(R.id.email);
        EditText password = login_form.findViewById(R.id.password);
        Button btn = login_form.findViewById(R.id.btn_auth);
        ImageView btnClose = login_form.findViewById(R.id.closest);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout form = findViewById(R.id.loginform);
                form.setVisibility(View.INVISIBLE);
                flag[0] = false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().length()>7&&password.getText().toString().length()>=6){
                    new Login().execute(ngrok+"/login", email.getText().toString(), password.getText().toString());
                }
                else {
                    TextView text = findViewById(R.id.errorauth);
                    text.setTextSize(16);
                }
            }
        });
        ConstraintLayout main = findViewById(R.id.main);
        if (!flag[0]){
            if (c1==0) {
                main.addView(login_form);
                c1++;
            }
            else{
                ConstraintLayout form = findViewById(R.id.loginform);
                form.setVisibility(View.VISIBLE);
            }
            flag[0] = true;
        }
    }

    // установка формы для регистрации
    void setRegisterForm(){
        LinearLayout.LayoutParams lpa = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lpa.gravity = Gravity.CENTER;
        View register_form = getLayoutInflater().inflate(R.layout.register_form, null);
        register_form.setLayoutParams(lpa);
        EditText name = register_form.findViewById(R.id.name);
        EditText surname = register_form.findViewById(R.id.surname);
        EditText email = register_form.findViewById(R.id.email);
        EditText password = register_form.findViewById(R.id.password);
        Button btn = register_form.findViewById(R.id.btn_reg);
        ImageView btnClose = register_form.findViewById(R.id.closest);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConstraintLayout form = findViewById(R.id.registerform);
                form.setVisibility(View.INVISIBLE);
                flag[0] = false;
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().length()<6 || email.getText().toString().indexOf('@')==-1 || email.getText().toString().indexOf('.')==-1 || email.getText().toString().lastIndexOf(".")<email.getText().toString().indexOf('@') || email.getText().toString().lastIndexOf("@")<email.getText().toString().indexOf('@')||email.getText().toString().length()<8){
                    if (password.getText().toString().length()<6){
                        TextView errorPass = register_form.findViewById(R.id.errorpassword);
                        errorPass.setTextSize(14);
                    }
                    else{
                        TextView errorPass = register_form.findViewById(R.id.erroremail);
                        errorPass.setTextSize(14);
                    }
                }
                else{
                    new Registration().execute(ngrok+"/register", name.getText().toString(), surname.getText().toString(), email.getText().toString(), password.getText().toString());
                }
            }
        });
        ConstraintLayout main = findViewById(R.id.main);
        if (!flag[0]) { // устанавливаем форму (можно было сделать проще)
            if (c2==0) {
                main.addView(register_form);
                c2++;
            }
            else{
                ConstraintLayout form = findViewById(R.id.registerform);
                form.setVisibility(View.VISIBLE);
            }
            flag[0] = true;
        }
    }

    // устанавливаем юзер бар
    void setUserBar(){
        Settings session = new Settings(ProfilePage.this);
        LinearLayout userBar = findViewById(R.id.userBar);
        if (session.getSession()==null) { // если нет сессии(не авторизирован)
            LayoutInflater inflater = getLayoutInflater();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            viewNoAuth = inflater.inflate(R.layout.no_auth_profeli, null);
            lp.setMargins(0,0,0,20);
            viewNoAuth.setLayoutParams(lp);
            TextView login = viewNoAuth.findViewById(R.id.auth);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLoginForm();
                } // логин форма
            });
            viewNoAuth.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setRegisterForm();
                } // регистер форма
            });

            userBar.addView(viewNoAuth);
        }
        else{ // сессия есть
            LayoutInflater inflater = getLayoutInflater();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            if (viewNoAuth!=null) {
                userBar.removeAllViewsInLayout();
                viewNoAuth = null;
            }
            View view = inflater.inflate(R.layout.auth_profile, null);
            lp.setMargins(0,0,0,20);
            view.setLayoutParams(lp);
            userBar.addView(view);
            String[] user = session.getUser();
            String name = user[0] + " " + user[1];
            String mail = user[2];
            TextView mailInPage = view.findViewById(R.id.mail);
            mailInPage.setText(mail);
            TextView nameInPage = view.findViewById(R.id.name);
            nameInPage.setText(name);
            LinearLayout login = view.findViewById(R.id.logoutLinear);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("Logout");
                    new GoLogout().execute(ngrok+"/logout");
                }
            });
            System.out.println("Пользователь вошел");
        }

    }
    // Get запрос logout (выход из аккаунта)
    class GoLogout extends AsyncTask<String, String, String> {
        Settings session = new Settings(ProfilePage.this);

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
                connection.setRequestProperty("Cookie", cookie); // устанавливаем куки
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n"); // сбор ответа
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
            if (result != null) { // успешно вышли
                session.deleteSession();
                LinearLayout userBar = findViewById(R.id.userBar);
                userBar.removeAllViewsInLayout();
                setContentView(R.layout.activity_profile_page);
                setUserBar();
            }
            else{
                ScrollView scrollView = findViewById(R.id.scroldelete);
                scrollView.removeAllViews();
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                View view = inflater.inflate(R.layout.error_window, null);
                view.setLayoutParams(lp);
                ConstraintLayout main = findViewById(R.id.main);
                main.addView(view);
            }
        }
    }

    // Post запрос на авторизацию
    class Login extends AsyncTask<String, String, String> {
        Settings session = new Settings(ProfilePage.this);

        protected void onPriExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {

                String email = strings[1], password = strings[2];
                String jsonInputString = "{\"email\": \""+email+"\", \"password\": \""+password+"\"}";
                byte[] out = jsonInputString.getBytes("utf-8");// тело запроса json
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST"); // устанавливаем метод
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.addRequestProperty("Content-Type", "application/json");
                connection.connect();
                OutputStream os = connection.getOutputStream();
                os.write(out, 0, out.length);
                os.close();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "", cookies="";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n"); // собираем ответ
                }
                JSONObject obj = new JSONObject(buffer.toString());
                if (obj.getBoolean("success")){
                    cookies = connection.getHeaderField("Set-Cookie").split(";")[0]; // собираем куки
                    JSONObject userinfo = obj.getJSONObject("userinfo");
                    String name = userinfo.getString("name");
                    String surname = userinfo.getString("surname");
                    session.setSession(cookies, name, surname, email);
                    System.out.println("Куки собрал: " + cookies);
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                    if (new JSONObject(result).getBoolean("success")) {
                        c1 = c2 =0;
                        flag[0] = false;
                        setContentView(R.layout.activity_profile_page);
                        setUserBar(); // устанавливаем юзер бар
                    }
                    else {
                        TextView textView = findViewById(R.id.errorauth);
                        textView.setTextSize(16);
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

    // Post запрос для регистрации
    class Registration extends AsyncTask<String, String, String> {
        protected void onPriExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String name = strings[1], surname = strings[2], email = strings[3], password = strings[4];
                String jsonInputString = "{\"name\": \""+name+"\", \"surname\": \""+surname+"\", \"email\": \""+email+"\", \"password\": \""+password+"\"}";
                byte[] out = jsonInputString.getBytes("utf-8"); // тело запроса Json
                URL url = new URL(strings[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST"); // устанавливаем метод
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.addRequestProperty("Content-Type", "application/json");
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
                JSONObject obj = new JSONObject(buffer.toString());
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
                    if (new JSONObject(result).getBoolean("success")) { // успешная регистрация
                        LayoutInflater inflater = getLayoutInflater();
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        View view = inflater.inflate(R.layout.success_register, null);
                        lp.gravity = Gravity.CENTER;
                        view.setLayoutParams(lp);
                        ConstraintLayout main = findViewById(R.id.main);
                        view.findViewById(R.id.btn_auth).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                c1=0;
                                c2=0;
                                flag[0]= false;
                                setContentView(R.layout.activity_profile_page);
                                setUserBar(); // сеттим юзербар
                            }
                        });
                        view.findViewById(R.id.closest).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                c1=0;
                                c2=0;
                                flag[0]= false;
                                setContentView(R.layout.activity_profile_page);
                                setUserBar(); // сеттим юезрбар
                            }
                        });
                        main.addView(view);
                    }
                    else{ // неудачная рега
                        System.out.println("Почта занята");
                        TextView emailerror = findViewById(R.id.erroremail);
                        emailerror.setTextSize(16);
                        emailerror.setText("Эта почта уже используется!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else { // нет инета, сеттим увед
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
    // ищем свайпы
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

    private void SwipeUp() { // нашли свайп, перезгрузка страницы
        System.out.println("It's swipe");
        c1=0;
        c2=0;
        flag[0]= false;
        setContentView(R.layout.activity_profile_page);
        setUserBar();
    }
    public void openMain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    } // на главную
    public void openReservation(View view){ // мои бронировании
        Intent intent = new Intent(this, ReservationPage.class);
        startActivity(intent);
    }
    public void openMap(View view){ // на карту
        Intent intent = new Intent(this, MapsPage.class);
        startActivity(intent);
    }

    public void openSuggestions(View view){ // предложения и пожелания
        Intent intent = new Intent(this, EmptyActivity.class);
        intent.putExtra("headers", "Предложения и пожелания");
        intent.putExtra("body", "Выразить свои предложения и пожелания вы можете... А может и не можете");
        startActivity(intent);
    }
    public void openForCafe(View view){ // анкеты для кафе
        Intent intent = new Intent(this, EmptyActivity.class);
        intent.putExtra("headers", "Анкета для кафе");
        intent.putExtra("body", "В разработке...");
        startActivity(intent);
    }
    public void openAboutUs(View view){ // о нас
        Intent intent = new Intent(this, EmptyActivity.class);
        intent.putExtra("headers", "О проекте");
        intent.putExtra("body", "Приложение разработано в рамках первого творческого проекта ИКТИБ.");
        startActivity(intent);
    }
    public void openNgrok(View view){ // настройки ngrok
        Intent intent = new Intent(this, NgrokActivity.class);
        startActivity(intent);
    }
}