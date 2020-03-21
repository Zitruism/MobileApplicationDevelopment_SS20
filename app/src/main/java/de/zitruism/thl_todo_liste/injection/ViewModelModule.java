package de.zitruism.thl_todo_liste.injection;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.ui.viewmodel.ViewModelFactory;

@Module
public class ViewModelModule {

    public ViewModelModule() {}

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(TodoRepository todoRepository) {
        return new ViewModelFactory(todoRepository);
    }

}