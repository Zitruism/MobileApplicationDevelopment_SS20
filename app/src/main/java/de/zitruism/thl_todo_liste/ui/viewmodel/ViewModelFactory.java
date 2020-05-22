package de.zitruism.thl_todo_liste.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final TodoRepository todoRepository;
    private final IWebService webService;

    public ViewModelFactory(TodoRepository todoRepository, IWebService webService) {
        this.todoRepository = todoRepository;
        this.webService = webService;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(DetailViewModel.class))
            return (T) new DetailViewModel(todoRepository, webService);
        else if(modelClass.isAssignableFrom(ListViewModel.class))
            return (T) new ListViewModel(todoRepository, webService);
        else if(modelClass.isAssignableFrom(StartViewModel.class))
            return (T) new StartViewModel(todoRepository, webService);
        else if(modelClass.isAssignableFrom(LoginViewModel.class))
            return (T) new LoginViewModel(webService);
        else
            throw new IllegalArgumentException("ViewModel not found!");
    }
}
