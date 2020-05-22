package de.zitruism.thl_todo_liste.network;

import android.os.AsyncTask;

import de.zitruism.thl_todo_liste.database.repository.TodoRepository;

public class DeleteAllLocalTodos extends AsyncTask<Void, Void, Void> {

    private TodoRepository repository;

    public DeleteAllLocalTodos(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        repository.deleteAll();
        return null;
    }
}