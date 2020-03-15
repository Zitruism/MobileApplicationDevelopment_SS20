package de.zitruism.thl_todo_liste.injection;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.zitruism.thl_todo_liste.MyApplication;

@Module
public class ApplicationModule {
    private final MyApplication application;

    public ApplicationModule(MyApplication application) {
        this.application = application;
    }

    @Provides
    MyApplication provideApplication(){
        return application;
    }
}