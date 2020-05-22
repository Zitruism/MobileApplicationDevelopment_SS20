package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.Contact;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.interfaces.IDetailViewCallback;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewModel extends ViewModel {

    private TodoRepository todoRepository;
    private IWebService webservice;

    private MutableLiveData<List<Contact>> todoContacts = new MutableLiveData<>();
    private MutableLiveData<Todo> todo = new MutableLiveData<>();
    private MutableLiveData<Boolean> locked = new MutableLiveData<>(false);

    DetailViewModel(TodoRepository todoRepository, IWebService webservice) {
        this.todoRepository = todoRepository;
        this.webservice = webservice;
    }

    public MutableLiveData<Boolean> getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked.setValue(locked);
    }

    public LiveData<Todo> getTodo(Long id){
        return todoRepository.getTodo(id);
    }

    public void deleteTodo(Todo todo, boolean doWebCall, IDetailViewCallback callback){
        new DeleteTodo(todoRepository, webservice, doWebCall, callback).execute(todo);
    }

    public void updateTodo(Todo todo, boolean doWebCall) {
        new UpdateTodo(todoRepository, webservice, doWebCall).execute(todo);
    }

    public void insert(Todo todo, boolean doWebCall){
        new InsertTodo(todoRepository, webservice, doWebCall).execute(todo);
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

    public MutableLiveData<Todo> getTodo(){
        return this.todo;
    }

    private static class DeleteTodo extends AsyncTask<Todo, Void, Long> {

        private final TodoRepository repository;
        private final IWebService webService;
        private final boolean doWebCall;
        private final IDetailViewCallback callback;

        DeleteTodo(TodoRepository repository, IWebService webService, boolean doWebCall, IDetailViewCallback callback) {
            this.repository = repository;
            this.webService = webService;
            this.doWebCall = doWebCall;
            this.callback = callback;
        }

        @Override
        protected Long doInBackground(Todo... todos) {
            Todo todo = todos[0];
            int deletedRows = repository.delete(todo);
            if(deletedRows > 0){
                return todo.getId();
            }else{
                return -1L;
            }
        }

        @Override
        protected void onPostExecute(Long id) {
            if(doWebCall && id != -1L){
                //Delete on server
                Call<Boolean> call= webService.deleteTodo(id);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                        callback.onDeleted();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {

                    }
                });
            }else{
                callback.onDeleted();
            }
            super.onPostExecute(id);
        }
    }

    private static class UpdateTodo extends AsyncTask<Todo, Void, Todo> {

        private final TodoRepository todoRepository;
        private final IWebService webservice;
        private final boolean doWebCall;

        UpdateTodo(TodoRepository todoRepository, IWebService webservice, boolean doWebCall) {
            this.todoRepository = todoRepository;
            this.webservice = webservice;
            this.doWebCall = doWebCall;
        }

        @Override
        protected Todo doInBackground(Todo... todos) {
            Todo todo = todos[0];
            todoRepository.updateTodo(todo);
            return todo;
        }

        @Override
        protected void onPostExecute(Todo todo) {
            if(doWebCall){
                Call<Todo> call = webservice.putTodo(todo.getId(), todo);
                call.enqueue(new Callback<Todo>() {
                    @Override
                    public void onResponse(Call<Todo> call, Response<Todo> response) {

                    }

                    @Override
                    public void onFailure(Call<Todo> call, Throwable t) {

                    }
                });
            }
            super.onPostExecute(todo);
        }
    }

    private static class InsertTodo extends AsyncTask<Todo, Void, Todo> {

        private TodoRepository todoRepository;
        private IWebService webservice;
        private final boolean doWebCall;

        InsertTodo(TodoRepository todoRepository, IWebService webservice, boolean doWebCall) {
            this.todoRepository = todoRepository;
            this.webservice = webservice;
            this.doWebCall = doWebCall;
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
            if(doWebCall){
                Call<Todo> call = webservice.postTodo(todo);
                call.enqueue(new Callback<Todo>() {
                    @Override
                    public void onResponse(Call<Todo> call, Response<Todo> response) {

                    }

                    @Override
                    public void onFailure(Call<Todo> call, Throwable t) {

                    }
                });
            }
            super.onPostExecute(todo);
        }
    }
}
