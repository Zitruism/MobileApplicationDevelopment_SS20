package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewModel extends ViewModel {

    private TodoRepository todoRepository;
    private IWebService webservice;

    private MutableLiveData<List<Contact>> todoContacts = new MutableLiveData<>();
    private MutableLiveData<Todo> todo = new MutableLiveData<>();

    DetailViewModel(TodoRepository todoRepository, IWebService webservice) {
        this.todoRepository = todoRepository;
        this.webservice = webservice;
    }

    public LiveData<Todo> getTodo(Long id){
        return todoRepository.getTodo(id);
    }

    public void deleteTodo(Todo todo){
        new DeleteTodo(todoRepository, webservice).execute(todo);
    }

    public void updateTodo(Todo todo) {
        new UpdateTodo(todoRepository, webservice).execute(todo);
    }

    public void insert(Todo todo){
        new InsertTodo(todoRepository, webservice).execute(todo);
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

    private static class DeleteTodo extends AsyncTask<Todo, Void, Void> {

        private final TodoRepository repository;
        private final IWebService webService;

        DeleteTodo(TodoRepository repository, IWebService webService) {
            this.repository = repository;
            this.webService = webService;
        }

        @Override
        protected Void doInBackground(Todo... todos) {
            Todo todo = todos[0];
            repository.delete(todo);
            try {
                webService.deleteTodo(todo.getId()).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class UpdateTodo extends AsyncTask<Todo, Void, Todo> {

        private final TodoRepository todoRepository;
        private final IWebService webservice;

        UpdateTodo(TodoRepository todoRepository, IWebService webservice) {
            this.todoRepository = todoRepository;
            this.webservice = webservice;
        }

        @Override
        protected Todo doInBackground(Todo... todos) {
            Todo todo = todos[0];
            todoRepository.updateTodo(todo);
            return todo;
        }

        @Override
        protected void onPostExecute(Todo todo) {

            Call<Todo> call = webservice.putTodo(todo.getId(), todo);
            call.enqueue(new Callback<Todo>() {
                @Override
                public void onResponse(Call<Todo> call, Response<Todo> response) {

                }

                @Override
                public void onFailure(Call<Todo> call, Throwable t) {

                }
            });

            super.onPostExecute(todo);
        }
    }

    private static class InsertTodo extends AsyncTask<Todo, Void, Todo> {

        private TodoRepository todoRepository;
        private IWebService webservice;

        InsertTodo(TodoRepository todoRepository, IWebService webservice) {
            this.todoRepository = todoRepository;
            this.webservice = webservice;
        }

        @Override
        protected Todo doInBackground(Todo... todos) {

            Todo todo = todos[0];

            long id = todoRepository.insertTodo(todo);

            //set new id to the returned todo object
            todo.setId(id);

            return todo;
        }

        @Override
        protected void onPostExecute(Todo todo) {
            Call<Todo> call = webservice.postTodo(todo);
            call.enqueue(new Callback<Todo>() {
                @Override
                public void onResponse(Call<Todo> call, Response<Todo> response) {

                }

                @Override
                public void onFailure(Call<Todo> call, Throwable t) {

                }
            });

            super.onPostExecute(todo);
        }
    }
}
