package de.zitruism.thl_todo_liste.network;

import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.util.List;

import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Response;

public class SyncTodos extends AsyncTask<Void, Void, Void> {

    private TodoRepository repository;
    private IWebService webService;

    public SyncTodos(TodoRepository repository, IWebService webService) {
        this.repository = repository;
        this.webService = webService;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        int count = repository.count();
        if(count > 0){
            try {
                webService.deleteAll().execute();
                List<Todo> todos = repository.getAllTodos();
                for (Todo t : todos) {
                    webService.postTodo(t).execute();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                Response<List<Todo>> response = webService.getAll().execute();
                if(response.body() != null)
                    repository.insertTodos(response.body());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}