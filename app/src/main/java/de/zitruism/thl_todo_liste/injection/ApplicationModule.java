package de.zitruism.thl_todo_liste.injection;

import javax.inject.Singleton;

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
    @Singleton
    MyApplication provideApplication(){
        return application;
    }
}