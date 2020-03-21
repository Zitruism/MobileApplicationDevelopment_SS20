package de.zitruism.thl_todo_liste.injection;

import javax.inject.Singleton;

import dagger.Component;
import de.zitruism.thl_todo_liste.ui.DetailFragment;
import de.zitruism.thl_todo_liste.ui.ListFragment;
import de.zitruism.thl_todo_liste.ui.viewmodel.DetailViewModel;

@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class, ViewModelModule.class})
public interface ApplicationComponent {

    //ListView
    void inject(ListFragment listFragment);

    //DetailView
    void inject(DetailFragment listFragment);

    void inject(DetailViewModel detailViewModel);

}
