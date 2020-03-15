package de.zitruism.thl_todo_liste.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.zitruism.thl_todo_liste.MainActivity;
import de.zitruism.thl_todo_liste.MyApplication;
import de.zitruism.thl_todo_liste.ui.ListFragment;

@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class})
public interface ApplicationComponent {

    //Activity
    void inject(MainActivity mainActivity);

    //ListView
    void inject(ListFragment listFragment);


}
