package de.zitruism.thl_todo_liste.injection;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import de.zitruism.thl_todo_liste.ui.viewmodel.ViewModelFactory;
import retrofit2.Retrofit;

@Module
public class ViewModelModule {

    public ViewModelModule() {}

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(TodoRepository todoRepository, IWebService webService) {
        return new ViewModelFactory(todoRepository, webService);
    }

}