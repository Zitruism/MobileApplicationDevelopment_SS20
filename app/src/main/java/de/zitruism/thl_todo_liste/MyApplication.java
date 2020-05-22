package de.zitruism.thl_todo_liste;

import android.app.Application;

import de.zitruism.thl_todo_liste.injection.ApplicationComponent;
import de.zitruism.thl_todo_liste.injection.ApplicationModule;
import de.zitruism.thl_todo_liste.injection.DaggerApplicationComponent;
import de.zitruism.thl_todo_liste.injection.RequestModule;
import de.zitruism.thl_todo_liste.injection.RoomModule;
import de.zitruism.thl_todo_liste.injection.ViewModelModule;

public class MyApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .roomModule(new RoomModule(this))
                .viewModelModule(new ViewModelModule())
                .requestModule(new RequestModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}