package de.zitruism.thl_todo_liste.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

import de.zitruism.thl_todo_liste.database.model.Todo;
import de.zitruism.thl_todo_liste.database.repository.TodoRepository;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Response;

public class DeleteAllRemoteTodos extends AsyncTask<Void, Void, Void> {

    private IWebService webService;

    public DeleteAllRemoteTodos(IWebService webService) {
        this.webService = webService;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            webService.deleteAll().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}