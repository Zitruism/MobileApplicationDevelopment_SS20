package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.os.AsyncTask;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;

public class DetailViewModel extends ViewModel {

    private TodoRepository todoRepository;

    @Inject
    public DetailViewModel(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public LiveData<Todo> getTodo(int id){
        return todoRepository.getTodo(id);
    }

    public void updateTodo(Todo todo) {
        new UpdateTodo(todoRepository).execute(todo);
    }

    public void insert(Todo todo){
        new InsertTodo(todoRepository).execute(todo);
    }

    private static class UpdateTodo extends AsyncTask<Todo, Void, Void> {

        private final TodoRepository todoRepository;

        UpdateTodo(TodoRepository todoRepository) {
            this.todoRepository = todoRepository;
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            todoRepository.updateTodo(todos[0]);
            return null;
        }
    }

    private static class InsertTodo extends AsyncTask<Todo, Void, Void> {

        private TodoRepository todoRepository;

        InsertTodo(TodoRepository todoRepository) {
            this.todoRepository = todoRepository;
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            todoRepository.insertTodo(todos[0]);
            return null;
        }
    }



}
