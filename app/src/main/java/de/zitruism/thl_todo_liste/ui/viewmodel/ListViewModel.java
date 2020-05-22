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

    public void updateDone(Long id, boolean isDone, boolean doWebCall){
        new UpdateDone(todoRepository, id, isDone, webservice, doWebCall).execute();
    }

    public void updateFavorite(Long id, boolean isFavorite, boolean doWebCall){
        new UpdateFavorite(todoRepository, id, isFavorite, webservice, doWebCall).execute();
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
        private final boolean doWebCall;

        UpdateDone(TodoRepository todoRepository, Long id, boolean isDone, IWebService webService, boolean doWebCall) {
            this.todoRepository = todoRepository;
            this.id = id;
            this.isDone = isDone;
            this.webService = webService;
            this.doWebCall = doWebCall;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int updated = todoRepository.updateDone(id, isDone);
            if(updated > 0 && doWebCall){
                webService.putTodo(id, todoRepository.readTodo(id)).enqueue(new Callback<Todo>() {
                    @Override
                    public void onResponse(Call<Todo> call, Response<Todo> response) {

                    }

                    @Override
                    public void onFailure(Call<Todo> call, Throwable t) {

                    }
                });
            }
            return null;
        }
    }

    private static class UpdateFavorite extends AsyncTask<Void, Void, Void> {

        private final TodoRepository todoRepository;
        private final Long id;
        private final boolean isDone;
        private IWebService webService;
        private final boolean doWebCall;

        UpdateFavorite(TodoRepository todoRepository, Long id, boolean isDone, IWebService webService, boolean doWebCall) {
            this.todoRepository = todoRepository;
            this.id = id;
            this.isDone = isDone;
            this.webService = webService;
            this.doWebCall = doWebCall;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int updated = todoRepository.updateFavorite(id, isDone);
            if(updated > 0 && doWebCall){
                webService.putTodo(id, todoRepository.readTodo(id)).enqueue(new Callback<Todo>() {
                    @Override
                    public void onResponse(Call<Todo> call, Response<Todo> response) {

                    }

                    @Override
                    public void onFailure(Call<Todo> call, Throwable t) {

                    }
                });
            }
            return null;
        }
    }

   /* // Webservice calls
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
*/


}
