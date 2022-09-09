package com.example.flance20;
import android.app.Application;
import com.example.flance20.model.Settings;
import com.yandex.mapkit.MapKitFactory;

public class MainApplication extends Application {
    // Сеттим настройки при запуске приложения
    private final String MAPKIT_API_KEY = "b16667de-040a-4f36-b577-a3e1c221b563";

    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        Settings settings = new Settings(this);
        if (settings.getNgrokUrl()==null){
            settings.setNgrokUrl("58b3-95-174-108-193"); // дефолтный ngrok url, если нет вообще никакого
        }
    }
}
