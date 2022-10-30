package com.example.flance20;
import android.app.Application;
import com.example.flance20.model.Settings;
import com.yandex.mapkit.MapKitFactory;

public class MainApplication extends Application {
    // Сеттим настройки при запуске приложения
    private final String MAPKIT_API_KEY = "YOUR_MAP_API_KEY";

    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        Settings settings = new Settings(this);
        if (settings.getNgrokUrl()==null){
            settings.setNgrokUrl("YOUR_NGROK_DOMAIN"); // дефолтный ngrok url, если нет вообще никакого(без http//: и .eu.)
        }
    }
}
