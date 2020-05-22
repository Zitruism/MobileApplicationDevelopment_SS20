package de.zitruism.thl_todo_liste.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.zitruism.thl_todo_liste.ui.DetailFragment;
import de.zitruism.thl_todo_liste.ui.ListFragment;
import de.zitruism.thl_todo_liste.ui.LoginFragment;
import de.zitruism.thl_todo_liste.ui.StartFragment;

@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class, ViewModelModule.class, RequestModule.class})
public interface ApplicationComponent {

    //ListView
    void inject(ListFragment listFragment);

    //DetailView
    void inject(DetailFragment listFragment);

    //LoginView
    void inject(LoginFragment loginFragment);

    //Startup
    void inject(StartFragment startFragment);

}
