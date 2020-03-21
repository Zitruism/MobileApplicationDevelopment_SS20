package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;

public class DetailViewModel extends ViewModel {

    private TodoRepository todoRepository;

    private MutableLiveData<List<Contact>> todoContacts = new MutableLiveData<>();
    private MutableLiveData<Todo> todo = new MutableLiveData<>();

    DetailViewModel(TodoRepository todoRepository) {
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

    public void setTodoContacts(List<Contact> contacts){
        this.todoContacts.setValue(contacts);
    }

    public LiveData<List<Contact>> getTodoContacts(){
        return this.todoContacts;
    }

    public void setTodo(Todo todo){
        this.todo.setValue(todo);
    }

    public LiveData<Todo> getTodo(){
        return this.todo;
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
