package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;

public class ListViewModel extends ViewModel {

    private TodoRepository todoRepository;

    @Inject
    public ListViewModel(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public LiveData<List<Todo>> getTodos(){
        return todoRepository.getAll();
    }

    public void insert(Todo todo){
        new InsertTodo(todoRepository).execute(todo);
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
