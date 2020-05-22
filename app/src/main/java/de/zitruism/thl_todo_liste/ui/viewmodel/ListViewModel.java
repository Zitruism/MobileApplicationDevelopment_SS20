package de.zitruism.thl_todo_liste.ui.viewmodel;

import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.network.DeleteAllLocalTodos;
import de.zitruism.thl_todo_liste.network.DeleteAllRemoteTodos;
import de.zitruism.thl_todo_liste.network.SyncTodos;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ListViewModel extends ViewModel {

    private TodoRepository todoRepository;

    private IWebService webservice;

    ListViewModel(TodoRepository todoRepository, IWebService webservice) {
        this.todoRepository = todoRepository;
        this.webservice = webservice;
    }

    public LiveData<List<Todo>> getTodos(){
        return todoRepository.getAll();
    }

    public void updateDone(Long id, boolean isDone){
        new UpdateDone(todoRepository, id, isDone, webservice).execute();
    }

    public void updateFavorite(Long id, boolean isFavorite){
        new UpdateFavorite(todoRepository, id, isFavorite, webservice).execute();
    }

    public void syncTodos() {
        new SyncTodos(todoRepository, webservice).execute();
    }

    public void deleteLocal() {
        new DeleteAllLocalTodos(todoRepository).execute();
    }

    public void deleteRemote() {
        new DeleteAllRemoteTodos(webservice).execute();
    }

    private static class UpdateDone extends AsyncTask<Void, Void, Void> {

        private final TodoRepository todoRepository;
        private final Long id;
        private final boolean isDone;
        private IWebService webService;

        UpdateDone(TodoRepository todoRepository, Long id, boolean isDone, IWebService webService) {
            this.todoRepository = todoRepository;
            this.id = id;
            this.isDone = isDone;
            this.webService = webService;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            todoRepository.updateDone(id, isDone);
            webService.putTodo(id, todoRepository.readTodo(id)).enqueue(new Callback<Todo>() {
                @Override
                public void onResponse(Call<Todo> call, Response<Todo> response) {

                }

                @Override
                public void onFailure(Call<Todo> call, Throwable t) {

                }
            });
            return null;
        }
    }

    private static class UpdateFavorite extends AsyncTask<Void, Void, Void> {

        private final TodoRepository todoRepository;
        private final Long id;
        private final boolean isDone;
        private IWebService webService;

        UpdateFavorite(TodoRepository todoRepository, Long id, boolean isDone, IWebService webService) {
            this.todoRepository = todoRepository;
            this.id = id;
            this.isDone = isDone;
            this.webService = webService;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            todoRepository.updateFavorite(id, isDone);
            webService.putTodo(id, todoRepository.readTodo(id)).enqueue(new Callback<Todo>() {
                @Override
                public void onResponse(Call<Todo> call, Response<Todo> response) {

                }

                @Override
                public void onFailure(Call<Todo> call, Throwable t) {

                }
            });
            return null;
        }
    }

    // Webservice calls
    public void getWebTodos(Callback<List<Todo>> callback){


        Call<List<Todo>> call = webservice.getAll();
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(Call<List<Todo>> call, Response<List<Todo>> response) {

            }

            @Override
            public void onFailure(Call<List<Todo>> call, Throwable t) {

            }
        });

    }



}
