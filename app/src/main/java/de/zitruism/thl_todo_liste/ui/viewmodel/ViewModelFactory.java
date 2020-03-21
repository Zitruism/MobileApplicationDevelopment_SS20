package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final TodoRepository todoRepository;

    public ViewModelFactory(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(DetailViewModel.class))
            return (T) new DetailViewModel(todoRepository);
        else if(modelClass.isAssignableFrom(ListViewModel.class))
            return (T) new ListViewModel(todoRepository);
        else
            throw new IllegalArgumentException("ViewModel not found!");
    }



}
